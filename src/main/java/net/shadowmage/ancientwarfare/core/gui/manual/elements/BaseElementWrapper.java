package net.shadowmage.ancientwarfare.core.gui.manual.elements;

import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.manual.GuiManual;
import net.shadowmage.ancientwarfare.core.manual.IContentElement;

public abstract class BaseElementWrapper<T extends IContentElement> extends GuiElement {
	private final GuiManual gui;
	private T element;

	public BaseElementWrapper(GuiManual gui, int topLeftX, int topLeftY, int width, int height, T element) {
		super(topLeftX, topLeftY, width, height);
		this.gui = gui;
		this.element = element;
	}

	protected T getElement() {
		return element;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {

	}

	public int getTopLeftY() {
		return topLeftY;
	}

	public int getHeight() {
		return height;
	}

	public boolean shouldKeepWithNext() {
		return false;
	}

	protected GuiManual getGui() {
		return gui;
	}
}
