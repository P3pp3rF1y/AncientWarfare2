package net.shadowmage.ancientwarfare.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;

public class BlockEngineeringStation extends BlockRotatableTile {

    protected BlockEngineeringStation() {
        super(Material.rock);
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        setIcon(RelativeSide.ANY_SIDE, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.BOTTOM, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.TOP, "ancientwarfare:core/engineering_station_top");
        setIcon(RelativeSide.FRONT, "ancientwarfare:core/engineering_station_front");
        setIcon(RelativeSide.REAR, "ancientwarfare:core/engineering_station_front");
        setIcon(RelativeSide.LEFT, "ancientwarfare:core/engineering_station_side");
        setIcon(RelativeSide.RIGHT, "ancientwarfare:core/engineering_station_side");
        setHardness(2.f);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileEngineeringStation();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CRAFTING, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEngineeringStation tile = (TileEngineeringStation) world.getTileEntity(x, y, z);
        if (tile != null) {
            tile.onBlockBreak();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isNormalCube() {
        return false;
    }

}
