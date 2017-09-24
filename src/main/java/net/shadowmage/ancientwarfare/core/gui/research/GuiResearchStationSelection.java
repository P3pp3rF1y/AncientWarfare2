package net.shadowmage.ancientwarfare.core.gui.research;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchStation;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.ItemSlot;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Tooltip;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketResearchUpdate;
import net.shadowmage.ancientwarfare.core.research.ResearchGoal;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import org.lwjgl.input.Mouse;

public class GuiResearchStationSelection extends GuiContainerBase<ContainerResearchStation> {

    private final GuiResearchStation parent;

    private CompositeScrolled queueArea;
    private CompositeScrolled selectionArea;

    public GuiResearchStationSelection(GuiResearchStation parent, int x, int y) {
        super(parent.getContainer(), 400, 240);
        this.parent = parent;
        Mouse.setCursorPosition(x, y);
    }

    @Override
    public void initElements() {
        queueArea = new CompositeScrolled(this, 0, 40, 200, 200);
        addGuiElement(queueArea);

        selectionArea = new CompositeScrolled(this, 200, 40, 200, 200);
        addGuiElement(selectionArea);

        Label label = new Label(8, 8, "guistrings.research.queued_research");
        addGuiElement(label);

        label = new Label(240 + 8, 8, "guistrings.research.learnable_research");
        addGuiElement(label);
    }

    @Override
    public void setupElements() {
        selectionArea.clearElements();
        queueArea.clearElements();
        int goal = getContainer().currentGoal;

        int totalHeight = 8;

        if (goal >= 0) {
            totalHeight = addQueuedGoal(totalHeight, goal, false);
        }

        for (Integer g : getContainer().queuedResearch) {
            totalHeight = addQueuedGoal(totalHeight, g, true);
        }

        queueArea.setAreaSize(totalHeight + 8);

        totalHeight = 8;

        if (getContainer().researcherName != null) {
            for (Integer g : ResearchTracker.INSTANCE.getResearchableGoals(player.world, getContainer().researcherName)) {
                totalHeight = addSelectableGoal(totalHeight, g);
            }
        }

        selectionArea.setAreaSize(totalHeight + 8);
    }

    private int addQueuedGoal(int totalHeight, int goalNumber, boolean removeButton) {
        ResearchGoal g = ResearchGoal.getGoal(goalNumber);
        if (g == null) {
            return totalHeight;
        }
        String name = I18n.format(g.getName());
        if (!removeButton) {
            name = name + " (" + I18n.format("guistrings.research.current_goal") + ")";
        }

        Label label = new Label(8, totalHeight + 1, name);
        queueArea.addGuiElement(label);

        Tooltip selectableGoalTooltip = createGoal(label, g);

        if (removeButton) {
            GoalButton button = new GoalButton(200 - 8 - 12 - 12, totalHeight, 12, 12, g, false);
            button.setTooltip(selectableGoalTooltip);
            queueArea.addGuiElement(button);
        }
        return totalHeight + 12;
    }

    private int addSelectableGoal(int totalHeight, int goalNumber) {
        ResearchGoal g = ResearchGoal.getGoal(goalNumber);
        if (g == null) {
            return totalHeight;
        }

        Label label = new Label(8, totalHeight + 1, g.getName());
        selectionArea.addGuiElement(label);

        GoalButton button = new GoalButton(200 - 8 - 12 - 12, totalHeight, 12, 12, g, true);
        button.setTooltip(createGoal(label, g));
        selectionArea.addGuiElement(button);
        return totalHeight + 12;
    }

    private Tooltip createGoal(Label label, ResearchGoal g){
        Tooltip selectableGoalTooltip = new Tooltip(110, 75);
        selectableGoalTooltip.addTooltipElement(new Label(0, 0, I18n.format("guistrings.research.research_time", g.getTotalResearchTime())));
        selectableGoalTooltip.addTooltipElement(new Label(0, 10, "guistrings.research.resources_needed"));
        int x = 0, y = 0;
        for (ItemStack stack : g.getResources()) {
            selectableGoalTooltip.addTooltipElement(new ItemSlot(x * 18, y * 18 + 20, stack, this));
            x++;
            if (x > 2) {
                x = 0;
                y++;
            }
        }
        label.setTooltip(selectableGoalTooltip);
        return selectableGoalTooltip;
    }

    @Override
    protected boolean onGuiCloseRequested() {
        parent.refreshGui();
        getContainer().setGui(parent);
        getContainer().addSlots();
        Minecraft.getMinecraft().displayGuiScreen(parent);
        return false;
    }

    private class GoalButton extends Button {
        final ResearchGoal goal;
        final boolean add;

        public GoalButton(int topLeftX, int topLeftY, int width, int height, ResearchGoal goal, boolean add) {
            super(topLeftX, topLeftY, width, height, add ? "+" : "-");
            this.goal = goal;
            this.add = add;
        }

        @Override
        protected void onPressed() {
            PacketResearchUpdate pkt = new PacketResearchUpdate(getContainer().researcherName, goal.getId(), add, false);
            NetworkHandler.sendToServer(pkt);
        }
    }

}
