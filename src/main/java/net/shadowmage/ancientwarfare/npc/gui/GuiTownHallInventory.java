package net.shadowmage.ancientwarfare.npc.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.npc.container.ContainerTownHall;
import net.shadowmage.ancientwarfare.npc.gamedata.HeadquartersTracker;

public class GuiTownHallInventory extends GuiContainerBase<ContainerTownHall> {

    private NumberInput input;
    public GuiTownHallInventory(ContainerBase container) {
        super(container);
        this.ySize = 3 * 18 + 4 * 18 + 8 + 8 + 4 + 8 + 16;
        this.xSize = 178;
    }

    @Override
    public void initElements() {
        this.getContainer().addSlots();
        Button button = new Button(8, 8, 40, 12, "guistrings.npc.death_list") {
            @Override
            protected void onPressed() {
                getContainer().removeSlots();
                Minecraft.getMinecraft().displayGuiScreen(new GuiTownHallDeathList(GuiTownHallInventory.this));
            }
        };
        addGuiElement(button);
        
        int[] tpHubPos = HeadquartersTracker.get(player.worldObj).getTeleportHubPosition(this.player.worldObj);
        if (tpHubPos != null ) {
            button = new Button(50, 8, 54, 12, "Visit Hub") {
                @Override
                protected void onPressed() {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                    getContainer().teleportPlayer(player.getCommandSenderName());
                }
            };
            addGuiElement(button);
        }
        
        addGuiElement(new Label(110, 10, "guistrings.npc.town_range"));
        input = new NumberInput(145, 8, 24, getContainer().tileEntity.getRange(), this);
        input.setIntegerValue();
        addGuiElement(input);
    }

    @Override
    public void setupElements() {
        input.setValue(getContainer().tileEntity.getRange());
    }

    @Override
    protected boolean onGuiCloseRequested() {
        if(getContainer().tileEntity.getRange()!=input.getIntegerValue()) {
            getContainer().setRange(input.getIntegerValue());
        }
        return super.onGuiCloseRequested();
    }
}
