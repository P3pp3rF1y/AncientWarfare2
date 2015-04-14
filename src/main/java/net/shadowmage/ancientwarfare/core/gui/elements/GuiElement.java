package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;

import java.util.ArrayList;
import java.util.List;

/**
 * base GUI Element class
 * subclasses should add their own actionListeners during constructor
 * to handle default implementation details such as playing sound on button
 * click, toggling states or changing highlighting.
 *
 * @author Shadowmage
 */
public abstract class GuiElement {

    private List<Listener> actionListeners = new ArrayList<Listener>();

    protected boolean mouseInterface;
    protected boolean keyboardInterface;

    protected boolean enabled;
    protected boolean visible;
    protected boolean selected;//isFocused -- for text-input lines / etc
    protected boolean scrollInput = false;//if should intercept scroll input, mostly used inside compositescrolled containers, really should only be true for number input widget

    protected Tooltip tooltip;
    protected long hoverStart;

    protected int topLeftX;
    protected int topLeftY;

    protected int renderX;
    protected int renderY;

    protected int width;
    protected int height;

    public static ResourceLocation backgroundTextureLocation = new ResourceLocation("ancientwarfare", "textures/gui/guiBackgroundLarge.png");
    public static ResourceLocation widgetTexture1 = new ResourceLocation("ancientwarfare", "textures/gui/guiButtons1.png");
    public static ResourceLocation widgetTexture2 = new ResourceLocation("ancientwarfare", "textures/gui/guiButtons2.png");

    public GuiElement(int topLeftX, int topLeftY) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
        this.enabled = true;
        this.visible = true;
    }

    public GuiElement(int topLeftX, int topLeftY, int width, int height) {
        this(topLeftX, topLeftY);
        this.width = width;
        this.height = height;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setScrollable(boolean handleScroll) {
        this.scrollInput = handleScroll;
    }

    /**
     * called to update the internal positioning of this element.
     * needs to be called anytime the parent gui layout is changed (resized / etc)
     */
    public void updateGuiPosition(int guiLeft, int guiTop) {
        renderX = topLeftX + guiLeft;
        renderY = topLeftY + guiTop;
    }

    public void setRenderPosition(int topLeftX, int topLeftY) {
        this.topLeftX = topLeftX;
        this.topLeftY = topLeftY;
    }

    /**
     * called from GUI to process mouse interface.
     * all functionality should be implemented via ActionListeners
     * even default functionality (e.g. play sound on click, toggle state, etc)
     */
    public final void handleMouseInput(ActivationEvent evt) {
        if (mouseInterface && visible && enabled && !actionListeners.isEmpty()) {
            for (Listener o : this.actionListeners) {
                //   bitwise check of types, if it returns !=0 at least 1 type bit was shared, so should execute
                if ((o.type & evt.type) != 0) {
                    if (!o.onEvent(this, evt)) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * called from GUI to process keyboard interface
     * all functionality should be implemented via ActionListeners
     * including default functionality.
     * <p/>
     * will only fire events if this element is currently selected.
     * selection is currently handled manually --
     * must call element.setSelected() and element.clearSelected()
     * currently multiple elements may be selected concurrently and all
     * will receive the keyboard input events
     */
    public final void handleKeyboardInput(ActivationEvent evt) {
        if (keyboardInterface && visible && enabled && !actionListeners.isEmpty()) {
            for (Listener o : this.actionListeners) {
                //   bitwise check of types, if it returns !=0 at least 1 type bit was shared, so should execute
                if ((o.type & evt.type) != 0) {
                    if (!o.onEvent(this, evt)) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * add a new event listener to this element
     * if the element is not set to receive those event types
     * --auto-flag the element to receive those events
     *
     * @param listener the new listener to add
     */
    public final void addNewListener(Listener listener) {
        this.actionListeners.add(listener);
        if ((listener.type & Listener.MOUSE_TYPES) != 0) {
            this.mouseInterface = true;
        }
        if ((listener.type & Listener.KEY_TYPES) != 0) {
            this.keyboardInterface = true;
        }
    }

    public final boolean isMouseOverElement(int mouseX, int mouseY) {
        return mouseX >= renderX && mouseX < renderX + width && mouseY >= renderY && mouseY < renderY + height;
    }

    public abstract void render(int mouseX, int mouseY, float partialTick);//called from gui to draw this element

    /**
     * checks for tooltip-rendering for this gui-element.
     */
    public void postRender(int mouseX, int mouseY, float partialTick, long tick, ITooltipRenderer rend) {
        if (tooltip == null) {
            return;
        }
        if (isMouseOverElement(mouseX, mouseY)) {
            if (hoverStart == -1) {
                hoverStart = tick;
            } else if (tick - hoverStart > 500)//0.5 seconds
            {
                rend.handleElementTooltipRender(tooltip);
            }
        } else {
            hoverStart = -1;
        }
    }

    public void setTooltip(Tooltip tip) {
        this.tooltip = tip;
    }

    public boolean selected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
