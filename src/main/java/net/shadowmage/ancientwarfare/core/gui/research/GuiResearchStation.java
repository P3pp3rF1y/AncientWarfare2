package net.shadowmage.ancientwarfare.core.gui.research;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.block.Direction;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchBook;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchStation;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiResearchBook;
import net.shadowmage.ancientwarfare.core.gui.elements.*;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import org.lwjgl.input.Mouse;

import java.util.List;

public class GuiResearchStation extends GuiContainerBase<ContainerResearchStation> {

    private Label researcherLabel;
    private Label researchGoalLabel;
    private ProgressBar bar;
    private Checkbox useAdjacentInventory;
    private Button info, invDir, invSide;

    private ItemSlot[] layoutSlots = new ItemSlot[9];

    public GuiResearchStation(ContainerBase par1Container) {
        super(par1Container, 178, 240);
    }

    @Override
    public void initElements() {
        String name = getContainer().researcherName == null ? "guistrings.research.no_researcher" : getContainer().researcherName;
        researcherLabel = new Label(8, 8, name);
        addGuiElement(researcherLabel);

        name = "guistrings.research.no_research";
        final int goalNumber = getContainer().currentGoal;
        if (goalNumber >= 0) {
            ResearchGoal g = ResearchGoal.getGoal(goalNumber);
            if (g != null) {
                name = g.getName();
            }
        }
        researchGoalLabel = new Label(80, 8 + 18 * 2, name);
        addGuiElement(researchGoalLabel);
        addGuiElement(new Label(8, 8 + 18 * 2, I18n.format("guistrings.research.current_goal") + ":"));

        bar = new ProgressBar(70, 8 + 18 * 2 + 12, 178 - 70 - 8, 12);
        addGuiElement(bar);

        Button button = new Button(178 - 8 - 110, 8 + 12 + 4, 110, 12, "guistrings.research.research_queue") {
            @Override
            protected void onPressed() {
                getContainer().removeSlots();
                Minecraft.getMinecraft().displayGuiScreen(new GuiResearchStationSelection(GuiResearchStation.this, Mouse.getX(), Mouse.getY()));
            }
        };
        addGuiElement(button);
        info = new Button(30, 8 + 12 + 4, 24, 12, "guistrings.research.info") {
            @Override
            protected void onPressed() {
                Minecraft.getMinecraft().displayGuiScreen(new GuiResearchBook(new ContainerResearchBook(getContainer().player, 0, 0, 0)));
            }
        };
        addGuiElement(info);
        if(getContainer().researcherName == null) {
            info.setEnabled(false);
        }

        int x, y;
        for (int i = 0; i < layoutSlots.length; i++) {
            x = (i % 3) * 18 + 98;
            y = (i / 3) * 18 + 98;
            layoutSlots[i] = new ItemSlot(x, y, null, this);
            addGuiElement(layoutSlots[i]);
        }
        addGuiElement(new Label(8, 8 + 18 * 2 + 12 + 1, "guistrings.research.progress"));
        addGuiElement(new Label(8 + 18, 8 + 3 * 18 + 10 + 4 + 10, "guistrings.research.input"));
        addGuiElement(new Label(8 + 5 * 18, 8 + 3 * 18 + 10 + 4 + 10, "guistrings.research.needed"));

        useAdjacentInventory = new Checkbox(8, 8 + 3 * 18 + 6, 16, 16, "guistrings.research.adj_inv") {
            @Override
            public void onToggled() {
                getContainer().toggleUseAdjacentInventory();
                setChecked(getContainer().useAdjacentInventory);
            }
        };
        Tooltip tip = new Tooltip(50, 20);
        tip.addTooltipElement(new Label(0, 0, "guistrings.research.use_adjacent_inventory"));
        useAdjacentInventory.setTooltip(tip);
        addGuiElement(useAdjacentInventory);

        invDir = new Button(80, 8 + 3 * 18 + 6, 40, 16, Direction.getDirectionFor(getContainer().tileEntity.inventoryDirection.ordinal()).getTranslationKey()) {
            @Override
            protected void onPressed() {
                getContainer().onDirPressed();
                refreshGui();
            }
        };
        tip = new Tooltip(50, 20);
        tip.addTooltipElement(new Label(0, 0, "guistrings.research.invDir"));
        invDir.setTooltip(tip);
        addGuiElement(invDir);
        invSide = new Button(120, 8 + 3 * 18 + 6, 40, 16, Direction.getDirectionFor(getContainer().tileEntity.inventorySide.ordinal()).getTranslationKey()) {
            @Override
            protected void onPressed() {
                getContainer().onSidePressed();
                refreshGui();
            }
        };
        tip = new Tooltip(50, 20);
        tip.addTooltipElement(new Label(0, 0, "guistrings.research.invSide"));
        invSide.setTooltip(tip);
        addGuiElement(invSide);
    }

    @Override
    public void setupElements() {
        String name = getContainer().researcherName == null ? "guistrings.research.no_researcher" : getContainer().researcherName;
        researcherLabel.setText(name);

        if (getContainer().researcherName == null) {
            cleanLayout();
            info.setEnabled(false);
        }else{
            info.setEnabled(true);
        }

        name = "guistrings.research.no_research";
        int goalNumber = getContainer().currentGoal;
        float progress = 0.f;
        if (goalNumber >= 0) {
            ResearchGoal g = ResearchGoal.getGoal(goalNumber);
            if (g != null) {
                name = g.getName();

                float total = g.getTotalResearchTime();
                float time = getContainer().progress;
                if (total == 0) {
                    total = time;
                }
                progress = time / total;
            }
            cleanLayout();
        } else {
            List<Integer> queue = getContainer().queuedResearch;
            if (!queue.isEmpty()) {
                int g1 = queue.get(0);
                ResearchGoal g = ResearchGoal.getGoal(g1);
                if (g != null) {
                    name = g.getName();
                    List<ItemStack> resources = g.getResources();
                    for (int i = 0; i < layoutSlots.length; i++) {
                        if (i >= resources.size()) {
                            layoutSlots[i].setItem(null);
                        } else {
                            ItemStack resource = resources.get(i);
                            if (!InventoryTools.doItemStacksMatch(resource, layoutSlots[i].getStack())) {
                                layoutSlots[i].setItem(resource.copy());
                            }
                        }
                    }
                }
            } else {
                cleanLayout();
            }
        }
        bar.setProgress(progress);
        researchGoalLabel.setText(name);

        useAdjacentInventory.setChecked(getContainer().useAdjacentInventory);
        invDir.setText(Direction.getDirectionFor(getContainer().tileEntity.inventoryDirection.ordinal()).getTranslationKey());
        invSide.setText(Direction.getDirectionFor(getContainer().tileEntity.inventorySide.ordinal()).getTranslationKey());
    }

    private void cleanLayout(){
        for (ItemSlot layoutSlot : layoutSlots) {
            layoutSlot.setItem(null);
        }
    }

}
