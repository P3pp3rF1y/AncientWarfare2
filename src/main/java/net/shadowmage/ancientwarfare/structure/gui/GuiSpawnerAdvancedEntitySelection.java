package net.shadowmage.ancientwarfare.structure.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.gui.elements.Tooltip;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GuiSpawnerAdvancedEntitySelection extends GuiContainerBase {

    private final GuiContainerBase parent;
    private final EntitySpawnSettings settings;

    private CompositeScrolled area;
    private Label selectionLabel;
    private Text search;

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
        area = new CompositeScrolled(this, 0, 42, 256, 200);
        addGuiElement(area);

        Label label = new Label(8, 6, "guistrings.spawner.select_entity");
        addGuiElement(label);

        Button button = new Button(xSize - 8 - 55, 6, 55, 12, "guistrings.done") {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        selectionLabel = new Label(8, 18, settings.getEntityName());
        addGuiElement(selectionLabel);

        search = new Text(8, 30, 240, "", this) {
            @Override
            protected void handleKeyInput(int keyCode, char ch) {
                String old = getText();
                super.handleKeyInput(keyCode, ch);
                String text = getText();
                if(!text.equals(old)){
                    refreshGui();
                }
            }
        };
        addGuiElement(search);
    }

    @Override
    public void setupElements() {
        area.clearElements();

        List<ResourceLocation> entities = ForgeRegistries.ENTITIES.getKeys().stream().filter(rl -> {
			if(rl == null || AWStructureStatics.excludedSpawnerEntities.contains(rl.toString())){//skip excluded entities
				return false;
			}
			return (search.getText().isEmpty() || rl.toString().contains(search.getText().toLowerCase()));
		}).sorted(Comparator.comparing(registryName -> I18n.format(EntityTools.getUnlocName(registryName)))).collect(Collectors.toList());
        int totalHeight = 8;
        Button button;
        for (ResourceLocation registryName : entities) {
            button = new Button(8, totalHeight, 256 - 8 - 16, 12, I18n.format(EntityTools.getUnlocName(registryName))) {
                @Override
                protected void onPressed() {
                    settings.setEntityToSpawn(registryName);
                    selectionLabel.setText(settings.getEntityName());
                    refreshGui();
                }
            };
            Tooltip tip = new Tooltip(50, 10);
            tip.addTooltipElement(new Label(0, 0, registryName.toString()));
            button.setTooltip(tip);
            area.addGuiElement(button);
            totalHeight += 12;
        }
        area.setAreaSize(totalHeight);
    }

}
