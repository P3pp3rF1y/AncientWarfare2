package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteBoundsAdjust;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Rectangle;

public class GuiWorksiteBoundsAdjust extends GuiContainerBase<ContainerWorksiteBoundsAdjust> {

    private final boolean targetMode;

    private boolean boundsAdjusted = false, targetsAdjusted = false;
    private byte[] checkedMap = new byte[16 * 16];

    public GuiWorksiteBoundsAdjust(ContainerBase container) {
        super(container);
        this.shouldCloseOnVanillaKeys = true;
        targetMode = this.getContainer().getWorksite().userAdjustableBlocks();
    }

    private void setChecked(int x, int y, boolean checked) {
        if (targetMode) {
            checkedMap[y * 16 + x] = checked ? (byte) 1 : (byte) 0;
        }
    }

    private boolean isChecked(int x, int y) {
        return targetMode && checkedMap[y * 16 + x] == 1;
    }

    @Override
    public void initElements() {
        //read initial checked-map from container
    }

    @Override
    public void setupElements() {
        this.clearElements();
        Button b = new Button(48, 12, 40, 12, "guistrings.inventory.direction.north") {
            @Override
            protected void onPressed() {
                if (getContainer().max.z >= getContainer().getZ() && (getContainer().min.x > getContainer().getX() || getContainer().max.x < getContainer().getX())) {
                    getContainer().min = getContainer().min.moveForward(2, 1);
                    getContainer().max = getContainer().max.moveForward(2, 1);
                    boundsAdjusted = true;
                    refreshGui();
                }
            }
        };
        addGuiElement(b);

        b = new Button(48 + 40, 12, 40, 12, "guistrings.inventory.direction.south") {
            @Override
            protected void onPressed() {
                if (getContainer().min.z <= getContainer().getZ() && (getContainer().min.x > getContainer().getX() || getContainer().max.x < getContainer().getX())) {
                    getContainer().min = getContainer().min.moveForward(0, 1);
                    getContainer().max = getContainer().max.moveForward(0, 1);
                    boundsAdjusted = true;
                    refreshGui();
                }
            }
        };
        addGuiElement(b);

        b = new Button(48 + 80, 12, 40, 12, "guistrings.inventory.direction.west") {
            @Override
            protected void onPressed() {
                if (getContainer().max.x >= getContainer().getX() && (getContainer().min.z > getContainer().getZ() || getContainer().max.z < getContainer().getZ())) {
                    getContainer().min = getContainer().min.moveForward(1, 1);
                    getContainer().max = getContainer().max.moveForward(1, 1);
                    boundsAdjusted = true;
                    refreshGui();
                }
            }
        };
        addGuiElement(b);

        b = new Button(48 + 120, 12, 40, 12, "guistrings.inventory.direction.east") {
            @Override
            protected void onPressed() {
                if (getContainer().min.x <= getContainer().getX() && (getContainer().min.z > getContainer().getZ() || getContainer().max.z < getContainer().getZ())) {
                    getContainer().min = getContainer().min.moveForward(3, 1);
                    getContainer().max = getContainer().max.moveForward(3, 1);
                    boundsAdjusted = true;
                    refreshGui();
                }
            }
        };
        addGuiElement(b);

        b = new Button(48, 24, 40, 12, "XSIZE-") {
            @Override
            protected void onPressed() {
                if (getContainer().max.x <= getContainer().min.x) {
                    return;
                }
                if (getContainer().min.x < getContainer().getX()) {
                    getContainer().min = getContainer().min.moveForward(3, 1);
                    boundsAdjusted = true;
                    refreshGui();
                } else {
                    getContainer().max = getContainer().max.moveForward(1, 1);
                    boundsAdjusted = true;
                    refreshGui();
                }
            }
        };
        addGuiElement(b);

        b = new Button(48 + 40, 24, 40, 12, "XSIZE+") {
            @Override
            protected void onPressed() {
                int offset = getContainer().getWorksite().getBoundsMaxWidth() - getContainer().max.x + getContainer().min.x - 1;
                if (0 >= offset) {
                    return;
                }
                if(!isShiftKeyDown()){
                    offset = 1;
                }
                if (getContainer().min.x < getContainer().getX()) {
                    getContainer().min = getContainer().min.moveForward(1, offset);
                    boundsAdjusted = true;
                    refreshGui();
                } else {
                    getContainer().max = getContainer().max.moveForward(3, offset);
                    boundsAdjusted = true;
                    refreshGui();
                }
            }
        };
        addGuiElement(b);

        b = new Button(48 + 80, 24, 40, 12, "ZSIZE-") {
            @Override
            protected void onPressed() {
                if (getContainer().max.z <= getContainer().min.z) {
                    return;
                }
                if (getContainer().min.z < getContainer().getZ()) {
                    getContainer().min = getContainer().min.moveForward(0, 1);
                    boundsAdjusted = true;
                    refreshGui();
                } else {
                    getContainer().max = getContainer().max.moveForward(2, 1);
                    boundsAdjusted = true;
                    refreshGui();
                }
            }
        };
        addGuiElement(b);

        b = new Button(48 + 120, 24, 40, 12, "ZSIZE+") {
            @Override
            protected void onPressed() {
                int offset = getContainer().getWorksite().getBoundsMaxWidth() - getContainer().max.z + getContainer().min.z - 1;
                if (0 >= offset) {
                    return;
                }
                if(!isShiftKeyDown()){
                    offset = 1;
                }
                if (getContainer().min.z < getContainer().getZ()) {
                    getContainer().min = getContainer().min.moveForward(2, offset);
                    boundsAdjusted = true;
                    refreshGui();
                } else {
                    getContainer().max = getContainer().max.moveForward(0, offset);
                    boundsAdjusted = true;
                    refreshGui();
                }
            }
        };
        addGuiElement(b);

        addLayout();
    }

    private void addLayout() {
        int bits = (getContainer().getWorksite().getBoundsMaxWidth() + 2);
        int size = 184 / bits;

        int tlx = (256 - (size * bits)) / 2 + size;
        int tly = 44 + size;

        int a = getContainer().getX() - getContainer().min.x;
        int b = getContainer().getZ() - getContainer().min.z;

        Rectangle r = new Rectangle(tlx + a * size, tly + b * size, size, size, 0x0000ffff, 0x0000ffff);
        addGuiElement(r);

        int w = getContainer().max.x - getContainer().min.x;
        int l = getContainer().max.z - getContainer().min.z;
        for (int x = 0; x <= w; x++) {
            final int x1 = x;
            for (int y = 0; y <= l; y++) {
                final int y1 = y;
                r = new ToggledRectangle(tlx + x * size, tly + y * size, size, isChecked(x, y)) {
                    @Override
                    public void clicked(ActivationEvent evt) {
                        if (targetMode) {
                            super.clicked(evt);
                            setChecked(x1, y1, checked);
                            targetsAdjusted = true;
                        }
                    }
                };
                addGuiElement(r);
            }
        }
    }

    @Override
    public void handlePacketData(NBTTagCompound data) {
        if (data.hasKey("checkedMap")) {
            checkedMap = data.getByteArray("checkedMap");
            refreshGui();
        }
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().onClose(boundsAdjusted, targetsAdjusted && targetMode, checkedMap);
        return super.onGuiCloseRequested();
    }

    private class ToggledRectangle extends Rectangle {
        boolean checked;
        private final int checkedColor = 0xff0000ff;
        private final int hoverCheckedColor = 0xff8080ff;

        public ToggledRectangle(int topLeftX, int topLeftY, int size, boolean checked) {
            super(topLeftX, topLeftY, size, size, 0x000000ff, 0x808080ff);
            this.checked = checked;
            addNewListener(new Listener(Listener.MOUSE_DOWN) {
                @Override
                public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                    if (widget.isMouseOverElement(evt.mx, evt.my)) {
                        clicked(evt);
                    }
                    return true;
                }
            });
        }

        public void clicked(ActivationEvent evt) {
            checked = !checked;
        }

        @Override
        protected final int getColor(int mouseX, int mouseY) {
            if (checked) {
                return isMouseOverElement(mouseX, mouseY) ? hoverCheckedColor : checkedColor;
            }
            return super.getColor(mouseX, mouseY);
        }

    }

}
