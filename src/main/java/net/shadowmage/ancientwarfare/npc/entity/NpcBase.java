package net.shadowmage.ancientwarfare.npc.entity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.gamedata.Timekeeper;
import net.shadowmage.ancientwarfare.core.interfaces.IEntityPacketHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketEntity;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.ai.NpcNavigator;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.item.ItemShield;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinManager;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public abstract class NpcBase extends EntityCreature implements IEntityAdditionalSpawnData, IOwnable, IEntityPacketHandler {

    private String ownerName = "";//the owner of this NPC, used for checking teams
    private UUID ownerId;
    protected String followingPlayerName;//set/cleared onInteract from player if player.team==this.team

    protected NpcLevelingStats levelingStats;

    /**
     * a single base texture for ALL npcs to share, used in case other textures were not set
     */
    private static final ResourceLocation baseDefaultTexture = new ResourceLocation("ancientwarfare:textures/entity/npc/npc_default.png");

    private ResourceLocation currentTexture = null;
    public static final int ORDER_SLOT = 5, UPKEEP_SLOT = 6, SHIELD_SLOT = 7;
    public ItemStack ordersStack;
    public ItemStack upkeepStack;
    
    // used for flee/distress AI
    // this isn't really useful yet, combat NPC's will attack any mob they come in
    // contact with during a distress response
    public LinkedHashSet<Entity> nearbyHostiles = new LinkedHashSet<Entity>();

    private boolean aiEnabled = true;

    private int attackDamage = -1;//faction based only
    private int armorValue = -1;//faction based only
    private int maxHealthOverride = -1;
    private String customTexRef = "";//might as well allow for player-owned as well...
    
    private int bedDirection;
    private BlockPosition cachedBedPos;
    private boolean foundBed = false;
    private boolean rainedOn = false;

    public NpcBase(World par1World) {
        super(par1World);
        levelingStats = new NpcLevelingStats(this);
        this.equipmentDropChances = new float[]{1.f, 1.f, 1.f, 1.f, 1.f};
        this.func_110163_bv();//set persistence required=true
        this.navigator = new NpcNavigator(this);
        AncientWarfareNPC.statics.applyPathConfig(this);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.getDataWatcher().addObject(20, Integer.valueOf(0));//ai tasks
        this.getDataWatcher().addObjectByDataType(21, 5);//5 for ItemStack
        this.getDataWatcher().addObject(12, Integer.valueOf(0)); // ownedBed posX
        this.getDataWatcher().addObject(13, Integer.valueOf(0)); // ownedBed posY
        this.getDataWatcher().addObject(14, Integer.valueOf(0)); // ownedBed posZ
        this.getDataWatcher().addObject(15, Byte.valueOf((byte) 0)); // isSleeping flag
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
                if(customTexRef.startsWith("Player:")){
                    String name = customTexRef.split(":", 2)[1];
                    NBTTagCompound tagCompound = AncientWarfareNPC.proxy.cacheProfile(name);
                    if(tagCompound != null)
                        pkt.packetData.setTag("profileTex", tagCompound);
                }
                pkt.packetData.setString("customTex", customTexRef);
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
        float damage = (float) this.getAttackDamageOverride();
        ItemStack shield = null;
        if (damage < 0) {
            if (getShieldStack() != null) {
                shield = getShieldStack().copy();
                getAttributeMap().applyAttributeModifiers(shield.getAttributeModifiers());
            }
            damage = (float) this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
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
        if (shield != null) {
            getAttributeMap().removeAttributeModifiers(shield.getAttributeModifiers());
        }
        return targetHit;
    }

    /**
     * Proper calculations for all types of armors, including shields
     */
    @Override
    protected float applyArmorCalculations(DamageSource source, float amount){
        if (!source.isUnblockable())
        {
            if (getArmorValueOverride() >= 0) {
                return super.applyArmorCalculations(source, amount);
            }else{
                float value = ISpecialArmor.ArmorProperties.ApplyArmor(this, getLastActiveItems(), source, amount);
                if (value > 0.0F && getShieldStack() != null && getShieldStack().getItem() instanceof ItemShield) {
                    float absorb = value * ((ItemShield) getShieldStack().getItem()).getArmorBonusValue() / 25F;
                    int dmg = Math.max((int)absorb, 1);
                    getShieldStack().damageItem(dmg, this);
                    value -= absorb;
                }
                if(value < 0.0F)
                    return 0;
                return value;
            }
        }
        return amount;
    }

    /**
     * Deprecated vanilla armor calculations
     */
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
    public int getMaxSafePointTries() {
        return this.getAttackTarget() == null ? 4 : 4 + (int)(this.getHealth()/3);
    }

    @Override
    public float getBlockPathWeight(int varX, int varY, int varZ){
        Block below = worldObj.getBlock(varX, varY - 1, varZ);
        if(below.getMaterial() == Material.lava || below.getMaterial() == Material.cactus)//Avoid cacti and lava when wandering
            return -10;
        else if(below.getMaterial().isLiquid())//Don't try swimming too much
            return 0;
        /*if(this.ridingEntity instanceof EntityCreature)
            return ((EntityCreature)this.ridingEntity).getBlockPathWeight(varX, varY - 1, varZ);*/
        float level = worldObj.getLightBrightness(varX, varY, varZ);//Prefer lit areas
        if(level < 0)
            return 0;
        else
            return level + (below.isSideSolid(worldObj, varX, varY - 1, varZ, ForgeDirection.UP) ? 1 : 0);
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

    public BlockPosition getTownHallPosition() {
        return null;//NOOP on non-player owned npc
    }

    public void setHomeAreaAtCurrentPosition(){
        setHomeArea(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ), getHomeRange());
    }

    public int getHomeRange(){
        if(hasHome()){
            return MathHelper.floor_float(func_110174_bM());
        }
        return 5;
    }

    /**
     * Return true if this NPC should be within his home range.<br>
     * Should still allow for a combat NPC to attack targets outside his home range.
     */
    public boolean shouldBeAtHome() {
        if (getAttackTarget() != null || !hasHome()) {
            return false;
        }
        if (worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)))
            setRainedOn(true);
        return shouldSleep() || isWaitingForRainToStop();
    }
    
    private boolean isWaitingForRainToStop() {
        if (!this.worldObj.isRaining()) {
            // rain has stopped, reset
            setRainedOn(false);
            return false;
        }
        return rainedOn;
    }
    
    public void setRainedOn(boolean rainedOn) {
        this.rainedOn = rainedOn;
    }

    public void setIsAIEnabled(boolean val) {
        this.aiEnabled = val;
    }

    public boolean getIsAIEnabled() {
        return aiEnabled && !AWNPCStatics.npcAIDebugMode;
    }

    @Override
    protected boolean interact(EntityPlayer player) {
        return tryCommand(player);
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

    protected boolean tryCommand(EntityPlayer player) {
        boolean baton = player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton;
        if (!baton) {
            if(!worldObj.isRemote) {
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
            return true;
        }
        return false;
    }

    @Override
    public void applyEntityCollision(Entity entity){
        if(!isInWater() && !isHostileTowards(entity)){
            int d0 = (int)Math.signum(this.posX - entity.posX);
            int d1 = (int)Math.signum(this.posZ - entity.posZ);
            if(d0!=0 || d1!=0) {
                int x = MathHelper.floor_double(this.posX) + d0;
                int y = MathHelper.floor_double(this.boundingBox.minY) - 1;
                int z = MathHelper.floor_double(this.posZ) + d1;
                Material material = worldObj.getBlock(x, y, z).getMaterial();
                if(material.isLiquid() || material == Material.cactus) {
                    return;
                }
                this.entityCollisionReduction = 0.9F;
            }
        }
        super.applyEntityCollision(entity);
        this.entityCollisionReduction = 0;
    }

    @Override
    public final boolean attackEntityFrom(DamageSource source, float par2) {
        if (getSleeping()) // prevent suffocation damage (allows bunk beds and such)
            return false;
        if (source.getEntity() != null && !canBeAttackedBy(source.getEntity()))
            return false;
        if(source == DamageSource.inWall && this.ridingEntity instanceof EntityLiving) {
            knockFromDamage(par2, worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY + this.getEyeHeight()), MathHelper.floor_double(this.posZ)).getMaterial());
            return false;
        }
        if(source == DamageSource.cactus)
            knockFromDamage(par2, Material.cactus);
        else if(source == DamageSource.lava)
            knockFromDamage(par2, Material.lava);
        else if(source == DamageSource.drown)
            jump();
        return super.attackEntityFrom(source, par2);
    }

    private void knockFromDamage(float val, Material material){
        int x = MathHelper.floor_double(this.posX);
        int y = MathHelper.floor_double(this.boundingBox.minY + 0.5);
        int z = MathHelper.floor_double(this.posZ);
        if(worldObj.getBlock(x - 1, y, z).getMaterial() == material){
            knockBack(null, val, x - 1 - this.posX, 0);
        }else if(worldObj.getBlock(x, y, z - 1).getMaterial() == material){
            knockBack(null, val, 0, z - 1 - this.posZ);
        }else if(worldObj.getBlock(x + 1, y, z).getMaterial() == material){
            knockBack(null, val, x + 1 - this.posX, 0);
        }else if(worldObj.getBlock(x, y, z + 1).getMaterial() == material){
            knockBack(null, val, 0, z + 1 - this.posZ);
        }else if(worldObj.getBlock(x - 1, y, z - 1).getMaterial() == material){
            knockBack(null, val, x - 1 - this.posX, z - 1 - this.posZ);
        }else if(worldObj.getBlock(x + 1, y, z - 1).getMaterial() == material){
            knockBack(null, val, x + 1 - this.posX, z - 1 - this.posZ);
        }else if(worldObj.getBlock(x - 1, y, z + 1).getMaterial() == material){
            knockBack(null, val, x - 1 - this.posX, z + 1 - this.posZ);
        }else if(worldObj.getBlock(x + 1, y, z + 1).getMaterial() == material){
            knockBack(null, val, x + 1 - this.posX, z + 1 - this.posZ);
        }else if(worldObj.getBlock(x, y - 1, z).getMaterial() == material){
            knockBack(null, val, 2 * getRNG().nextFloat() - 1, 2 * getRNG().nextFloat() - 1);
        }
        if(worldObj.isRemote || getNavigator().noPath())
            return;
        PathPoint point = getNavigator().getPath().getPathPointFromIndex(getNavigator().getPath().getCurrentPathIndex());
        if(worldObj.getBlock(point.xCoord, point.yCoord, point.zCoord).getMaterial() == material){
            getNavigator().clearPathEntity();
        }else if(worldObj.getBlock(point.xCoord, point.yCoord - 1, point.zCoord).getMaterial() == material){
            getNavigator().clearPathEntity();
        }
    }

    @Override
    public void setWorld(World world){
        super.setWorld(world);
        ((NpcNavigator)navigator).onWorldChange();
    }

    @Override
    public void setAttackTarget(EntityLivingBase entity) {
        if (entity != null && !canTarget(entity))
            return;
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
            for (int i = 0; i < equipmentDropChances.length; i++) {
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
    public final ItemStack getEquipmentInSlot(int slot) {
        if (slot == ORDER_SLOT)
            return ordersStack;
        else if (slot == UPKEEP_SLOT)
            return upkeepStack;
        else if (slot == SHIELD_SLOT)
            return getShieldStack();
        else
            return super.getEquipmentInSlot(slot);
    }

    @Override
    public final void setCurrentItemOrArmor(int slot, ItemStack stack) {
        if (slot == SHIELD_SLOT) {
            setShieldStack(stack);
        } else if (slot == UPKEEP_SLOT) {
            upkeepStack = stack;
        } else if (slot == ORDER_SLOT) {
            ordersStack = stack;
            onOrdersInventoryChanged();
        } else {
            super.setCurrentItemOrArmor(slot, stack);
            if (slot == 0) {
                onWeaponInventoryChanged();
            }
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
        readBaseTags(tag);
    }

    /**
     * Implementations should write out any persistent entity-data needed to restore entity-state from an item-stack.<br>
     * This should include inventory, levels, orders, faction / etc
     */
    public final NBTTagCompound writeAdditionalItemData(NBTTagCompound tag) {
        NBTTagList equipmentList = new NBTTagList();
        ItemStack stack;
        NBTTagCompound equipmentTag;
        for (int i = 0; i < equipmentDropChances.length; i++) {
            stack = getEquipmentInSlot(i);
            if (stack == null) {
                continue;
            }
            equipmentTag = InventoryTools.writeItemStack(stack);
            equipmentTag.setInteger("slotNum", i);
            equipmentList.appendTag(equipmentTag);
        }
        tag.setTag("equipment", equipmentList);
        writeBaseTags(tag);
        return tag;
    }

    /**
     * is the input stack a valid orders-item for this npc?<br>
     * only used by player-owned NPCs
     */
    public boolean isValidOrdersStack(ItemStack stack) {
        return false;
    }

    /**
     * callback for when orders-stack changes.  implementations should inform any necessary AI tasks of the
     * change to order-items
     */
    public void onOrdersInventoryChanged() {
    }

    /**
     * callback for when weapon slot has been changed.<br>
     * Implementations should re-set any subtype or inform any AI that need to know when
     * weapon was changed.
     */
    public void onWeaponInventoryChanged() {
    }

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
    public String getCommandSenderName() {
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
        long l1 = buffer.readLong();
        long l2 = buffer.readLong();
        this.entityUniqueID = new UUID(l1, l2);
        ownerName = ByteBufUtils.readUTF8String(buffer);
        customTexRef = ByteBufUtils.readUTF8String(buffer);
        this.updateTexture();
    }

    @Override
    public void onUpdate() {
        worldObj.theProfiler.startSection("AWNpcTick");
        updateArmSwingProgress();
        if (ticksExisted % 200 == 0 && getHealth() < getMaxHealth() && isEntityAlive() && (!requiresUpkeep() || getFoodRemaining() > 0)) {
            setHealth(getHealth() + 1);
        }
        super.onUpdate();
        if(getHeldItem()!=null){
            try{//Inserting Item#onUpdate, to let it do whatever it needs to do. Used by QuiverBow for burst fire
                getHeldItem().updateAnimation(worldObj, this, 0, true);
            }catch (Exception ignored){}
        }
        worldObj.theProfiler.endSection();
    }

    @Override
    public boolean canAttackClass(Class claz) {
        return !EntityFlying.class.isAssignableFrom(claz);
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

    public boolean requiresUpkeep() {
        return this instanceof IKeepFood;//NOOP in non-player owned
    }

    @Override
    public void setOwner(EntityPlayer player){
        ownerId = player.getUniqueID();
        setOwnerName(player.getCommandSenderName());
    }

    public void setOwnerName(String name) {
        if (name == null) {
            name = "";
        }
        if (!worldObj.isRemote && !name.equals(ownerName)) {
            PacketEntity pkt = new PacketEntity(this);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setString("ownerName", name);
            EntityPlayer player = worldObj.getPlayerEntityByName(name);
            if(player!=null){
                ownerId = player.getUniqueID();
            }
            if(ownerId!=null)
                tag.setString("ownerId", ownerId.toString());
            pkt.packetData = tag;
            NetworkHandler.sendToAllTracking(this, pkt);
        }
        ownerName = name;
    }

    private void checkOwnerName(){
        if(ownerId!=null){
            EntityPlayer player = worldObj.func_152378_a(ownerId);
            if(player!=null && !player.getCommandSenderName().equals(ownerName)){
                setOwnerName(player.getCommandSenderName());
            }
        }
    }

    @Override
    public boolean isOwner(EntityPlayer player){
        if(player == null || player.getGameProfile() == null)
            return false;
        if(ownerId!=null)
            return player.getUniqueID().equals(ownerId);
        return player.getCommandSenderName().equals(ownerName);
    }

    @Override
    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public Team getTeam() {
        return worldObj.getScoreboard().getPlayersTeam(ownerName);
    }
     
    public boolean hasCommandPermissions(String playerName) {
        // ensure the NPC is actually owned
        if (ownerName.isEmpty())
            return false;
        if (playerName == null)
            return false;
        // check if same player
        if (playerName.equals(getOwnerName()))
            return true;
        // check if same team
        Team npcTeam = getTeam();
        if (npcTeam != null)
            if (npcTeam.isSameTeam(worldObj.getScoreboard().getPlayersTeam(playerName)))
                return true;
        // check if friends in FTBUtils
        if (ModAccessors.FTBU.areFriends(getOwnerName(), playerName))
            return true;
        return false;
    }
    
    @Override
    protected int getExperiencePoints(EntityPlayer attacker){
        if(attacker!=null && isHostileTowards(attacker) && canBeAttackedBy(attacker)){
            return super.getExperiencePoints(attacker);
        }
        return 0;
    }

    public void setExperienceDrop(int exp){
        this.experienceValue = exp;
    }

    public abstract boolean isHostileTowards(Entity e);

    public abstract boolean canTarget(Entity e);

    public abstract boolean canBeAttackedBy(Entity e);
    
    public boolean isEntitySameTeamOrFriends(Entity entityTarget) {
        Team targetTeam;
        String targetOwnerOrName;
        if (entityTarget instanceof NpcPlayerOwned) {
            targetTeam = ((NpcPlayerOwned) entityTarget).getTeam();
            targetOwnerOrName = ((NpcPlayerOwned) entityTarget).getOwnerName();
        } else if (entityTarget instanceof EntityPlayer) {
            targetTeam = ((EntityPlayer) entityTarget).getTeam();
            targetOwnerOrName = ((EntityPlayer) entityTarget).getCommandSenderName();
        } else
            return false;
        
        if (targetTeam != null)
            if (targetTeam.isSameTeam(getTeam()))
                return true;
        if (ModAccessors.FTBU.areFriends(targetOwnerOrName, getOwnerName()))
            return true;
        
        return false;
    }

    public final EntityLivingBase getFollowingEntity() {
        if (followingPlayerName == null) {
            return null;
        }
        return worldObj.getPlayerEntityByName(followingPlayerName);
    }

    public final void setFollowingEntity(EntityLivingBase entity) {
        if (entity instanceof EntityPlayer && hasCommandPermissions(entity.getCommandSenderName())) {
            this.followingPlayerName = entity.getCommandSenderName();
        }
    }
    
    public final void clearFollowingEntity() {
        this.followingPlayerName = null;
    }

    @Override
    public boolean allowLeashing() {
        return false;
    }

    public final void repackEntity(EntityPlayer player) {
        // we already hide the button but we need to prevent repack server-side too
        if (AWNPCStatics.repackCreativeOnly && !player.capabilities.isCreativeMode)
            return;
        if (!player.worldObj.isRemote && isEntityAlive()) {
            onRepack();
            ItemStack item = InventoryTools.mergeItemStack(player.inventory, this.getItemToSpawn(), -1);
            if (item != null) {
                InventoryTools.dropItemInWorld(player.worldObj, item, player.posX, player.posY, player.posZ);
            }
        }
        setDead();
    }

    /**
     * called when NPC is being repacked into item-form.  Called prior to item being created and prior to entity being set-dead.<br>
     * Main function is for faction-mounted NPCs to disappear their mounts when repacked.
     */
    protected void onRepack() {

    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        for (int i = 0; i < equipmentDropChances.length; i++) {
            setCurrentItemOrArmor(i, null);
        }
        super.readEntityFromNBT(tag);
        if (tag.hasKey("home")) {
            int[] ccia = tag.getIntArray("home");
            setHomeArea(ccia[0], ccia[1], ccia[2], ccia[3]);
        }
        if (tag.hasKey("bedDirection"))
            setBedDirection(tag.getInteger("bedDirection"));
        if (tag.hasKey("isSleeping"))
            setSleeping(tag.getBoolean("isSleeping"));
        if (tag.hasKey("cachedBedPos")) {
            int[] cachedBedPos = tag.getIntArray("cachedBedPos");
            this.cachedBedPos = new BlockPosition(cachedBedPos[0], cachedBedPos[1], cachedBedPos[2]);
        }
        if (tag.hasKey("bedPos")) {
            int[] bedPos = tag.getIntArray("bedPos");
            this.setBedPosition(new BlockPosition(bedPos[0], bedPos[1], bedPos[2]));
        }
        if (tag.hasKey("foundBed"))
            this.foundBed = tag.getBoolean("foundBed");
        
        readBaseTags(tag);
        onWeaponInventoryChanged();
    }

    private void readBaseTags(NBTTagCompound tag){
        if (tag.hasKey("ordersStack")) {
            setCurrentItemOrArmor(ORDER_SLOT, InventoryTools.readItemStack(tag.getCompoundTag("ordersStack")));
        }
        if (tag.hasKey("upkeepStack")) {
            setCurrentItemOrArmor(UPKEEP_SLOT, InventoryTools.readItemStack(tag.getCompoundTag("upkeepStack")));
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
            setIsAIEnabled(tag.getBoolean("aiEnabled"));
        }
        setOwnerName(tag.getString("owner"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        if (!hasHome()) {
            BlockPosition position = getTownHallPosition();
            if(position != null)
                setHomeArea(position.x, position.y, position.z, getHomeRange());
            else
                setHomeAreaAtCurrentPosition();
        }
        ChunkCoordinates cc = getHomePosition();
        int[] ccia = new int[]{cc.posX, cc.posY, cc.posZ, getHomeRange()};
        tag.setIntArray("home", ccia);
        tag.setString("owner", ownerName);
        tag.setInteger("attackDamageOverride", attackDamage);
        tag.setInteger("armorValueOverride", armorValue);
        tag.setString("customTex", customTexRef);
        tag.setBoolean("aiEnabled", aiEnabled);
        tag.setInteger("bedDirection", bedDirection);
        tag.setBoolean("isSleeping", this.getSleeping());
        BlockPosition bedPos = this.getBedPosition();
        tag.setIntArray("bedPos", new int[]{bedPos.x, bedPos.y, bedPos.z});
        if (cachedBedPos != null)
            tag.setIntArray("cachedBedPos", new int[]{cachedBedPos.x, cachedBedPos.y, cachedBedPos.z});
        tag.setBoolean("foundBed", foundBed);
    }

    private void writeBaseTags(NBTTagCompound tag){
        if (ordersStack != null) {
            tag.setTag("ordersStack", InventoryTools.writeItemStack(ordersStack));
        }
        if (upkeepStack != null) {
            tag.setTag("upkeepStack", InventoryTools.writeItemStack(upkeepStack));
        }
        if (getShieldStack() != null) {
            tag.setTag("shieldStack", InventoryTools.writeItemStack(getShieldStack()));
        }
        tag.setTag("levelingStats", getLevelingStats().writeToNBT(new NBTTagCompound()));
        tag.setFloat("maxHealth", getMaxHealth());
        tag.setFloat("health", getHealth());
        tag.setInteger("food", getFoodRemaining());
        if (hasCustomNameTag()) {
            tag.setString("name", getCustomNameTag());
        }
        checkOwnerName();
    }

    public final ResourceLocation getTexture() {
        if (currentTexture == null) {
            updateTexture();
        }
        return currentTexture == null ? getDefaultTexture() : currentTexture;
    }

    public final void updateTexture() {
        if(customTexRef.startsWith("Player:")){
            try {
                currentTexture = AncientWarfareNPC.proxy.getPlayerSkin(customTexRef.split(":", 2)[1]);
            }catch (Throwable ignored){}
        }else {
            currentTexture = NpcSkinManager.INSTANCE.getTextureFor(this);
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("ownerName")) {
            setOwnerName(tag.getString("ownerName"));
            if(tag.hasKey("ownerId")){
                ownerId = UUID.fromString(tag.getString("ownerId"));
                checkOwnerName();
            }
        } else if (tag.hasKey("profileTex") && tag.hasKey("customTex")) {
            customTexRef = tag.getString("customTex");
            NBTTagCompound tah = tag.getCompoundTag("profileTex");
            if(worldObj.isRemote) {
                try {
                    AncientWarfareNPC.proxy.cacheProfile(NBTUtil.func_152459_a(tah));
                }catch (Throwable ignored){}
            }
            updateTexture();
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
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            if (ItemStack.areItemStacksEqual(player.inventory.getStackInSlot(i), item)) {
                player.inventory.currentItem = i;
                return;
            }
        }
        if (player.getHeldItem() != null)//first try to put under currently selected slot, if it is occupied, find first unoccupied slot
        {
            for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
                if (player.inventory.getStackInSlot(i) == null) {
                    player.inventory.setInventorySlotContents(i, item);
                    return;
                }
            }
        }else
            player.inventory.setInventorySlotContents(player.inventory.currentItem, item);
    }

    public double getDistanceSq(BlockPosition pos) {
        return getDistanceSq(pos.x + 0.5d, pos.y, pos.z + 0.5d);
    }
    
    public BlockPosition findBed() {
        if (!foundBed) {
            int originX = MathHelper.floor_double(this.posX);
            int originY = MathHelper.floor_double(this.posY);
            int originZ = MathHelper.floor_double(this.posZ);
            int maxSearchRange = 6;
            int minX = originX - maxSearchRange;
            int maxX = originX + maxSearchRange;
            int minY = originY - maxSearchRange;
            int maxY = originY + maxSearchRange;
            int minZ = originZ - maxSearchRange;
            int maxZ = originZ + maxSearchRange;
            List<BlockPosition> foundBeds = new ArrayList<BlockPosition>();
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        if (this.worldObj.getBlock(x, y, z) instanceof BlockBed) {
                            int bedMeta = this.worldObj.getBlockMetadata(x, y, z);
                            if (!BlockBed.isBlockHeadOfBed(bedMeta))
                                continue;
                            if (!BlockBed.func_149976_c(bedMeta)) { // occupied check
                                foundBeds.add(new BlockPosition(x, y, z));
                            }
                        }
                    }
                }
            }
        
            int closetBedIndex = -1;
            for (int i = 0; i < foundBeds.size(); i++) {
                float bedDistance = foundBeds.get(i).getCenterDistanceFrom(originX, originY, originZ);
                if ((closetBedIndex == -1) || (bedDistance < foundBeds.get(closetBedIndex).getCenterDistanceFrom(originX, originY, originZ)))
                    closetBedIndex = i;
            }
            if (closetBedIndex == -1) {
                foundBed = false;
                return null;
            }
            foundBed = true;
            cachedBedPos = foundBeds.get(closetBedIndex);
        }
        
        if (this.worldObj.getBlock(cachedBedPos.x, cachedBedPos.y, cachedBedPos.z) instanceof BlockBed) {
            setBedDirection(BlockBed.getDirection(worldObj.getBlockMetadata(cachedBedPos.x, cachedBedPos.y, cachedBedPos.z)));
            return cachedBedPos;
        }
        else {
            foundBed = false;
            return null; // try again in a while
        }
    }
    
    public boolean lieDown(BlockPosition pos) {
        if (!foundBed)
            return false;
        if (worldObj.blockExists(pos.x, pos.y, pos.z) && worldObj.getBlock(pos.x, pos.y, pos.z) instanceof BlockBed) {
            int bedMeta = this.worldObj.getBlockMetadata(pos.x, pos.y, pos.z);
            if (BlockBed.func_149976_c(bedMeta)) {
                // occupied check
                foundBed = false;
                return false;
            }
            BlockBed.func_149979_a(worldObj, pos.x, pos.y, pos.z, true);
            setBedPosition(pos);
            setSleeping(true);
            this.setPositionToBed();
            return true;
        }
        return false;
    }
    
    public void wakeUp() {
        setSleeping(false);
        
        BlockPosition bedPos = getBedPosition();
        // set vacant
        BlockBed.func_149979_a(this.worldObj, bedPos.x, bedPos.y, bedPos.z, false);
        
        
        // Try placing the NPC to an empty spot next to the bed. We don't want them standing on top of the bed, chance for suffocation
        switch (getBedDirection()) {
            case 1:
                if (tryMovingToBedside(bedPos.x + 0.5, bedPos.y, bedPos.z - 0.5)) return;
                if (tryMovingToBedside(bedPos.x + 0.5, bedPos.y, bedPos.z + 1.5)) return;
                if (tryMovingToBedside(bedPos.x + 1.5, bedPos.y, bedPos.z - 0.5)) return;
                if (tryMovingToBedside(bedPos.x + 1.5, bedPos.y, bedPos.z + 1.5)) return;
                break;
            case 3:
                if (tryMovingToBedside(bedPos.x + 0.5, bedPos.y, bedPos.z - 0.5)) return;
                if (tryMovingToBedside(bedPos.x + 0.5, bedPos.y, bedPos.z + 1.5)) return;
                if (tryMovingToBedside(bedPos.x - 0.5, bedPos.y, bedPos.z - 0.5)) return;
                if (tryMovingToBedside(bedPos.x - 0.5, bedPos.y, bedPos.z + 1.5)) return;
                break;
            case 0:
                if (tryMovingToBedside(bedPos.x - 0.5, bedPos.y, bedPos.z + 0.5)) return;
                if (tryMovingToBedside(bedPos.x + 1.5, bedPos.y, bedPos.z + 0.5)) return;
                if (tryMovingToBedside(bedPos.x - 0.5, bedPos.y, bedPos.z - 0.5)) return;
                if (tryMovingToBedside(bedPos.x + 1.5, bedPos.y, bedPos.z - 0.5)) return;
                break;
            case 2:
                if (tryMovingToBedside(bedPos.x - 0.5, bedPos.y, bedPos.z + 0.5)) return;
                if (tryMovingToBedside(bedPos.x + 1.5, bedPos.y, bedPos.z + 0.5)) return;
                if (tryMovingToBedside(bedPos.x - 0.5, bedPos.y, bedPos.z + 1.5)) return;
                if (tryMovingToBedside(bedPos.x + 1.5, bedPos.y, bedPos.z + 1.5)) return;
                break;
        }
        setSleeping(false);
    }
    
    private boolean tryMovingToBedside(double x, double y, double z) {
        System.out.println(x + "x" + y + "x" + z);
        if (worldObj.getBlock((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z)).getMaterial().blocksMovement())
            return false;
        if (worldObj.getBlock((int)Math.floor(x), (int)Math.floor(y + 1.0), (int)Math.floor(z)).getMaterial().blocksMovement())
            return false;
        if (!worldObj.getBlock((int)Math.floor(x), (int)Math.floor(y - 1.0), (int)Math.floor(z)).getMaterial().blocksMovement())
            return false;
        this.aiEnabled = false;
        this.setPosition(x, y, z);
        this.aiEnabled = true;
        setSleeping(false);
        return true;
    }
    
    public final BlockPosition getBedPosition() {
        return new BlockPosition(getDataWatcher().getWatchableObjectInt(12), getDataWatcher().getWatchableObjectInt(13), getDataWatcher().getWatchableObjectInt(14));
    }
    
    private final void setBedPosition(BlockPosition pos) {
        this.getDataWatcher().updateObject(12, Integer.valueOf(pos.x));
        this.getDataWatcher().updateObject(13, Integer.valueOf(pos.y));
        this.getDataWatcher().updateObject(14, Integer.valueOf(pos.z));
    }
    
    @Override
    public void setPosition(double posX, double posY, double posZ) {
        if (getSleeping()) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            float width = this.width / 2.0F;
            float height = this.height;
            
            double minX = posX - (double) width;
            double minZ = posZ - (double) width;
            double maxX = posX + (double) width;
            double maxZ = posZ + (double) width;
            
            switch (this.getBedDirection()) {
                case 0:
                case 2:
                    minZ -= 0.6f;
                    maxZ += 0.6f;
                    break;
                case 1:
                case 3:
                    minX -= 0.6f;
                    maxX += 0.6f;
                    break;
            }
            
            this.boundingBox.setBounds(minX, posY - (double)this.yOffset + (double)this.ySize, minZ, maxX, posY - (double)this.yOffset + (double)this.ySize + (double) height - 1.5f, maxZ);
            return;
        }
        
        super.setPosition(posX, posY, posZ);
    }
    
    // TODO: sitting around when idle
    //@Override
    //public boolean isRiding() {
    //    return true;
    //}
    
    public void setPositionToBed() {
        int posX = cachedBedPos.x;
        int posZ = cachedBedPos.z;
        
        float xOffset = 0.5F;
        float zOffset = 0.5F;
        
        switch (this.getBedDirection()) {
            case 0:
                zOffset -= 0.5f;
                break;
            case 1:
                xOffset += 0.5f;
                break;
            case 2:
                zOffset += 0.5f;
                break;
            case 3:
                xOffset -= 0.5f;
                break;
        }
        setPosition(cachedBedPos.x + xOffset, cachedBedPos.y + 0.6, cachedBedPos.z + zOffset);
    }
    
    public boolean isBedCacheValid() {
        return (worldObj.getBlock(cachedBedPos.x, cachedBedPos.y, cachedBedPos.z) instanceof BlockBed);
    }
    
    @Override
    public void onCollideWithPlayer(EntityPlayer player) {
        if (!getSleeping())
            super.onCollideWithPlayer(player);
    }
    
    @Override
    public boolean canBePushed() {
        return (!getSleeping());
    }
    
    @Override
    protected void collideWithEntity(Entity entity) {
        if (!getSleeping())
            super.collideWithEntity(entity);
    }
    
    public void setSleeping(boolean isSleeping) {
        this.getDataWatcher().updateObject(15, Byte.valueOf((byte) (isSleeping ? 1 : 0)));
    }
    
    public boolean getSleeping() {
        if (getDataWatcher() == null)
            return false;
        return getDataWatcher().getWatchableObjectByte(15) == 1 ? true : false;
    }
    
    public void setBedDirection(int direction) {
        bedDirection = direction;
    }
    
    public int getBedDirection() {
        return bedDirection;
    }
    
    public boolean shouldSleep() {
        return Timekeeper.INSTANCE.isNighttime();
    }
    
    // Only used by the renderer
    @SideOnly(Side.CLIENT)
    public float getBedOrientationInDegrees(int x, int y, int z) {
        Block bed = worldObj.getBlock(x, y, z);
        if (bed != null && bed instanceof BlockBed) {
            setBedDirection(bed.getBedDirection(worldObj, x, y, z));
            switch (bedDirection) {
                case 0:
                    return 90.0F;
                case 1:
                    return 0.0F;
                case 2:
                    return 270.0F;
                case 3:
                    return 180.0F;
            }
        }
        return -1;
    }
}
