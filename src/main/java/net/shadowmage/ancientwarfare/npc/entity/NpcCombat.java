package net.shadowmage.ancientwarfare.npc.entity;

import cpw.mods.fml.common.Loader;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.*;
import net.shadowmage.ancientwarfare.npc.ai.owned.*;
import net.shadowmage.ancientwarfare.npc.item.ItemCombatOrder;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;

import java.util.Collection;

public class NpcCombat extends NpcPlayerOwned implements IRangedAttackMob {

    private EntityAIBase collideAI;
    private EntityAIBase arrowAI;
    private NpcAIPlayerOwnedPatrol patrolAI;

    public NpcCombat(World par1World) {
        super(par1World);
        collideAI = new NpcAIAttackMeleeLongRange(this);
        arrowAI = new NpcAIPlayerOwnedAttackRanged(this);

        IEntitySelector selector = new IEntitySelector() {
            @Override
            public boolean isEntityApplicable(Entity entity) {
                return isHostileTowards(entity);
            }
        };

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
    public boolean isValidOrdersStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemCombatOrder;
    }

    @Override
    public void onOrdersInventoryChanged() {
        patrolAI.onOrdersInventoryChanged();
    }

    @Override
    public void onWeaponInventoryChanged() {
        super.onWeaponInventoryChanged();
        if (!worldObj.isRemote) {
            this.tasks.removeTask(arrowAI);
            this.tasks.removeTask(collideAI);
            ItemStack stack = getHeldItem();
            if (stack!=null && isBow(stack.getItem())) {
                this.tasks.addTask(7, arrowAI);
            } else {
                this.tasks.addTask(7, collideAI);
            }
        }
    }

    @Override
    public boolean canAttackClass(Class claz) {
        return (getHeldItem() != null && isBow(getHeldItem().getItem())) || super.canAttackClass(claz);
    }

    private boolean isBow(Item item){
        // Inserting QuiverBow recognition here (for b78)
        if (Loader.isModLoaded("quiverchevsky")) {
            if (item instanceof com.domochevsky.quiverbow.weapons._WeaponBase) {
                return true;
            }
        }
        return item instanceof ItemBow;
    }

    @Override
    public String getNpcSubType() {
        return getSubtypeFromEquipment();
    }

    protected String getSubtypeFromEquipment() {
        ItemStack stack = getHeldItem();
        if (stack != null && stack.getItem() != null) {
            Item item = stack.getItem();
            Collection<String> tools = item.getToolClasses(stack);
            if(tools.contains("axe")){
                return "medic";
            }else if(tools.contains("hammer"))
                return "engineer";
            if (isBow(item)) {
                return "archer";
            } else if (item instanceof ItemCommandBaton) {
                return "commander";
            } else if (item.isItemTool(stack)) {
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
    public void attackEntityWithRangedAttack(EntityLivingBase par1EntityLivingBase, float par2) {
        RangeAttackHelper.DEFAULT.doRangedAttack(this, par1EntityLivingBase, par2);
    }

}
