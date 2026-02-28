package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.interfaces.IScrollableCallback;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

@SideOnly(Side.CLIENT)
public class CompositeScrolled extends Composite implements IScrollableCallback {

	protected Scrollbar scrollbar;
	private int currentTop = 0;

	public CompositeScrolled(GuiContainerBase gui, int topLeftX, int topLeftY, int width, int height) {
		super(gui, topLeftX, topLeftY, width, height);
		scrollbar = new Scrollbar(width - 12, 0, 12, height, this);
	}

	@Override
	public void onScrolled(int newTop) {
		currentTop = newTop;
		this.updateElementPositions();
	}

	@Override
	protected void updateElementPositions() {
		scrollbar.updateGuiPosition(renderX, renderY);
		for (GuiElement element : this.elements) {
			element.updateGuiPosition(renderX, renderY - currentTop);
		}
	}

	public void setAreaSize(int height) {
		this.scrollbar.setAreaSize(height);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (!visible) {
			return;
		}

		if (!isMouseOverElement(mouseX, mouseY)) {
			mouseX = Integer.MIN_VALUE;
			mouseY = Integer.MIN_VALUE;
		}
		Minecraft.getMinecraft().renderEngine.bindTexture(backgroundTextureLocation);
		//render background before setting viewport so that it is not cropped
		RenderTools.renderQuarteredTexture(256, 256, 0, 0, 256, 240, renderX, renderY, width, height);
		setViewport();
		for (GuiElement element : this.elements) {
			if (element.renderY > renderY + height || element.renderY + element.height < renderY) {
				continue;//manual frustrum culling of elements, on Y axis
			}
			if (element.renderX > renderX + width || element.renderX + element.width < renderX) {
				continue;//manual frustrum culling of elements, on X axis
			}
			element.render(mouseX, mouseY, partialTick);
		}
		popViewport();
		//render scrollbar after resetting viewport so that it is not cropped
		scrollbar.render(mouseX, mouseY, partialTick);
	}

	@Override
	public void postRender(int mouseX, int mouseY, float partialTick, long tick, ITooltipRenderer rend) {
		for (GuiElement element : this.elements) {
			element.postRender(mouseX, mouseY, partialTick, tick, rend);
		}
	}

	@Override
	protected void addDefaultListeners() {
		this.addNewListener(new Listener(Listener.ALL_EVENTS) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if ((evt.type & Listener.KEY_TYPES) != 0) {
					for (GuiElement element : elements) {
						element.handleKeyboardInput(evt);
					}
				} else if ((evt.type & Listener.MOUSE_TYPES) != 0) {
					if (isMouseOverElement(evt.mx, evt.my)) {
						boolean overElement = false;
						scrollbar.handleMouseInput(evt);//in case input was directly over scrollbar
						for (GuiElement element : elements) {
							if (element.isMouseOverElement(evt.mx, evt.my)) {
								overElement = evt.mw == 0 || element.scrollInput;
							}
							element.handleMouseInput(evt);
						}
						if (!overElement && !scrollbar.isMouseOverElement(evt.mx, evt.my) && evt.mw != 0) {
							scrollbar.handleMouseScroll(evt);
						}
					} else {
						if (evt.type == Listener.MOUSE_UP) {
							for (GuiElement element : elements) {
								element.handleMouseInput(evt);
								element.setSelected(false);
							}
						}
						//handle mouse leaving window, cancel scrollbar interaction
						scrollbar.dragging = false;
						scrollbar.pressed = false;
					}
				}
				return true;
			}
		});
	}

	@Override
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		int h = scrollbar.totalAreaSize;
		this.elements.remove(scrollbar);
		scrollbar = new Scrollbar(width - 12, 0, 12, height, this);
		scrollbar.setAreaSize(h);
	}

}
