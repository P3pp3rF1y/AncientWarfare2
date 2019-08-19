package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.GuiIngameForge;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiSelectFromList;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.structure.container.ContainerStatue;
import net.shadowmage.ancientwarfare.structure.render.statue.StatueEntityRegistry;
import net.shadowmage.ancientwarfare.structure.tile.EntityStatueInfo;
import net.shadowmage.ancientwarfare.structure.tile.EntityStatueInfo.Transform;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GuiStatue extends GuiContainerBase<ContainerStatue> {
	private String selectedPart = "";
	private boolean hideGUI;

	public GuiStatue(ContainerBase container) {
		super(container, getScaledResolution().getScaledWidth(), getScaledResolution().getScaledHeight());
		hideGUI = Minecraft.getMinecraft().gameSettings.hideGUI;
		Minecraft.getMinecraft().gameSettings.hideGUI = true;
		GuiIngameForge.renderCrosshairs = false;
		GuiIngameForge.renderHotbar = false;
	}

	private static ScaledResolution getScaledResolution() {
		return new ScaledResolution(Minecraft.getMinecraft());
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		//noop
	}

	@Override
	public void initElements() {
	}

	@Override
	protected boolean onGuiCloseRequested() {
		GuiIngameForge.renderCrosshairs = true;
		GuiIngameForge.renderHotbar = true;

		Minecraft.getMinecraft().gameSettings.hideGUI = hideGUI;
		return super.onGuiCloseRequested();
	}

	private void updateOverallTransform(Consumer<Transform> update) {
		Transform transform = getContainer().getStatueInfo().getOverallTransform();
		update.accept(transform);
		getContainer().updateServer();
	}

	private void updatePartTransform(Consumer<Transform> update) {
		Transform transform = getSelectedPartTransform();
		update.accept(transform);
		getContainer().getStatueInfo().setPartTransform(selectedPart, transform);
		getContainer().updateServer();
	}

	private Transform getSelectedPartTransform() {
		return getContainer().getStatueInfo().getPartTransforms().getOrDefault(selectedPart, new Transform());
	}

	private Float getPartTransformValue(Function<Transform, Float> getValue) {
		return getValue.apply(getSelectedPartTransform());
	}

	private Set<String> getModelPartNames(EntityStatueInfo statueInfo) {
		return StatueEntityRegistry.getStatueEntity(statueInfo.getStatueEntityName()).getStatueModel().getModelPartNames();
	}

	private void addRotationInput(int topY, int leftOffset, Supplier<Float> getCurrentValue, Consumer<Float> updateValue) {
		NumberInput rotation = new NumberInput(leftOffset, topY, 30, getCurrentValue.get(), this) {
			@Override
			public void onValueUpdated(float value) {
				updateValue.accept(value);
			}
		};
		rotation.setIntegerValue();
		rotation.setIncrementAmount(5);
		rotation.setAllowNegative();
		addGuiElement(rotation);
	}

	private void addXYZLabels(int topY, int leftOffset) {
		addGuiElement(new Label(leftOffset + 13, topY, "X"));
		addGuiElement(new Label(leftOffset + 47, topY, "Y"));
		addGuiElement(new Label(leftOffset + 87, topY, "Z"));
	}

	private void addOffsetInput(int topY, int leftOffset, Supplier<Float> getCurrentValue, Consumer<Float> updateValue) {
		NumberInput offset = new NumberInput(leftOffset, topY, 30, getCurrentValue.get(), this) {
			@Override
			public void onValueUpdated(float value) {
				updateValue.accept(value);
			}
		};
		offset.setIncrementAmount(1 / 16f);
		offset.setDecimalPlaces(1);
		offset.setAllowNegative();
		addGuiElement(offset);
	}

	@Override
	public void setupElements() {
		clearElements();
		int topY = 4;
		int leftOffset = 4;
		EntityStatueInfo statueInfo = getContainer().getStatueInfo();

		addGuiElement(new Button(leftOffset, topY, 100, 14, statueInfo.getStatueEntityName()) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiStatue.this, text, s -> s,
						() -> StatueEntityRegistry.getStatueEntityNames().stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()), s -> {
					statueInfo.setStatueEntityName(s);
					getContainer().updateServer();
					refreshGui();
				}));
			}
		});
		topY += 18;

		addGuiElement(new Label(leftOffset, topY, "Offset"));
		topY += 12;

		addXYZLabels(topY, leftOffset);
		topY += 12;

		addOffsetInput(topY, leftOffset, () -> statueInfo.getOverallTransform().getOffsetX(), val -> updateOverallTransform(t -> t.setOffsetX(val)));
		addOffsetInput(topY, leftOffset + 35, () -> statueInfo.getOverallTransform().getOffsetY(), val -> updateOverallTransform(t -> t.setOffsetY(val)));
		addOffsetInput(topY, leftOffset + 70, () -> statueInfo.getOverallTransform().getOffsetZ(), val -> updateOverallTransform(t -> t.setOffsetZ(val)));
		topY += 16;

		addGuiElement(new Label(leftOffset, topY, "Rotation"));
		topY += 12;

		addXYZLabels(topY, leftOffset);
		topY += 12;

		addRotationInput(topY, leftOffset, () -> statueInfo.getOverallTransform().getRotationX(), val ->updateOverallTransform(t -> t.setRotationX(val)));
		addRotationInput(topY, leftOffset + 35, () -> statueInfo.getOverallTransform().getRotationY(), val -> updateOverallTransform(t -> t.setRotationY(val)));
		addRotationInput(topY, leftOffset + 70, () -> statueInfo.getOverallTransform().getRotationZ(), val -> updateOverallTransform(t -> t.setRotationZ(val)));
		topY += 16;

		topY += 2;
		addGuiElement(new Label(leftOffset, topY, "Scale"));
		NumberInput scaleInput = new NumberInput(leftOffset + 70, topY, 30, statueInfo.getOverallTransform().getScale(), this) {
			@Override
			public void onValueUpdated(float value) {
				updateOverallTransform(t -> t.setScale(value));
			}
		};
		scaleInput.setDecimalPlaces(1);
		scaleInput.setIncrementAmount(0.1f);
		addGuiElement(scaleInput);
		topY += 16;

		topY += 4;

		if (selectedPart.isEmpty()) {
			Iterator<String> it = getModelPartNames(statueInfo).iterator();
			if (it.hasNext()) {
				selectedPart = it.next();
			}
		}

		addGuiElement(new Button(leftOffset, topY, 80, 14, selectedPart.isEmpty() ? "Select Model Part" : selectedPart) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSelectFromList<>(GuiStatue.this, text, s -> s,
						() -> getModelPartNames(statueInfo).stream().sorted(Comparator.naturalOrder()).collect(Collectors.toList()), s -> {
					selectedPart = s;
					refreshGui();
				}, false));
			}
		});
		topY += 18;

		if (selectedPart.isEmpty()) {
			return;
		}

		addGuiElement(new Label(leftOffset, topY, "Offset"));
		topY += 12;

		addXYZLabels(topY, leftOffset);
		topY += 12;

		addOffsetInput(topY, leftOffset, () -> getPartTransformValue(Transform::getOffsetX), val -> updatePartTransform(t -> t.setOffsetX(val)));
		addOffsetInput(topY, leftOffset + 35, () -> getPartTransformValue(Transform::getOffsetY), val -> updatePartTransform(t -> t.setOffsetY(val)));
		addOffsetInput(topY, leftOffset + 70, () -> getPartTransformValue(Transform::getOffsetZ), val -> updatePartTransform(t -> t.setOffsetZ(val)));
		topY += 16;

		addGuiElement(new Label(leftOffset, topY, "Rotation"));
		topY += 12;

		addXYZLabels(topY, leftOffset);
		topY += 12;

		addRotationInput(topY, leftOffset, () -> getPartTransformValue(t -> Trig.toDegrees(t.getRotationX())), val -> updatePartTransform(t -> t.setRotationX(Trig.toRadians(val))));
		addRotationInput(topY, leftOffset + 35, () -> getPartTransformValue(t -> Trig.toDegrees(t.getRotationY())), val -> updatePartTransform(t -> t.setRotationY(Trig.toRadians(val))));
		addRotationInput(topY, leftOffset + 70, () -> getPartTransformValue(t -> Trig.toDegrees(t.getRotationZ())), val -> updatePartTransform(t -> t.setRotationZ(Trig.toRadians(val))));

		addGuiElement(new Label(leftOffset + 120, height - 32, "Press ESC to exit this screen"));
		addGuiElement(new Label(leftOffset + 120, height - 16, "HINT: Use mouse wheel over values to change them quickly"));
	}

	@Override
	public void drawDefaultBackground() {
		//noop
	}
}
