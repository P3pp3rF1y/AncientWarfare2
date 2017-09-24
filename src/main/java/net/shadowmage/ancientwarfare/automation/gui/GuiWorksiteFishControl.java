package net.shadowmage.ancientwarfare.automation.gui;

import net.shadowmage.ancientwarfare.automation.container.ContainerWorksiteFishControl;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;

public class GuiWorksiteFishControl extends GuiContainerBase<ContainerWorksiteFishControl> {

    private Checkbox fishBox;
    private Checkbox inkBox;

    public GuiWorksiteFishControl(ContainerBase par1Container) {
        super(par1Container, 168, 48);
    }

    @Override
    public void initElements() {
        fishBox = new Checkbox(8, 8, 16, 16, "guistrings.automation.harvest_fish") {
            @Override
            public void onToggled() {
                getContainer().harvestFish = checked();
                getContainer().sendSettingsToServer();
            }
        };
        addGuiElement(fishBox);

        inkBox = new Checkbox(8, 8 + 16, 16, 16, "guistrings.automation.harvest_ink") {
            @Override
            public void onToggled() {
                getContainer().harvestInk = checked();
                getContainer().sendSettingsToServer();
            }
        };
        addGuiElement(inkBox);
    }

    @Override
    public void setupElements() {
        fishBox.setChecked(getContainer().harvestFish);
        inkBox.setChecked(getContainer().harvestInk);
    }

    @Override
    protected boolean onGuiCloseRequested() {
        getContainer().sendSettingsToServer();
        NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_WORKSITE_FISH_FARM, getContainer().tileEntity.getPos());
        return false;
    }

}
