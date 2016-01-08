package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteUserBlocks;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class ContainerWorksiteBoundsAdjust extends ContainerTileBase {

    public final BlockPosition min, max;

    public ContainerWorksiteBoundsAdjust(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);
        if(tileEntity instanceof IBoundedSite) {
            min = getWorksite().getWorkBoundsMin().copy();
            max = getWorksite().getWorkBoundsMax().copy();
        }else
            throw new IllegalArgumentException("Couldn't find work site");
    }

    @Override
    public void sendInitData() {
        if (tileEntity instanceof TileWorksiteUserBlocks) {
            TileWorksiteUserBlocks twub = (TileWorksiteUserBlocks) tileEntity;
            NBTTagCompound tag = new NBTTagCompound();
            tag.setByteArray("checkedMap", twub.getTargetMap());
            sendDataToGui(tag);
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound tag) {
        if (tag.hasKey("guiClosed")) {
            if (tag.hasKey("min") && tag.hasKey("max")) {
                BlockPosition min = new BlockPosition(tag.getCompoundTag("min"));
                BlockPosition max = new BlockPosition(tag.getCompoundTag("max"));
                getWorksite().setWorkBoundsMin(min);
                getWorksite().setWorkBoundsMax(max);
                getWorksite().onBoundsAdjusted();
                getWorksite().onPostBoundsAdjusted();
            }
            if (tag.hasKey("checkedMap") && tileEntity instanceof TileWorksiteUserBlocks) {
                TileWorksiteUserBlocks twub = (TileWorksiteUserBlocks) tileEntity;
                byte[] map = tag.getByteArray("checkedMap");
                twub.setTargetBlocks(map);
            }
            player.worldObj.markBlockForUpdate(getX(), getY(), getZ());
        }
    }

    public void onClose(boolean boundsAdjusted, boolean targetsAdjusted, byte[] checkedMap) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("guiClosed", true);
        if (boundsAdjusted) {
            tag.setTag("min", min.writeToNBT(new NBTTagCompound()));
            tag.setTag("max", max.writeToNBT(new NBTTagCompound()));
        }
        if (targetsAdjusted && tileEntity instanceof TileWorksiteUserBlocks) {
            tag.setByteArray("checkedMap", checkedMap);
        }
        sendDataToServer(tag);
    }

    public int getX() {
        return tileEntity.xCoord;
    }

    public int getY() {
        return tileEntity.yCoord;
    }

    public int getZ() {
        return tileEntity.zCoord;
    }

    public IBoundedSite getWorksite() {
        return (IBoundedSite) tileEntity;
    }
}
