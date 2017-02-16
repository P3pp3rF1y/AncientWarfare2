package net.shadowmage.ancientwarfare.npc.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class BlockTownHall extends Block {

    public IIcon[] icons = new IIcon[6];
    
    public BlockTownHall() {
        super(Material.rock);
        this.setCreativeTab(AWNpcItemLoader.npcTab);
        setHardness(2.f);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        IInventory tile = (IInventory) world.getTileEntity(x, y, z);
        if (tile != null) {
            InventoryTools.dropInventoryInWorld(world, tile, x, y, z);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons[0] = register.registerIcon("ancientwarfare:npc/town_hall_bottom");
        icons[1] = register.registerIcon("ancientwarfare:npc/town_hall_top");
        icons[2] = register.registerIcon("ancientwarfare:npc/town_hall_side");
        icons[3] = register.registerIcon("ancientwarfare:npc/town_hall_side");
        icons[4] = register.registerIcon("ancientwarfare:npc/town_hall_side");
        icons[5] = register.registerIcon("ancientwarfare:npc/town_hall_side");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[side];
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return new TileTownHall();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideHit, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(x, y, z);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player);
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    @Override
    public void onNeighborBlockChange(World world, int posX, int posY, int posZ, Block block) {
        if (!world.isRemote) {
            if (dropBlockIfNotStable(world, posX, posY, posZ, block))
                return;
            TileTownHall tileTownHall = (TileTownHall) world.getTileEntity(posX, posY, posZ);
            if (world.isBlockIndirectlyGettingPowered(posX, posY, posZ))
                tileTownHall.alarmActive = true;
            else
                tileTownHall.alarmActive = false;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, int posX, int posY, int posZ, EntityLivingBase placer, ItemStack is) {
        if (!world.isRemote) {
            if (dropBlockIfNotStable(world, posX, posY, posZ, world.getBlock(posX, posY, posZ)))
                return;
            ModAccessors.FTBU.claimChunks(world, placer, posX, posY, posZ);
            
            if (placer instanceof EntityPlayer && ModAccessors.FTBU_LOADED) {
                if (!HeadquartersTracker.get(world).validateCurrentHq(placer.getCommandSenderName(), world)) {
                    world.setBlock(posX, posY, posZ, AWNPCBlockLoader.headquarters, 0, 3);
                    HeadquartersTracker.get(world).setNewHq(placer.getCommandSenderName(), world, posX, posY, posZ);
                    HeadquartersTracker.notifyHqNew(placer.getCommandSenderName(), posX, posZ);
                }
            }
            
        }
    }

    public boolean dropBlockIfNotStable(World world, int posX, int posY, int posZ, Block block) {
        if (!world.isSideSolid(posX - 1, posY, posZ, ForgeDirection.EAST,  false) &&
                !world.isSideSolid(posX + 1, posY, posZ, ForgeDirection.WEST,  false) &&
                !world.isSideSolid(posX, posY, posZ - 1, ForgeDirection.SOUTH, false) &&
                !world.isSideSolid(posX, posY, posZ + 1, ForgeDirection.NORTH, false) &&
                !world.isSideSolid(posX, posY + 1, posZ, ForgeDirection.DOWN, false) &&
                !World.doesBlockHaveSolidTopSurface(world, posX, posY - 1, posZ)) {
            this.breakBlock(world, posX, posY, posZ, block, world.getBlockMetadata(posX, posY, posZ));
            this.dropBlockAsItem(world, posX, posY, posZ, world.getBlockMetadata(posX, posY, posZ), 0);
            world.setBlock(posX, posY, posZ, Blocks.air, 0, 3);
            return true;
        }
        return false;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int posX, int posY, int posZ, boolean willHarvest) {
        if (!world.isRemote) {
            String teOwner = ((TileTownHall) world.getTileEntity(posX, posY, posZ)).getOwnerName();
            ModAccessors.FTBU.unclaimChunks(world, teOwner, posX, posY, posZ);
            ((TileTownHall) world.getTileEntity(posX, posY, posZ)).unloadChunks();
        }
        return super.removedByPlayer(world, player, posX, posY, posZ, willHarvest);
    }
}
