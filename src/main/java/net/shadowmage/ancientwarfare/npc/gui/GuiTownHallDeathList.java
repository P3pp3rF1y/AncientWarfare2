package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall.NpcDeathEntry;

import java.util.List;

public class GuiTownHallDeathList extends GuiContainerBase<ContainerTownHall> {

    private final GuiTownHallInventory parent;
    private CompositeScrolled area;

    public GuiTownHallDeathList(GuiTownHallInventory parent) {
        super(parent.getContainer());
        this.parent = parent;
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 40, xSize, ySize - 40);
        addGuiElement(area);
        Button button = new Button(8, 8, 55, 12, "guistrings.npc.clear_death_list") {
            @Override
            protected void onPressed() {
                getContainer().clearList();
            }
        };
        addGuiElement(button);
    }

    @Override
    public void setupElements() {
        area.clearElements();
        List<NpcDeathEntry> deathList = parent.getContainer().getDeathList();
        int totalHeight = 8;

        Label label;
        String labelText;
        for (NpcDeathEntry entry : deathList) {
            labelText = StatCollector.translateToLocal("guistrings.npc.npc_name");
            label = new Label(8, totalHeight, labelText + ": " + entry.npcName);
            area.addGuiElement(label);
            totalHeight += 12;

            labelText = StatCollector.translateToLocal("guistrings.npc.npc_type");
            label = new Label(8, totalHeight, labelText + ": " + StatCollector.translateToLocal("entity.AncientWarfareNpc." + entry.npcType + ".name"));
            area.addGuiElement(label);
            totalHeight += 12;

            labelText = StatCollector.translateToLocalFormatted("guistrings.npc.death_cause", entry.deathCause);
            label = new Label(8, totalHeight, labelText);
            area.addGuiElement(label);
            totalHeight += 12;

            labelText = StatCollector.translateToLocalFormatted("guistrings.npc.can_res", entry.canRes);
            label = new Label(8, totalHeight, labelText);
            area.addGuiElement(label);
            totalHeight += 12;

            if (entry.canRes) {
                labelText = StatCollector.translateToLocalFormatted("guistrings.npc.resurrected", entry.resurrected);
                label = new Label(8, totalHeight, labelText);
                area.addGuiElement(label);
                totalHeight += 12;
            }

            area.addGuiElement(new Line(0, totalHeight - 1, xSize, totalHeight - 1, 1, 0x000000ff));
            totalHeight += 4;
        }
        area.setAreaSize(totalHeight);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        Minecraft.getMinecraft().displayGuiScreen(parent);
        parent.getContainer().addSlots();
        parent.refreshGui();
        return false;
    }

}
