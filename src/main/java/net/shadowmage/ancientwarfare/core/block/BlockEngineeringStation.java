package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;

public class BlockEngineeringStation extends BlockRotatableTile {

    protected BlockEngineeringStation() {
        super(Material.ROCK);
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
/*
        setIcon(RelativeSide.ANY_SIDE, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.BOTTOM, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.TOP, "ancientwarfare:core/engineering_station_top");
        setIcon(RelativeSide.FRONT, "ancientwarfare:core/engineering_station_front");
        setIcon(RelativeSide.REAR, "ancientwarfare:core/engineering_station_front");
        setIcon(RelativeSide.LEFT, "ancientwarfare:core/engineering_station_side");
        setIcon(RelativeSide.RIGHT, "ancientwarfare:core/engineering_station_side");
*/
        setHardness(2.f);
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEngineeringStation();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CRAFTING, pos);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEngineeringStation tile = (TileEngineeringStation) world.getTileEntity(pos);
        if (tile != null) {
            tile.onBlockBreak();
        }
        super.breakBlock(world, pos, state);
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
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

}
