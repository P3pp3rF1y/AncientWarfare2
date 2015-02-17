package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.item.ItemCombatOrder;

import java.util.ArrayList;
import java.util.List;

public class CombatOrder extends NpcOrders {

    int patrolDimensionId = 0;

    List<BlockPosition> patrolPoints = new ArrayList<BlockPosition>();

    public CombatOrder() {

    }

    public void addPatrolPoint(World world, BlockPosition point) {
        if (world.provider.dimensionId != patrolDimensionId) {
            patrolPoints.clear();
        }
        patrolDimensionId = world.provider.dimensionId;
        patrolPoints.add(point);
    }

    public void incrementPointPosition(int index) {
        if (index >= 1 && index < patrolPoints.size()) {
            BlockPosition entry = patrolPoints.remove(index);
            patrolPoints.add(index - 1, entry);
        }
    }

    public void decrementPointPosition(int index) {
        if (index >= 0 && index < patrolPoints.size() - 1) {
            BlockPosition entry = patrolPoints.remove(index);
            patrolPoints.add(index + 1, entry);
        }
    }

    public BlockPosition getPatrolPoint(int index) {
        return patrolPoints.get(index);
    }

    public void removePatrolPoint(int index) {
        patrolPoints.remove(index);
    }

    public void clearPatrol() {
        patrolPoints.clear();
    }

    public int getPatrolSize() {
        return patrolPoints.size();
    }

    public int getPatrolDimension() {
        return patrolDimensionId;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        patrolPoints.clear();
        patrolDimensionId = tag.getInteger("dim");
        if (tag.hasKey("pointList")) {
            NBTTagList list = tag.getTagList("pointList", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                patrolPoints.add(new BlockPosition(list.getCompoundTagAt(i)));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("dim", patrolDimensionId);
        if (!patrolPoints.isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (BlockPosition point : patrolPoints) {
                list.appendTag(point.writeToNBT(new NBTTagCompound()));
            }
            tag.setTag("pointList", list);
        }
        return tag;
    }

    public static CombatOrder getCombatOrder(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemCombatOrder) {
            CombatOrder order = new CombatOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public static void writeCombatOrder(ItemStack stack, CombatOrder order) {
        if (stack != null && stack.getItem() instanceof ItemCombatOrder) {
            stack.setTagInfo("orders", order.writeToNBT(new NBTTagCompound()));
        }
    }


}
