package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.world.ChunkCoordIntPair;
import net.shadowmage.ancientwarfare.automation.container.ContainerChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;

public class GuiChunkLoaderDeluxe extends GuiContainerBase<ContainerChunkLoaderDeluxe> {
    public GuiChunkLoaderDeluxe(ContainerBase container) {
        super(container);
        this.xSize = 76;
        this.ySize = 76;
    }

    @Override
    public void initElements() {

    }

    @Override
    public void setupElements() {
        clearElements();
        int cx = getContainer().tileEntity.xCoord >> 4;
        int cz = getContainer().tileEntity.zCoord >> 4;
        ChunkCoordIntPair ccip;
        Button button;

        for (int x = cx - 2, xPos = 0; x <= cx + 2; x++, xPos++) {
            for (int z = cz - 2, yPos = 0; z <= cz + 2; z++, yPos++) {
                if (x == cx && z == cz) {
                    continue;
                }//center button added manually...
                ccip = new ChunkCoordIntPair(x, z);
                button = new ButtonChunk(xPos, yPos, getContainer().ccipSet.contains(ccip), ccip) {
                    @Override
                    protected void onPressed() {
                        getContainer().force(ccip);
                    }
                };
                addGuiElement(button);
            }
        }

        /**
         * 'center' button -- always forced
         */
        ccip = new ChunkCoordIntPair(cx, cz);
        button = new ButtonChunk(2, 2, true, ccip);
        addGuiElement(button);
    }


    private class ButtonChunk extends Button {
        final ChunkCoordIntPair ccip;

        public ButtonChunk(int xPos, int yPos, boolean forced, ChunkCoordIntPair ccip) {
            super(8 + xPos * 12, 8 + yPos * 12, 12, 12, forced ? "X" : "");
            this.ccip = ccip;
        }
    }

}
