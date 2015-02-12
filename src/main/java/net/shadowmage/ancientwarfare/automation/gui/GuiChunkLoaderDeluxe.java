package net.shadowmage.ancientwarfare.automation.gui;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ChunkCoordIntPair;
import net.shadowmage.ancientwarfare.automation.container.ContainerChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;

public class GuiChunkLoaderDeluxe extends GuiContainerBase {

    ContainerChunkLoaderDeluxe container;

    public GuiChunkLoaderDeluxe(ContainerBase container) {
        super(container);
        this.container = (ContainerChunkLoaderDeluxe) container;
        this.xSize = 76;
        this.ySize = 76;
    }

    @Override
    public void initElements() {

    }

    @Override
    public void setupElements() {
        clearElements();
        int cx = container.chunkLoader.xCoord >> 4;
        int cz = container.chunkLoader.zCoord >> 4;
        ChunkCoordIntPair ccip;
        Button button;

        for (int x = cx - 2, xPos = 0; x <= cx + 2; x++, xPos++) {
            for (int z = cz - 2, yPos = 0; z <= cz + 2; z++, yPos++) {
                if (x == cx && z == cz) {
                    continue;
                }//center button added manually...
                ccip = new ChunkCoordIntPair(x, z);
                button = new ButtonChunk(8 + xPos * 12, 8 + yPos * 12, 12, 12, container.ccipSet.contains(ccip), ccip) {
                    @Override
                    protected void onPressed() {
                        NBTTagCompound tag = new NBTTagCompound();
                        tag.setBoolean("forced", true);
                        tag.setInteger("x", ccip.chunkXPos);
                        tag.setInteger("z", ccip.chunkZPos);
                        sendDataToContainer(tag);
                    }
                };
                addGuiElement(button);
            }
        }

        /**
         * 'center' button -- always forced
         */
        ccip = new ChunkCoordIntPair(cx, cz);
        button = new ButtonChunk(8 + 2 * 12, 8 + 2 * 12, 12, 12, true, ccip);
        addGuiElement(button);
    }


    private class ButtonChunk extends Button {
        boolean forced;
        ChunkCoordIntPair ccip;

        public ButtonChunk(int topLeftX, int topLeftY, int width, int height, boolean forced, ChunkCoordIntPair ccip) {
            super(topLeftX, topLeftY, width, height, forced ? "X" : "");
            this.ccip = ccip;
        }
    }

}
