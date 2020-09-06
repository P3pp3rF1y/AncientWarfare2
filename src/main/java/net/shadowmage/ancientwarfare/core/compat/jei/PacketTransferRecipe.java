package net.shadowmage.ancientwarfare.core.compat.jei;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import net.shadowmage.ancientwarfare.core.container.ICraftingContainer;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResourceLocation;
import net.shadowmage.ancientwarfare.core.network.PacketBase;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import java.io.IOException;
import java.util.List;

public class PacketTransferRecipe extends PacketBase {
	private ICraftingRecipe recipe;

	public PacketTransferRecipe() {}

	public PacketTransferRecipe(ICraftingRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	protected void writeToStream(ByteBuf data) {
		PacketBuffer buffer = new PacketBuffer(data);
		buffer.writeString(recipe.getRegistryName().toString());
	}

	@Override
	protected void readFromStream(ByteBuf data) throws IOException {
		PacketBuffer buffer = new PacketBuffer(data);
		recipe = AWCraftingManager.getRecipe(RecipeResourceLocation.deserialize(buffer.readString(256)));
	}

	@Override
	protected void execute(EntityPlayer player) {
		if (player.openContainer instanceof ICraftingContainer) {
			ICraftingContainer craftingContainer = (ICraftingContainer) player.openContainer;

			if (craftingContainer.pushCraftingMatrixToInventories()) {
				IItemHandler handler = new CombinedInvWrapper(craftingContainer.getInventories());

				NonNullList<ItemStack> resources = AWCraftingManager.getRecipeInventoryMatch(recipe, handler);

				if (resources.isEmpty()) {
					return;
				}

				List<Slot> slots = craftingContainer.getCraftingMemoryContainer().getCraftingMatrixSlots();

				for (int x = 0; x < recipe.getRecipeWidth(); x++) {
					for (int y = 0; y < recipe.getRecipeHeight(); y++) {
						int slotIndex = y * 3 + x;
						int ingredientIndex = y * recipe.getRecipeWidth() + x;
						Slot slot = slots.get(slotIndex);
						slot.putStack(resources.get(ingredientIndex));
					}
				}
				craftingContainer.getCraftingMemoryContainer().setRecipe(recipe, true);
				craftingContainer.getCraftingMemoryContainer().setUpdatePending();
				craftingContainer.getCraftingMemoryContainer().updateClients();

				InventoryTools.removeItems(handler, resources);
			}
		}
	}
}
