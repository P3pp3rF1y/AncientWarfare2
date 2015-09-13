package net.shadowmage.ancientwarfare.core.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class BlockResearchStation extends Block implements IRotatableBlock {

    BlockIconMap iconMap = new BlockIconMap();

    public BlockResearchStation() {
        super(Material.rock);
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        iconMap.setIconTexture(0, 0, "ancientwarfare:core/research_station_bottom");
        iconMap.setIconTexture(1, 0, "ancientwarfare:core/research_station_top");
        iconMap.setIconTexture(2, 0, "ancientwarfare:core/research_station_front");
        iconMap.setIconTexture(3, 0, "ancientwarfare:core/research_station_front");
        iconMap.setIconTexture(4, 0, "ancientwarfare:core/research_station_side");
        iconMap.setIconTexture(5, 0, "ancientwarfare:core/research_station_side");
        setHardness(2.f);
    }

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

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileResearchStation();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideHit, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile instanceof IInventory) {
            InventoryTools.dropInventoryInWorld(world, (IInventory)tile, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_) {
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

    @Override
    public RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

    @Override
    public Block setIcon(RelativeSide side, String texName) {
        return this;
    }

}
