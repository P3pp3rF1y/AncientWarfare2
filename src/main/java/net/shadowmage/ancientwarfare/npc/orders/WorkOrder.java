package net.shadowmage.ancientwarfare.npc.orders;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.interfaces.IWorker;
import net.shadowmage.ancientwarfare.core.util.OrderingList;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemWorkOrder;

import java.util.List;

public class WorkOrder extends OrderingList<WorkOrder.WorkEntry> implements INBTSerializable<NBTTagCompound> {
    public static final int MAX_SIZE = 8;
    private WorkPriorityType priorityType = WorkPriorityType.ROUTE;
    private boolean nightShift;

    public boolean isNightShift(){
        return nightShift;
    }

    public void toggleShift(){
        nightShift = !nightShift;
    }

    public WorkPriorityType getPriorityType() {
        return priorityType;
    }

    public List<WorkEntry> getEntries() {
        return points;
    }

    //return true if successfully added
    public boolean addWorkPosition(World world, BlockPos position) {
        if (position != null && size() < MAX_SIZE) {
            add(new WorkEntry(position, world.provider.getDimension(), 0));
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Work Orders size: " + size() + " of type: " + priorityType;
    }

    public static WorkOrder getWorkOrder(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemWorkOrder) {
            WorkOrder order = new WorkOrder();
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("orders")) {
                order.deserializeNBT(stack.getTagCompound().getCompoundTag("orders"));
            }
            return order;
        }
        return null;
    }

    public void write(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemWorkOrder) {
            stack.setTagInfo("orders", serializeNBT());
        }
    }

    public void togglePriority() {
        WorkPriorityType[] type = WorkPriorityType.values();
        priorityType = type[(priorityType.ordinal() + 1) % type.length];
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagList entryList = new NBTTagList();
        for (WorkEntry entry : points) {
            entryList.appendTag(entry.writeToNBT(new NBTTagCompound()));
        }
        tag.setTag("entryList", entryList);
        tag.setInteger("priorityType", priorityType.ordinal());
        tag.setBoolean("nightShift", nightShift);
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound tag) {
        clear();
        NBTTagList entryList = tag.getTagList("entryList", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < entryList.tagCount(); i++) {
            add(new WorkEntry(entryList.getCompoundTagAt(i)));
        }
        priorityType = WorkPriorityType.values()[tag.getInteger("priorityType")];
        nightShift = tag.getBoolean("nightShift");
    }

    public static final class WorkEntry {

        private BlockPos position;
        int dimension;
        private int workLength;

        private WorkEntry(NBTTagCompound tag) {
            readFromNBT(tag);
        }//nbt constructor

        public WorkEntry(BlockPos position, int dimension, int workLength) {
            this.setPosition(position);
            this.dimension = dimension;
            this.setWorkLength(workLength);
        }

        public void readFromNBT(NBTTagCompound tag) {
            setPosition(BlockPos.fromLong(tag.getLong("pos")));
            dimension = tag.getInteger("dim");
            setWorkLength(tag.getInteger("length"));
        }

        public NBTTagCompound writeToNBT(NBTTagCompound tag) {
            tag.setLong("pos", getPosition().toLong());
            tag.setInteger("dim", dimension);
            tag.setInteger("length", getWorkLength());
            return tag;
        }

        /**
         * @return the block
         */
        public Block getBlock() {
            //TODO likely needs fixing the world and may need the world passed in instead
            World world = AncientWarfareCore.proxy.getWorld(dimension);
            if(world==null)
                return null;
            return world.getBlockState(getPosition()).getBlock();
        }

        /**
         * @return the position
         */
        public BlockPos getPosition() {
            return position;
        }

        /**
         * @param position the position to set
         */
        public void setPosition(BlockPos position) {
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
        SITE_NEED{
            @Override
            public int getNextWorkIndex(int current, List<WorkEntry> orders, NpcBase npc){
                for (int i = 0; i < orders.size(); i++) {
                    BlockPos pos = orders.get(i).getPosition();
                    TileEntity te = npc.world.getTileEntity(pos);
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

        public boolean isTimed(){
            return this == TIMED;
        }
    }

}
