package net.shadowmage.ancientwarfare.core.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.shadowmage.ancientwarfare.core.gui.GuiBackpack;
import net.shadowmage.ancientwarfare.core.inventory.ItemHandlerBackpack;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;

import javax.annotation.Nonnull;
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
		if (!world.isRemote)
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_BACKPACK, 0, 0, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return super.getUnlocalizedName(par1ItemStack) + "." + par1ItemStack.getItemDamage();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}

		for (int i = 0; i < 4; i++) {
			items.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "core", false);

		NetworkHandler.registerGui(NetworkHandler.GUI_BACKPACK, GuiBackpack.class);
	}

	@Nullable
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		return new ICapabilityProvider() {
			@Override
			public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
				return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
			}

			@Nullable
			@Override
			public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				return (T) new ItemHandlerBackpack(stack);
			}
		};
	}
}
