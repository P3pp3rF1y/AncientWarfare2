package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;

import java.util.ArrayList;
import java.util.List;

public class WorkOrder extends NpcOrders {

    private WorkPriorityType priorityType = WorkPriorityType.ROUTE;

    private List<WorkEntry> entries = new ArrayList<WorkEntry>();

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        entries.clear();
        NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
        WorkEntry entry;
        for (int i = 0; i < entryList.tagCount(); i++) {
            entry = new WorkEntry();
            entry.readFromNBT(entryList.getCompoundTagAt(i));
            entries.add(entry);
        }
        priorityType = WorkPriorityType.values()[tag.getInteger("priorityType")];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList entryList = new NBTTagList();
        for (WorkEntry entry : entries) {
            entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("entryList", entryList);
        tag.setInteger("priorityType", priorityType.ordinal());
        return tag;
    }

    public WorkPriorityType getPriorityType() {
        return priorityType;
    }

    public List<WorkEntry> getEntries() {
        return entries;
    }

    public boolean addWorkPosition(World world, BlockPosition position, int length) {
        if (entries.size() < 8) {
            entries.add(new WorkEntry(world, position, world.provider.dimensionId, length));
            return true;
        }
        return false;//return true if successfully added
    }

    public void removePosition(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
        }
    }

    public void incrementPosition(int index) {
        if (index >= 1 && index < entries.size()) {
            WorkEntry entry = entries.remove(index);
            entries.add(index - 1, entry);
        }
    }

    public void decrementPosition(int index) {
        if (index >= 0 && index < entries.size() - 1) {
            WorkEntry entry = entries.remove(index);
            entries.add(index + 1, entry);
        }
    }

    @Override
    public String toString() {
        return "Work Orders size: " + entries.size() + " of type: " + priorityType;
    }

    public static WorkOrder getWorkOrder(ItemStack stack) {
        if (stack != null && stack.getItem() == AWNpcItemLoader.workOrder) {
            WorkOrder order = new WorkOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public static void writeWorkOrder(ItemStack stack, WorkOrder order) {
        if (stack != null && stack.getItem() == AWNpcItemLoader.workOrder) {
            stack.setTagInfo("orders", order.writeToNBT(new NBTTagCompound()));
        }
    }

    public static final class WorkEntry {

        private Block block;
        int blockMeta;
        private BlockPosition position = new BlockPosition();
        int dimension;
        private int workLength;

        private WorkEntry() {
        }//nbt constructor

        public WorkEntry(World world, BlockPosition position, int dimension, int workLength) {
            this.setBlock(world.getBlock(position.x, position.y, position.z));
            this.blockMeta = world.getBlockMetadata(position.x, position.y, position.z);
            this.setPosition(position);
            this.dimension = dimension;
            this.setWorkLength(workLength);
        }

        public void readFromNBT(NBTTagCompound tag) {
            setPosition(new BlockPosition(tag.getCompoundTag("pos")));
            dimension = tag.getInteger("dim");
            setWorkLength(tag.getInteger("length"));
            setBlock(Block.getBlockFromName(tag.getString("block")));
            blockMeta = tag.getInteger("blockMeta");
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setTag("pos", getPosition().writeToNBT(new NBTTagCompound()));
            tag.setInteger("dim", dimension);
            tag.setInteger("length", getWorkLength());
            tag.setString("block", Block.blockRegistry.getNameForObject(getBlock()));
            tag.setInteger("blockMeta", blockMeta);
            return tag;
        }

        /**
         * @return the block
         */
        public Block getBlock() {
            return block;
        }

        /**
         * @param block the block to set
         */
        public void setBlock(Block block) {
            this.block = block;
        }

        /**
         * @return the position
         */
        public BlockPosition getPosition() {
            return position;
        }

        /**
         * @param position the position to set
         */
        public void setPosition(BlockPosition position) {
            this.position = position;
        }

        /**
         * @return the workLength
         */
        public int getWorkLength() {
            return workLength;
        }

        /**
         * @param workLength the workLength to set
         */
        public void setWorkLength(int workLength) {
            this.workLength = workLength;
        }
    }

    public static enum WorkPriorityType {
        PRIORITY_LIST,
        ROUTE,
        TIMED
    }

}
