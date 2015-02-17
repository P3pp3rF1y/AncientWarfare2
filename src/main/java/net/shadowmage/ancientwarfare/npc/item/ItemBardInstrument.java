package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class ItemBardInstrument extends Item {

    private final String[] instrumentNames = new String[]{"lute", "flute", "harp", "drum"};
    private final IIcon[] icons = new IIcon[instrumentNames.length];

    public ItemBardInstrument(String regName) {
        setUnlocalizedName(regName);
        setCreativeTab(AWNpcItemLoader.npcTab);
        setHasSubtypes(true);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List stackList) {
        for (int i = 0; i < instrumentNames.length; i++) {
            stackList.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + instrumentNames[par1ItemStack.getItemDamage()];
    }

    @Override
    public void registerIcons(IIconRegister par1IconRegister) {
        for (int i = 0; i < instrumentNames.length; i++) {
            icons[i] = par1IconRegister.registerIcon("ancientwarfare:npc/instrument_" + instrumentNames[i]);
        }
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        return icons[par1];
    }

}
