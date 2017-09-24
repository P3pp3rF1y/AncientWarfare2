package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class BlockHeadquarters extends BlockTownHall {

    public BlockHeadquarters() {
        super("headquarters");
    }

/*
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister register) {
        icons[0] = register.registerIcon("ancientwarfare:npc/town_hall_bottom");
        icons[1] = register.registerIcon("ancientwarfare:npc/town_hall_top_hq");
        icons[2] = register.registerIcon("ancientwarfare:npc/town_hall_side_hq");
        icons[3] = register.registerIcon("ancientwarfare:npc/town_hall_side_hq");
        icons[4] = register.registerIcon("ancientwarfare:npc/town_hall_side_hq");
        icons[5] = register.registerIcon("ancientwarfare:npc/town_hall_side_hq");
    }
*/

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        drops.add(new ItemStack(Item.getItemFromBlock(AWNPCBlocks.townHall)));
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!player.world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            String currentOwnerName = ((TileTownHall) te).getOwnerName();
            if (!player.getName().equals(currentOwnerName)) {
                // different player to the owner has used the town hall
                //TODO ftbutils integration
//                if (!ModAccessors.FTBU.areFriends(player.getName(), currentOwnerName)) {
//                    // new player is NOT a friend, change this HQ back to a town hall block
                    world.setBlockState(pos, AWNPCBlocks.townHall.getDefaultState(), 3);
                    IBlockState newState = world.getBlockState(pos);
                    return newState.getBlock().onBlockActivated(world, pos, newState, player, hand, facing, hitX, hitY, hitZ);
//                }
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }
}
