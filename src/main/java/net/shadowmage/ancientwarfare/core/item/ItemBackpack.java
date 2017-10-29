package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.gui.GuiBackpack;
import net.shadowmage.ancientwarfare.core.inventory.InventoryBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

import javax.annotation.Nullable;
import java.util.List;

public class ItemBackpack extends ItemBaseCore {

    public ItemBackpack() {
        super("backpack");
        setMaxStackSize(1);
        setHasSubtypes(true);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(I18n.format("guistrings.core.backpack.size", ((stack.getItemDamage() + 1) * 9)));
        tooltip.add(I18n.format("guistrings.core.backpack.click_to_open"));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if(!world.isRemote)
            NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BACKPACK, 0, 0, 0);
        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        return super.getUnlocalizedName(par1ItemStack) + "." + par1ItemStack.getItemDamage();
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab != AWCoreBlockLoader.coreTab)
            return;

        for (int i = 0; i < 4; i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    public static InventoryBackpack getInventoryFor(ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemBackpack) {
            InventoryBackpack pack = new InventoryBackpack((stack.getItemDamage() + 1) * 9);
            //noinspection ConstantConditions
            if (stack.hasTagCompound() && stack.getTagCompound().hasKey("backpackItems")) {
                InventoryTools.readInventoryFromNBT(pack, stack.getTagCompound().getCompoundTag("backpackItems"));
            }
            return pack;
        }
        return null;
    }

    public static void writeBackpackToItem(InventoryBackpack pack, ItemStack stack) {
        if (!stack.isEmpty() && stack.getItem() instanceof ItemBackpack) {
            NBTTagCompound invTag = InventoryTools.writeInventoryToNBT(pack, new NBTTagCompound());
            stack.setTagInfo("backpackItems", invTag);
        }
    }

    @Override
    public void registerClient() {
        ModelLoaderHelper.registerItem(this, "core", false);

        NetworkHandler.registerGui(NetworkHandler.GUI_BACKPACK, GuiBackpack.class);
    }
}
