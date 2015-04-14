package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class BlockEngineeringStation extends BlockRotatableTile {

    protected BlockEngineeringStation() {
        super(Material.rock);
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        setIcon(RelativeSide.BOTTOM, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.TOP, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.FRONT, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.REAR, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.LEFT, "ancientwarfare:core/engineering_station_bottom");
        setIcon(RelativeSide.RIGHT, "ancientwarfare:core/engineering_station_bottom");
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (!world.isRemote) {
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_CRAFTING, x, y, z);
        }
        return true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEngineeringStation tile = (TileEngineeringStation) world.getTileEntity(x, y, z);
        if (tile != null) {
            InventoryTools.dropInventoryInWorld(world, tile.bookInventory, x, y, z);
            InventoryTools.dropInventoryInWorld(world, tile.extraSlots, x, y, z);
            InventoryTools.dropInventoryInWorld(world, tile.layoutMatrix, x, y, z);
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

}
