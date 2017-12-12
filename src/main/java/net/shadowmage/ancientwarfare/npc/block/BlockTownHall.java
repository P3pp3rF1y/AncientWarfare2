package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interfaces.IInteractableTile;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class BlockTownHall extends BlockBaseNPC {

    public BlockTownHall() {
        this("town_hall");
    }

    protected BlockTownHall(String regName) {
        super(Material.ROCK, regName);
        setHardness(2.f);
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        IInventory tile = (IInventory) world.getTileEntity(pos);
        if (tile != null) {
            InventoryTools.dropInventoryInWorld(world, tile, pos);
        }
        super.breakBlock(world, pos, state);
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

    /*
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        if (!world.isRemote) {
            TileTownHall tileTownHall = (TileTownHall) world.getTileEntity(pos);
            if (world.isBlockIndirectlyGettingPowered(pos) > 0)
                tileTownHall.alarmActive = true;
            else
                tileTownHall.alarmActive = false;
        }
    }
}
