package net.shadowmage.ancientwarfare.core.gui;

import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;

public abstract class Listener {
    public static final int KEY_UP = 1;
    public static final int KEY_DOWN = 2;
    public static final int MOUSE_UP = 4;
    public static final int MOUSE_DOWN = 8;
    public static final int MOUSE_WHEEL = 16;
    public static final int MOUSE_MOVED = 32;

    public static final int KEY_TYPES = KEY_UP + KEY_DOWN;
    public static final int MOUSE_TYPES = MOUSE_UP + MOUSE_DOWN + MOUSE_WHEEL + MOUSE_MOVED;
    public static final int ALL_EVENTS = 0xffffffff;

    public final int type;

    public Listener(int type) {
        this.type = type;
    }

    public abstract boolean onEvent(GuiElement widget, ActivationEvent evt);

}
