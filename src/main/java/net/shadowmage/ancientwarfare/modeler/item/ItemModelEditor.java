package net.shadowmage.ancientwarfare.modeler.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.modeler.AncientWarfareModeler;

import java.util.List;

public class ItemModelEditor extends Item {

    public static final CreativeTabs editorTab = new CreativeTabs("tabs.editor") {
        @Override
        @SideOnly(Side.CLIENT)
        public Item getTabIconItem() {
            return Items.stick;
        }
    };

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add(StatCollector.translateToLocal("guistrings.modeler.right_click_to_open"));
    }

    public ItemModelEditor(String localizationKey) {
        this.setUnlocalizedName(localizationKey);
        this.setCreativeTab(editorTab);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            AncientWarfareModeler.proxy.openGui(player);
        }
        return stack;
    }
}
