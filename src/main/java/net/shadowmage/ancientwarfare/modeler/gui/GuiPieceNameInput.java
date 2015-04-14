package net.shadowmage.ancientwarfare.modeler.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;

public class GuiPieceNameInput extends GuiContainerBase {

    GuiModelEditor parent;
    Text input;

    public GuiPieceNameInput(GuiModelEditor parent) {
        super(parent.getContainer(), 256, 60);
        this.parent = parent;
    }

    @Override
    public void initElements() {
        Label label = new Label(8, 8, "Name: ");
        addGuiElement(label);

        input = new Text(60, 8, 180, "", this);
        addGuiElement(input);

        Button button = new Button(8, 24, 55, 12, "Accept") {
            @Override
            protected void onPressed() {
                onNameSelected(input.getText());
                getContainer().setGui(parent);
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        button = new Button(8 + 55 + 4, 24, 55, 12, "Cancel") {
            @Override
            protected void onPressed() {
                getContainer().setGui(parent);
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);
    }

    @Override
    public void setupElements() {

    }

    /**
     * anonymous classes should override for a callback mechanism for when a name is selected
     */
    protected void onNameSelected(String name) {

    }

}
