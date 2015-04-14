package net.shadowmage.ancientwarfare.automation.block;

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
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseInterface;
import net.shadowmage.ancientwarfare.core.block.BlockIconMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

public class BlockWarehouseInterface extends Block {

    private BlockIconMap iconMap = new BlockIconMap();

    public BlockWarehouseInterface(String regName) {
        super(Material.rock);
        this.setBlockName(regName);
        setCreativeTab(AWAutomationItemLoader.automationTab);
        setHardness(2.f);
    }

    public BlockWarehouseInterface setIcon(int meta, int side, String texName) {
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
        return new TileWarehouseInterface();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideHit, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IInventory) {
            InventoryTools.dropInventoryInWorld(world, (IInventory) te, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }
}
