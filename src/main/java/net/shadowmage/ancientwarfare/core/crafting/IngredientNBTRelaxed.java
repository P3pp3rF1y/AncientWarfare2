package net.shadowmage.ancientwarfare.core.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public class IngredientNBTRelaxed extends Ingredient {
	private final ItemStack stack;

	protected IngredientNBTRelaxed(ItemStack stack) {
		super(stack);
		this.stack = stack;
	}

	@Override
	public boolean apply(@Nullable ItemStack input) {
		if (input == null)
			return false;
		//Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
		return this.stack.getItem() == input.getItem() && this.stack.getItemDamage() == input.getItemDamage() && nbtTagsMatch(input);
	}

	private boolean nbtTagsMatch(ItemStack input) {
		if (!stack.hasTagCompound()) {
			return true;
		}

		if (!input.hasTagCompound()) {
			return false;
		}
		//noinspection ConstantConditions
		NBTTagCompound original = input.getTagCompound().copy();
		NBTTagCompound merged = original.copy();

		//noinspection ConstantConditions
		merged.merge(stack.getTagCompound());

		//if all the NBT values of the ingredient were in the input stack's NBT the merged one will match the one before merge
		return original.equals(merged);
	}

	@Override
	public boolean isSimple() {
		return false;
	}
}
