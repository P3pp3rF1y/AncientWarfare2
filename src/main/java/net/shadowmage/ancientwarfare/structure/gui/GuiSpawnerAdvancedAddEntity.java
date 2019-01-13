package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.gui.elements.Tooltip;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnGroup;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiSpawnerAdvancedAddEntity extends GuiContainerBase {

	private CompositeScrolled area;
	private final GuiContainerBase parent;
	private final EntitySpawnGroup group;
	private EntitySpawnSettings settings;
	private final boolean showAddButton;

	private final List<String> tagInput = new ArrayList<>();
	private boolean showAddTagButton = true;

	private final HashMap<Button, Integer> buttonToLineMap = new HashMap<>();
	private final HashMap<Text, Integer> textToLineMap = new HashMap<>();

	public GuiSpawnerAdvancedAddEntity(GuiContainerBase parent, EntitySpawnGroup group, EntitySpawnSettings settings) {
		super(parent.getContainer());
		this.parent = parent;
		this.group = group;
		this.settings = settings;
		if (this.settings == null) {
			showAddButton = true;
			this.settings = new EntitySpawnSettings(group);
		} else
			showAddButton = false;
		loadData();
	}

	private void loadData() {
		NBTTagCompound tag = this.settings.getCustomTag();
		if (tag != null) {
			String[] splits = tag.toString().split("}");
			for (String t : splits) {
				tagInput.add(t + "}");
			}
			showAddTagButton = false;
		}
	}

	@Override
	protected boolean onGuiCloseRequested() {
		Minecraft.getMinecraft().displayGuiScreen(parent);
		return false;
	}

	@Override
	public void initElements() {
		Button button;

		if (showAddButton) {
			button = new Button(8, 8, 160, 12, "guistrings.spawner.add_entity") {
				@Override
				protected void onPressed() {
					group.addSpawnSetting(settings);
					Minecraft.getMinecraft().displayGuiScreen(parent);
					parent.refreshGui();
				}
			};
			addGuiElement(button);
		}

		button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done") {
			@Override
			protected void onPressed() {
				saveTag();
				Minecraft.getMinecraft().displayGuiScreen(parent);
				parent.refreshGui();
			}
		};
		addGuiElement(button);

		Label label = new Label(8, 40 - 14, "guistrings.spawner.set_entity_properties");
		addGuiElement(label);

		area = new CompositeScrolled(this, 0, 40, 256, 200);
		addGuiElement(area);
	}

	private void saveTag() {
		if (!tagInput.isEmpty()) {
			StringBuilder tagBuffer = new StringBuilder();
			for (String string : tagInput) {
				tagBuffer.append(string);
			}
			String tag = tagBuffer.toString();
			try {
				NBTTagCompound base = JsonToNBT.getTagFromJson(tag);
				if (base != null && !base.hasNoTags()) {
					settings.setCustomSpawnTag(base);
				}
			}
			catch (Throwable t) {
				t.printStackTrace();
			}
		} else {
			settings.setCustomSpawnTag(null);
		}
	}

	@Override
	public void setupElements() {
		area.clearElements();
		buttonToLineMap.clear();

		Label label;
		NumberInput input;
		Button button;
		Text text;

		int lineNumber = 0;
		int totalHeight = 8;

		label = new Label(8, totalHeight, "guistrings.spawner.select_entity");
		area.addGuiElement(label);

		button = new Button(100, totalHeight, 120, 12, settings.getEntityName()) {
			@Override
			protected void onPressed() {
				Minecraft.getMinecraft().displayGuiScreen(new GuiSpawnerAdvancedEntitySelection(GuiSpawnerAdvancedAddEntity.this, settings));
			}
		};
		area.addGuiElement(button);
		totalHeight += 12;

		label = new Label(8, totalHeight, "guistrings.spawner.min");
		area.addGuiElement(label);
		input = new NumberInput(120, totalHeight, 30, settings.getSpawnMin(), this) {
			@Override
			public void onValueUpdated(float value) {
				settings.setSpawnCountMin((int) value);
			}
		};
		input.setIntegerValue();
		area.addGuiElement(input);
		totalHeight += 12;

		label = new Label(8, totalHeight, "guistrings.spawner.max");
		area.addGuiElement(label);
		input = new NumberInput(120, totalHeight, 30, settings.getSpawnMax(), this) {
			@Override
			public void onValueUpdated(float value) {
				settings.setSpawnCountMax((int) value);
			}
		};
		input.setIntegerValue();
		area.addGuiElement(input);
		totalHeight += 12;

		label = new Label(8, totalHeight, "guistrings.spawner.total");
		area.addGuiElement(label);
		input = new NumberInput(120, totalHeight, 30, settings.getSpawnTotal(), this) {
			@Override
			public void onValueUpdated(float value) {
				settings.setSpawnLimitTotal((int) value);
			}
		};
		input.setIntegerValue();
		input.setAllowNegative();
		input.setValue(settings.getSpawnTotal());
		area.addGuiElement(input);
		totalHeight += 12;

		label = new Label(8, totalHeight, "guistrings.spawner.custom_tag");
		area.addGuiElement(label);
		totalHeight += 12;

		Tooltip tip;
		if (showAddTagButton) {
			button = new Button(8, totalHeight, 120, 12, "guistrings.spawner.add_custom_tag") {
				@Override
				protected void onPressed() {
					tagInput.add("{");
					tagInput.add("");
					tagInput.add("}");
					refreshGui();
					showAddTagButton = false;
				}
			};
			tip = new Tooltip(50, 20);
			tip.addTooltipElement(new Label(0, 0, "guistrings.spawner.custom_tag_tip"));
			button.setTooltip(tip);
			area.addGuiElement(button);
			totalHeight += 12;
		}

		for (String line : tagInput) {
			text = new Text(8, totalHeight, 200, line, this) {
				@Override
				protected void handleKeyInput(int keyCode, char ch) {
					super.handleKeyInput(keyCode, ch);
					int lineNumber = textToLineMap.get(this);
					tagInput.set(lineNumber, getText());
				}
			};
			textToLineMap.put(text, lineNumber);
			area.addGuiElement(text);

			button = new Button(208, totalHeight, 12, 12, "guistrings.spawner.add") {
				@Override
				protected void onPressed() {
					int lineNumber = buttonToLineMap.get(this);
					tagInput.add(lineNumber, "");
					refreshGui();
				}
			};
			buttonToLineMap.put(button, lineNumber);
			area.addGuiElement(button);

			button = new Button(220, totalHeight, 12, 12, "guistrings.spawner.remove") {
				@Override
				protected void onPressed() {
					int lineNumber = buttonToLineMap.get(this);
					tagInput.remove(lineNumber);
					if (tagInput.isEmpty()) {
						showAddTagButton = true;
					}
					refreshGui();
				}
			};
			buttonToLineMap.put(button, lineNumber);
			area.addGuiElement(button);

			totalHeight += 12;
			lineNumber++;
		}

		label = new Label(8, totalHeight, "guistrings.spawner.custom_data");
		area.addGuiElement(label);
		totalHeight += 12;
		area.setAreaSize(totalHeight);
	}

}
