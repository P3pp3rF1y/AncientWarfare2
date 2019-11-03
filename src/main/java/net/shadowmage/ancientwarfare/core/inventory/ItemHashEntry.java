package net.shadowmage.ancientwarfare.core.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/*
 * Lightweight wrapper for an item stack as a hashable object suitable for use as keys in maps.<br>
 * Uses item, item damage, and nbt-tag for hash-code.<br>
 * Ignores quantity.<br>
 * Immutable.
 *
 * @author Shadowmage
 */
public final class ItemHashEntry {
	private final NBTTagCompound itemTag;
	private ItemStack cacheStack = ItemStack.EMPTY;
	private String cachedNameAndTooltip = "";

	/*
	 * @param item MUST NOT BE NULL
	 */
	public ItemHashEntry(ItemStack item) {
		ItemStack copy = item.copy();
		copy.setCount(1);
		itemTag = copy.writeToNBT(new NBTTagCompound());
	}

	@Override
	public int hashCode() {
		return itemTag.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		//noinspection SimplifiableIfStatement
		if (!(obj instanceof ItemHashEntry)) {
			return false;
		}
		return itemTag.equals(((ItemHashEntry) obj).itemTag);
	}

	public ItemStack getItemStack() {
		if (cacheStack.isEmpty()) {
			cacheStack = new ItemStack(itemTag);
		}
		return cacheStack;
	}

	@SideOnly(Side.CLIENT)
	public String getNameAndTooltip() {
		if (cachedNameAndTooltip.isEmpty()) {
			String stackText = cacheStack.getDisplayName().toLowerCase() + " ";
			stackText += String.join(" ", cacheStack.getTooltip(Minecraft.getMinecraft().player, ITooltipFlag.TooltipFlags.NORMAL)).toLowerCase();

			cachedNameAndTooltip = stackText;
		}

		return cachedNameAndTooltip;
	}

	public NBTTagCompound writeToNBT() {
		return itemTag.copy();
	}

	public static ItemHashEntry readFromNBT(NBTTagCompound tag) {
		return new ItemHashEntry(new ItemStack(tag));
	}
}
