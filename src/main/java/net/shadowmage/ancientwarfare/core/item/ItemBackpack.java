package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.interfaces.IItemClickable;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.util.List;

public class ItemBackpack extends Item implements IItemClickable {

    public ItemBackpack(String regName) {
        this.setUnlocalizedName(regName);
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
        this.setTextureName("ancientwarfare:core/backpack");
        this.setMaxStackSize(1);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void addInformation(ItemStack stack, EntityPlayer par2EntityPlayer, List list, boolean par4) {
        list.add(StatCollector.translateToLocalFormatted("guistrings.core.backpack.size", ((stack.getItemDamage() + 1) * 9)));
        list.add(StatCollector.translateToLocal("guistrings.core.backpack.click_to_open"));
    }

    @Override
    public boolean onRightClickClient(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public void onRightClick(EntityPlayer player, ItemStack stack) {
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BACKPACK, 0, 0, 0);
    }

    @Override
    public boolean onLeftClickClient(EntityPlayer player, ItemStack stack) {
        //noop
        return false;
    }

    @Override
    public void onLeftClick(EntityPlayer player, ItemStack stack) {
        //noop
    }

    @Override
    public boolean cancelRightClick(EntityPlayer player, ItemStack stack) {
        return true;
    }

    @Override
    public boolean cancelLeftClick(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean getShareTag() {
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + par1ItemStack.getItemDamage();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List displayList) {
        for (int i = 0; i < 4; i++) {
            displayList.add(new ItemStack(this, 1, i));
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
