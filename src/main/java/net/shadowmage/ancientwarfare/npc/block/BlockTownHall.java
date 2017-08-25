package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.util.ArrayList;
import java.util.List;

public class BlockTownHall extends Block {

    public IIcon[] icons = new IIcon[6];
    
    public BlockTownHall() {
        super(Material.ROCK);
        this.setCreativeTab(AWNpcItemLoader.npcTab);
        setHardness(2.f);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        IInventory tile = (IInventory) world.getTileEntity(pos);
        if (tile instanceof TileTownHall) {
            ((TileTownHall) tile).isHq = false; // is this even necessary?
        }
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
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileTownHall();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof IInteractableTile && ((IInteractableTile) te).onBlockClicked(player, hand);
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
            ModAccessors.FTBU.addClaim(world, placer, posX, posY, posZ);
            if (placer instanceof EntityPlayer && ModAccessors.FTBU_LOADED) {
                if (!HeadquartersTracker.get(world).validateCurrentHq(placer.getName(), world)) {
                    world.setBlock(posX, posY, posZ, AWNPCBlockLoader.headquarters, 0, 3);
                    ((TileTownHall) world.getTileEntity(posX, posY, posZ)).isHq = true;
                    HeadquartersTracker.get(world).setNewHq(placer.getName(), world, posX, posY, posZ);
                    HeadquartersTracker.notifyHqNew(placer.getName(), posX, posZ);
                }
            }
        }
    }

    public boolean dropBlockIfNotStable(World world, int posX, int posY, int posZ, Block block) {
        if (!world.isSideSolid(posX - 1, posY, posZ, EnumFacing.EAST,  false) &&
                !world.isSideSolid(posX + 1, posY, posZ, EnumFacing.WEST,  false) &&
                !world.isSideSolid(posX, posY, posZ - 1, EnumFacing.SOUTH, false) &&
                !world.isSideSolid(posX, posY, posZ + 1, EnumFacing.NORTH, false) &&
                !world.isSideSolid(posX, posY + 1, posZ, EnumFacing.DOWN, false) &&
                !World.doesBlockHaveSolidTopSurface(world, posX, posY - 1, posZ)) {
            this.dropBlock(world, posX, posY, posZ, block);
            return true;
        }
        return false;
    }
    
    public void dropBlock(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        this.breakBlock(world, pos, state);
        this.dropBlockAsItem(world, pos, state, 0);
        world.setBlockToAir(pos);
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int posX, int posY, int posZ, boolean willHarvest) {
        if (!world.isRemote) {
            // check if the player who removed it is different
            String townHallOwner = ((TileTownHall) world.getTileEntity(posX, posY, posZ)).getOwnerName();
            if (!player.getName().equals(townHallOwner)) {
                // notify the owner that this new player captured their town hall
                Chunk thisChunk = world.getChunkFromBlockCoords(posX, posZ);
                String notificationTitle = "ftbu_aw2.notification.townhall_lost";
                TextComponentTranslation notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_destroyed.msg", player.getName());
                List<TextComponentTranslation> notificationTooltip = new ArrayList<TextComponentTranslation>();
                notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.chunk_name_and_position", ((TileTownHall) world.getTileEntity(posX, posY, posZ)).name, thisChunk.xPosition, thisChunk.zPosition));
                notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.click_to_remove"));
                ModAccessors.FTBU.notifyPlayer(EnumChatFormatting.RED, townHallOwner, notificationTitle, notificationMsg, notificationTooltip);
                ((TileTownHall) world.getTileEntity(posX, posY, posZ)).unloadChunks();
            }
        }
        return super.removedByPlayer(world, player, posX, posY, posZ, willHarvest);
    }
}
