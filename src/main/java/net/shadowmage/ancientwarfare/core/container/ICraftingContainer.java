package net.shadowmage.ancientwarfare.core.container;

import net.minecraftforge.items.IItemHandlerModifiable;

public interface ICraftingContainer {
	ContainerCraftingRecipeMemory getCraftingMemoryContainer();

	IItemHandlerModifiable[] getInventories();

	boolean pushCraftingMatrixToInventories();
}
