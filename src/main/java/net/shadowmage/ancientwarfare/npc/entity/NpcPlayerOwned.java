package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.CommandType;
import net.shadowmage.ancientwarfare.npc.orders.UpkeepOrder;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class NpcPlayerOwned extends NpcBase implements IKeepFood, INpc {

	public boolean isAlarmed = false;
	public boolean deathNotifiedTownhall = false;

	private Command playerIssuedCommand;//TODO load/save
	private int foodValueRemaining = 0;

	protected NpcAIPlayerOwnedRideHorse horseAI;

	private BlockPos townHallPosition;
	private BlockPos upkeepAutoBlock;

	public NpcPlayerOwned(World par1World) {
		super(par1World);
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
			TileTownHall townHall = getTownHall();
			if (townHall != null) {
				deathNotifiedTownhall = true;
				townHall.handleNpcDeath(this, source);
			}
		}
		super.onDeath(source);
	}

	@Override
	public final int getArmorValueOverride() {
		return -1;
	}

	@Override
	public final int getAttackDamageOverride() {
		return -1;
	}

	public void setTownHallPosition(BlockPos pos) {
		if (pos != null) {
			this.townHallPosition = pos;
		} else {
			this.townHallPosition = null;
		}
	}

	@Override
	public BlockPos getTownHallPosition() {
		return townHallPosition;
	}

	public TileTownHall getTownHall() {
		BlockPos pos = getTownHallPosition();
		if (pos != null) {
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TileTownHall) {
				return (TileTownHall) te;
			}
		}
		return null;
	}

	public void handleTownHallBroadcast(TileTownHall tile, BlockPos position) {
		validateTownHallPosition();
		BlockPos pos = getTownHallPosition();
		if (pos != null) {
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
		isAlarmed = getTownHall().alarmActive;
	}

	private boolean validateTownHallPosition() {
		BlockPos pos = getTownHallPosition();
		if (pos == null) {
			return false;
		}
		if (!world.isBlockLoaded(pos)) {
			return true;
		}//cannot validate, unloaded...assume good
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TileTownHall) {
			TileTownHall townHall = (TileTownHall) te;
			if (hasCommandPermissions(townHall.getOwnerUuid(), townHall.getOwnerName()))
				return true;
		}
		setTownHallPosition(null);
		return false;
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
	public void handlePlayerCommand(Command cmd) {
		if (cmd != null && cmd.type == CommandType.ATTACK) {
			Entity e = cmd.getEntityTarget(world);
			AWLog.logDebug("Handling attack command : " + e);
			if (e instanceof EntityLivingBase) {
				EntityLivingBase elb = (EntityLivingBase) e;
				if (canTarget(elb))//only attacked allowed targets
				{
					setAttackTarget(elb);
				}
			}
			cmd = null;
		}
		this.setPlayerCommand(cmd);
	}

	public void setPlayerCommand(Command cmd) {
		this.playerIssuedCommand = cmd;
	}

	@Override
	public boolean isHostileTowards(Entity entityTarget) {
		if (NpcAI.isAlwaysHostileToNpcs(entityTarget))
			return true;
		else if ((entityTarget instanceof NpcPlayerOwned) || (entityTarget instanceof EntityPlayer)) {
			return !isEntitySameTeamOrFriends(entityTarget);
		} else if (entityTarget instanceof NpcFaction) {
			return ((NpcFaction) entityTarget).isHostileTowards(this); // hostility is based on faction standing
		} else {
			// TODO
			// This is for forced inclusions, which we don't currently support in new auto-targeting. This
			// is complicated because reasons. See comments in the AWNPCStatics class for details.

			if (!AncientWarfareNPC.statics.autoTargetting) {
				String n = EntityList.getEntityString(entityTarget);
				List<String> targets = AncientWarfareNPC.statics.getValidTargetsFor(getNpcType(), getNpcSubType());
				if (targets.contains(n)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean canTarget(Entity e) {
		if (isEntitySameTeamOrFriends(e))
			return false; // don't let npcs target their own teams npcs/players
		return e instanceof EntityLivingBase;
	}

	@Override
	public boolean canBeAttackedBy(Entity e) {
		if (isEntitySameTeamOrFriends(e))
			return false; // can only be attacked by different team - prevent friendly fire and neutral infighting
		return true;
	}

	@Override
	public void onWeaponInventoryChanged() {
		updateTexture();
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
	public BlockPos getUpkeepPoint() {
		UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
		if (order != null) {
			return order.getUpkeepPosition();
		}
		return upkeepAutoBlock;
	}

	@Override
	public void setUpkeepAutoPosition(BlockPos pos) {
		upkeepAutoBlock = pos;
	}

	@Override
	public EnumFacing getUpkeepBlockSide() {
		UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
		if (order != null) {
			return order.getUpkeepBlockSide();
		}
		return EnumFacing.DOWN;
	}

	@Override
	public int getUpkeepDimensionId() {
		UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
		if (order != null) {
			return order.getUpkeepDimension();
		}
		return world.provider.getDimension();
	}

	@Override
	public int getUpkeepAmount() {
		UpkeepOrder order = UpkeepOrder.getUpkeepOrder(upkeepStack);
		if (order != null) {
			return order.getUpkeepAmount();
		}
		return AWNPCStatics.npcDefaultUpkeepWithdraw;
	}

	@Override
	protected boolean tryCommand(EntityPlayer player) {
		if (hasCommandPermissions(player.getUniqueID(), player.getName()))
			return super.tryCommand(player);
		return false;
	}

	public boolean withdrawFood(IItemHandler handler) {
		int amount = getUpkeepAmount() - getFoodRemaining();
		if (amount <= 0) {
			return true;
		}
		@Nonnull ItemStack stack;
		int val;
		int eaten = 0;
		for (int i = 0; i < handler.getSlots(); i++) {
			stack = handler.getStackInSlot(i);
			val = AncientWarfareNPC.statics.getFoodValue(stack);
			if (val <= 0) {
				continue;
			}
			while (eaten < amount && !stack.isEmpty()) {
				eaten += val;
				stack.shrink(1);
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
		if (tag.hasKey("command")) {
			playerIssuedCommand = new Command(tag.getCompoundTag("command"));
		}
		if (tag.hasKey("townHall")) {
			townHallPosition = BlockPos.fromLong(tag.getLong("townHall"));
		}
		if (tag.hasKey("upkeepPos")) {
			upkeepAutoBlock = BlockPos.fromLong(tag.getLong("upkeepPos"));
		}
		onOrdersInventoryChanged();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		tag.setInteger("foodValue", foodValueRemaining);
		if (playerIssuedCommand != null) {
			tag.setTag("command", playerIssuedCommand.writeToNBT(new NBTTagCompound()));
		}
		if (townHallPosition != null) {
			tag.setLong("townHall", townHallPosition.toLong());
		}
		if (upkeepAutoBlock != null) {
			tag.setLong("upkeepPos", upkeepAutoBlock.toLong());
		}
	}

}
