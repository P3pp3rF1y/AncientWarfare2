package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

@SideOnly(Side.CLIENT)
public class TexturedButton extends GuiElement {

	private final TextureSet textureSet;
	private boolean pressed = false;

	public TexturedButton(int topLeftX, int topLeftY, TextureSet textureSet) {
		super(topLeftX, topLeftY, textureSet.getWidth(), textureSet.getHeight());
		this.textureSet = textureSet;
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

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (visible) {
			Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);
			int textureSize = 256;
			boolean mouseOver = isMouseOverElement(mouseX, mouseY);
			Texture texture = enabled ? mouseOver ? textureSet.getHighlighted() : textureSet.getEnabled() : textureSet.getDisabled();
			RenderTools.renderQuarteredTexture(textureSize, textureSize, texture.getU(), texture.getV(), textureSet.getWidth(), textureSet.getHeight(), renderX, renderY, width, height);
		}
	}

	/*
	 * sub-classes may override this as an on-pressed callback
	 * method is called whenever the 'pressed' sound is played.
	 * uses built-in click listener for sound to trigger method
	 */
	protected void onPressed() {

	}

	/*
	 * Button-sensitive version of onPressed. 0 = LMB, 1 = RMB
	 * @param mButton
	 */
	protected void onPressed(int mButton) {
		onPressed(); // backwards compatibility
	}

	public enum TextureSet {
		LEFT_ARROW(Texture.LEFT_ARROW_DISABLED, Texture.LEFT_ARROW_ENABLED, Texture.LEFT_ARROW_HIGHLIGHTED, 10, 16), RIGHT_ARROW(Texture.RIGHT_ARROW_DISABLED, Texture.RIGHT_ARROW_ENABLED, Texture.RIGHT_ARROW_HIGHLIGHTED, 10, 16);

		public Texture getDisabled() {
			return disabled;
		}

		public Texture getEnabled() {
			return enabled;
		}

		public Texture getHighlighted() {
			return highlighted;
		}

		private final Texture disabled;
		private final Texture enabled;
		private final Texture highlighted;

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		private final int width;
		private final int height;

		TextureSet(Texture disabled, Texture enabled, Texture highlighted, int width, int height) {

			this.disabled = disabled;
			this.enabled = enabled;
			this.highlighted = highlighted;
			this.width = width;
			this.height = height;
		}
	}

	private enum Texture {
		LEFT_ARROW_DISABLED(170, 121), LEFT_ARROW_ENABLED(190, 121), LEFT_ARROW_HIGHLIGHTED(210, 121), RIGHT_ARROW_DISABLED(180, 121), RIGHT_ARROW_ENABLED(200, 121), RIGHT_ARROW_HIGHLIGHTED(220, 121);

		public int getU() {
			return u;
		}

		public int getV() {
			return v;
		}

		private final int u;
		private final int v;

		Texture(int u, int v) {
			this.u = u;
			this.v = v;
		}
	}

}
