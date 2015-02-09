package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.entity.EntityList;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerPlacer;

import java.util.HashMap;
import java.util.Set;

public class GuiSpawnerPlacer extends GuiContainerBase<ContainerSpawnerPlacer> {

    Label currentSelectionName;
    CompositeScrolled typeSelectionArea;
    CompositeScrolled attributesArea;

    @SuppressWarnings("rawtypes")
    private HashMap<Label, String> labelToClass = new HashMap<Label, String>();

    public GuiSpawnerPlacer(ContainerBase par1Container) {
        super(par1Container, 256, 240, defaultBackground);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().sendDataToServer();
        return true;
    }

    @Override
    public void initElements() {
        Button button = new Button(256 - 8 - 55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done")) {
            @Override
            protected void onPressed() {
                closeGui();
            }
        };
        addGuiElement(button);

        currentSelectionName = new Label(8, 8, "");
        updateSelectionName();
        addGuiElement(currentSelectionName);

        typeSelectionArea = new CompositeScrolled(this, 0, 30, 256, 105);
        addGuiElement(typeSelectionArea);

        attributesArea = new CompositeScrolled(this, 0, 30 + 105, 256, 105);
        addGuiElement(attributesArea);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public void setupElements() {
        typeSelectionArea.clearElements();
        attributesArea.clearElements();
        labelToClass.clear();

        int totalHeight = 3;
        Set<String> mp = EntityList.stringToClassMapping.keySet();

        Listener listener = new Listener(Listener.MOUSE_UP) {
            @Override
            public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                if (widget.isMouseOverElement(evt.mx, evt.my)) {
                    getContainer().entityId = labelToClass.get(widget);
                    updateSelectionName();
                }
                return true;
            }
        };

        Label label;
        for (String name : mp) {
            if (AWStructureStatics.excludedSpawnerEntities.contains(name)) {
                continue;//skip excluded entities
            }
            label = new Label(8, totalHeight, name);
            label.addNewListener(listener);
            typeSelectionArea.addGuiElement(label);
            labelToClass.put(label, name);
            totalHeight += 12;
        }
        typeSelectionArea.setAreaSize(totalHeight);

        updateSelectionName();

        totalHeight = 3;

        NumberInput input;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.delay"));
        attributesArea.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 112, getContainer().delay, this) {
            @Override
            public void onValueUpdated(float value) {
                ((ContainerSpawnerPlacer) inventorySlots).delay = (short) value;
            }
        };
        input.setIntegerValue();
        attributesArea.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.min_spawn_delay"));
        attributesArea.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 112, getContainer().minSpawnDelay, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().minSpawnDelay = (short) value;
            }
        };
        input.setIntegerValue();
        attributesArea.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.max_spawn_delay"));
        attributesArea.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 112, getContainer().maxSpawnDelay, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().maxSpawnDelay = (short) value;
            }
        };
        input.setIntegerValue();
        attributesArea.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.spawn_count"));
        attributesArea.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 112, getContainer().spawnCount, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().spawnCount = (short) value;
            }
        };
        input.setIntegerValue();
        attributesArea.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.max_nearby_entities"));
        attributesArea.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 112, getContainer().maxNearbyEntities, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().maxNearbyEntities = (short) value;
            }
        };
        input.setIntegerValue();
        attributesArea.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.required_player_range"));
        attributesArea.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 112, getContainer().requiredPlayerRange, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().requiredPlayerRange = (short) value;
            }
        };
        input.setIntegerValue();
        attributesArea.addGuiElement(input);
        totalHeight += 12;

        label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.spawn_range"));
        attributesArea.addGuiElement(label);
        input = new NumberInput(120, totalHeight, 112, getContainer().spawnRange, this) {
            @Override
            public void onValueUpdated(float value) {
                getContainer().spawnRange = (short) value;
            }
        };
        input.setIntegerValue();
        attributesArea.addGuiElement(input);
        totalHeight += 12;

        attributesArea.setAreaSize(totalHeight);
    }

    private void updateSelectionName() {
        currentSelectionName.setText(StatCollector.translateToLocal("guistrings.current_selection") + ": " + StatCollector.translateToLocal("entity."+getContainer().entityId+".name"));
    }

}
