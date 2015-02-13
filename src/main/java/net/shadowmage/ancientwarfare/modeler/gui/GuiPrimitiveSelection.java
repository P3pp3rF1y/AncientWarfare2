package net.shadowmage.ancientwarfare.modeler.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.model.*;

public class GuiPrimitiveSelection extends GuiContainerBase {

    GuiModelEditor parent;

    public GuiPrimitiveSelection(GuiModelEditor parent) {
        super(parent.getContainer(), 256, 60, defaultBackground);
        this.parent = parent;
    }

    @Override
    public void initElements() {
        Label label = new Label(8, 8, "Select Primitive Type:");
        addGuiElement(label);

        Button button = new Button(8, 24, 60, 12, "Box") {
            @Override
            protected void onPressed() {
                ModelPiece piece = parent.getModelPiece();
                Primitive p = new PrimitiveBox(piece, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0);
                onPrimitiveCreated(p);
                getContainer().setGui(parent);
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        button = new Button(8 + 60, 24, 60, 12, "Triangle") {
            @Override
            protected void onPressed() {
                ModelPiece piece = parent.getModelPiece();
                PrimitiveTriangle p = new PrimitiveTriangle(piece);
                p.setBounds(0, 0, 0, 1, 1, 0, 1, 0, 0);
                onPrimitiveCreated(p);
                getContainer().setGui(parent);
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        button = new Button(8 + 60 + 60, 24, 60, 12, "Quad") {
            @Override
            protected void onPressed() {
                ModelPiece piece = parent.getModelPiece();
                PrimitiveQuad p = new PrimitiveQuad(piece);
                p.setBounds(0, 0, 1, 1);
                onPrimitiveCreated(p);
                getContainer().setGui(parent);
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        button = new Button(8 + 60 + 60 + 60, 24, 60, 12, "Cancel") {
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
     * should override to provide callback for when a new piece is created from the GUI
     */
    protected void onPrimitiveCreated(Primitive p) {

    }

}
