package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.collect.Multimap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemShield extends ItemBaseNPC {
	public static final UUID shieldID = UUID.fromString("CB3F55D3-564C-4F38-A497-9C13A33DB5CF");
	private final int armorValue;
	private final ToolMaterial material;

	public ItemShield(String name, ToolMaterial material, int durability) {

		super(name);
		this.setFull3D();
		this.maxStackSize = 1;
		this.armorValue = material.getHarvestLevel() * 2 + 1;
		this.setMaxDamage(durability);
		this.material = material;
		this.addPropertyOverride(new ResourceLocation("blocking"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				return entityIn != null && entityIn.isHandActive() && entityIn.getActiveItemStack() == stack ? 1.0F : 0.0F;

			}
		});
	}

	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack) {
		return false;
	}

	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BLOCK;
	}

	public int getArmorBonusValue() {
		return armorValue;
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
		//TODO perhaps figure out a different way shield could work now that there is actual off hand
		if (slot != EntityEquipmentSlot.MAINHAND) {
			return super.getAttributeModifiers(slot, stack);
		}

		Multimap map = super.getAttributeModifiers(slot, stack);
		map.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(shieldID, "ArmorToughness modifier", 0.5, 2));
		return map;
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase attacked, EntityLivingBase attacker) {
		stack.damageItem(2, attacker);
		return true;
	}

	public int getMaxItemUseDuration(ItemStack stack) {
		return 72000;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.setActiveHand(handIn);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}

	/*
	 * Return whether this item is repairable in an anvil.
	 */
	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
		ItemStack mat = material.getRepairItemStack();
		if (!mat.isEmpty() && OreDictionary.itemMatches(mat, repair, false))
			return true;
		return super.getIsRepairable(toRepair, repair);
	}

	@Override
	public boolean isShield(ItemStack stack, @Nullable EntityLivingBase entity) {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("guistrings.npc.shield"));
	}
}