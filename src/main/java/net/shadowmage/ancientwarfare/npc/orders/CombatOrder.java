package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.OrderingList;
import net.shadowmage.ancientwarfare.npc.item.ItemCombatOrder;

public class CombatOrder extends OrderingList<BlockPosition> implements INBTSerialable {

    int patrolDimensionId = 0;

    public CombatOrder() {

    }

    public void addPatrolPoint(World world, BlockPosition point) {
        if (world.provider.dimensionId != patrolDimensionId) {
            clear();
            patrolDimensionId = world.provider.dimensionId;
        }
        add(point);
    }

    public int getPatrolDimension() {
        return patrolDimensionId;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        clear();
        patrolDimensionId = tag.getInteger("dim");
        if (tag.hasKey("pointList")) {
            NBTTagList list = tag.getTagList("pointList", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                add(new BlockPosition(list.getCompoundTagAt(i)));
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setInteger("dim", patrolDimensionId);
        if (!isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (BlockPosition point : points) {
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

    public void write(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemCombatOrder) {
            stack.setTagInfo("orders", writeToNBT(new NBTTagCompound()));
        }
    }


}
