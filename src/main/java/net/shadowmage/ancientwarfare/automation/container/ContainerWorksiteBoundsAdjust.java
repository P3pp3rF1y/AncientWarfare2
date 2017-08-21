package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteUserBlocks;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.interfaces.IBoundedSite;
import net.shadowmage.ancientwarfare.core.util.BlockTools;

public class ContainerWorksiteBoundsAdjust extends ContainerTileBase {

    public BlockPos min, max;

    public ContainerWorksiteBoundsAdjust(EntityPlayer player, BlockPos pos) {
        super(player, pos);
        if(tileEntity instanceof IBoundedSite) {
            min = getWorksite().getWorkBoundsMin();
            max = getWorksite().getWorkBoundsMax();
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
                BlockPos min = BlockPos.fromLong(tag.getLong("min"));
                BlockPos max = BlockPos.fromLong(tag.getLong("max"));
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
            BlockTools.notifyBlockUpdate(player.world, getPos());
        }
    }

    public void onClose(boolean boundsAdjusted, boolean targetsAdjusted, byte[] checkedMap) {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setBoolean("guiClosed", true);
        if (boundsAdjusted) {
            tag.setLong("min", min.toLong());
            tag.setLong("max", max.toLong());
        }
        if (targetsAdjusted && tileEntity instanceof TileWorksiteUserBlocks) {
            tag.setByteArray("checkedMap", checkedMap);
        }
        sendDataToServer(tag);
    }

    public BlockPos getPos() {
        return tileEntity.getPos();
    }

    public int getX() {
        return tileEntity.getPos().getX();
    }

    public int getY() {
        return tileEntity.getPos().getY();
    }

    public int getZ() {
        return tileEntity.getPos().getZ();
    }

    public IBoundedSite getWorksite() {
        return (IBoundedSite) tileEntity;
    }
}
