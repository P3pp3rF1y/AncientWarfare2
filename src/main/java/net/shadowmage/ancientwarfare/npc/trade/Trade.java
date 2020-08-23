package net.shadowmage.ancientwarfare.npc.trade;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.IItemHandler;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/*
 * Created by Olivier on 12/05/2015.
 */
public abstract class Trade {
	protected NonNullList<ItemStack> input = NonNullList.withSize(size(), ItemStack.EMPTY);
	protected NonNullList<ItemStack> output = NonNullList.withSize(size(), ItemStack.EMPTY);

	public int size() {
		return 9;
	}

	public ItemStack getInputStack(int index) {
		return input.get(index);
	}

	public List<ItemStack> getInput() {
		return input;
	}

	public ItemStack getOutputStack(int index) {
		return output.get(index);
	}

	public List<ItemStack> getOutput() {
		return output;
	}

	public void setInputStack(int index, ItemStack stack) {
		input.set(index, stack);
	}

	public void setOutputStack(int index, ItemStack stack) {
		output.set(index, stack);
	}

	/*
	 * If items are all present in trade grid, delegate to #doTrade
	 */
	public boolean performTrade(EntityPlayer player, @Nullable IItemHandler storage) {
		NonNullList<ItemStack> list = NonNullList.create();
		for (ItemStack temp : input) {
			if (!temp.isEmpty()) {
				list.add(temp.copy());
			}
		}
		list = InventoryTools.compactStackList(list);
		for (ItemStack stack : list) {
			if (!hasEnoughOf(player, stack)) {
				return false;
			}
		}
		doTrade(player, storage);
		return true;
	}

	private boolean hasEnoughOf(EntityPlayer player, ItemStack stack) {
		return InventoryTools.getItemHandlerFrom(player, null).map(handler -> InventoryTools.getCountOf(handler, stack) >= stack.getCount())
				.orElse(false);
	}

	/*
	 * will remove necessary items from player inventory and add to storage
	 * and remove result from storage and merge into player inventory/drop on ground<br>
	 */
	protected void doTrade(EntityPlayer player, @Nullable IItemHandler storage) {
		InventoryTools.getItemHandlerFrom(player, null).ifPresent(playerInventory -> {
			for (ItemStack inputStack : input) {
				if (inputStack.isEmpty()) {
					continue;
				}
				ItemStack result = InventoryTools.removeItems(playerInventory, inputStack, inputStack.getCount());//remove from trade grid
				if (!result.isEmpty() && storage != null) {
					InventoryTools.mergeItemStack(storage, result);//merge into storage
				}
			}
			for (ItemStack outputStack : output) {
				if (outputStack.isEmpty()) {
					continue;
				}
				if (storage != null)
					outputStack = InventoryTools.removeItems(storage, outputStack, outputStack.getCount());//remove from storage
				else
					outputStack = outputStack.copy();
				outputStack = InventoryTools.mergeItemStack(playerInventory, outputStack);//merge into player inventory, drop any unused portion on next line
				if (!outputStack.isEmpty() && !player.world.isRemote) {//only drop into world if on server!
					InventoryHelper.spawnItemStack(player.world, player.posX, player.posY, player.posZ, outputStack);
				}
			}
		});
	}

	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList list = new NBTTagList();
		NBTTagCompound itemTag;

		for (int i = 0; i < input.size(); i++) {
			if (input.get(i).isEmpty()) {
				continue;
			}
			itemTag = input.get(i).writeToNBT(new NBTTagCompound());
			itemTag.setInteger("slot", i);
			list.appendTag(itemTag);
		}
		tag.setTag("inputItems", list);

		list = new NBTTagList();
		for (int i = 0; i < output.size(); i++) {
			if (output.get(i).isEmpty()) {
				continue;
			}
			itemTag = output.get(i).writeToNBT(new NBTTagCompound());
			itemTag.setInteger("slot", i);
			list.appendTag(itemTag);
		}
		tag.setTag("outputItems", list);
		return tag;
	}

	public void readFromNBT(NBTTagCompound tag) {
		NBTTagCompound itemTag;

		NBTTagList inputList = tag.getTagList("inputItems", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < inputList.tagCount(); i++) {
			itemTag = inputList.getCompoundTagAt(i);
			input.set(itemTag.getInteger("slot"), new ItemStack(itemTag));
		}

		NBTTagList outputList = tag.getTagList("outputItems", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < outputList.tagCount(); i++) {
			itemTag = outputList.getCompoundTagAt(i);
			output.set(itemTag.getInteger("slot"), new ItemStack(itemTag));
		}
	}

	public boolean isValid() {
		return input.stream().anyMatch(s -> !s.isEmpty()) && output.stream().anyMatch(s -> !s.isEmpty());
	}
}
