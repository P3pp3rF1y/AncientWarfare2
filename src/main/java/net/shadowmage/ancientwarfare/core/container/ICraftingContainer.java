package net.shadowmage.ancientwarfare.core.container;

import net.minecraftforge.items.IItemHandlerModifiable;
import net.shadowmage.ancientwarfare.automation.container.ContainerCraftingRecipeMemory;

public interface ICraftingContainer {
	ContainerCraftingRecipeMemory getCraftingMemoryContainer();

	IItemHandlerModifiable[] getInventories();

	boolean pushCraftingMatrixToInventories();
}
