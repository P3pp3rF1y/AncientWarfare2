package net.shadowmage.ancientwarfare.core.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.crafting.ICraftingRecipe;
import net.shadowmage.ancientwarfare.core.crafting.RecipeResourceLocation;
import net.shadowmage.ancientwarfare.core.crafting.wrappers.NoRecipeWrapper;
import net.shadowmage.ancientwarfare.core.inventory.SlotResearchCrafting;
import net.shadowmage.ancientwarfare.core.item.ItemResearchBook;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketGui;
import net.shadowmage.ancientwarfare.core.tile.CraftingRecipeMemory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ContainerCraftingRecipeMemory {
	private static final String RECIPE_TAG = "recipe";
	private static final String FORCE_SET_TAG = "forceSet";
	private final SlotResearchCrafting craftingSlot;
	private CraftingRecipeMemory craftingRecipeMemory;

	private boolean updatePending = false;
	private int selectedRecipeIndex = -1;
	private boolean recipeUpdateCooldown = false;
	private long cooldownTime = 0;

	private boolean isOpening = false;

	public List<Slot> getSlots() {
		return slots;
	}

	public List<Slot> getCraftingMatrixSlots() {
		return slots.subList(2, slots.size());
	}

	public List<ItemStack> getCraftingStacks() {
		List<ItemStack> ret = NonNullList.create();
		getCraftingMatrixSlots().forEach(slot -> ret.add(slot.getStack()));
		return ret;
	}

	public InventoryCrafting getCraftMatrix() {
		return craftingRecipeMemory.craftMatrix;
	}

	private List<Slot> slots = new ArrayList<>();

	private List<ICraftingRecipe> recipes = new ArrayList<>();
	private final World world;

	public ContainerCraftingRecipeMemory(CraftingRecipeMemory craftingRecipeMemory, EntityPlayer player) {
		this.craftingRecipeMemory = craftingRecipeMemory;
		this.world = player.world;

		InventoryCrafting inventory = craftingRecipeMemory.craftMatrix;

		craftingSlot = new SlotResearchCrafting(player, craftingRecipeMemory, inventory, craftingRecipeMemory.outputSlot, 0, 3 * 18 + 3 * 18 + 8 + 18, 18 + 8) {
			@Override
			public boolean canTakeStack(EntityPlayer player) {
				return canTakeStackFromOutput(player);
			}

			@Override
			public ItemStack onTake(EntityPlayer player, ItemStack stack) {
				OnTakeResult result = handleOnTake(player, stack);
				ItemStack ret = result.getStack();
				if (result.getResult() != EnumActionResult.SUCCESS) {
					ret = super.onTake(player, stack);
				}

				updateRecipes();
				updateSelectedRecipe();
				return ret;
			}
		};

		slots.add(craftingSlot);

		Slot slot = new SlotItemHandler(craftingRecipeMemory.bookSlot, 0, 8, 18 + 8) {
			@Override
			public boolean isItemValid(ItemStack par1ItemStack) {
				return ItemResearchBook.getResearcherName(par1ItemStack) != null;
			}

			@Override
			public void onSlotChanged() {
				super.onSlotChanged();

				updateRecipes();
				updateSelectedRecipe();
			}

		};
		slots.add(slot);

		for (int y1 = 0; y1 < 3; y1++) {
			int y2 = y1 * 18 + 8;
			for (int x1 = 0; x1 < 3; x1++) {
				int x2 = x1 * 18 + 8 + 3 * 18;
				int slotNum = y1 * 3 + x1;
				slot = new Slot(inventory, slotNum, x2, y2) {
					@Override
					public void onSlotChanged() {
						super.onSlotChanged();
						if (!isOpening) {
							updateRecipes();
							updateSelectedRecipe();
						}
					}
				};
				slots.add(slot);
			}
		}
	}

	protected boolean canTakeStackFromOutput(EntityPlayer player) {
		return false;
	}

	protected OnTakeResult handleOnTake(EntityPlayer player, ItemStack stack) {
		return new OnTakeResult(EnumActionResult.PASS, stack);
	}

	private void updateRecipes() {
		recipes = AWCraftingManager.findMatchingRecipes(craftingRecipeMemory.craftMatrix, world, craftingRecipeMemory.getCrafterName());
		recipes.sort(Comparator.comparing(r -> r.getRegistryName().toString()));
		updateSelectedIndex();
	}

	private void updateSelectedIndex() {
		if (recipes.stream().anyMatch(r -> r.getRegistryName().equals(craftingRecipeMemory.getRecipe().getRegistryName()))) {
			setSelectedRecipeIndex(recipes.indexOf(recipes.stream().filter(r -> r.getRegistryName().equals(craftingRecipeMemory.getRecipe().getRegistryName())).findFirst().orElseGet(null)));
		} else {
			setSelectedRecipeIndex(recipes.isEmpty() ? -1 : 0);
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
			data.setString(RECIPE_TAG, craftingRecipeMemory.getRecipe().getRegistryName().toString());
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
		if (tag.hasKey(RECIPE_TAG)) {
			setRecipe(AWCraftingManager.getRecipe(RecipeResourceLocation.deserialize(tag.getString(RECIPE_TAG))), tag.getBoolean(FORCE_SET_TAG));
			updateSelectedIndex();

			if (world.isRemote && tag.hasKey(FORCE_SET_TAG)) {
				recipeUpdateCooldown = true;
				cooldownTime = world.getWorldTime() + 20;
			} else {
				updateClients(tag.getString(RECIPE_TAG));
			}
		}
	}

	private void updateClients(String recipeRegistryName) {
		updateClients(recipeRegistryName, false);
	}

	private void updateClients(String recipeRegistryName, boolean cooldown) {
		if (!world.isRemote && isUpdatePending()) {
			NBTTagCompound data = new NBTTagCompound();
			data.setString(RECIPE_TAG, recipeRegistryName);
			if (cooldown) {
				data.setBoolean(FORCE_SET_TAG, true);
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

	public void setOpening(boolean opening) {
		isOpening = opening;
	}

	public class OnTakeResult {
		private final EnumActionResult result;
		private final ItemStack stack;

		public OnTakeResult(EnumActionResult result, ItemStack stack) {
			this.result = result;
			this.stack = stack;
		}

		public EnumActionResult getResult() {
			return result;
		}

		public ItemStack getStack() {
			return stack;
		}
	}

	@Nullable
	public String getCrafterName() {
		return craftingRecipeMemory.getCrafterName();
	}
}
