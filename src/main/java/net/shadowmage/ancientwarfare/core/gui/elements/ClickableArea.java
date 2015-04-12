package net.shadowmage.ancientwarfare.core.gui.elements;

import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;

/**
 * simple clickable area interface widget...may be stacked on top of other buttons/labels
 * in order to easier facilitate a common on-clicked functionality for an entire group/area
 * of widgets.
 *
 * @author Shadowmage
 */
public abstract class ClickableArea extends GuiElement {

    public ClickableArea(int topLeftX, int topLeftY, int width, int height) {
        super(topLeftX, topLeftY, width, height);
        Listener listener = new Listener(Listener.MOUSE_DOWN) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (ClickableArea.this.isMouseOverElement(evt.mx, evt.my)) {
                    onClicked();
                }
                return true;
            }
        };
        addNewListener(listener);
    }

    protected abstract void onClicked();

    @Override
    public void render(int mouseX, int mouseY, float partialTick) {
        //NOOP
    }

}
