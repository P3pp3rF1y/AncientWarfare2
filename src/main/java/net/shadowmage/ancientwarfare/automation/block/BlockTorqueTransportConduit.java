package net.shadowmage.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitHeavy;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitLight;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileConduitMedium;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueSidedCell;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;

import java.util.List;

public class BlockTorqueTransportConduit extends BlockTorqueBase {

    protected BlockTorqueTransportConduit(String regName) {
        super(Material.rock);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
        this.setBlockName(regName);
        this.setLightOpacity(1);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        switch (metadata) {
            case 0:
                return new TileConduitLight();
            case 1:
                return new TileConduitMedium();
            case 2:
                return new TileConduitHeavy();
        }
        return new TileConduitLight();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List list) {
        list.add(new ItemStack(item, 1, 0));
        list.add(new ItemStack(item, 1, 1));
        list.add(new ItemStack(item, 1, 2));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
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
        return RotationType.SIX_WAY;
    }

    @Override
    public boolean invertFacing() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch (meta) {
            case 0: {
                return Blocks.planks.getIcon(side, 0);
            }
            case 1: {
                return Blocks.iron_block.getIcon(side, 0);
            }
            case 2: {
                //TODO change this to steel block icon...once I make a steel block...
                return Blocks.iron_block.getIcon(side, 0);
            }
        }
        return Blocks.iron_block.getIcon(side, 0);
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float min = 0.1875f, max = 0.8125f;
        setBlockBounds(min, 0, min, max, 1, max);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        setBlockBoundsBasedOnState(world, x, y, z);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float min = 0.1875f, max = 0.8125f;
        float x1 = min, y1 = min, z1 = min, x2 = max, y2 = max, z2 = max;
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileTorqueSidedCell) {
            TileTorqueSidedCell tile = (TileTorqueSidedCell) world.getTileEntity(x, y, z);
            boolean[] sides = tile.getConnections();
            if (sides[0]) {
                y1 = 0.f;
            }
            if (sides[1]) {
                y2 = 1.f;
            }
            if (sides[2]) {
                z1 = 0.f;
            }
            if (sides[3]) {
                z2 = 1.f;
            }
            if (sides[4]) {
                x1 = 0.f;
            }
            if (sides[5]) {
                x2 = 1.f;
            }
        }
        setBlockBounds(x1, y1, z1, x2, y2, z2);
    }

}
