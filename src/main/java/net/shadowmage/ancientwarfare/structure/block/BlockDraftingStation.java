package net.shadowmage.ancientwarfare.structure.block;

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
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;

public class BlockDraftingStation extends BlockAWStructureBase {

    //private BlockIconMap iconMap = new BlockIconMap();

    public BlockDraftingStation() {
        super(Material.ROCK, "drafting_station");
        setHardness(2.f);
    }

//    public BlockDraftingStation setIcon(int side, String texName) {
//        this.iconMap.setIconTexture(side, 0, texName);
//        return this;
//    }

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

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void registerBlockIcons(IIconRegister reg) {
//        iconMap.registerIcons(reg);
//    }
//
//    @Override
//    @SideOnly(Side.CLIENT)
//    public IIcon getIcon(int side, int meta) {
//        return iconMap.getIconFor(side, meta);
//    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileDraftingStation();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_DRAFTING_STATION, pos);
        }
        return true;
    }
}
