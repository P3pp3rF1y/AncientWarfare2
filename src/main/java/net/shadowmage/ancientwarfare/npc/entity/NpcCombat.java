package net.shadowmage.ancientwarfare.npc.entity;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackMeleeLongRange;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIAttackNearest;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDistressResponse;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIDoor;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIFollowPlayer;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIHurt;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMedicBase;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIMoveHome;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIWander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedAlarmResponse;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedAttackRanged;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedCommander;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedFollowCommand;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedGetFood;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedIdleWhenHungry;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedPatrol;
import net.shadowmage.ancientwarfare.npc.ai.owned.NpcAIPlayerOwnedRideHorse;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.item.ItemCombatOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;

import javax.annotation.Nonnull;
import java.util.Collection;

public class NpcCombat extends NpcPlayerOwned implements IRangedAttackMob {

    private EntityAIBase collideAI;
    private EntityAIBase arrowAI;
    private NpcAIPlayerOwnedPatrol patrolAI;
    
    private NpcBase distressedTarget;

    public NpcCombat(World par1World) {
        super(par1World);
        collideAI = new NpcAIAttackMeleeLongRange(this);
        arrowAI = new NpcAIPlayerOwnedAttackRanged(this);

        Predicate<Entity> selector = this::isHostileTowards;

        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new NpcAIDoor(this, true));
        this.tasks.addTask(0, (horseAI = new NpcAIPlayerOwnedRideHorse(this)));
        this.tasks.addTask(2, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
        this.tasks.addTask(3, new NpcAIPlayerOwnedAlarmResponse(this));
        this.tasks.addTask(4, new NpcAIPlayerOwnedGetFood(this));
        this.tasks.addTask(5, new NpcAIPlayerOwnedIdleWhenHungry(this));
        //6--empty....
        //7==combat task, inserted from onweaponinventoryupdated
        this.tasks.addTask(8, new NpcAIMedicBase(this));
        this.tasks.addTask(8, new NpcAIDistressResponse(this));
        this.tasks.addTask(9, (patrolAI = new NpcAIPlayerOwnedPatrol(this)));

        this.tasks.addTask(10, new NpcAIMoveHome(this, 50F, 5F, 20F, 5F));

        //post-100 -- used by delayed shared tasks (look at random stuff, wander)
        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

        this.targetTasks.addTask(0, new NpcAIPlayerOwnedCommander(this));
        this.targetTasks.addTask(1, new NpcAIHurt(this));
        this.targetTasks.addTask(2, new NpcAIAttackNearest(this, selector));
    }

    @Override
    public final boolean canPickUpLoot(){
        return !"archer".equals(getSubtypeFromEquipment());
    }

    @Override
    public boolean isValidOrdersStack(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemCombatOrder;
    }

    @Override
    public void onOrdersInventoryChanged() {
        patrolAI.onOrdersInventoryChanged();
    }

    @Override
    public void onWeaponInventoryChanged() {
        super.onWeaponInventoryChanged();
        if (!world.isRemote) {
            this.tasks.removeTask(arrowAI);
            this.tasks.removeTask(collideAI);
            @Nonnull ItemStack stack = getHeldItemMainhand();
            if (isBow(stack.getItem())) {
                this.tasks.addTask(7, arrowAI);
            } else {
                this.tasks.addTask(7, collideAI);
            }
        }
    }

    @Override
    public boolean canAttackClass(Class claz) {
        return (isBow(getHeldItemMainhand().getItem())) || super.canAttackClass(claz);
    }

    private boolean isBow(Item item){
        // Inserting QuiverBow recognition here (for b78)
        // TODO quiverbow integration??
//        if (Loader.isModLoaded("quiverchevsky")) {
//            if (item instanceof com.domochevsky.quiverbow.weapons._WeaponBase) {
//                return true;
//            }
//        }
        return item instanceof ItemBow;
    }

    @Override
    public String getNpcSubType() {
        return getSubtypeFromEquipment();
    }

    protected String getSubtypeFromEquipment() {
        @Nonnull ItemStack stack = getHeldItemMainhand();
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            Collection<String> tools = item.getToolClasses(stack);
            if(tools.contains("axe")) {
                return "medic";
            } else if(tools.contains("hammer"))
                return "engineer";
            if (isBow(item)) {
                return "archer";
            } else if (item instanceof ItemCommandBaton) {
                return "commander";
            } else if (item.isEnchantable(stack)) {
                return "soldier";
            }
        }
        return "";
    }

    @Override
    public String getNpcType() {
        return "combat";
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        onWeaponInventoryChanged();
        if (tag.hasKey("patrolAI")) {
            patrolAI.readFromNBT(tag.getCompoundTag("patrolAI"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setTag("patrolAI", patrolAI.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float force) {
        // minimum precision = 10.0f, slowly reaches 0 (or close to it) as the NPC reaches max level
        float precision = 10.0f - ( (float) this.getLevelingStats().getBaseLevel() / (float) AWNPCStatics.maxNpcLevel * 10.0f );
        RangeAttackHelper.doRangedAttack(this, target, force, precision);
    }

    public void respondToDistress(NpcBase source) {
        // TODO: Target prioritizing or something...?
        distressedTarget = source;
    }

    public NpcBase getDistressedTarget() {
        return distressedTarget;
    }
    
    public void clearDistress() {
        distressedTarget = null;
    }
}
