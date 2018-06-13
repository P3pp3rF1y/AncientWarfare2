package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.interfaces.ITabCallback;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

/*
 * tab element for use by CompositeTabbed.  Only has minimal self function
 *
 * @author Shadowmage
 */
@SideOnly(Side.CLIENT)
public class Tab extends GuiElement {

	ITabCallback parent;
	String label;
	boolean top;

	public Tab(int topLeftX, int topLeftY, boolean top, String label, ITabCallback parentCaller) {
		super(topLeftX, topLeftY);
		this.width = Minecraft.getMinecraft().fontRenderer.getStringWidth(label) + 6;
		this.label = label;
		this.setTooltipIfFound(label);
		this.height = 14;
		this.parent = parentCaller;
		this.top = top;
		this.addNewListener(new Listener(Listener.MOUSE_UP) {
			@Override
			public boolean onEvent(GuiElement widget, ActivationEvent evt) {
				if (visible && enabled && !selected() && isMouseOverElement(evt.mx, evt.my)) {
					setSelected(true);
					Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
					if (parent != null) {
						parent.onTabSelected(Tab.this);
					}
				}
				return true;
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		if (visible) {
			int y = 162;
			if (selected) {
				y = 138;
			}
			if (!top) {
				y += 48;
			}
			Minecraft.getMinecraft().renderEngine.bindTexture(widgetTexture1);
			RenderTools.renderQuarteredTexture(256, 256, 152, y, 104, 24, renderX, renderY, width, 16);
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(label, renderX + 3, renderY + 4, 0xffffffff);
		}
	}

}
