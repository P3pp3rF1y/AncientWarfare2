package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.util.Tuple;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.structure.container.ContainerLootChestPlacer;

import java.util.Comparator;
import java.util.stream.Collectors;

public class GuiLootChestPlacer extends GuiContainerBase<ContainerLootChestPlacer> {
	private static final int TOP_HEIGHT = 42;
	private static final int FORM_WIDTH = 300;
	private static final int FORM_HEIGHT = 200;

	private CompositeScrolled selectionArea;
	private Label selection;
	private Text filterInput;
	private NumberInput lootRolls;
	private Checkbox placeBasket;

	public GuiLootChestPlacer(ContainerBase container) {
		super(container, FORM_WIDTH, FORM_HEIGHT);
	}

	@Override
	public void initElements() {
		addGuiElement(new Label(8, 8, "guistrings.current_selection"));
		addGuiElement(new Label(150, 8, "guistrings.loot_rolls"));

		selection = new Label(8, 20, getSelectedLootTable());
		addGuiElement(selection);

		lootRolls = new NumberInput(205, 6, 15, getLootRolls(), this) {
			@Override
			public void onValueUpdated(float value) {
				saveLootChestPlacerParams();
			}
		};
		lootRolls.setIntegerValue();
		addGuiElement(lootRolls);

		placeBasket = new Checkbox(230, 6, 14, 14, "guistrings.loot_basket.top_inventory") {
			@Override
			public void onToggled() {
				super.onToggled();
				saveLootChestPlacerParams();
			}
		};
		placeBasket.setChecked(getContainer().getPlaceBasket());
		addGuiElement(placeBasket);

		filterInput = new Text(8, 18 + 12, FORM_WIDTH - 16, "", this) {
			//kind of dirty...should possibly implement a real onCharEntered callback for when input actually changes
			@Override
			protected void handleKeyInput(int keyCode, char ch) {
				super.handleKeyInput(keyCode, ch);
				refreshGui();
			}
		};
		addGuiElement(filterInput);

		selectionArea = new CompositeScrolled(this, 0, TOP_HEIGHT, FORM_WIDTH, FORM_HEIGHT - TOP_HEIGHT);
		addGuiElement(selectionArea);
	}

	private void saveLootChestPlacerParams() {
		getContainer().setLootParameters(selection.getText(), (byte) lootRolls.getIntegerValue(), placeBasket.checked());
	}

	private String getSelectedLootTable() {
		return getContainer().getLootParameters().map(t -> t.getFirst().toString()).orElse("guistrings.none");
	}

	private Byte getLootRolls() {
		return getContainer().getLootParameters().map(Tuple::getSecond).orElse((byte) 0);
	}

	@Override
	public void setupElements() {
		int totalHeight = 8;

		selectionArea.clearElements();
		for (String lootTableName : getContainer().getLootTableNames().stream().filter(lt -> lt.toLowerCase().contains(filterInput.getText().toLowerCase()))
				.sorted(Comparator.naturalOrder()).collect(Collectors.toList())) {
			Button button = new Button(8, totalHeight, 272, 12, lootTableName);
			button.addNewListener(new Listener(Listener.MOUSE_UP) {
				@Override
				public boolean onEvent(GuiElement widget, ActivationEvent evt) {
					if (evt.mButton == 0 && widget.isMouseOverElement(evt.mx, evt.my)) {
						selection.setText(lootTableName);
						getContainer().setLootParameters(lootTableName, (byte) lootRolls.getIntegerValue(), placeBasket.checked());
					}
					return true;
				}
			});
			totalHeight += 12;
			selectionArea.addGuiElement(button);
		}

		selectionArea.setAreaSize(totalHeight + 8);
	}
}
