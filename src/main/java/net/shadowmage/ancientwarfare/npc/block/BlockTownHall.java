package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;
import net.shadowmage.ancientwarfare.npc.item.AWNPCItemLoader;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import java.util.ArrayList;
import java.util.List;

public class BlockTownHall extends Block {

    public BlockTownHall() {
        this("town_hall");
    }

    protected BlockTownHall(String regName) {
        super(Material.ROCK);
        this.setCreativeTab(AWNPCItemLoader.npcTab);
        setHardness(2.f);
        this.setUnlocalizedName(regName);
        this.setRegistryName(new ResourceLocation(AncientWarfareNPC.modID, regName));
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        IInventory tile = (IInventory) world.getTileEntity(pos);
        if (tile instanceof TileTownHall) {
            ((TileTownHall) tile).isHq = false; // is this even necessary?
        }
        if (tile != null) {
            InventoryTools.dropInventoryInWorld(world, tile, pos);
        }
        super.breakBlock(world, pos, state);
    }

/*
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
*/

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

    /*
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote) {
            if (dropBlockIfNotStable(world, pos))
                return;
            TileTownHall tileTownHall = (TileTownHall) world.getTileEntity(pos);
            if (world.isBlockIndirectlyGettingPowered(pos) > 0)
                tileTownHall.alarmActive = true;
            else
                tileTownHall.alarmActive = false;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote) {
            if (dropBlockIfNotStable(world, pos))
                return;
            //TODO fix FTBUtils integration
            //ModAccessors.FTBU.addClaim(world, placer, pos);
            if (placer instanceof EntityPlayer && ModAccessors.FTBU_LOADED) {
                if (!HeadquartersTracker.get(world).validateCurrentHq(placer.getName(), world)) {
                    world.setBlockState(pos, AWNPCBlocks.headquarters.getDefaultState(), 3);
                    ((TileTownHall) world.getTileEntity(pos)).isHq = true;
                    HeadquartersTracker.get(world).setNewHq(placer.getName(), world, pos);
                    HeadquartersTracker.notifyHqNew(placer.getName(), pos);
                }
            }
        }
    }

    public boolean dropBlockIfNotStable(World world, BlockPos pos) {
        if (!world.isSideSolid(pos.offset(EnumFacing.WEST), EnumFacing.EAST,  false) &&
                !world.isSideSolid(pos.offset(EnumFacing.EAST), EnumFacing.WEST,  false) &&
                !world.isSideSolid(pos.offset(EnumFacing.NORTH), EnumFacing.SOUTH, false) &&
                !world.isSideSolid(pos.offset(EnumFacing.SOUTH), EnumFacing.NORTH, false) &&
                !world.isSideSolid(pos.offset(EnumFacing.UP), EnumFacing.DOWN, false) &&
                !world.getBlockState(pos.offset(EnumFacing.DOWN)).isSideSolid(world, pos.offset(EnumFacing.DOWN), EnumFacing.UP)) {
            this.dropBlock(world, pos);
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
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        if (!world.isRemote) {
            // check if the player who removed it is different
            String townHallOwner = ((TileTownHall) world.getTileEntity(pos)).getOwnerName();
            if (!player.getName().equals(townHallOwner)) {
                // notify the owner that this new player captured their town hall
                Chunk thisChunk = world.getChunkFromBlockCoords(pos);
                String notificationTitle = "ftbu_aw2.notification.townhall_lost";
                TextComponentTranslation notificationMsg = new TextComponentTranslation("ftbu_aw2.notification.townhall_destroyed.msg", player.getName());
                List<TextComponentTranslation> notificationTooltip = new ArrayList<>();
                notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.chunk_name_and_position", ((TileTownHall) world.getTileEntity(pos)).name, thisChunk.getPos().x, thisChunk.getPos().z));
                notificationTooltip.add(new TextComponentTranslation("ftbu_aw2.notification.click_to_remove"));
                //TODO ftbutils integration
                //ModAccessors.FTBU.notifyPlayer(TextFormatting.RED, townHallOwner, notificationTitle, notificationMsg, notificationTooltip);
                ((TileTownHall) world.getTileEntity(pos)).unloadChunks();
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }
}
