package net.shadowmage.ancientwarfare.npc.block;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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
}
