package net.shadowmage.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;

import java.util.List;

public class BlockFlywheelStorage extends Block {

    public BlockFlywheelStorage(String regName) {
        super(Material.rock);
        this.setBlockName(regName);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b) {
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity.receiveClientEvent(a, b);
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        super.onPostBlockPlaced(world, x, y, z, meta);
        TileFlywheelStorage te = (TileFlywheelStorage) world.getTileEntity(x, y, z);
        te.blockPlaced();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
        TileFlywheelStorage te = (TileFlywheelStorage) world.getTileEntity(x, y, z);
        super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
        te.blockBroken();//have to call post block-break so that the controller properly sees the block as gone
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileFlywheelStorage();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List list) {
        list.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
        list.add(new ItemStack(Item.getItemFromBlock(this), 1, 1));
        list.add(new ItemStack(Item.getItemFromBlock(this), 1, 2));
    }

}
