package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityList;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;

import java.util.Map;

public class GuiSpawnerAdvancedEntitySelection extends GuiContainerBase {

    private GuiContainerBase parent;
    private EntitySpawnSettings settings;
    CompositeScrolled area;

    Label selectionLabel;

    public GuiSpawnerAdvancedEntitySelection(GuiContainerBase parent, EntitySpawnSettings settings) {
        super(parent.getContainer());
        this.parent = parent;
        this.settings = settings;
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        return false;
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 40, 256, 200);
        addGuiElement(area);

        Label label = new Label(8, 8, StatCollector.translateToLocal("guistrings.spawner.select_entity"));
        addGuiElement(label);

        Button button = new Button(xSize - 8 - 55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done")) {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        selectionLabel = new Label(8, 20, settings.getEntityId());
        addGuiElement(selectionLabel);
    }

    @Override
    public void setupElements() {
        area.clearElements();

        int totalHeight = 8;
        @SuppressWarnings({"unchecked", "rawtypes"})
        Map<String, Class> mp = EntityList.stringToClassMapping;

        Button button;
        for (String name : mp.keySet()) {
            if (AWStructureStatics.excludedSpawnerEntities.contains(name)) {
                continue;//skip excluded entities
            }
            button = new Button(8, totalHeight, 256 - 8 - 16, 12, name) {
                @Override
                protected void onPressed() {
                    settings.setEntityToSpawn(text);
                    selectionLabel.setText(text);
                    refreshGui();
                }
            };
            area.addGuiElement(button);
            totalHeight += 12;
        }
        area.setAreaSize(totalHeight);
    }

}
