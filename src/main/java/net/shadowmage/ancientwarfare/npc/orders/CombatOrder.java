package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.util.OrderingList;
import net.shadowmage.ancientwarfare.npc.item.ItemCombatOrder;

public class CombatOrder extends OrderingList<BlockPos> implements INBTSerializable<NBTTagCompound> {

    int patrolDimensionId = 0;

    public CombatOrder() {

    }

    public void addPatrolPoint(World world, BlockPos point) {
        if (world.provider.getDimension() != patrolDimensionId) {
            clear();
            patrolDimensionId = world.provider.getDimension();
        }
        add(point);
    }

    public int getPatrolDimension() {
        return patrolDimensionId;
    }

    public static CombatOrder getCombatOrder(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemCombatOrder) {
            CombatOrder order = new CombatOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.deserializeNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public void write(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemCombatOrder) {
            stack.setTagInfo("orders", serializeNBT());
        }
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("dim", patrolDimensionId);
        if (!isEmpty()) {
            NBTTagList list = new NBTTagList();
            for (BlockPos point : points) {
                list.appendTag(new NBTTagLong(point.toLong()));
            }
            tag.setTag("pointList", list);
        }
        return tag;
    }


    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        clear();
        patrolDimensionId = tag.getInteger("dim");
        if (tag.hasKey("pointList")) {
            NBTTagList list = tag.getTagList("pointList", Constants.NBT.TAG_LONG);
            for (int i = 0; i < list.tagCount(); i++) {
                add(BlockPos.fromLong(((NBTTagLong)list.get(i)).getLong())); //TODO make sure that getting long from list here works correctly
            }
        }
    }
}
