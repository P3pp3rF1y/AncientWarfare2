package net.shadowmage.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.core.item.ItemHammer;

public class BlockWindmillBlade extends Block {

    public BlockWindmillBlade(String regName) {
        super(Material.wood);
        this.setBlockName(regName);
        this.setHardness(1.f);
        this.setHarvestLevel("axe", 1);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int a, int b) {
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity.receiveClientEvent(a, b);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(net.minecraft.world.IBlockAccess access, int x, int y, int z, int side) {
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
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.glass.getIcon(side, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int meta) {
        super.onPostBlockPlaced(world, x, y, z, meta);
        TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(x, y, z);
        //te.blockPlaced();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int face) {
        TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(x, y, z);
        te.blockBroken(); // Need to be called here to find and invalidate the entire mill 
        super.breakBlock(world, x, y, z, block, face);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileWindmillBlade();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fx, float fy, float fz) {
        ItemStack held = player.getHeldItem();
        if (held != null && held.getItem() instanceof ItemHammer) {
            TileWindmillBlade te = (TileWindmillBlade) world.getTileEntity(x, y, z);
            te.blockPlaced();
            return true;
        }
        return false;
    }
}
