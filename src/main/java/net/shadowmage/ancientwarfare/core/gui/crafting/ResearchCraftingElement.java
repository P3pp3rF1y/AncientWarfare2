package net.shadowmage.ancientwarfare.core.gui.crafting;

import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerCraftingRecipeMemory;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Composite;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.TexturedButton;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;

public class ResearchCraftingElement extends Composite {
	private static final int ELEMENT_WIDTH = 176;
	private static final int ELEMENT_HEIGHT = 64;

	private final ContainerCraftingRecipeMemory container;

	private final TexturedButton previous;
	private final TexturedButton next;

	public ResearchCraftingElement(GuiContainerBase gui, ContainerCraftingRecipeMemory container, int topLeftX, int topLeftY) {
		super(gui, topLeftX, topLeftY, ELEMENT_WIDTH, ELEMENT_HEIGHT);
		this.container = container;

		ItemSlot bookSlotIcon = new ItemSlot(8, 8, new ItemStack(AWCoreItems.RESEARCH_BOOK), gui);
		bookSlotIcon.setRenderTooltip(false).setHighlightOnMouseOver(false).setRenderSlotBackground(false).setRenderItemQuantity(false);
		addGuiElement(bookSlotIcon);

		previous = new TexturedButton(122, 26, TexturedButton.TextureSet.LEFT_ARROW) {
			@Override
			protected void onPressed() {
				container.previousRecipe();
			}
		};
		addGuiElement(previous);

		next = new TexturedButton(152, 26, TexturedButton.TextureSet.RIGHT_ARROW) {
			@Override
			protected void onPressed() {
				container.nextRecipe();
			}
		};
		addGuiElement(next);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		updateButtonVisibility();
		for (GuiElement element : this.elements) {
			element.render(mouseX, mouseY, partialTick);
		}
	}

	private void updateButtonVisibility() {
		boolean visible = container.getRecipes().size() > 1;
		previous.setVisible(visible);
		next.setVisible(visible);
	}
}
