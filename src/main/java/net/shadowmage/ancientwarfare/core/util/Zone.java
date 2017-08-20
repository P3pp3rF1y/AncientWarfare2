package net.shadowmage.ancientwarfare.core.util;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.interfaces.INBTSerialable;

public class Zone implements INBTSerialable{

    public BlockPos min, max;

    public Zone() {
        min = new BlockPos();
        max = new BlockPos();
    }

    public Zone(BlockPos p1, BlockPos p2)
    {
        min = BlockTools.getMin(p1, p2);
        max = BlockTools.getMax(p1, p2);
    }

    /**
     * does the input share any block position with this zone ?
     */
    public boolean crossWith(Zone z){
        if (max.x < z.min.x || max.y < z.min.y || max.z < z.min.z || min.x > z.max.x || min.y > z.max.y || min.z > z.max.z) {
            return false;
        }
        return true;
    }

    public boolean isPositionIn(int x, int y, int z)
    {
        if(x<min.x || y<min.y || z<min.z || x>max.x || z>max.z || y>max.y)
        {
            return false;
        }
        return true;
    }

    public boolean isPositionIn(BlockPos pos) {
        return isPositionIn(pos.x, pos.y, pos.z);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        min = new BlockPos(tag.getCompoundTag("min"));
        max = new BlockPos(tag.getCompoundTag("max"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag)
    {
        tag.setTag("min", min.writeToNBT(new NBTTagCompound()));
        tag.setTag("max", max.writeToNBT(new NBTTagCompound()));
        return tag;
    }

    public boolean equals(BlockPos min, BlockPos max) {
        return min.equals(this.min) && max.equals(this.max);
    }

    @Override
    public boolean equals(Object object){
        return object instanceof Zone && this.equals(((Zone) object).min, ((Zone) object).max);
    }

    @Override
    public int hashCode() {
        return 31 * min.hashCode() + max.hashCode();
    }

    @Override
    public String toString()
    {
        return String.format("From %s to %s", min, max);
    }
}
