package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class BlockResearchStation extends BlockBaseCore implements IRotatableBlock {

/*
    BlockIconMap iconMap = new BlockIconMap();
*/

    public BlockResearchStation() {
        super(Material.ROCK, "research_station");
/*
        iconMap.setIconTexture(0, 0, "ancientwarfare:core/research_station_bottom");
        iconMap.setIconTexture(1, 0, "ancientwarfare:core/research_station_top");
        iconMap.setIconTexture(2, 0, "ancientwarfare:core/research_station_front");
        iconMap.setIconTexture(3, 0, "ancientwarfare:core/research_station_front");
        iconMap.setIconTexture(4, 0, "ancientwarfare:core/research_station_side");
        iconMap.setIconTexture(5, 0, "ancientwarfare:core/research_station_side");
*/
        setHardness(2.f);
    }

/*
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return iconMap.getIconFor(p_149691_1_, p_149691_2_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        iconMap.registerIcons(p_149651_1_);
    }
*/

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileResearchStation();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player, hand);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof IInventory) {
            InventoryTools.dropInventoryInWorld(world, (IInventory)tile, pos);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

/*
    @Override
    public Block setIcon(RelativeSide side, String texName) {
        return this;
    }
*/

}
