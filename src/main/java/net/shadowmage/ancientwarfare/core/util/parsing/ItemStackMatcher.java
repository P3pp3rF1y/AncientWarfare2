package net.shadowmage.ancientwarfare.core.util.parsing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class ItemStackMatcher implements Predicate<ItemStack> {
	private final Item item;
	private final Predicate<Integer> metaMatches;
	@Nullable
	private final NBTTagCompound tagCompound;

	private ItemStackMatcher(Item item, Predicate<Integer> metaMatches, @Nullable NBTTagCompound tagCompound) {
		this.item = item;
		this.metaMatches = metaMatches;
		this.tagCompound = tagCompound;
	}

	@Override
	@SuppressWarnings("squid:S2259")
	public boolean test(ItemStack input) {
		//noinspection ConstantConditions
		return input.getItem() == item && metaMatches.test(input.getMetadata()) && (tagCompound == null && !input.hasTagCompound() || tagCompound.equals(input.getTagCompound()));
	}

	public static class Builder {
		private Item item;
		private int meta = -1;
		private NBTTagCompound tagCompound = null;

		public Builder(Item item) {
			this.item = item;
		}

		public Builder setMeta(int meta) {
			this.meta = meta;
			return this;
		}

		public Builder setTagCompound(@Nullable NBTTagCompound tagCompound) {
			this.tagCompound = tagCompound;
			return this;
		}

		public ItemStackMatcher build() {
			return new ItemStackMatcher(item, meta == -1 ? i -> true : i -> i == meta, tagCompound);
		}
	}

}
