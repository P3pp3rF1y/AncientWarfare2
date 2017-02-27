package net.shadowmage.ancientwarfare.npc.block;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.interop.ModAccessors;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class BlockHeadquarters extends BlockTownHall {

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
    
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        drops.add(new ItemStack(Item.getItemFromBlock(AWNPCBlockLoader.townHall)));
        return drops;
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int sideHit, float hitX, float hitY, float hitZ) {
        if (!player.worldObj.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            String currentOwnerName = ((TileTownHall) te).getOwnerName();
            if (!player.getCommandSenderName().equals(currentOwnerName)) {
                // different player to the owner has used the town hall
                if (!ModAccessors.FTBU.areFriends(player.getCommandSenderName(), currentOwnerName)) {
                    // new player is NOT a friend, change this HQ back to a town hall block
                    world.setBlock(x, y, z, AWNPCBlockLoader.townHall, 0, 3);
                    return world.getBlock(x, y, z).onBlockActivated(world, x, y, z, player, sideHit, hitX, hitY, hitZ);
                }
            }
        }
        return super.onBlockActivated(world, x, y, z, player, sideHit, hitX, hitY, hitZ);
    }
}
