package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite.WorkType;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.item.ItemHammer;
import net.shadowmage.ancientwarfare.core.item.ItemQuill;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.ai.*;
import net.shadowmage.ancientwarfare.npc.ai.owned.*;
import net.shadowmage.ancientwarfare.npc.item.ItemWorkOrder;
import net.shadowmage.ancientwarfare.npc.orders.WorkOrder;

import java.util.Collection;

public class NpcWorker extends NpcPlayerOwned implements IWorker {

    public BlockPosition autoWorkTarget;
    private NpcAIPlayerOwnedWork workAI;
    private NpcAIPlayerOwnedWorkRandom workRandomAI;

    public NpcWorker(World par1World) {
        super(par1World);
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(0, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(0, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(0, (horseAI = new NpcAIPlayerOwnedRideHorse(this)));
        this.tasks.addTask(2, new NpcAIFollowPlayer(this));
        this.tasks.addTask(2, new NpcAIPlayerOwnedFollowCommand(this));
        this.tasks.addTask(3, new NpcAIFleeHostiles(this));
        this.tasks.addTask(4, new NpcAIPlayerOwnedGetFood(this));
        this.tasks.addTask(5, new NpcAIPlayerOwnedIdleWhenHungry(this));
        this.tasks.addTask(6, (workAI = new NpcAIPlayerOwnedWork(this)));
        this.tasks.addTask(7, (workRandomAI = new NpcAIPlayerOwnedWorkRandom(this)));
        this.tasks.addTask(8, new NpcAIMoveHome(this, 50F, 3F, 30F, 3F));

        //post-100 -- used by delayed shared tasks (look at random stuff, wander)
        this.tasks.addTask(101, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(102, new NpcAIWander(this));
        this.tasks.addTask(103, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));

        this.targetTasks.addTask(0, new NpcAIPlayerOwnedFindWorksite(this));
    }

    @Override
    public String getNpcSubType() {
        WorkType type = getWorkTypeFromEquipment();
        switch (type) {
            case CRAFTING:
                return "craftsman";
            case FARMING:
                return "farmer";
            case FORESTRY:
                return "lumberjack";
            case MINING:
                return "miner";
            case RESEARCH:
                return "researcher";
            case NONE:
            default:
                return "";
        }
    }

    public void handleWorksiteBroadcast(IWorkSite site, BlockPosition pos) {

    }

    @Override
    public String getNpcType() {
        return "worker";
    }

    @Override
    public float getWorkEffectiveness(WorkType type) {
        if (canWorkAt(type)) {
            float effectiveness = 1.f;
            float level = this.getLevelingStats().getLevel();

            effectiveness += level * 0.05f;
            Item item = getHeldItem().getItem();
            if (item instanceof ItemTool) {
                effectiveness += ((ItemTool) item).func_150913_i().getEfficiencyOnProperMaterial() * 0.05f;
            } else if (item instanceof ItemHoe) {
                ToolMaterial mat = ToolMaterial.valueOf(((ItemHoe) item).getToolMaterialName());
                effectiveness += mat.getEfficiencyOnProperMaterial() * 0.05f;
            } else if (item instanceof ItemHammer) {
                effectiveness += ((ItemHammer) item).getMaterial().getEfficiencyOnProperMaterial() * 0.05f;
            } else if (item instanceof ItemQuill) {
                effectiveness += ((ItemQuill) item).getMaterial().getEfficiencyOnProperMaterial() * 0.05f;
            }
            return effectiveness;
        }
        return 0.F;
    }

    @Override
    public boolean shouldBeAtHome() {
        WorkOrder order = WorkOrder.getWorkOrder(ordersStack);
        if(order == null || !order.isNightShift()){
            return super.shouldBeAtHome();
        }else{
            if (getAttackTarget() != null || !hasHome() || worldObj.provider.hasNoSky) {
                return false;
            }
            return worldObj.isDaytime() || worldObj.canLightningStrikeAt(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ));
        }
    }

    @Override
    public double getWorkRangeSq(){
        return NpcAI.ACTION_RANGE;
    }

    @Override
    public boolean canWorkAt(WorkType type) {
        return type == getWorkTypeFromEquipment();
    }

    @Override
    public boolean isValidOrdersStack(ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemWorkOrder;
    }

    protected WorkType getWorkTypeFromEquipment() {
        ItemStack stack = getHeldItem();
        if (stack != null && stack.getItem() != null) {
            if (stack.getItem() instanceof ItemHoe) {
                return WorkType.FARMING;
            } else {
                Collection<String> tools = stack.getItem().getToolClasses(stack);
                if (tools.contains("axe")) {
                    return WorkType.FORESTRY;
                } else if (tools.contains("pickaxe")) {
                    return WorkType.MINING;
                } else if (tools.contains("hammer")) {
                    return WorkType.CRAFTING;
                } else if (tools.contains("quill")) {
                    return WorkType.RESEARCH;
                }
            }
        }
        return WorkType.NONE;
    }

    @Override
    public void onOrdersInventoryChanged() {
        this.workAI.onOrdersChanged();
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        if (tag.hasKey("workAI")) {
            workAI.readFromNBT(tag.getCompoundTag("workAI"));
        }
        if (tag.hasKey("workRandomAI")) {
            workRandomAI.readFromNBT(tag.getCompoundTag("workRandomAI"));
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setTag("workAI", workAI.writeToNBT(new NBTTagCompound()));
        tag.setTag("workRandomAI", workRandomAI.writeToNBT(new NBTTagCompound()));
    }

}
