package net.shadowmage.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.TileChunkLoaderSimple;
import net.shadowmage.ancientwarfare.core.block.BlockIconMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;

public class BlockChunkLoaderSimple extends Block {

    BlockIconMap iconMap = new BlockIconMap();

    protected BlockChunkLoaderSimple(String regName) {
        super(Material.rock);
        this.setBlockName(regName);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
        setHardness(2.f);
        String icon = "ancientwarfare:automation/" + regName + "_bottom";
        setIcon(0, 0, icon);
        setIcon(0, 1, icon);
        icon = "ancientwarfare:automation/" + regName + "_side";
        setIcon(0, 2, icon);
        setIcon(0, 3, icon);
        setIcon(0, 4, icon);
        setIcon(0, 5, icon);
    }

    public BlockChunkLoaderSimple setIcon(int meta, int side, String texName) {
        this.iconMap.setIconTexture(side, meta, texName);
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        iconMap.registerIcons(reg);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconMap.getIconFor(side, meta);
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileChunkLoaderSimple();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int wtf) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileChunkLoaderSimple) {
            ((TileChunkLoaderSimple) te).releaseTicket();
        }
        super.breakBlock(world, x, y, z, block, wtf);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player);
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int blockMeta) {
        super.onPostBlockPlaced(world, x, y, z, blockMeta);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileChunkLoaderSimple) {
                ((TileChunkLoaderSimple) te).setupInitialTicket();
            }
        }
    }

}
