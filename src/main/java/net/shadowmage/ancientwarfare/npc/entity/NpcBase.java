package net.shadowmage.ancientwarfare.npc.entity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.item.ItemShield;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand.Command;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinManager;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.util.UUID;

public abstract class NpcBase extends EntityCreature implements IEntityAdditionalSpawnData, IOwnable, IEntityPacketHandler {

    private String ownerName = "";//the owner of this NPC, used for checking teams

    protected String followingPlayerName;//set/cleared onInteract from player if player.team==this.team

    protected NpcLevelingStats levelingStats;

    /**
     * a single base texture for ALL npcs to share, used in case other textures were not set
     */
    private final ResourceLocation baseDefaultTexture;

    private ResourceLocation currentTexture = null;

    public ItemStack ordersStack;

    public ItemStack upkeepStack;

    private boolean aiEnabled = true;

    private int attackDamage = -1;//faction based only
    private int armorValue = -1;//faction based only
    private int maxHealthOverride = -1;
    private String customTexRef = "";//might as well allow for player-owned as well...

    public NpcBase(World par1World) {
        super(par1World);
        baseDefaultTexture = new ResourceLocation("ancientwarfare:textures/entity/npc/npc_default.png");
        levelingStats = new NpcLevelingStats(this);
        this.equipmentDropChances = new float[]{1.f, 1.f, 1.f, 1.f, 1.f};
        this.width = 0.6f;
        this.func_110163_bv();//set persistence required==true
        AncientWarfareNPC.statics.applyPathConfig(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataWatcher().addObject(20, Integer.valueOf(0));//ai tasks
        this.getDataWatcher().addObjectByDataType(21, 5);//5 for ItemStack
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        AncientWarfareNPC.statics.applyAttributes(this);
    }

    public ItemStack getShieldStack() {
        return this.getDataWatcher().getWatchableObjectItemStack(21);
    }

    public void setShieldStack(ItemStack stack) {
        this.getDataWatcher().updateObject(21, stack);
    }

    public int getMaxHealthOverride() {
        return maxHealthOverride;
    }

    public void setMaxHealthOverride(int maxHealthOverride) {
        this.maxHealthOverride = maxHealthOverride;
        if (maxHealthOverride > 0) {
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(maxHealthOverride);
            if (getHealth() < getMaxHealth()) {
                setHealth(getMaxHealth());
            }
        }
    }

    public void setCustomTexRef(String customTexRef) {
        if (customTexRef == null) {
            customTexRef = "";
        }
        if (!worldObj.isRemote) {
            if (!customTexRef.equals(this.customTexRef)) {
                PacketEntity pkt = new PacketEntity(this);
                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("customTex", customTexRef);
                pkt.packetData = tag;
                NetworkHandler.sendToAllTracking(this, pkt);
            }
            this.customTexRef = customTexRef;
        } else {
            this.customTexRef = customTexRef;
            this.updateTexture();
        }
    }

    public void setAttackDamageOverride(int attackDamage) {
        this.attackDamage = attackDamage;
    }

    public void setArmorValueOverride(int armorValue) {
        this.armorValue = armorValue;
    }

    public String getCustomTex() {
        return customTexRef;
    }

    public int getArmorValueOverride() {
        return armorValue;
    }

    public int getAttackDamageOverride() {
        return attackDamage;
    }

    @Override
    public boolean attackEntityAsMob(Entity target) {
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        if (getAttackDamageOverride() >= 0) {
            damage = (float) this.getAttackDamageOverride();
        } else if (getShieldStack() != null && getHeldItem() != null) {
            if (getShieldStack().getAttributeModifiers().containsKey(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName())) {
                damage *= 1.5f;
            }
        }
        int knockback = 0;
        if (target instanceof EntityLivingBase) {
            damage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase) target);
            knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase) target);
        }
        boolean targetHit = target.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
        if (targetHit) {
            if (knockback > 0) {
                target.addVelocity((double) (-MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F) * (float) knockback * 0.5F), 0.1D, (double) (MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F) * (float) knockback * 0.5F));
                this.motionX *= 0.6D;
                this.motionZ *= 0.6D;
            }
            int fireDamage = EnchantmentHelper.getFireAspectModifier(this);

            if (fireDamage > 0) {
                target.setFire(fireDamage * 4);
            }
            if (target instanceof EntityLivingBase) {
                EnchantmentHelper.func_151384_a((EntityLivingBase) target, this);
            }
            EnchantmentHelper.func_151385_b(this, target);
        }
        return targetHit;
    }

    @Override
    public int getTotalArmorValue() {
        if (getArmorValueOverride() >= 0) {
            return getArmorValueOverride();
        }
        int value = super.getTotalArmorValue();
        if (getShieldStack() != null && getShieldStack().getItem() instanceof ItemShield) {
            ItemShield shield = (ItemShield) getShieldStack().getItem();
            value += shield.getArmorBonusValue();
        }
        return value;
    }

    @Override
    public final double getYOffset() {
        return (double) (this.yOffset - 0.5F);//fixes mounted offset for horses, probably minecarts
    }

    @Override
    public PathNavigate getNavigator() {
        if (this.ridingEntity instanceof EntityLiving) {
            return ((EntityLiving) this.ridingEntity).getNavigator();
        }
        return super.getNavigator();
    }

    @Override
    public void onEntityUpdate() {
        /**
         * this is pushOutOfBlocks ...
         * need to test how well it works for an npc (perhaps drop sand on their head?)
         */
        if (!worldObj.isRemote) {
            this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        }
        super.onEntityUpdate();
    }

    public double getDistanceSqFromHome() {
        if (!hasHome()) {
            return 0;
        }
        ChunkCoordinates home = getHomePosition();
        return getDistanceSq(home.posX + 0.5d, home.posY, home.posZ + 0.5d);
    }

    public void setTownHallPosition(BlockPosition pos) {
        //NOOP on non-player owned npc
    }

    public BlockPosition getTownHallPosition() {
        return null;//NOOP on non-player owned npc
    }

    public TileTownHall getTownHall() {
        return null;//NOOP on non-player owned npc
    }

    public void handleTownHallBroadcast(TileTownHall tile, BlockPosition position) {
        //NOOP on non-player owned npc
    }

    /**
     * Return true if this NPC should be within his home range.<br>
     * Should still allow for a combat NPC to attack targets outside his home range.
     */
    public boolean shouldBeAtHome() {
        if (getAttackTarget() != null) {
            return false;
        }
        if (!hasHome()) {
            return false;
        }
        return (!worldObj.provider.hasNoSky && !worldObj.provider.isDaytime()) || worldObj.isRaining();//if is at night (and not an underground world type), or if it is raining, return true
    }

    public void setIsAIEnabled(boolean val) {
        this.aiEnabled = val;
    }

    public boolean getIsAIEnabled() {
        return aiEnabled && !AWNPCStatics.npcAIDebugMode;
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        if (player.worldObj.isRemote) {
            return false;
        }
        tryCommand(player);
        return true;
    }

    /**
     * should be implemented by any npc that wishes to open a GUI on interact<br>
     * must be called from interact code to actually open the GUI<br>
     * allows for subtypes/etc to vary the opened GUI without re-implementing the interact logic
     */
    public void openGUI(EntityPlayer player) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, getEntityId(), 0, 0);
    }

    /**
     * if this npc has an alt-control GUI, open it here.<br>
     * should called from the npc inventory gui.
     */
    public void openAltGui(EntityPlayer player) {

    }

    /**
     * used by the npc inventory gui to determine if it should display the 'alt control gui' button<br>
     * this setting must return true -on the client- if the button is to be displayed.
     */
    public boolean hasAltGui() {
        return false;
    }

    /**
     * Used by command baton and town-hall to determine if this NPC is commandable by a player / team
     */
    public boolean canBeCommandedBy(String playerName) {
        if (ownerName.isEmpty()) {
            return false;
        }
        if (playerName == null) {
            return false;
        }
        Team team = getTeam();
        if (team == null) {
            return playerName.equals(ownerName);
        } else {
            return team == worldObj.getScoreboard().getPlayersTeam(playerName);
        }
    }

    protected void tryCommand(EntityPlayer player){
        boolean baton = player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
        if(!baton) {
            if (player.isSneaking()) {
                if (this.followingPlayerName != null && this.followingPlayerName.equals(player.getCommandSenderName())) {
                    this.followingPlayerName = null;
                } else {
                    this.followingPlayerName = player.getCommandSenderName();
                }
            } else {
                openGUI(player);
            }
        }
    }

    @Override
    public final boolean attackEntityFrom(DamageSource source, float par2) {
        if (source.getEntity() != null && !canBeAttackedBy(source.getEntity())) {
            return false;
        }
        return super.attackEntityFrom(source, par2);
    }

    @Override
    public void setAttackTarget(EntityLivingBase entity) {
        if (entity != null && !canTarget(entity)) {
            return;
        }
        super.setAttackTarget(entity);
    }

    @Override
    public final void setRevengeTarget(EntityLivingBase entity) {
        if (entity != null && !canTarget(entity)) {
            return;
        }
        super.setRevengeTarget(entity);
    }

    @Override
    protected final void dropEquipment(boolean par1, int par2) {
        if (!worldObj.isRemote) {
            ItemStack stack;
            for (int i = 0; i < 5; i++) {
                stack = getEquipmentInSlot(i);
                if (stack != null) {
                    entityDropItem(stack, 0.f);
                }
                setCurrentItemOrArmor(i, null);
            }
            if (ordersStack != null) {
                entityDropItem(ordersStack, 0.f);
            }
            if (upkeepStack != null) {
                entityDropItem(upkeepStack, 0.f);
            }
            if (getShieldStack() != null) {
                entityDropItem(getShieldStack(), 0.f);
            }
            ordersStack = null;
            upkeepStack = null;
            setShieldStack(null);
        }
    }

    @Override
    public final void onKillEntity(EntityLivingBase par1EntityLivingBase) {
        super.onKillEntity(par1EntityLivingBase);
        if (!worldObj.isRemote) {
            addExperience(AWNPCStatics.npcXpFromKill);
            if (par1EntityLivingBase == this.getAttackTarget()) {
                this.setAttackTarget(null);
            }
        }
    }

    /**
     * Returns the currently following player-issues command, or null if none
     */
    public Command getCurrentCommand() {
        return null;//NOOP on non-player owned npc
    }

    /**
     * input path from command baton - default implementation for player-owned NPC is to set current command==input command and then let AI do the rest
     */
    public void handlePlayerCommand(Command cmd) {
//NOOP on non-player owned npc
    }

    public void setPlayerCommand(Command cmd) {

    }

    /**
     * return the bitfield containing all of the currently executing AI tasks<br>
     * used by player-owned npcs for rendering ai-tasks
     */
    public final int getAITasks() {
        return getDataWatcher().getWatchableObjectInt(20);
    }

    /**
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

    /**
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

    /**
     * set ai tasks -- only used internally
     */
    private final void setAITasks(int tasks) {
        this.getDataWatcher().updateObject(20, Integer.valueOf(tasks));
    }

    /**
     * add an amount of experience to this npcs leveling stats<br>
     * experience is added for base level, and subtype level(if any)
     */
    public final void addExperience(int amount) {
        getLevelingStats().addExperience(amount);
    }

    /**
     * implementations should read in any data written during {@link #writeAdditionalItemData(NBTTagCompound)}
     */
    public final void readAdditionalItemData(NBTTagCompound tag) {
        NBTTagList equipmentList = tag.getTagList("equipment", Constants.NBT.TAG_COMPOUND);
        ItemStack stack;
        NBTTagCompound equipmentTag;
        for (int i = 0; i < equipmentList.tagCount(); i++) {
            equipmentTag = equipmentList.getCompoundTagAt(i);
            stack = InventoryTools.readItemStack(equipmentTag);
            if (equipmentTag.hasKey("slotNum")) {
                setCurrentItemOrArmor(equipmentTag.getInteger("slotNum"), stack);
            }
        }
        if (tag.hasKey("ordersStack")) {
            ordersStack = InventoryTools.readItemStack(tag.getCompoundTag("ordersStack"));
        }
        if (tag.hasKey("upkeepStack")) {
            upkeepStack = InventoryTools.readItemStack(tag.getCompoundTag("upkeepStack"));
        }
        if (tag.hasKey("shieldStack")) {
            setShieldStack(InventoryTools.readItemStack(tag.getCompoundTag("shieldStack")));
        }
        if (tag.hasKey("levelingStats")) {
            getLevelingStats().readFromNBT(tag.getCompoundTag("levelingStats"));
        }
        if (tag.hasKey("maxHealth")) {
            getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(tag.getFloat("maxHealth"));
        }
        if (tag.hasKey("health")) {
            setHealth(tag.getFloat("health"));
        }
        if (tag.hasKey("name")) {
            setCustomNameTag(tag.getString("name"));
        }
        if (tag.hasKey("food")) {
            setFoodRemaining(tag.getInteger("food"));
        }
        if (tag.hasKey("attackDamageOverride")) {
            setAttackDamageOverride(tag.getInteger("attackDamageOverride"));
        }
        if (tag.hasKey("armorValueOverride")) {
            setArmorValueOverride(tag.getInteger("armorValueOverride"));
        }
        if (tag.hasKey("customTex")) {
            setCustomTexRef(tag.getString("customTex"));
        }
        if (tag.hasKey("aiEnabled")) {
            aiEnabled = tag.getBoolean("aiEnabled");
        }
        ownerName = tag.getString("owner");
        onOrdersInventoryChanged();
        onWeaponInventoryChanged();
    }

    /**
     * Implementations should write out any persistent entity-data needed to restore entity-state from an item-stack.<br>
     * This should include inventory, levels, orders, faction / etc
     */
    public final NBTTagCompound writeAdditionalItemData(NBTTagCompound tag) {
        NBTTagList equipmentList = new NBTTagList();
        ItemStack stack;
        NBTTagCompound equipmentTag;
        for (int i = 0; i < 5; i++) {
            stack = getEquipmentInSlot(i);
            if (stack == null) {
                continue;
            }
            equipmentTag = InventoryTools.writeItemStack(stack, new NBTTagCompound());
            equipmentTag.setInteger("slotNum", i);
            equipmentList.appendTag(equipmentTag);
        }
        tag.setTag("equipment", equipmentList);
        if (ordersStack != null) {
            tag.setTag("ordersStack", InventoryTools.writeItemStack(ordersStack, new NBTTagCompound()));
        }
        if (upkeepStack != null) {
            tag.setTag("upkeepStack", InventoryTools.writeItemStack(upkeepStack, new NBTTagCompound()));
        }
        if (getShieldStack() != null) {
            tag.setTag("shieldStack", InventoryTools.writeItemStack(getShieldStack(), new NBTTagCompound()));
        }
        tag.setTag("levelingStats", getLevelingStats().writeToNBT(new NBTTagCompound()));
        tag.setFloat("maxHealth", getMaxHealth());
        tag.setFloat("health", getHealth());
        tag.setInteger("food", getFoodRemaining());
        if (hasCustomNameTag()) {
            tag.setString("name", getCustomNameTag());
        }
        tag.setString("owner", ownerName);
        tag.setInteger("attackDamageOverride", attackDamage);
        tag.setInteger("armorValueOverride", armorValue);
        tag.setString("customTex", customTexRef);
        tag.setBoolean("aiEnabled", aiEnabled);
        return tag;
    }

    /**
     * is the input stack a valid orders-item for this npc?<br>
     * used by player-owned NPCs
     * TODO noop this in base, re-abstract in npc-player owned class
     */
    public abstract boolean isValidOrdersStack(ItemStack stack);

    /**
     * callback for when orders-stack changes.  implementations should inform any necessary AI tasks of the
     * change to order-items
     */
    public abstract void onOrdersInventoryChanged();

    /**
     * callback for when weapon slot has been changed.<br>
     * Implementations should re-set any subtype or inform any AI that need to know when
     * weapon was changed.
     */
    public abstract void onWeaponInventoryChanged();

    /**
     * return the NPCs subtype.<br>
     * this subtype may vary at runtime.
     */
    public abstract String getNpcSubType();

    /**
     * return the NPCs type.  This type should be unique for the class of entity,
     * or at least unique pertaining to the entity registration.
     */
    public abstract String getNpcType();

    /**
     * return the full NPC type for this npc<br>
     * returns npcType if subtype is empty, else npcType.npcSubtype
     */
    public final String getNpcFullType() {
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
    public String getCommandSenderName(){
        String name = StatCollector.translateToLocal("entity.AncientWarfareNpc." + getNpcFullType() + ".name");
        if (hasCustomNameTag()) {
            name = name + " : " + getCustomNameTag();
        }
        return name;
    }

    public final NpcLevelingStats getLevelingStats() {
        return levelingStats;
    }

    public final ResourceLocation getDefaultTexture() {
        return baseDefaultTexture;
    }

    public final ItemStack getItemToSpawn() {
        return ItemNpcSpawner.getSpawnerItemForNpc(this);
    }

    public final long getIDForSkin() {
        return this.entityUniqueID.getLeastSignificantBits();
    }

    @Override
    public final ItemStack getPickedResult(MovingObjectPosition target) {
        EntityPlayer player = AncientWarfareCore.proxy.getClientPlayer();
        if (player != null) {
            PacketEntity pkt = new PacketEntity(this);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("playerID", player.getEntityId());
            pkt.packetData.setTag("pickEntity", tag);
            NetworkHandler.sendToServer(pkt);
        }
        return null;
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeLong(getUniqueID().getMostSignificantBits());
        buffer.writeLong(getUniqueID().getLeastSignificantBits());
        ByteBufUtils.writeUTF8String(buffer, ownerName);
        ByteBufUtils.writeUTF8String(buffer, customTexRef);
    }

    @Override
    public void readSpawnData(ByteBuf buffer) {
        long l1, l2;
        l1 = buffer.readLong();
        l2 = buffer.readLong();
        this.entityUniqueID = new UUID(l1, l2);
        ownerName = ByteBufUtils.readUTF8String(buffer);
        customTexRef = ByteBufUtils.readUTF8String(buffer);
        this.updateTexture();
    }

    @Override
    public void onUpdate() {
        worldObj.theProfiler.startSection("AWNpcTick");
        updateArmSwingProgress();
        if (ticksExisted % 200 == 0 && getHealth() < getMaxHealth() && getHealth() > 0 && !isDead && (!requiresUpkeep() || getFoodRemaining() > 0)) {
            setHealth(getHealth() + 1);
        }
        super.onUpdate();
        worldObj.theProfiler.endSection();
    }

    @Override
    protected final boolean canDespawn() {
        return false;
    }

    @Override
    protected final boolean isAIEnabled() {
        return true;
    }

    /**
     * called whenever level changes, to update the damage-done stat for the entity
     */
    public final void updateDamageFromLevel() {
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(AncientWarfareNPC.statics.getAttack(this));
    }

    public int getFoodRemaining() {
        return 0;//NOOP in non-player owned
    }

    public void setFoodRemaining(int food) {
        //NOOP in non-player owned
    }

    public int getUpkeepBlockSide() {
        return 0;//NOOP in non-player owned
    }

    public BlockPosition getUpkeepPoint() {
        return null;//NOOP in non-player owned
    }

    public void setUpkeepAutoPosition(BlockPosition pos) {

    }

    public int getUpkeepAmount() {
        return 0;//NOOP in non-player owned
    }

    public int getUpkeepDimensionId() {
        return 0;//NOOP in non-player owned
    }

    public boolean requiresUpkeep() {
        return false;//NOOP in non-player owned
    }

    @Override
    public void setOwnerName(String name) {
        if (name == null) {
            name = "";
        }
        if (!worldObj.isRemote && !name.equals(ownerName)) {
            PacketEntity pkt = new PacketEntity(this);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("ownerName", name);
            pkt.packetData = tag;
            NetworkHandler.sendToAllTracking(this, pkt);
        }
        ownerName = name;
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public Team getTeam() {
        return worldObj.getScoreboard().getPlayersTeam(ownerName);
    }

    public abstract boolean isHostileTowards(Entity e);

    public abstract boolean canTarget(Entity e);

    public abstract boolean canBeAttackedBy(Entity e);

    public final EntityLivingBase getFollowingEntity() {
        if (followingPlayerName == null) {
            return null;
        }
        return worldObj.getPlayerEntityByName(followingPlayerName);
    }

    public final void setFollowingEntity(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && canBeCommandedBy(entity.getCommandSenderName())) {
            this.followingPlayerName = entity.getCommandSenderName();
        }
    }

    @Override
    public boolean allowLeashing() {
        return false;
    }

    public final void repackEntity(EntityPlayer player) {
        if (!player.worldObj.isRemote) {
            onRepack();
            ItemStack item = InventoryTools.mergeItemStack(player.inventory, this.getItemToSpawn(), -1);
            if (item != null) {
                InventoryTools.dropItemInWorld(player.worldObj, item, player.posX, player.posY, player.posZ);
            }
            setDead();
        }
    }

    /**
     * called when NPC is being repacked into item-form.  Called prior to item being created and prior to entity being set-dead.<br>
     * Main function is for faction-mounted NPCs to disappear their mounts when repacked.
     */
    protected void onRepack() {

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        for (int i = 0; i < 5; i++) {
            setCurrentItemOrArmor(i, null);
        }
        super.readEntityFromNBT(tag);
        ownerName = tag.getString("owner");
        if (tag.hasKey("ordersStack")) {
            ordersStack = InventoryTools.readItemStack(tag.getCompoundTag("ordersStack"));
        }
        if (tag.hasKey("upkeepStack")) {
            upkeepStack = InventoryTools.readItemStack(tag.getCompoundTag("upkeepStack"));
        }
        if (tag.hasKey("shieldStack")) {
            setShieldStack(InventoryTools.readItemStack(tag.getCompoundTag("shieldStack")));
        }
        if (tag.hasKey("home")) {
            int[] ccia = tag.getIntArray("home");
            setHomeArea(ccia[0], ccia[1], ccia[2], ccia[3]);
        }
        if (tag.hasKey("levelingStats")) {
            levelingStats.readFromNBT(tag.getCompoundTag("levelingStats"));
        }
        attackDamage = tag.getInteger("attackDamageOverride");
        armorValue = tag.getInteger("armorValueOverride");
        customTexRef = tag.getString("customTex");

        //TODO remove these when I figure out why JSON reads an empty string as ""
        if ("\"\"".equals(customTexRef)) {
            customTexRef = "";
        }
        if ("\"\"".equals(getCustomNameTag())) {
            setCustomNameTag("");
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setString("owner", ownerName);
        if (ordersStack != null) {
            tag.setTag("ordersStack", InventoryTools.writeItemStack(ordersStack, new NBTTagCompound()));
        }
        if (upkeepStack != null) {
            tag.setTag("upkeepStack", InventoryTools.writeItemStack(upkeepStack, new NBTTagCompound()));
        }
        if (getShieldStack() != null) {
            tag.setTag("shieldStack", InventoryTools.writeItemStack(getShieldStack(), new NBTTagCompound()));
        }
        if (getHomePosition() != null) {
            ChunkCoordinates cc = getHomePosition();
            int[] ccia = new int[]{cc.posX, cc.posY, cc.posZ, (int) func_110174_bM()};
            tag.setIntArray("home", ccia);
        }
        tag.setTag("levelingStats", levelingStats.writeToNBT(new NBTTagCompound()));
        tag.setInteger("attackDamageOverride", attackDamage);
        tag.setInteger("armorValueOverride", armorValue);
        tag.setString("customTex", customTexRef);
        //TODO
    }

    public final ResourceLocation getTexture() {
        if (currentTexture == null) {
            updateTexture();
        }
        return currentTexture == null ? getDefaultTexture() : currentTexture;
    }

    public final void updateTexture() {
        currentTexture = NpcSkinManager.INSTANCE.getTextureFor(this);
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("ownerName")) {
            setOwnerName(tag.getString("ownerName"));
        } else if (tag.hasKey("customTex")) {
            setCustomTexRef(tag.getString("customTex"));
        } else if (tag.hasKey("pickEntity") && !worldObj.isRemote) {
            int id = tag.getCompoundTag("pickEntity").getInteger("playerID");
            EntityPlayer player = (EntityPlayer) worldObj.getEntityByID(id);
            if (player != null) {
                handlePickEntity(player);
            }
        }
    }

    private void handlePickEntity(EntityPlayer player) {
        ItemStack item = this.getItemToSpawn();
        for (int i = 0; i < 9; i++) {
            if (ItemStack.areItemStacksEqual(player.inventory.getStackInSlot(i), item)) {
                return;
            }
        }
        int slotNum = player.inventory.currentItem;
        if (player.inventory.getCurrentItem() != null)//first try to put under currently selected slot, if it is occupied, find first unoccupied slot
        {
            for (int i = 0; i < 9; i++) {
                if (player.inventory.getStackInSlot(i) == null) {
                    slotNum = i;
                    break;
                }
            }
        }
        player.inventory.setInventorySlotContents(slotNum, item);
    }

    public double getDistanceSq(BlockPosition pos) {
        return getDistanceSq(pos.x + 0.5d, pos.y, pos.z + 0.5d);
    }
}
