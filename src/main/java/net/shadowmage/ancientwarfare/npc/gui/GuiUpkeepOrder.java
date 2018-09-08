package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.npc.container.ContainerUpkeepOrder;

import java.util.Optional;

public class GuiUpkeepOrder extends GuiContainerBase<ContainerUpkeepOrder> {

	private boolean hasChanged = false;

	public GuiUpkeepOrder(ContainerBase container) {
		super(container, 246, 38);
	}

	@Override
	public void initElements() {
		//noop
	}

	@Override
	public void setupElements() {
		clearElements();
		Optional<BlockPos> pos = getContainer().upkeepOrder.getUpkeepPosition();
		ItemSlot slot;
		Button button;
		Label label;

		if (pos.isPresent()) {
			slot = new ItemSlot(8, 10, getContainer().upkeepBlock, this);
			addGuiElement(slot);

			label = new Label(8 + 18 + 4, 8, StringTools.formatPos(pos.get()));
			addGuiElement(label);

			button = new Button(8 + 18 + 55 + 20, 8 + 10, 55, 12, getSideName(getContainer().upkeepOrder.getUpkeepBlockSide())) {
				@Override
				protected void onPressed() {
					getContainer().upkeepOrder.changeBlockSide();
					setText(getSideName(getContainer().upkeepOrder.getUpkeepBlockSide()));
					hasChanged = true;
					refreshGui();
				}
			};
			addGuiElement(button);

			label = new Label(8 + 18 + 55 + 55 + 30, 8, "guistrings.npc.upkeep_time");
			addGuiElement(label);

			NumberInput input = new NumberInput(8 + 18 + 55 + 55 + 30, 8 + 10, 60, (float) getContainer().upkeepOrder.getUpkeepAmount() / 1200.f, this) {
				@Override
				public void onValueUpdated(float value) {
					float val = value * 1200.f;
					getContainer().upkeepOrder.setUpkeepAmount((int) val);
					hasChanged = true;
				}
			};
			addGuiElement(input);
		} else {
			label = new Label(8, 8, "guistrings.npc.assign_upkeep_point");
			addGuiElement(label);
		}
	}

	private String getSideName(EnumFacing side) {
		return side == null ? "" : "guistrings.inventory.direction." + side.getName();
	}

	@Override
	protected boolean onGuiCloseRequested() {
		if (hasChanged) {
			getContainer().onClose();
		}
		return super.onGuiCloseRequested();
	}

}
