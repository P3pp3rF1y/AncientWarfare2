package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.AIHelper;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefault;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public abstract class NpcPlayerOwned extends NpcBase implements IKeepFood, INpc {
	private static final String COMMAND_TAG = "command";
	private static final String TOWN_HALL_TAG = "townHall";
	private static final String UPKEEP_POS_TAG = "upkeepPos";
	public boolean isAlarmed = false;

	private Command playerIssuedCommand;
	private int foodValueRemaining = 0;

	NpcAIPlayerOwnedRideHorse horseAI;

	private BlockPos townHallPosition;
	private BlockPos upkeepAutoBlock;

	public NpcPlayerOwned(World par1World) {
		super(par1World);
		NpcDefault npcDefault = NpcDefaultsRegistry.getOwnedNpcDefault(this);
		npcDefault.applyPathSettings((PathNavigateGround) getNavigator());
		npcDefault.applyAttributes(this);
		inventoryArmorDropChances = new float[] {1.f, 1.f, 1.f, 1.f};
		inventoryHandsDropChances = new float[] {1.f, 1.f};

	}

	@Override
	public int getMaxFallHeight() {
		return super.getMaxFallHeight() - 1;
	}

	@Override
	public void onDeath(DamageSource source) {
		if (!world.isRemote) {
			if (horseAI != null) {
				horseAI.onKilled();
			}
			validateTownHallPosition();
			getTownHall().ifPresent(townHall -> townHall.handleNpcDeath(this, source));
		}
		super.onDeath(source);
	}

	@Override
	protected void onRepack() {
		super.onRepack();
		if (horseAI != null) {
			horseAI.onKilled();
		}
	}

	@Override
	public final int getArmorValueOverride() {
		return -1;
	}

	@Override
	public final int getAttackDamageOverride() {
		return -1;
	}

	private void setTownHallPosition(@Nullable BlockPos pos) {
		townHallPosition = pos;
	}

	@Override
	public Optional<BlockPos> getTownHallPosition() {
		return Optional.ofNullable(townHallPosition);
	}

	public Optional<TileTownHall> getTownHall() {
		return getTownHallPosition().flatMap(p -> WorldTools.getTile(world, p, TileTownHall.class));
	}

	public void handleTownHallBroadcast(BlockPos position) {
		validateTownHallPosition();
		Optional<BlockPos> townHallPos = getTownHallPosition();
		if (townHallPos.isPresent()) {
			BlockPos pos = townHallPos.get();
			double curDist = getDistanceSq(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
			double newDist = getDistanceSq(position.getX() + 0.5d, position.getY(), position.getZ() + 0.5d);
			if (newDist < curDist) {
				setTownHallPosition(position);
				if (upkeepAutoBlock == null || upkeepAutoBlock.equals(pos)) {
					upkeepAutoBlock = position;
				}
			}
		} else {
			setTownHallPosition(position);
			if (upkeepAutoBlock == null) {
				upkeepAutoBlock = position;
			}
		}
		// (un)set alarmed status
		isAlarmed = getTownHall().map(t -> t.alarmActive).orElse(false);
	}

	private void validateTownHallPosition() {
		Optional<BlockPos> townHallPos = getTownHallPosition();
		if (!townHallPos.isPresent()) {
			return;
		}
		BlockPos pos = townHallPos.get();
		if (!world.isBlockLoaded(pos)) {
			return;
		}//cannot validate, unloaded...assume good
		Optional<TileTownHall> te = WorldTools.getTile(world, pos, TileTownHall.class);
		if (te.isPresent() && hasCommandPermissions(te.get().getOwner())) {
			return;
		}
		setTownHallPosition(null);
	}

	/*
	 * Returns the currently following player-issues command, or null if none
	 */
	public Command getCurrentCommand() {
		return playerIssuedCommand;
	}

	/*
	 * input path from command baton - default implementation for player-owned NPC is to set current command==input command and then let AI do the rest
	 */
	public void handlePlayerCommand(@Nullable Command cmd) {
		if (cmd != null && cmd.type == CommandType.ATTACK) {
			handleAttackCommand(cmd);
			return;
		}
		setPlayerCommand(cmd);
	}

	private void handleAttackCommand(Command cmd) {
		Entity e = cmd.getEntityTarget(world);
		if (e instanceof EntityLivingBase) {
			EntityLivingBase elb = (EntityLivingBase) e;
			if (canTarget(elb))//only attacked allowed targets
			{
				setAttackTarget(elb);
			}
		}
		setPlayerCommand(null);
	}

	public void setPlayerCommand(@Nullable Command cmd) {
		this.playerIssuedCommand = cmd;
	}

	@Override
	public boolean isHostileTowards(Entity entityTarget) {
		if ((entityTarget instanceof NpcPlayerOwned) || (entityTarget instanceof EntityPlayer)) {
			return !getOwner().isOwnerOrSameTeamOrFriend(entityTarget);
		} else if (entityTarget instanceof NpcFaction) {
			return ((NpcFaction) entityTarget).isHostileTowards(this); // hostility is based on faction standing
		}
		return NpcDefaultsRegistry.getOwnedNpcDefault(this).isTarget(entityTarget) || AIHelper.isAdditionalEntityToTarget(entityTarget);
	}

	@Override
	public boolean canTarget(Entity e) {
		// don't let npcs target their own teams npcs/players
		return !getOwner().isOwnerOrSameTeamOrFriend(e) && e instanceof EntityLivingBase;
	}

	@Override
	public boolean canBeAttackedBy(Entity e) {
		return !getOwner().isOwnerOrSameTeamOrFriend(e);
	}

	@Override
	public void onWeaponInventoryChanged() {
		//TODO do we need to do anything to update skin info on client here?
	}

	@Override
	public int getFoodRemaining() {
		return foodValueRemaining;
	}

	@Override
	public void setFoodRemaining(int food) {
		this.foodValueRemaining = food;
	}

	@Override
	public Optional<BlockPos> getUpkeepPoint() {
		return UpkeepOrder.getUpkeepOrder(upkeepStack).map(UpkeepOrder::getUpkeepPosition).orElse(Optional.ofNullable(upkeepAutoBlock));
	}

	@Override
	public void setUpkeepAutoPosition(@Nullable BlockPos pos) {
		upkeepAutoBlock = pos;
	}

	@Override
	public EnumFacing getUpkeepBlockSide() {
		return UpkeepOrder.getUpkeepOrder(upkeepStack).map(UpkeepOrder::getUpkeepBlockSide).orElse(EnumFacing.DOWN);
	}

	@Override
	public int getUpkeepDimensionId() {
		return UpkeepOrder.getUpkeepOrder(upkeepStack).map(UpkeepOrder::getUpkeepDimension).orElse(world.provider.getDimension());
	}

	@Override
	public int getUpkeepAmount() {
		return UpkeepOrder.getUpkeepOrder(upkeepStack).map(UpkeepOrder::getUpkeepAmount).orElse(AWNPCStatics.npcDefaultUpkeepWithdraw);
	}

	@Override
	protected boolean tryCommand(EntityPlayer player) {
		return hasCommandPermissions(player.getUniqueID(), player.getName()) && super.tryCommand(player);
	}

	@Override
	protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			ItemStack itemstack = this.getItemStackFromSlot(slot);
			if (!itemstack.isEmpty()) {
				this.entityDropItem(itemstack, 0.0F);
			}
			setItemStackToSlot(slot, ItemStack.EMPTY);
		}
		if (!AWNPCStatics.persistOrdersOnDeath) {
			if (!ordersStack.isEmpty()) {
				entityDropItem(ordersStack, 0.f);
			}
			if (!upkeepStack.isEmpty()) {
				entityDropItem(upkeepStack, 0.f);
			}
			ordersStack = ItemStack.EMPTY;
			upkeepStack = ItemStack.EMPTY;
		}
	}

	public boolean withdrawFood(IItemHandler handler) {
		int amount = getUpkeepAmount() - getFoodRemaining();
		if (amount <= 0) {
			return true;
		}
		@Nonnull ItemStack stack;
		int val;
		int eaten = 0;
		for (int slot = 0; slot < handler.getSlots(); slot++) {
			stack = handler.getStackInSlot(slot);
			val = AncientWarfareNPC.statics.getFoodValue(stack);
			if (val <= 0) {
				continue;
			}
			while (eaten < amount && !stack.isEmpty()) {
				eaten += val;
				handler.extractItem(slot, 1, false);
			}
		}
		setFoodRemaining(getFoodRemaining() + eaten);
		return getFoodRemaining() >= getUpkeepAmount();
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		if (getFoodRemaining() < getUpkeepAmount()) {
			int value = AncientWarfareNPC.statics.getFoodValue(player.getHeldItem(hand));
			if (value > 0) {
				if (!world.isRemote) {
					player.getHeldItem(hand).shrink(1);
				}
				foodValueRemaining += value;
				return true;
			}
		}
		return super.processInteract(player, hand);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (foodValueRemaining > 0 && !isSleeping()) {
			foodValueRemaining--;
		}
	}

	@Nullable
	@Override
	public Entity changeDimension(int dimensionIn) {
		this.townHallPosition = null;
		this.upkeepAutoBlock = null;
		return super.changeDimension(dimensionIn);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		super.readEntityFromNBT(tag);
		foodValueRemaining = tag.getInteger("foodValue");
		if (tag.hasKey(COMMAND_TAG)) {
			playerIssuedCommand = new Command(tag.getCompoundTag(COMMAND_TAG));
		}
		if (tag.hasKey(TOWN_HALL_TAG)) {
			townHallPosition = BlockPos.fromLong(tag.getLong(TOWN_HALL_TAG));
		}
		if (tag.hasKey(UPKEEP_POS_TAG)) {
			upkeepAutoBlock = BlockPos.fromLong(tag.getLong(UPKEEP_POS_TAG));
		}
		onOrdersInventoryChanged();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger("foodValue", foodValueRemaining);
		if (playerIssuedCommand != null) {
			tag.setTag(COMMAND_TAG, playerIssuedCommand.writeToNBT(new NBTTagCompound()));
		}
		if (townHallPosition != null) {
			tag.setLong(TOWN_HALL_TAG, townHallPosition.toLong());
		}
		if (upkeepAutoBlock != null) {
			tag.setLong(UPKEEP_POS_TAG, upkeepAutoBlock.toLong());
		}
	}

}
