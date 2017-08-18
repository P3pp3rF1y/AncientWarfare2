package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.List;

public class ItemBackpack extends Item {

    public ItemBackpack() {
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        list.add(I18n.format("guistrings.core.backpack.size", ((stack.getItemDamage() + 1) * 9)));
        list.add(I18n.format("guistrings.core.backpack.click_to_open"));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(!world.isRemote)
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BACKPACK, 0, 0, 0);
        return stack;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + par1ItemStack.getItemDamage();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List displayList) {
        for (int i = 0; i < 4; i++) {
            displayList.add(new ItemStack(item, 1, i));
        }
    }

    public static InventoryBackpack getInventoryFor(ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBackpack) {
            InventoryBackpack pack = new InventoryBackpack((stack.getItemDamage() + 1) * 9);
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("backpackItems")) {
                InventoryTools.readInventoryFromNBT(pack, stack.getTagCompound().getCompoundTag("backpackItems"));
            }
            return pack;
        }
        return null;
    }

    public static void writeBackpackToItem(InventoryBackpack pack, ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemBackpack) {
            NBTTagCompound invTag = InventoryTools.writeInventoryToNBT(pack, new NBTTagCompound());
            stack.setTagInfo("backpackItems", invTag);
        }
    }

}
