package net.shadowmage.ancientwarfare.npc.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityFlying;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.walkers.ItemStackData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketEntity;
import net.shadowmage.ancientwarfare.core.owner.IOwnable;
import net.shadowmage.ancientwarfare.core.owner.Owner;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.npc.ai.NpcNavigator;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.item.ItemShield;
import net.shadowmage.ancientwarfare.npc.registry.NpcDefaultsRegistry;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinSettings;
import net.shadowmage.ancientwarfare.vehicle.entity.IPathableEntity;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.pathing.Node;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static net.shadowmage.ancientwarfare.npc.config.AWNPCStatics.npcLevelDamageMultiplier;

public abstract class NpcBase extends EntityCreature implements IEntityAdditionalSpawnData, IOwnable, IEntityPacketHandler, IPathableEntity, INpc {

	private static final DataParameter<Integer> AI_TASKS = EntityDataManager.createKey(NpcBase.class, DataSerializers.VARINT);
	private static final DataParameter<BlockPos> BED_POS = EntityDataManager.createKey(NpcBase.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Byte> BED_DIRECTION = EntityDataManager.createKey(NpcBase.class, DataSerializers.BYTE);
	private static final DataParameter<Boolean> IS_SLEEPING = EntityDataManager.createKey(NpcBase.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(NpcBase.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> BLOCKING = EntityDataManager.createKey(NpcBase.class, DataSerializers.BOOLEAN);
	private static final String SLOT_NUM_TAG = "slotNum";
	private static final String BED_DIRECTION_TAG = "bedDirection";
	private static final String IS_SLEEPING_TAG = "isSleeping";
	private static final String CACHED_BED_POS_TAG = "cachedBedPos";
	private static final String BED_POS_TAG = "bedPos";
	private static final String FOUND_BED_TAG = "foundBed";
	private static final String ORDERS_STACK_TAG = "ordersStack";
	private static final String UPKEEP_STACK_TAG = "upkeepStack";
	private static final String LEVELING_STATS_TAG = "levelingStats";
	private static final String MAX_HEALTH_TAG = "maxHealth";
	private static final String HEALTH_TAG = "health";
	private static final String ATTACK_DAMAGE_OVERRIDE_TAG = "attackDamageOverride";
	private static final String ARMOR_VALUE_OVERRIDE_TAG = "armorValueOverride";
	private static final String AI_ENABLED_TAG = "aiEnabled";
	private static final String HAS_CUSTOM_EQUIPMENT_TAG = "hasCustomEquipment";
	public static final int ORDER_SLOT = 6;
	public static final int UPKEEP_SLOT = 7;

	@Nonnull
	public ItemStack ordersStack = ItemStack.EMPTY;
	private static final String DO_NOT_PURSUE = "donotpursue";
	private Owner owner = Owner.EMPTY;

	private String followingPlayerName;//set/cleared onInteract from player if player.team==this.team
	private NpcLevelingStats levelingStats;

	private NpcSkinSettings skinSettings = new NpcSkinSettings();

	@Nonnull
	public ItemStack upkeepStack = ItemStack.EMPTY;

	// used for flee/distress AI
	// this isn't really useful yet, combat NPC's will attack any mob they come in
	// contact with during a distress response
	public Set<Entity> nearbyHostiles = new LinkedHashSet<Entity>();

	private boolean aiEnabled = true;
	public boolean doNotPursue = false; //if the npc should not pursue targets away from its position/route
	private boolean hasCustomEquipment = false; //faction based only

	private int attackDamage = -1;//faction based only
	private int armorValue = -1;//faction based only
	private int maxHealthOverride = -1;

	private BlockPos cachedBedPos;
	private boolean foundBed = false;
	private boolean rainedOn = false;
	private float originalWidth;
	private float originalHeight;

	public NpcBase(World par1World) {
		super(par1World);
		levelingStats = new NpcLevelingStats(this);
		navigator = new NpcNavigator(this);
		setPathPriority(PathNodeType.DOOR_WOOD_CLOSED, 0);
	}

	@Override
	protected void entityInit() {
		super.entityInit();
		dataManager.register(AI_TASKS, 0);
		dataManager.register(BED_POS, BlockPos.ORIGIN);
		dataManager.register(BED_DIRECTION, (byte) EnumFacing.NORTH.ordinal());
		dataManager.register(IS_SLEEPING, false);
		dataManager.register(SWINGING_ARMS, false);
		dataManager.register(BLOCKING, false);
	}

	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_SPEED);
	}

	private ItemStack getShieldStack() {
		return getHeldItem(EnumHand.OFF_HAND);
	}

	public void setShieldStack(ItemStack stack) {
		setHeldItem(EnumHand.OFF_HAND, stack);
	}

	public int getMaxHealthOverride() {
		return maxHealthOverride;
	}

	public void setMaxHealthOverride(int maxHealthOverride) {
		this.maxHealthOverride = maxHealthOverride;
		if (maxHealthOverride > 0) {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(maxHealthOverride);
			if (getHealth() < getMaxHealth()) {
				setHealth(getMaxHealth());
			}
		}
	}

	public void setAttackDamageOverride(int attackDamage) {
		this.attackDamage = attackDamage;
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(attackDamage);
	}

	public void setArmorValueOverride(int armorValue) {
		this.armorValue = armorValue;
	}

	public int getArmorValueOverride() {
		return armorValue;
	}

	public int getAttackDamageOverride() {
		return attackDamage;
	}

	public void setCustomEquipmentOverride(boolean val) {
		hasCustomEquipment = val;
	}

	public boolean getCustomEquipmentOverride() {
		return hasCustomEquipment;
	}

	@Override
	public boolean attackEntityAsMob(Entity target) {
		float damage = (float) getAttackDamageOverride();

		ItemStack shield = ItemStack.EMPTY;
		if (damage < 0 && !getShieldStack().isEmpty()) {
			shield = getShieldStack().copy();
			getAttributeMap().applyAttributeModifiers(shield.getAttributeModifiers(EntityEquipmentSlot.OFFHAND));
		}

		boolean targetHit = vanillaAttackEntityAsMob(target);

		if (targetHit && target instanceof EntityLivingBase) {
			ItemStack item = getHeldItemMainhand();
			getHeldItemMainhand().getItem().hitEntity(item, (EntityLivingBase) target, this);
		}

		if (!shield.isEmpty()) {
			getAttributeMap().removeAttributeModifiers(shield.getAttributeModifiers(EntityEquipmentSlot.OFFHAND));
		}
		return targetHit;
	}

	@SuppressWarnings({"squid:S3776", "ConstantConditions"})
	/*
		not changing this because it's the exact copy of implementation of attackEntityAsMob from EntityMob that we should have here
		this should make upgrading to new versions easier because it should only mean copying the code from EntityMob here in case something changes there
		*/
	private boolean vanillaAttackEntityAsMob(Entity target) {
		float f = (float) getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;

		if (target instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(getHeldItemMainhand(), ((EntityLivingBase) target).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = target.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (flag) {
			if (i > 0 && target instanceof EntityLivingBase) {
				((EntityLivingBase) target).knockBack(this, (float) i * 0.5F, MathHelper.sin(rotationYaw * 0.017453292F), -MathHelper.cos(rotationYaw * 0.017453292F));
				motionX *= 0.6D;
				motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0) {
				target.setFire(j * 4);
			}

			if (target instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) target;
				ItemStack itemstack = getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

				if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, this) && itemstack1.getItem().isShield(itemstack1, entityplayer)) {
					float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
						world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}

			applyEnchantments(this, target);
		}
		return flag;
	}

	/*
	 * Proper calculations for all types of armors, including shields
	 */
	@Override
	protected float applyArmorCalculations(DamageSource source, float amount) {
		if (!source.isUnblockable()) {
			if (getArmorValueOverride() >= 0) {
				return super.applyArmorCalculations(source, amount);
			} else {
				NonNullList<ItemStack> armor = NonNullList.create();
				for (ItemStack armorPiece : getArmorInventoryList()) {
					armor.add(armorPiece);
				}
				float value = ISpecialArmor.ArmorProperties.applyArmor(this, armor, source, amount);
				if (value > 0.0F && getShieldStack().getItem() instanceof ItemShield) {
					float absorb = value * ((ItemShield) getShieldStack().getItem()).getArmorBonusValue() / 25F;
					int dmg = Math.max((int) absorb, 1);
					getShieldStack().damageItem(dmg, this);
					value -= absorb;
				}
				if (value < 0.0F)
					return 0;
				return value;
			}
		}
		return amount;
	}

	/*
	 * Deprecated vanilla armor calculations
	 */
	@Override
	public int getTotalArmorValue() {
		if (getArmorValueOverride() >= 0) {
			return getArmorValueOverride();
		}
		int value = super.getTotalArmorValue();
		if (getShieldStack().getItem() instanceof ItemShield) {
			ItemShield shield = (ItemShield) getShieldStack().getItem();
			value += shield.getArmorBonusValue();
		}
		return value;
	}

	@Override
	public final double getYOffset() {
		return -0.35D;
	}

	@Override
	public int getMaxFallHeight() {
		return getAttackTarget() == null ? 4 : 4 + (int) (getHealth() / 3);
	}

	@Override
	public float getBlockPathWeight(BlockPos pos) {
		IBlockState stateBelow = world.getBlockState(pos.down());
		if (stateBelow.getMaterial() == Material.LAVA || stateBelow.getMaterial() == Material.CACTUS)//Avoid cacti and lava when wandering
			return -10;
		else if (stateBelow.getMaterial().isLiquid())//Don't try swimming too much
			return 0;
		float level = getLitBlockWeight(pos);//Prefer lit areas
		if (level < 0)
			return 0;
		else
			return level + (stateBelow.isSideSolid(world, pos.down(), EnumFacing.UP) ? 1 : 0);
	}

	protected float getLitBlockWeight(BlockPos pos) {
		return world.getLightBrightness(pos);
	}

	public double getDistanceSqFromHome() {
		if (!hasHome()) {
			return 0;
		}
		BlockPos home = getHomePosition();
		return getDistanceSq(home.getX() + 0.5d, home.getY(), home.getZ() + 0.5d);
	}

	public Optional<BlockPos> getTownHallPosition() {
		return Optional.empty();//NOOP on non-player owned npc
	}

	public void setHomeAreaAtCurrentPosition() {
		setHomePosAndDistance(new BlockPos(MathHelper.floor(posX), MathHelper.floor(posY), MathHelper.floor(posZ)), getHomeRange());
	}

	public int getHomeRange() {
		if (hasHome()) {
			return MathHelper.floor(getMaximumHomeDistance());
		}
		return 5;
	}

	/*
	 * Return true if this NPC should be within his home range.<br>
	 * Should still allow for a combat NPC to attack targets outside his home range.
	 */
	public boolean shouldBeAtHome() {
		if (getAttackTarget() != null || !hasHome()) {
			return false;
		}
		if (world.isRainingAt(getPosition())) {
			setRainedOn(true);
		}
		return shouldSleep() || isWaitingForRainToStop();
	}

	private boolean isWaitingForRainToStop() {
		if (worksInRain() || !world.isRaining()) {
			// rain has stopped, reset
			setRainedOn(false);
			return false;
		}
		return rainedOn;
	}

	public boolean worksInRain() {
		return false;
	}

	public boolean isPassive() {
		return true;
	}

	private void setRainedOn(boolean rainedOn) {
		this.rainedOn = rainedOn;
	}

	public void setIsAIEnabled(boolean val) {
		aiEnabled = val;
	}

	public boolean getIsAIEnabled() {
		return aiEnabled && !AWNPCStatics.npcAIDebugMode;
	}

	@Override
	protected boolean processInteract(EntityPlayer player, EnumHand hand) {
		return tryCommand(player);
	}

	/*
	 * should be implemented by any npc that wishes to open a GUI on interact<br>
	 * must be called from interact code to actually open the GUI<br>
	 * allows for subtypes/etc to vary the opened GUI without re-implementing the interact logic
	 */
	public void openGUI(EntityPlayer player) {
		NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);
	}

	/*
	 * if this npc has an alt-control GUI, open it here.<br>
	 * should called from the npc inventory gui.
	 */
	public void openAltGui(EntityPlayer player) {

	}

	/*
	 * used by the npc inventory gui to determine if it should display the 'alt control gui' button<br>
	 * this setting must return true -on the client- if the button is to be displayed.
	 */
	public boolean hasAltGui() {
		return false;
	}

	protected boolean tryCommand(EntityPlayer player) {
		boolean baton = !EntityTools.getItemFromEitherHand(player, ItemCommandBaton.class).isEmpty();
		if (!baton) {
			if (!world.isRemote) {
				if (player.isSneaking()) {
					if (followingPlayerName != null && followingPlayerName.equals(player.getName())) {
						followingPlayerName = null;
					} else {
						followingPlayerName = player.getName();
					}
				} else {
					openGUI(player);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
		if (!isInWater() && !isHostileTowards(entity)) {
			int d0 = (int) Math.signum(posX - entity.posX);
			int d1 = (int) Math.signum(posZ - entity.posZ);
			if (d0 != 0 || d1 != 0) {
				Material material = world.getBlockState(new BlockPos(posX + d0, getEntityBoundingBox().minY - 1, posZ + d1)).getMaterial();
				if (material.isLiquid() || material == Material.CACTUS) {
					return;
				}
				entityCollisionReduction = 0.9F;
			}
		}
		super.applyEntityCollision(entity);
		entityCollisionReduction = 0;
	}

	@Override
	public final boolean attackEntityFrom(DamageSource source, float damage) {
		if (isSleeping()) { // prevent suffocation damage (allows bunk beds and such)
			return false;
		}
		if (source.getTrueSource() != null && !canBeAttackedBy(source.getTrueSource())) {
			return false;
		}
		return super.attackEntityFrom(source, damage);
	}

	protected void damageShield(float damage)
	{
		if (damage >= 3.0F && activeItemStack.getItem().isShield(activeItemStack, this))
		{
			int i = 1 + MathHelper.floor(damage);
			activeItemStack.damageItem(i, this);
			if (activeItemStack.isEmpty()) //shield breaks
			{
				EnumHand enumhand = getActiveHand();
				if (enumhand == EnumHand.MAIN_HAND)
				{
					setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
				}
				else
				{
					setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
				}

				activeItemStack = ItemStack.EMPTY;
				playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + world.rand.nextFloat() * 0.4F);
			}
		}
	}

	@Override
	public void setWorld(World world) {
		super.setWorld(world);
		((NpcNavigator) navigator).onWorldChange();
	}

	@Override
	public void setAttackTarget(@Nullable EntityLivingBase entity) {
		if (entity != null && !canTarget(entity)) {
			return;
		}
		super.setAttackTarget(entity);

		if (!world.isRemote && entity != null) {
			updateAttackTargetClient(entity);
		}
	}

	private void updateAttackTargetClient(EntityLivingBase entity) {
		PacketEntity pkt = new PacketEntity(this);
		pkt.packetData.setInteger("attackTarget", entity.getEntityId());
		NetworkHandler.sendToAllTracking(this, pkt);
	}

	@Override
	public final void setRevengeTarget(@Nullable EntityLivingBase entity) {
		if (entity != null && !canTarget(entity)) {
			return;
		}
		super.setRevengeTarget(entity);
	}

	@Override
	public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn) {
		return super.getItemStackFromSlot(slotIn);
	}

	public final ItemStack getItemStackFromSlot(int slot) {
		if (slot == ORDER_SLOT) {
			return ordersStack;
		} else if (slot == UPKEEP_SLOT) {
			return upkeepStack;
		} else if (slot >= 0 && slot < EntityEquipmentSlot.values().length) {
			return super.getItemStackFromSlot(EntityEquipmentSlot.values()[slot]);
		}
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemStackToSlot(EntityEquipmentSlot slot, ItemStack stack) {
		super.setItemStackToSlot(slot, stack);
		if (slot == EntityEquipmentSlot.MAINHAND) {
			onWeaponInventoryChanged();
		}
	}

	public final void setItemStackToSlot(int slot, ItemStack stack) {
		if (slot >= 0 && slot < EntityEquipmentSlot.values().length) {
			setItemStackToSlot(EntityEquipmentSlot.values()[slot], stack);
		} else if (slot == UPKEEP_SLOT) {
			upkeepStack = stack;
		} else if (slot == ORDER_SLOT) {
			ordersStack = stack;
			onOrdersInventoryChanged();
		}
	}

	@Override
	public final void onKillEntity(EntityLivingBase par1EntityLivingBase) {
		super.onKillEntity(par1EntityLivingBase);
		if (!world.isRemote) {
			addExperience(AWNPCStatics.npcXpFromKill);
			if (par1EntityLivingBase == getAttackTarget()) {
				setAttackTarget(null);
			}
		}
	}

	/*
	 * return the bitfield containing all of the currently executing AI tasks<br>
	 * used by player-owned npcs for rendering ai-tasks
	 */
	public final int getAITasks() {
		return dataManager.get(AI_TASKS);
	}

	/*
	 * add a task to the bitfield of currently executing tasks<br>
	 * input should be a ^2, or combination of (e.g. 1+2 or 2+4)<br>
	 */
	public final void addAITask(int task) {
		int tasks = getAITasks();
		int tc = tasks;
		tasks = tasks | task;
		if (tc != tasks) {
			setAITasks(tasks);
		}
	}

	/*
	 * remove a task from the bitfield of currently executing tasks<br>
	 * input should be a ^2, or combination of (e.g. 1+2 or 2+4)<br>
	 */
	public final void removeAITask(int task) {
		int tasks = getAITasks();
		int tc = tasks;
		tasks = tasks & (~task);
		if (tc != tasks) {
			setAITasks(tasks);
		}
	}

	/*
	 * set ai tasks -- only used internally
	 */
	private void setAITasks(int tasks) {
		dataManager.set(AI_TASKS, tasks);
	}

	/*
	 * add an amount of experience to this npcs leveling stats<br>
	 * experience is added for base level, and subtype level(if any)
	 */
	public final void addExperience(int amount) {
		getLevelingStats().addExperience(amount);
	}

	/*
	 * implementations should read in any data written during {@link #writeAdditionalItemData(NBTTagCompound)}
	 */
	public void readAdditionalItemData(NBTTagCompound tag) {
		NBTTagList equipmentList = tag.getTagList("equipment", Constants.NBT.TAG_COMPOUND);
		@Nonnull ItemStack stack;
		NBTTagCompound equipmentTag;
		for (int i = 0; i < equipmentList.tagCount(); i++) {
			equipmentTag = equipmentList.getCompoundTagAt(i);
			stack = new ItemStack(equipmentTag);
			if (equipmentTag.hasKey(SLOT_NUM_TAG)) {
				setItemStackToSlot(equipmentTag.getInteger(SLOT_NUM_TAG), stack);
			}
		}
		readBaseTags(tag);
	}

	/*
	 * Implementations should write out any persistent entity-data needed to restore entity-state from an item-stack.<br>
	 * This should include inventory, levels, orders, faction / etc
	 */
	public void writeAdditionalItemData(NBTTagCompound tag) {
		NBTTagList equipmentList = new NBTTagList();
		@Nonnull ItemStack stack;
		NBTTagCompound equipmentTag;
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			stack = getItemStackFromSlot(slot);
			if (stack.isEmpty()) {
				continue;
			}
			equipmentTag = stack.writeToNBT(new NBTTagCompound());
			equipmentTag.setInteger(SLOT_NUM_TAG, slot.ordinal());
			equipmentList.appendTag(equipmentTag);
		}
		tag.setTag("equipment", equipmentList);
		writeBaseTags(tag);
	}

	/*
	 * is the input stack a valid orders-item for this npc?<br>
	 * only used by player-owned NPCs
	 */
	@SuppressWarnings("squid:S1172")
	public boolean isValidOrdersStack(ItemStack stack) {
		return false;
	}

	/*
	 * callback for when orders-stack changes.  implementations should inform any necessary AI tasks of the
	 * change to order-items
	 */
	public void onOrdersInventoryChanged() {
	}

	/*
	 * callback for when weapon slot has been changed.<br>
	 * Implementations should re-set any subtype or inform any AI that need to know when
	 * weapon was changed.
	 */
	public void onWeaponInventoryChanged() {
	}

	/*
	 * return the NPCs subtype.<br>
	 * this subtype may vary at runtime.
	 */
	public abstract String getNpcSubType();

	/*
	 * return the NPCs type.  This type should be unique for the class of entity,
	 * or at least unique pertaining to the entity registration.
	 */
	public abstract String getNpcType();

	/*
	 * return the full NPC type for this npc<br>
	 * returns npcType if subtype is empty, else npcType.npcSubtype
	 */
	public String getNpcFullType() {
		String type = getNpcType();
		if (type == null || type.isEmpty()) {
			throw new RuntimeException("Type must not be null or empty:");
		}
		String sub = getNpcSubType();
		if (sub == null) {
			throw new RuntimeException("Subtype must not be null...type: " + type);
		}
		if (!sub.isEmpty()) {
			type = type + "." + sub;
		}
		return type;
	}

	@Override
	public String getName() {
		String name = I18n.translateToLocal("entity.ancientwarfarenpc." + getNpcFullType() + ".name");
		if (hasCustomName()) {
			name = name + " : " + getCustomNameTag();
		}
		return name;
	}

	public final NpcLevelingStats getLevelingStats() {
		return levelingStats;
	}

	private ItemStack getItemToSpawn() {
		return ItemNpcSpawner.getSpawnerItemForNpc(this);
	}

	public final long getIDForSkin() {
		return entityUniqueID.getLeastSignificantBits();
	}

	@Override
	public final ItemStack getPickedResult(RayTraceResult target) {
		return getItemToSpawn();
	}

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		buffer.writeLong(getUniqueID().getMostSignificantBits());
		buffer.writeLong(getUniqueID().getLeastSignificantBits());
		owner.serializeToBuffer(buffer);
		skinSettings.serializeToBuffer(buffer);
	}

	@Override
	public void readSpawnData(ByteBuf buffer) {
		long l1 = buffer.readLong();
		long l2 = buffer.readLong();
		entityUniqueID = new UUID(l1, l2);
		owner = new Owner(buffer);
		skinSettings = NpcSkinSettings.deserializeFromBuffer(buffer);
	}

	@Override
	public void onUpdate() {
		world.profiler.startSection("AWNpcTick");
		updateArmSwingProgress();
		if (ticksExisted % 200 == 0 && getHealth() < getMaxHealth() && isEntityAlive() && (!requiresUpkeep() || getFoodRemaining() > 0)) {
			setHealth(getHealth() + 1);
		}
		super.onUpdate();
		if (!getHeldItemMainhand().isEmpty()) {
			getHeldItemMainhand().updateAnimation(world, this, 0, true);
		}
		world.profiler.endSection();
	}

	@Override
	public boolean canAttackClass(Class claz) {
		return !EntityFlying.class.isAssignableFrom(claz);
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	final void updateDamageFromLevel() {
		getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(getLeveledAttack());
	}

	private double getLeveledAttack() {
		double dmg = getBaseAttack();
		int level = getLevelingStats().getLevel();
		return dmg * (1 + level * npcLevelDamageMultiplier);
	}

	private double getBaseAttack() {
		//TODO get rid of this instanceof check once player owned attribs migrated ot the same way
		return this instanceof NpcFaction ? NpcDefaultsRegistry.getFactionNpcDefault((NpcFaction) this).getBaseAttack() :
				NpcDefaultsRegistry.getOwnedNpcDefault((NpcPlayerOwned) this).getBaseAttack();
	}

	public int getFoodRemaining() {
		return 0;//NOOP in non-player owned
	}

	public void setFoodRemaining(int food) {
		//NOOP in non-player owned
	}

	public boolean requiresUpkeep() {
		return this instanceof IKeepFood;//NOOP in non-player owned
	}

	@Override
	public void setOwner(EntityPlayer player) {
		owner = new Owner(player);
	}

	@Override
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public void setOwnerName(String name) {
		owner = new Owner(world, name);
		if (!world.isRemote && !name.equals(owner.getName())) {
			PacketEntity pkt = new PacketEntity(this);
			pkt.packetData = owner.serializeToNBT(new NBTTagCompound());
			NetworkHandler.sendToAllTracking(this, pkt);
		}
	}

	@Override
	public boolean isOwner(EntityPlayer player) {
		return owner.isOwnerOrSameTeamOrFriend(player);
	}

	@Override
	public Owner getOwner() {
		return owner;
	}

	@Override
	public Team getTeam() {
		return world.getScoreboard().getPlayersTeam(owner.getName());
	}

	public boolean hasCommandPermissions(Owner owner) {
		return hasCommandPermissions(owner.getUUID(), owner.getName());
	}

	public boolean hasCommandPermissions(UUID playerId, String playerName) {
		return owner.playerHasCommandPermissions(world, playerId, playerName);
	}

	@Override
	protected int getExperiencePoints(@Nullable EntityPlayer attacker) {
		if (attacker == null || (isHostileTowards(attacker) && canBeAttackedBy(attacker))) {
			return super.getExperiencePoints(attacker);
		}
		return 0;
	}

	public abstract boolean isHostileTowards(Entity e);

	public abstract boolean canTarget(Entity e);

	public abstract boolean canBeAttackedBy(Entity e);

	public final EntityLivingBase getFollowingEntity() {
		if (followingPlayerName == null) {
			return null;
		}
		return world.getPlayerEntityByName(followingPlayerName);
	}

	public final void setFollowingEntity(EntityLivingBase entity) {
		if (entity instanceof EntityPlayer && hasCommandPermissions(entity.getUniqueID(), entity.getName())) {
			followingPlayerName = entity.getName();
		}
	}

	public final void clearFollowingEntity() {
		followingPlayerName = null;
	}

	public void setDoNotPursue(boolean val) {
		doNotPursue = val;
	}

	public boolean getDoNotPursue() {
		return doNotPursue;
	}

	@Override
	public boolean canBeLeashedTo(EntityPlayer player) {
		return false;
	}

	public final void repackEntity(EntityPlayer player) {
		// we already hide the button but we need to prevent repack server-side too
		if (AWNPCStatics.repackCreativeOnly && !player.capabilities.isCreativeMode) {
			return;
		}
		if (!player.world.isRemote && isEntityAlive()) {
			onRepack();
			//noinspection ConstantConditions
			@Nonnull
			ItemStack item = InventoryTools.mergeItemStack(player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), getItemToSpawn());
			if (!item.isEmpty()) {
				InventoryHelper.spawnItemStack(player.world, player.posX, player.posY, player.posZ, item);
			}
		}
		setDead();
	}

	/*
	 * called when NPC is being repacked into item-form.  Called prior to item being created and prior to entity being set-dead.<br>
	 * Main function is for faction-mounted NPCs to disappear their mounts when repacked.
	 */
	protected void onRepack() {

	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag) {
		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
			setItemStackToSlot(slot, ItemStack.EMPTY);
		}
		super.readEntityFromNBT(tag);
		if (tag.hasKey("home")) {
			setHomePosAndDistance(BlockPos.fromLong(tag.getLong("home")), tag.getInteger("homeRange"));
		}
		if (tag.hasKey(BED_DIRECTION_TAG)) {
			setBedDirection(EnumFacing.VALUES[tag.getByte(BED_DIRECTION_TAG)]);
		}
		if (tag.hasKey(IS_SLEEPING_TAG)) {
			setSleeping(tag.getBoolean(IS_SLEEPING_TAG));
		}
		if (tag.hasKey(CACHED_BED_POS_TAG)) {
			cachedBedPos = BlockPos.fromLong(tag.getLong(CACHED_BED_POS_TAG));
		}
		if (tag.hasKey(BED_POS_TAG)) {
			setBedPosition(BlockPos.fromLong(tag.getLong(BED_POS_TAG)));
		}
		if (tag.hasKey(FOUND_BED_TAG)) {
			foundBed = tag.getBoolean(FOUND_BED_TAG);
		}

		originalWidth = tag.getFloat("originalWidth");
		originalHeight = tag.getFloat("originalHeight");

		readBaseTags(tag);
		onWeaponInventoryChanged();
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound tag) {
		super.writeEntityToNBT(tag);
		if (!hasHome()) {
			Optional<BlockPos> position = getTownHallPosition();
			if (position.isPresent()) {
				setHomePosAndDistance(position.get(), getHomeRange());
			} else {
				setHomeAreaAtCurrentPosition();
			}
		}
		tag.setLong("home", getHomePosition().toLong());
		tag.setInteger("homeRange", getHomeRange());
		tag.setByte(BED_DIRECTION_TAG, (byte) getBedDirection().ordinal());
		tag.setBoolean(IS_SLEEPING_TAG, isSleeping());
		if (cachedBedPos != null) {
			tag.setLong(CACHED_BED_POS_TAG, cachedBedPos.toLong());
		}
		BlockPos bedPos = getBedPosition();
		tag.setLong(BED_POS_TAG, bedPos.toLong());
		tag.setBoolean(FOUND_BED_TAG, foundBed);
		tag.setFloat("originalWidth", originalWidth);
		tag.setFloat("originalHeight", originalHeight);

		writeBaseTags(tag);
	}

	private void readBaseTags(NBTTagCompound tag) {
		if (tag.hasKey(ORDERS_STACK_TAG)) {
			setItemStackToSlot(ORDER_SLOT, new ItemStack(tag.getCompoundTag(ORDERS_STACK_TAG)));
		}
		if (tag.hasKey(UPKEEP_STACK_TAG)) {
			setItemStackToSlot(UPKEEP_SLOT, new ItemStack(tag.getCompoundTag(UPKEEP_STACK_TAG)));
		}
		if (tag.hasKey(LEVELING_STATS_TAG)) {
			getLevelingStats().readFromNBT(tag.getCompoundTag(LEVELING_STATS_TAG));
		}
		if (tag.hasKey(MAX_HEALTH_TAG)) {
			getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(tag.getFloat(MAX_HEALTH_TAG));
		}
		if (tag.hasKey(HEALTH_TAG)) {
			setHealth(tag.getFloat(HEALTH_TAG));
		}
		if (tag.hasKey("name")) {
			setCustomNameTag(tag.getString("name"));
		}
		if (tag.hasKey("food")) {
			setFoodRemaining(tag.getInteger("food"));
		}
		if (tag.hasKey(ATTACK_DAMAGE_OVERRIDE_TAG)) {
			setAttackDamageOverride(tag.getInteger(ATTACK_DAMAGE_OVERRIDE_TAG));
		}
		if (tag.hasKey(ARMOR_VALUE_OVERRIDE_TAG)) {
			setArmorValueOverride(tag.getInteger(ARMOR_VALUE_OVERRIDE_TAG));
		}
		if (tag.hasKey(AI_ENABLED_TAG)) {
			setIsAIEnabled(tag.getBoolean(AI_ENABLED_TAG));
		}
		if (tag.hasKey(DO_NOT_PURSUE)) {
			setDoNotPursue(tag.getBoolean(DO_NOT_PURSUE));
		}
		if (tag.hasKey(HAS_CUSTOM_EQUIPMENT_TAG)) {
			setCustomEquipmentOverride(tag.getBoolean(HAS_CUSTOM_EQUIPMENT_TAG));
		}
		setSkinSettings(NpcSkinSettings.deserializeNBT(tag.getCompoundTag("skinSettings")).minimizeData());
		owner = Owner.deserializeFromNBT(tag);
	}

	public static void registerFixesNpc(DataFixer fixer, Class<?> entityClass) {
		EntityLiving.registerFixesMob(fixer, entityClass);
		fixer.registerWalker(FixTypes.ENTITY, new ItemStackData(entityClass, ORDERS_STACK_TAG, UPKEEP_STACK_TAG));
	}

	private void writeBaseTags(NBTTagCompound tag) {
		if (!ordersStack.isEmpty()) {
			tag.setTag(ORDERS_STACK_TAG, ordersStack.writeToNBT(new NBTTagCompound()));
		}
		if (!upkeepStack.isEmpty()) {
			tag.setTag(UPKEEP_STACK_TAG, upkeepStack.writeToNBT(new NBTTagCompound()));
		}
		tag.setTag(LEVELING_STATS_TAG, getLevelingStats().writeToNBT(new NBTTagCompound()));
		tag.setFloat(MAX_HEALTH_TAG, getMaxHealth());
		tag.setFloat(HEALTH_TAG, getHealth());
		if (hasCustomName()) {
			tag.setString("name", getCustomNameTag());
		}
		tag.setInteger("food", getFoodRemaining());
		tag.setInteger(ATTACK_DAMAGE_OVERRIDE_TAG, attackDamage);
		tag.setInteger(ARMOR_VALUE_OVERRIDE_TAG, armorValue);
		tag.setBoolean(AI_ENABLED_TAG, aiEnabled);
		tag.setBoolean(DO_NOT_PURSUE, doNotPursue);
		tag.setBoolean(HAS_CUSTOM_EQUIPMENT_TAG, hasCustomEquipment);
		tag.setTag("skinSettings", skinSettings.serializeNBT());
		owner.serializeToNBT(tag);
	}

	public final ResourceLocation getTexture() {
		return skinSettings.getTexture(this);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("ownerName")) {
			owner = Owner.deserializeFromNBT(tag);
		} else if (tag.hasKey(NpcSkinSettings.PACKET_TAG_NAME)) {
			skinSettings.handlePacketData(tag);
		} else if (tag.hasKey("attackTarget")) {
			int entityId = tag.getInteger("attackTarget");
			setAttackTarget((EntityLivingBase) world.getEntityByID(entityId));
		}
	}

	@Override
	public double getDistanceSq(BlockPos pos) {
		return getDistanceSq(pos.getX() + 0.5d, pos.getY(), pos.getZ() + 0.5d);
	}

	public BlockPos findBed() {
		if (!foundBed) {
			int originX = MathHelper.floor(posX);
			int originY = MathHelper.floor(posY);
			int originZ = MathHelper.floor(posZ);
			int maxSearchRange = 6;
			int minX = originX - maxSearchRange;
			int maxX = originX + maxSearchRange;
			int minY = originY - maxSearchRange;
			int maxY = originY + maxSearchRange;
			int minZ = originZ - maxSearchRange;
			int maxZ = originZ + maxSearchRange;
			List<BlockPos> foundBeds = new ArrayList<>();
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					for (int z = minZ; z <= maxZ; z++) {
						IBlockState state = world.getBlockState(new BlockPos(x, y, z));
						if (state.getBlock() instanceof BlockBed) {
							if (state.getValue(BlockBed.PART) != BlockBed.EnumPartType.FOOT) {
								continue;
							}
							if (!state.getValue(BlockBed.OCCUPIED)) { // occupied check
								foundBeds.add(new BlockPos(x, y, z));
							}
						}
					}
				}
			}

			int closetBedIndex = -1;
			for (int i = 0; i < foundBeds.size(); i++) {
				double bedDistance = foundBeds.get(i).distanceSqToCenter(originX, originY, originZ);
				if ((closetBedIndex == -1) || (bedDistance < foundBeds.get(closetBedIndex).distanceSqToCenter(originX, originY, originZ))) {
					closetBedIndex = i;
				}
			}
			if (closetBedIndex == -1) {
				foundBed = false;
				return null;
			}
			foundBed = true;
			cachedBedPos = foundBeds.get(closetBedIndex);
		}

		IBlockState state = world.getBlockState(cachedBedPos);
		if (state.getBlock() instanceof BlockBed) {
			return cachedBedPos;
		} else {
			foundBed = false;
			return null; // try again in a while
		}
	}

	public boolean lieDown(BlockPos pos) {
		if (!foundBed) {
			return false;
		}
		if (world.isBlockLoaded(pos) && world.getBlockState(pos).getBlock() instanceof BlockBed) {
			IBlockState state = world.getBlockState(pos);
			if (state.getValue(BlockBed.OCCUPIED)) {
				// occupied check
				foundBed = false;
				return false;
			}
			state = state.withProperty(BlockBed.OCCUPIED, true);
			world.setBlockState(pos, state);
			setBedPosition(pos);
			setBedDirection(state.getValue(BlockBed.FACING));
			setSleeping(true);
			originalHeight = height;
			originalWidth = width;
			setSize(0.2F, 0.2F);
			setPositionToBed();
			return true;
		}
		return false;
	}

	public void wakeUp() {
		setSleeping(false);
		BlockPos bedPos = getBedPosition();
		// set vacant
		IBlockState bedState = world.getBlockState(bedPos);
		if (bedState.getBlock() == Blocks.BED) {
			world.setBlockState(bedPos, bedState.withProperty(BlockBed.OCCUPIED, false), 4);
		}

		// Try placing the NPC to an empty spot next to the bed. We don't want them standing on top of the bed, chance for suffocation
		EnumFacing bedDirection = getBedDirection();

		setSize(originalWidth, originalHeight);

		if (tryMovingToBedside(bedPos.offset(bedDirection.rotateY()))) {
			return;
		}
		if (tryMovingToBedside(bedPos.offset(bedDirection.rotateYCCW()))) {
			return;
		}

		BlockPos offsetPos = bedPos.offset(bedDirection.getOpposite());
		if (tryMovingToBedside(offsetPos)) {
			return;
		}
		if (tryMovingToBedside(offsetPos.offset(bedDirection.rotateY()))) {
			return;
		}
		tryMovingToBedside(offsetPos.offset(bedDirection.rotateYCCW()));
	}

	private boolean tryMovingToBedside(BlockPos posToMove) {
		if (world.getBlockState(posToMove).getMaterial().blocksMovement()) {
			return false;
		}
		if (world.getBlockState(posToMove.up()).getMaterial().blocksMovement()) {
			return false;
		}
		if (!world.getBlockState(posToMove.down()).getMaterial().blocksMovement()) {
			return false;
		}
		aiEnabled = false;
		setPosition(posToMove.getX() + 0.5, posToMove.getY() + 0.5, posToMove.getZ() + 0.5);
		aiEnabled = true;
		return true;
	}

	private BlockPos getBedPosition() {
		return dataManager.get(BED_POS);
	}

	private void setBedPosition(BlockPos pos) {
		dataManager.set(BED_POS, pos);
	}

	public void setPositionToBed() {
		float xOffset = 0.5F;
		float yOffset = 0.6F;
		float zOffset = 0.5F;

		setPosition(cachedBedPos.getX() + xOffset, cachedBedPos.getY() + yOffset, cachedBedPos.getZ() + zOffset);
	}

	@Override
	public void travel(float strafe, float vertical, float forward) {
		if (isSleeping()) {
			isJumping = false;
			moveStrafing = 0.0F;
			moveForward = 0.0F;
			randomYawVelocity = 0.0F;
			super.travel(0, 0, 0);
		} else {
			super.travel(strafe, vertical, forward);
		}
	}

	public boolean isBedCacheValid() {
		return (world.getBlockState(cachedBedPos).getBlock() instanceof BlockBed);
	}

	@Override
	public void onCollideWithPlayer(EntityPlayer player) {
		if (!isSleeping()) {
			super.onCollideWithPlayer(player);
		}
	}

	@Override
	public boolean canBePushed() {
		return (!isSleeping());
	}

	@Override
	protected void collideWithEntity(Entity entity) {
		if (!isSleeping()) {
			super.collideWithEntity(entity);
		}
	}

	private void setSleeping(boolean isSleeping) {
		dataManager.set(IS_SLEEPING, isSleeping);
	}

	public boolean isSleeping() {
		return dataManager == null ? false : dataManager.get(IS_SLEEPING);
	}

	private void setBedDirection(EnumFacing direction) {
		dataManager.set(BED_DIRECTION, (byte) direction.ordinal());
	}

	public EnumFacing getBedDirection() {
		return EnumFacing.VALUES[dataManager.get(BED_DIRECTION)];
	}

	public boolean shouldSleep() {
		return !WorldTools.isDaytimeInDimension(world);
	}

	// Only used by the renderer
	@SideOnly(Side.CLIENT)
	public float getBedOrientationInDegrees() {
		BlockPos bedLocation = getBedPosition();
		IBlockState state = bedLocation == null ? null : world.getBlockState(bedLocation);
		if (state != null && state.getBlock().isBed(state, world, bedLocation, this)) {
			EnumFacing enumfacing = state.getBlock().getBedDirection(state, world, bedLocation);

			switch (enumfacing) {
				case SOUTH:
					return 90.0F;
				case WEST:
					return 0.0F;
				case NORTH:
					return 270.0F;
				case EAST:
					return 180.0F;
			}
		}

		return 0.0F;
	}

	@SideOnly(Side.CLIENT)
	public boolean isSwingingArms() {
		return dataManager.get(SWINGING_ARMS);
	}

	public void setSwingingArms(boolean swingingArms) {
		dataManager.set(SWINGING_ARMS, swingingArms);
	}

	@SideOnly(Side.CLIENT)
	public boolean isBlocking() {
		return dataManager.get(BLOCKING);
	}

	public void setBlocking(boolean blocking) {
		dataManager.set(BLOCKING, blocking);
	}
	//TODO refactor vehicle stuff out - perhaps capability??

	@Nullable
	private VehicleBase getRidingVehicle() {
		if (getRidingEntity() instanceof VehicleBase) {
			return (VehicleBase) getRidingEntity();
		}
		return null;
	}

	@Override
	public void setPath(List<Node> path) {
		VehicleBase vehicle = getRidingVehicle();
		if (vehicle != null) {
			vehicle.nav.forcePath(path);
		} else {
			//TODO implement forcePath for NPC
			//nav.forcePath(path);
		}
	}

	@Override
	public void setMoveTo(double x, double y, double z, float moveSpeed) {
		getMoveHelper().setMoveTo(x, y, z, moveSpeed);
	}

	@Override
	public float getDefaultMoveSpeed() {
		return 1f;
	}

	@Override
	public boolean isPathableEntityOnLadder() {
		return isOnLadder();
	}

	@Override
	public Entity getEntity() {
		return this;
	}

	@Override
	public void onStuckDetected() {
		//noop
	}

	public boolean isFemale() {
		return false;
	}

	public NpcSkinSettings getSkinSettings() {
		return skinSettings;
	}

	public void setSkinSettings(NpcSkinSettings skinSettings) {
		this.skinSettings = skinSettings;
		this.skinSettings.onNpcSet(this);
	}
}
