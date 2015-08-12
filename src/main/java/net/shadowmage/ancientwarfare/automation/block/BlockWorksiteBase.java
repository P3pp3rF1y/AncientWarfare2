package net.shadowmage.ancientwarfare.automation.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.scoreboard.Team;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.common.util.RotationHelper;
import net.shadowmage.ancientwarfare.automation.item.AWAutomationItemLoader;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableBlock;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.IRotatableTile;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RelativeSide;
import net.shadowmage.ancientwarfare.core.block.BlockRotationHandler.RotationType;
import net.shadowmage.ancientwarfare.core.block.IconRotationMap;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interfaces.IOwnable;
import net.shadowmage.ancientwarfare.core.interfaces.IWorkSite;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.lang.reflect.Constructor;

public class BlockWorksiteBase extends Block implements IRotatableBlock {

    IconRotationMap iconMap = new IconRotationMap();
    public int maxWorkSize = 16;
    public int maxWorkSizeVertical = 1;
    private Constructor<? extends TileEntity> tile;

    public BlockWorksiteBase(String regName) {
        super(Material.rock);
        this.setCreativeTab(AWAutomationItemLoader.automationTab);
        this.setBlockName(regName);
        setHardness(2.f);
    }

    public BlockWorksiteBase setIcon(RelativeSide relativeSide, String texName) {
        this.iconMap.setIcon(this, relativeSide, texName);
        return this;
    }

    public BlockWorksiteBase setWorkSize(int size) {
        this.maxWorkSize = size;
        return this;
    }

    public BlockWorksiteBase setWorkVerticalSize(int size) {
        this.maxWorkSizeVertical = size;
        return this;
    }

    public BlockWorksiteBase setTileEntity(Class<? extends TileEntity> clzz) {
        try {
            tile = clzz.getConstructor();
        } catch (Exception e) {
        }
        return this;
    }

    /**
     * made into a generic method so that farm blocks are easier to setup
     * returned tiles must implement IWorksite (for team reference) and IInteractableTile (for interaction callback) if they wish to receive onBlockActivated calls<br>
     * returned tiles must implement IBoundedTile if they want workbounds set from ItemBlockWorksite<br>
     * returned tiles must implement IOwnable if they want owner-name set from ItemBlockWorksite<br>
     */
    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        try {
            return tile.newInstance();
        } catch (Exception e) {
        }
        return null;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return tile != null;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_) {
        iconMap.registerIcons(p_149651_1_);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return iconMap.getIcon(this, meta, side);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IRotatableTile) {
            return getIcon(side, ((IRotatableTile) te).getPrimaryFacing().ordinal());
        }
        return super.getIcon(world, x, y, z, side);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IInteractableTile) {
            boolean canClick = false;
            if(te instanceof IOwnable && player.getCommandSenderName().equals(((IOwnable) te).getOwnerName())){
                canClick = true;
            }else if(te instanceof IWorkSite) {
                Team t1 = ((IWorkSite) te).getTeam();
                if(player.getTeam() == t1)
                    canClick = true;
            }
            if (canClick) {
                return ((IInteractableTile) te).onBlockClicked(player);
            }
        }
        return false;
    }

    @Override
    public final RotationType getRotationType() {
        return RotationType.FOUR_WAY;
    }

    @Override
    public boolean invertFacing() {
        return true;
    }

    @Override
    public final boolean rotateBlock(World worldObj, int x, int y, int z, ForgeDirection axis) {
        if (axis == ForgeDirection.DOWN || axis == ForgeDirection.UP) {
            TileEntity te = worldObj.getTileEntity(x, y, z);
            if (te instanceof IRotatableTile) {
                if(!worldObj.isRemote) {
                    ForgeDirection o = ((IRotatableTile) te).getPrimaryFacing().getRotation(axis);
                    ((IRotatableTile) te).setPrimaryFacing(o);//twb will send update packets / etc
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public final ForgeDirection[] getValidRotations(World worldObj, int x, int y, int z) {
        return new ForgeDirection[]{ForgeDirection.DOWN, ForgeDirection.UP};
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof IInventory) {
            InventoryTools.dropInventoryInWorld(world, (IInventory) te, x, y, z);
        }
        if (te instanceof IWorkSite) {
            ((IWorkSite) te).onBlockBroken();
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

}
