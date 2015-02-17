package net.shadowmage.ancientwarfare.modeler.gui;

import net.minecraft.client.Minecraft;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.GuiElement;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.model.ModelBaseAW;
import net.shadowmage.ancientwarfare.core.model.ModelPiece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiPieceSelection extends GuiContainerBase {

    CompositeScrolled area;
    GuiModelEditor parent;

    Label selectedPiece;

    Label selectedPieceDisplayLabel;

    ModelPiece excludedPiece;

    public GuiPieceSelection(GuiModelEditor parent) {
        super(parent.getContainer(), 256, 240, defaultBackground);
        this.parent = parent;
    }

    public GuiPieceSelection(GuiModelEditor parent, ModelPiece excludedPiece) {
        super(parent.getContainer(), 256, 240, defaultBackground);
        this.parent = parent;
        this.excludedPiece = excludedPiece;
    }

    @Override
    public void initElements() {
        area = new CompositeScrolled(this, 0, 40, 256, 200);
        addGuiElement(area);

        selectedPieceDisplayLabel = new Label(8, 8, "No Selection");
        addGuiElement(selectedPieceDisplayLabel);

        Button button = new Button(256 - 8 - 55 - 55 - 4, 8, 55, 12, "Cancel") {
            @Override
            protected void onPressed() {
                getContainer().setGui(parent);
                Minecraft.getMinecraft().displayGuiScreen(parent);
            }
        };
        addGuiElement(button);

        button = new Button(256 - 8 - 55, 8, 55, 12, "Done") {
            @Override
            protected void onPressed() {
                if (selectedPiece != null) {
                    onPieceSelected(pieceMap.get(selectedPiece));
                    getContainer().setGui(parent);
                    Minecraft.getMinecraft().displayGuiScreen(parent);
                }
            }
        };
        addGuiElement(button);

        int totalHeight = 3;

        ModelBaseAW model = parent.modelWidget.getModel();
        List<ModelPiece> pieces = new ArrayList<ModelPiece>();
        model.getPieces(pieces);

        Label label;
        for (ModelPiece piece : pieces) {
            if (piece == excludedPiece) {
                continue;
            }

            label = new Label(3, totalHeight, piece.getName());
            label.addNewListener(new Listener(Listener.MOUSE_UP) {
                @Override
                public boolean onEvent(GuiElement widget, ActivationEvent evt) {
                    if (widget.isMouseOverElement(evt.mx, evt.my)) {
                        selectedPiece = (Label) widget;
                        selectedPieceDisplayLabel.setText("Selected Piece: " + pieceMap.get(widget).getName());
                    }
                    return true;
                }
            });
            area.addGuiElement(label);
            pieceMap.put(label, piece);
            totalHeight += 12;
        }

        area.setAreaSize(totalHeight + 8);
    }

    private HashMap<Label, ModelPiece> pieceMap = new HashMap<Label, ModelPiece>();

    @Override
    public void setupElements() {

    }

    /**
     * anonymous classes should override for piece-selection functionality
     */
    protected void onPieceSelected(ModelPiece piece) {

    }

}
