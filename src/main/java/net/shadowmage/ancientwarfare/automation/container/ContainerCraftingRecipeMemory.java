package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.automation.tile.CraftingRecipeMemory;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResourceLocation;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.NoRecipeWrapper;
import net.shadowmage.ancientwarfare.core.inventory.SlotResearchCrafting;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;

import java.util.ArrayList;
import java.util.List;

public class ContainerCraftingRecipeMemory {
	private final SlotResearchCrafting craftingSlot;
	private CraftingRecipeMemory craftingRecipeMemory;

	private boolean updatePending = false;
	private int selectedRecipeIndex = -1;
	private boolean recipeUpdateCooldown = false;
	private long cooldownTime = 0;

	public List<Slot> getSlots() {
		return slots;
	}

	public List<Slot> getCraftingMatrixSlots() {
		return slots.subList(2, slots.size());
	}

	public List<Slot> slots = new ArrayList<>();

	private List<ICraftingRecipe> recipes = new ArrayList<>();
	private final World world;

	public ContainerCraftingRecipeMemory(CraftingRecipeMemory craftingRecipeMemory, EntityPlayer player) {
		this.craftingRecipeMemory = craftingRecipeMemory;
		this.world = player.world;

		InventoryCrafting inventory = craftingRecipeMemory.craftMatrix;

		craftingSlot = new SlotResearchCrafting(player, craftingRecipeMemory.getCrafterName(), inventory, craftingRecipeMemory.outputSlot, 0,
				3 * 18 + 3 * 18 + 8 + 18, 18 + 8) {
			@Override
			public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
				return false;
			}
		};

		slots.add(craftingSlot);

		Slot slot = new SlotItemHandler(craftingRecipeMemory.bookSlot, 0, 8, 18 + 8) {
			@Override
			public boolean isItemValid(ItemStack par1ItemStack) {
				return ItemResearchBook.getResearcherName(par1ItemStack) != null;
			}
		};
		slots.add(slot);

		int x2, y2, slotNum;
		for (int y1 = 0; y1 < 3; y1++) {
			y2 = y1 * 18 + 8;
			for (int x1 = 0; x1 < 3; x1++) {
				x2 = x1 * 18 + 8 + 3 * 18;
				slotNum = y1 * 3 + x1;
				slot = new Slot(inventory, slotNum, x2, y2) {
					@Override
					public void onSlotChanged() {
						super.onSlotChanged();
						updateRecipes();
						updateSelectedRecipe();
					}
				};
				slots.add(slot);
			}
		}
	}

	private void updateRecipes() {
		recipes = AWCraftingManager.findMatchinRecipes(craftingRecipeMemory.craftMatrix, world, craftingRecipeMemory.getCrafterName());
		updateSelectedIndex();
	}

	private void updateSelectedIndex() {
		if (recipes.stream().anyMatch(r -> r.getRegistryName().equals(craftingRecipeMemory.getRecipe().getRegistryName()))) {
			setSelectedRecipeIndex(recipes.indexOf(
					recipes.stream().filter(r -> r.getRegistryName().equals(craftingRecipeMemory.getRecipe().getRegistryName())).findFirst().orElseGet(null)));
		} else {
			setSelectedRecipeIndex(recipes.size() == 0 ? -1 : 0);
		}
	}

	public void nextRecipe() {
		if (recipes.size() > 1) {
			setSelectedRecipeIndex(selectedRecipeIndex + 1 < recipes.size() ? selectedRecipeIndex + 1 : 0);
			updateSelectedRecipe();
			updateServer();
		}
	}

	public void previousRecipe() {
		if (recipes.size() > 1) {
			setSelectedRecipeIndex(selectedRecipeIndex - 1 >= 0 ? selectedRecipeIndex - 1 : recipes.size() - 1);
			updateSelectedRecipe();
			updateServer();
		}
	}

	private void setSelectedRecipeIndex(int newIndex) {
		if (selectedRecipeIndex != newIndex) {
			updatePending = true;
		}
		selectedRecipeIndex = newIndex;
	}

	private boolean isUpdatePending() {
		return updatePending;
	}

	private void updateSelectedRecipe() {
		if (isInRecipeUpdateCooldown()) {
			return;
		}

		if (selectedRecipeIndex == -1) {
			craftingRecipeMemory.setRecipe(NoRecipeWrapper.INSTANCE);
		} else {
			craftingRecipeMemory.setRecipe(recipes.get(selectedRecipeIndex));
		}
	}

	private void updateServer() {
		if (isUpdatePending()) {
			updatePending = false;
			NBTTagCompound data = new NBTTagCompound();
			data.setString("recipe", craftingRecipeMemory.getRecipe().getRegistryName().toString());
			NetworkHandler.sendToServer(new PacketGui(data));
		}
	}

	public List<ICraftingRecipe> getRecipes() {
		return recipes;
	}

	public void setRecipe(ICraftingRecipe recipe, boolean forceSet) {
		if (forceSet || (!isInRecipeUpdateCooldown() && recipes.stream().anyMatch(r -> r.getRegistryName().equals(recipe.getRegistryName())))) {
			craftingRecipeMemory.setRecipe(recipe);
			updateSelectedIndex();
		}
	}

	private boolean isInRecipeUpdateCooldown() {
		if (!world.isRemote) {
			return false;
		}

		if (recipeUpdateCooldown && world.getWorldTime() > cooldownTime) {
			recipeUpdateCooldown = false;
		}
		return recipeUpdateCooldown;
	}

	public void handleRecipeUpdate(NBTTagCompound tag) {
		if (tag.hasKey("recipe")) {
			setRecipe(AWCraftingManager.getRecipe(RecipeResourceLocation.deserialize(tag.getString("recipe"))), tag.getBoolean("forceSet"));
			updateSelectedIndex();

			if (world.isRemote && tag.hasKey("forceSet")) {
				recipeUpdateCooldown = true;
				if (recipeUpdateCooldown) {
					cooldownTime = world.getWorldTime() + 20;
				}
			} else {
				updateClients(tag.getString("recipe"));
			}
		}
	}

	private void updateClients(String recipeRegistryName) {
		updateClients(recipeRegistryName, false);
	}

	private void updateClients(String recipeRegistryName, boolean cooldown) {
		if (!world.isRemote && isUpdatePending()) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString("recipe", recipeRegistryName);
			if (cooldown) {
				data.setBoolean("forceSet", true);
			}
			NetworkHandler.sendToAllPlayers(new PacketGui(data));
		}
	}

	public void updateClients() {
		updateClients(craftingRecipeMemory.getRecipe().getRegistryName().toString(), true);
	}

	public void setUpdatePending() {
		updatePending = true;
	}
}
