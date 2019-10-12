package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.ModelLoaderHelper;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

import javax.annotation.Nullable;
import java.util.List;

public class ItemMacuahuitl extends ItemSword implements IClientRegister {

	public ItemMacuahuitl(ToolMaterial material, String registryName) {
		super(material);
		setUnlocalizedName(registryName);
		setRegistryName(new ResourceLocation(AncientWarfareNPC.MOD_ID, registryName));
		setCreativeTab(AncientWarfareNPC.TAB);
		this.setMaxDamage(material.getMaxUses() - 100);
		AncientWarfareNPC.proxy.addClientRegister(this);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return TextFormatting.DARK_PURPLE + super.getItemStackDisplayName(stack) + TextFormatting.RESET;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ModelLoaderHelper.registerItem(this, "npc");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("item.macuahuitl.tooltip"));
	}

	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		return repair.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN) ? true : super.getIsRepairable(toRepair, repair);
	}
}
