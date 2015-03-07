package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnGroup;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiSpawnerAdvancedAddEntity extends GuiContainerBase {

    CompositeScrolled area;
    GuiContainerBase parent;
    EntitySpawnGroup group;
    EntitySpawnSettings settings = new EntitySpawnSettings();
    boolean showAddButton;

    List<String> tagInput = new ArrayList<String>();
    boolean showAddTagButton = true;

    public GuiSpawnerAdvancedAddEntity(GuiContainerBase parent, EntitySpawnGroup group, EntitySpawnSettings settings) {
        super(parent.getContainer());
        this.parent = parent;
        this.group = group;
        this.settings = settings;
        if (this.settings == null) {
            showAddButton = true;
            this.settings = new EntitySpawnSettings();
            this.settings.setEntityToSpawn("Pig");
            this.settings.setSpawnCountMin(2);
            this.settings.setSpawnCountMax(4);
            this.settings.setSpawnLimitTotal(-1);
        }
        if (this.settings.getCustomTag() != null) {
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
            button = new Button(8, 8, 160, 12, StatCollector.translateToLocal("guistrings.spawner.add_entity")) {
                @Override
                protected void onPressed() {
                    group.addSpawnSetting(settings);
                    Minecraft.getMinecraft().displayGuiScreen(parent);
                    parent.refreshGui();
                }
            };
            addGuiElement(button);
        }

        button = new Button(256 - 8 - 55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done")) {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(parent);
                parent.refreshGui();
            }
        };
        addGuiElement(button);

        Label label = new Label(8, 40 - 14, StatCollector.translateToLocal("guistrings.spawner.set_entity_properties"));
        addGuiElement(label);

        area = new CompositeScrolled(this, 0, 40, 256, 200);
        addGuiElement(area);
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

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.select_entity"));
        area.addGuiElement(label);

        button = new Button(100, totalHeight, 120, 12, settings.getEntityId()) {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(new GuiSpawnerAdvancedEntitySelection(GuiSpawnerAdvancedAddEntity.this, settings));
            }
        };
        area.addGuiElement(button);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.min"));
        area.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 30, settings.getSpawnMin(), this) {
            @Override
            public void onValueUpdated(float value) {
                int val = (int) value;
                settings.setSpawnCountMin(val);
            }
        };
        input.setIntegerValue();
        area.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.max"));
        area.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 30, settings.getSpawnMax(), this) {
            @Override
            public void onValueUpdated(float value) {
                int val = (int) value;
                settings.setSpawnCountMax(val);
            }
        };
        input.setIntegerValue();
        area.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.total"));
        area.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 30, settings.getSpawnTotal(), this) {
            @Override
            public void onValueUpdated(float value) {
                int val = (int) value;
                settings.setSpawnLimitTotal(val);
            }
        };
        input.setIntegerValue();
        input.setAllowNegative();
        input.setValue(settings.getSpawnTotal());
        area.addGuiElement(input);
        totalHeight += 12;


        totalHeight += 8;
        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.custom_tag"));
        area.addGuiElement(label);
        totalHeight += 12;

        if (showAddTagButton) {
            button = new Button(8, totalHeight, 120, 12, StatCollector.translateToLocal("guistrings.spawner.add_custom_tag")) {
                @Override
                protected void onPressed() {
                    tagInput.add("TAG=10={");
                    tagInput.add("");
                    tagInput.add("}");
                    refreshGui();
                    showAddTagButton = false;
                }
            };
            area.addGuiElement(button);
            totalHeight += 12;
        }

        for (String line : tagInput) {
            text = new Text(8, totalHeight, 200, line, this) {
                @Override
                protected void handleKeyInput(int keyCode, char ch) {
                    super.handleKeyInput(keyCode, ch);
                    int lineNumber = textToLineMap.get(this);
                    String text = getText();
                    tagInput.remove(lineNumber);
                    tagInput.add(lineNumber, text);
                }
            };
            textToLineMap.put(text, lineNumber);
            area.addGuiElement(text);

            button = new Button(208, totalHeight, 12, 12, StatCollector.translateToLocal("guistrings.spawner.add")) {
                @Override
                protected void onPressed() {
                    int lineNumber = buttonToLineMap.get(this);
                    tagInput.add(lineNumber, "");
                    refreshGui();
                }
            };
            buttonToLineMap.put(button, lineNumber);
            area.addGuiElement(button);

            button = new Button(220, totalHeight, 12, 12, StatCollector.translateToLocal("guistrings.spawner.remove")) {
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

        if (!showAddTagButton) {
            button = new Button(8, totalHeight, 120, 12, StatCollector.translateToLocal("guistrings.spawner.add_custom_tag_line")) {
                @Override
                protected void onPressed() {
                    tagInput.add("");
                    refreshGui();
                }
            };
            area.addGuiElement(button);
            totalHeight += 12;
        }

        area.setAreaSize(totalHeight);
    }

    HashMap<Button, Integer> buttonToLineMap = new HashMap<Button, Integer>();
    HashMap<Text, Integer> textToLineMap = new HashMap<Text, Integer>();

}
