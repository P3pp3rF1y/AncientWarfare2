package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;
import net.shadowmage.ancientwarfare.core.util.OrderingList;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemWorkOrder;

import java.util.List;

public class WorkOrder extends OrderingList<WorkOrder.WorkEntry> implements INBTSerialable {

    private WorkPriorityType priorityType = WorkPriorityType.ROUTE;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        clear();
        NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
        WorkEntry entry;
        for (int i = 0; i < entryList.tagCount(); i++) {
            entry = new WorkEntry();
            entry.readFromNBT(entryList.getCompoundTagAt(i));
            add(entry);
        }
        priorityType = WorkPriorityType.values()[tag.getInteger("priorityType")];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        NBTTagList entryList = new NBTTagList();
        for (WorkEntry entry : points) {
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
        return points;
    }

    public boolean addWorkPosition(World world, BlockPosition position) {
        if (position != null && size() < 8 && world.getTileEntity(position.x, position.y, position.z) instanceof IWorkSite) {
            add(new WorkEntry(position, world.provider.dimensionId, 0));
            return true;
        }
        return false;//return true if successfully added
    }

    @Override
    public String toString() {
        return "Work Orders size: " + size() + " of type: " + priorityType;
    }

    public static WorkOrder getWorkOrder(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemWorkOrder) {
            WorkOrder order = new WorkOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.readFromNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public void write(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemWorkOrder) {
            stack.setTagInfo("orders", writeToNBT(new NBTTagCompound()));
        }
    }

    public static final class WorkEntry {

        private BlockPosition position = new BlockPosition();
        int dimension;
        private int workLength;

        private WorkEntry() {
        }//nbt constructor

        public WorkEntry(BlockPosition position, int dimension, int workLength) {
            this.setPosition(position);
            this.dimension = dimension;
            this.setWorkLength(workLength);
        }

        public void readFromNBT(NBTTagCompound tag) {
            setPosition(new BlockPosition(tag.getCompoundTag("pos")));
            dimension = tag.getInteger("dim");
            setWorkLength(tag.getInteger("length"));
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setTag("pos", getPosition().writeToNBT(new NBTTagCompound()));
            tag.setInteger("dim", dimension);
            tag.setInteger("length", getWorkLength());
            return tag;
        }

        /**
         * @return the block
         */
        public Block getBlock() {
            return getPosition().get(dimension);
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

    public enum WorkPriorityType {
        PRIORITY_LIST{
            @Override
            public int getNextWorkIndex(int current, List<WorkEntry> orders, NpcBase npc){
                for (int i = 0; i < orders.size(); i++) {
                    BlockPosition pos = orders.get(i).getPosition();
                    TileEntity te = npc.worldObj.getTileEntity(pos.x, pos.y, pos.z);
                    if (te instanceof IWorkSite) {
                        IWorkSite site = (IWorkSite) te;
                        if (((IWorker)npc).canWorkAt(site.getWorkType()) && site.hasWork()) {
                            return i;
                        }
                    }
                }
                return 0;
            }
        },
        ROUTE,
        TIMED;

        public int getNextWorkIndex(int current, List<WorkEntry> orders, NpcBase npcBase){
            if(current+1>=orders.size()){
                return 0;
            }
            return current+1;
        }
    }

}
