package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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
				if (getContainer().max.getZ() >= getContainer().getZ() && (getContainer().min.getX() > getContainer().getX() || getContainer().max
						.getX() < getContainer().getX())) {
					getContainer().min = getContainer().min.offset(EnumFacing.NORTH);
					getContainer().max = getContainer().max.offset(EnumFacing.NORTH);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 40, 12, 40, 12, "guistrings.inventory.direction.south") {
			@Override
			protected void onPressed() {
				if (getContainer().min.getZ() <= getContainer().getZ() && (getContainer().min.getX() > getContainer().getX() || getContainer().max
						.getX() < getContainer().getX())) {
					getContainer().min = getContainer().min.offset(EnumFacing.SOUTH);
					getContainer().max = getContainer().max.offset(EnumFacing.SOUTH);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 80, 12, 40, 12, "guistrings.inventory.direction.west") {
			@Override
			protected void onPressed() {
				if (getContainer().max.getX() >= getContainer().getX() && (getContainer().min.getZ() > getContainer().getZ() || getContainer().max
						.getZ() < getContainer().getZ())) {
					getContainer().min = getContainer().min.offset(EnumFacing.WEST);
					getContainer().max = getContainer().max.offset(EnumFacing.WEST);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 120, 12, 40, 12, "guistrings.inventory.direction.east") {
			@Override
			protected void onPressed() {
				if (getContainer().min.getX() <= getContainer().getX() && (getContainer().min.getZ() > getContainer().getZ() || getContainer().max
						.getZ() < getContainer().getZ())) {
					getContainer().min = getContainer().min.offset(EnumFacing.EAST);
					getContainer().max = getContainer().max.offset(EnumFacing.EAST);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48, 24, 40, 12, "XSIZE-") {
			@Override
			protected void onPressed() {
				if (getContainer().max.getX() <= getContainer().min.getX()) {
					return;
				}
				if (getContainer().min.getX() < getContainer().getX()) {
					getContainer().min = getContainer().min.offset(EnumFacing.EAST);
					boundsAdjusted = true;
					refreshGui();
				} else {
					getContainer().max = getContainer().max.offset(EnumFacing.WEST);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 40, 24, 40, 12, "XSIZE+") {
			@Override
			protected void onPressed() {
				int offset = getContainer().getWorksite().getBoundsMaxWidth() - getContainer().max.getX() + getContainer().min.getX() - 1;
				if (0 >= offset) {
					return;
				}
				if (!isShiftKeyDown()) {
					offset = 1;
				}
				if (getContainer().min.getX() < getContainer().getX()) {
					getContainer().min = getContainer().min.offset(EnumFacing.WEST, offset);
					boundsAdjusted = true;
					refreshGui();
				} else {
					getContainer().max = getContainer().max.offset(EnumFacing.EAST, offset);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 80, 24, 40, 12, "ZSIZE-") {
			@Override
			protected void onPressed() {
				if (getContainer().max.getZ() <= getContainer().min.getZ()) {
					return;
				}
				if (getContainer().min.getZ() < getContainer().getZ()) {
					getContainer().min = getContainer().min.offset(EnumFacing.SOUTH);
					boundsAdjusted = true;
					refreshGui();
				} else {
					getContainer().max = getContainer().max.offset(EnumFacing.NORTH);
					boundsAdjusted = true;
					refreshGui();
				}
			}
		};
		addGuiElement(b);

		b = new Button(48 + 120, 24, 40, 12, "ZSIZE+") {
			@Override
			protected void onPressed() {
				int offset = getContainer().getWorksite().getBoundsMaxWidth() - getContainer().max.getZ() + getContainer().min.getZ() - 1;
				if (0 >= offset) {
					return;
				}
				if (!isShiftKeyDown()) {
					offset = 1;
				}
				if (getContainer().min.getZ() < getContainer().getZ()) {
					getContainer().min = getContainer().min.offset(EnumFacing.NORTH, offset);
					boundsAdjusted = true;
					refreshGui();
				} else {
					getContainer().max = getContainer().max.offset(EnumFacing.SOUTH, offset);
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

		int a = getContainer().getX() - getContainer().min.getX();
		int b = getContainer().getZ() - getContainer().min.getZ();

		Rectangle r = new Rectangle(tlx + a * size, tly + b * size, size, size, 0x0000ffff, 0x0000ffff);
		addGuiElement(r);

		int w = getContainer().max.getX() - getContainer().min.getX();
		int l = getContainer().max.getZ() - getContainer().min.getZ();
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
