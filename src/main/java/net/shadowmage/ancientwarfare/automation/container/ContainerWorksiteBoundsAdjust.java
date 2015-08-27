package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteUserBlocks;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.BlockPosition;

public class ContainerWorksiteBoundsAdjust extends ContainerBase {

    public final int x, y, z;
    public final BlockPosition min, max;
    public final IWorkSite worksite;

    public ContainerWorksiteBoundsAdjust(EntityPlayer player, int x, int y, int z) {
        super(player);
        this.x = x;
        this.y = y;
        this.z = z;
        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if(te instanceof IWorkSite) {
            worksite = (IWorkSite) te;
            min = worksite.getWorkBoundsMin().copy();
            max = worksite.getWorkBoundsMax().copy();
        }else
            throw new IllegalArgumentException("Couldn't find work site");
    }

    @Override
    public void sendInitData() {
        if (worksite instanceof TileWorksiteUserBlocks) {
            TileWorksiteUserBlocks twub = (TileWorksiteUserBlocks) worksite;
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
                worksite.setWorkBoundsMin(min);
                worksite.setWorkBoundsMax(max);
                worksite.onBoundsAdjusted();
                worksite.onPostBoundsAdjusted();
            }
            if (tag.hasKey("checkedMap") && worksite instanceof TileWorksiteUserBlocks) {
                TileWorksiteUserBlocks twub = (TileWorksiteUserBlocks) worksite;
                byte[] map = tag.getByteArray("checkedMap");
                twub.setTargetBlocks(map);
            }
            player.worldObj.markBlockForUpdate(x, y, z);
        }
    }

    public void onClose(boolean boundsAdjusted, boolean targetsAdjusted, byte[] checkedMap) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("guiClosed", true);
        if (boundsAdjusted) {
            tag.setTag("min", min.writeToNBT(new NBTTagCompound()));
            tag.setTag("max", max.writeToNBT(new NBTTagCompound()));
        }
        if (targetsAdjusted && worksite instanceof TileWorksiteUserBlocks) {
            tag.setByteArray("checkedMap", checkedMap);
        }
        sendDataToServer(tag);
    }
}
