package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerPlacer;

import java.util.HashMap;
import java.util.Set;

public class GuiSpawnerPlacer extends GuiContainerBase<ContainerSpawnerPlacer> {

    private Label currentSelectionName;
    private CompositeScrolled typeSelectionArea;
    private CompositeScrolled attributesArea;

    private final HashMap<Label, String> labelToClass = new HashMap<>();

    public GuiSpawnerPlacer(ContainerBase par1Container) {
        super(par1Container);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().sendDataToServer();
        return true;
    }

    @Override
    public void initElements() {
        Button button = new Button(256 - 8 - 55, 8, 55, 12, "guistrings.done") {
            @Override
            protected void onPressed() {
                closeGui();
            }
        };
        addGuiElement(button);
        addGuiElement(new Label(8, 8, "guistrings.current_selection"));
        currentSelectionName = new Label(8, 18, "");
        updateSelectionName();
        addGuiElement(currentSelectionName);

        typeSelectionArea = new CompositeScrolled(this, 0, 30, 256, 105);
        addGuiElement(typeSelectionArea);

        attributesArea = new CompositeScrolled(this, 0, 30 + 105, 256, 105);
        addGuiElement(attributesArea);
    }


    @Override
    public void setupElements() {
        typeSelectionArea.clearElements();
        attributesArea.clearElements();
        labelToClass.clear();

        int totalHeight = 3;
        Set<ResourceLocation> mp = EntityList.ENTITY_EGGS.keySet();

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
        for (ResourceLocation registryName : mp) {
            String name = registryName.toString();

            if (AWStructureStatics.excludedSpawnerEntities.contains(name)) {
                continue;//skip excluded entities
            }
            label = new Label(8, totalHeight, "entity." + name + ".name");
            label.addNewListener(listener);
            typeSelectionArea.addGuiElement(label);
            labelToClass.put(label, name);
            totalHeight += 12;
        }
        typeSelectionArea.setAreaSize(totalHeight);

        updateSelectionName();

        totalHeight = 3;

        NumberInput input;

        label = new Label(8, totalHeight, "guistrings.spawner.delay");
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

        label = new Label(8, totalHeight, "guistrings.spawner.min_spawn_delay");
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

        label = new Label(8, totalHeight, "guistrings.spawner.max_spawn_delay");
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

        label = new Label(8, totalHeight, "guistrings.spawner.spawn_count");
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

        label = new Label(8, totalHeight, "guistrings.spawner.max_nearby_entities");
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

        label = new Label(8, totalHeight, "guistrings.required_player_range");
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

        label = new Label(8, totalHeight, "guistrings.spawner.spawn_range");
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
        currentSelectionName.setText("entity." + getContainer().entityId + ".name");
    }

}
