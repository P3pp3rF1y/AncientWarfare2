package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.util.RenderTools;

/**
 * Simple checkbox w/ label;<br><br>
 * <p/>
 * Inner classes can override the onToggled() method instead of adding a mouse-event listener.<br>
 * <p/>
 * Or, you may add listeners.<br>
 * Either onToggled() or listeners will be called _after_ the internal state
 * has been toggled, so when querying toggled/checked state, you will get the NEW state.<br><br>
 * <p/>
 * 12x12 is the minimum supported size.<br>
 * 16x16 is the preferred size.<br>
 * 40x40 is the maximum supported size due to textures/rendering.  Functionality will
 * work at larger sizes, but it will not render properly
 *
 * @author Shadowmage
 */
public class Checkbox extends GuiElement {

    private boolean pressed = false;
    private boolean checked = false;
    protected String label;

    /**
     * @param label (optional -- use null for none)
     */
    public Checkbox(int topLeftX, int topLeftY, int width, int height, String label) {
        super(topLeftX, topLeftY, width, height);
        this.label = I18n.format(label);
        this.setTooltipIfFound(label);
        this.addNewListener(new Listener(Listener.MOUSE_UP) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (pressed && visible && enabled && isMouseOverElement(evt.mx, evt.my)) {
                    Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
                    checked = !checked;
                    onToggled();
                }
                pressed = false;
                return true;
            }
        });

        this.addNewListener(new Listener(Listener.MOUSE_DOWN) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
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
            int y = enabled ? isMouseOverElement(mouseX, mouseY) ? 200 : 160 : 120;
            int x = checked ? 40 : 0;
            RenderTools.renderQuarteredTexture(256, 256, x, y, 40, 40, renderX, renderY, width, height);

            if (label != null) {
                int v = (height - 8) / 2;
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(label, renderX + width + 2, renderY + v, 0xffffffff);
            }
        }
    }

    /**
     * Anonymous classes can override this for an easy-access toggled-listener for this element
     */
    public void onToggled() {

    }

    public void setChecked(boolean val) {
        this.checked = val;
    }

    public boolean checked() {
        return checked;
    }

}
