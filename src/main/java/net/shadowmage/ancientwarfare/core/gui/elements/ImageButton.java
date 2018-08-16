package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.SoundEvents;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.TextureData;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

import java.awt.*;

public abstract class ImageButton extends GuiElement {
	private final TextureData textureData;
	private final Color colorOutOfFocus;
	private final Color colorInFocus;
	private boolean pressed = false;

	public ImageButton(int topLeftX, int topLeftY, int width, int height, TextureData textureData, Color colorOutOfFocus, Color colorInFocus) {
		super(topLeftX, topLeftY, width, height);
		this.textureData = textureData;
		this.colorOutOfFocus = colorOutOfFocus;
		this.colorInFocus = colorInFocus;

		this.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, GuiContainerBase.ActivationEvent evt) {
				if (pressed && enabled && visible && isMouseOverElement(evt.mx, evt.my)) {
					Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					onPressed(evt.mButton);
				}
				pressed = false;
				return true;
			}
		});
		this.addNewListener(new Listener(Listener.MOUSE_DOWN) {
			@Override
			public boolean onEvent(GuiElement widget, GuiContainerBase.ActivationEvent evt) {
				if (enabled && visible && isMouseOverElement(evt.mx, evt.my)) {
					pressed = true;
				}
				return true;
			}
		});

	}

	protected abstract void onPressed(int mButton);

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (visible) {
			Minecraft.getMinecraft().renderEngine.bindTexture(textureData.getTexture());
			Color color = isMouseOverElement(mouseX, mouseY) ? colorInFocus : colorOutOfFocus;
			GlStateManager.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
			RenderTools.renderQuarteredTexture(textureData.getTextureWidth(), textureData.getTextureHeight(), textureData.getTextureU(), textureData.getTextureV(),
					textureData.getPartWidth(), textureData.getPartHeight(), renderX, renderY, width, height);
			GlStateManager.color(1, 1, 1, 1);
		}
	}
}
