package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerWorksiteAnimalFarm extends ContainerWorksiteBase {

    public ContainerWorksiteAnimalFarm(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);

        int layerY = 8;
        int labelGap = 12;

        topLabel = layerY;
        layerY += labelGap;
        layerY = addSlots(8, layerY, 0, 27) + 4;
        frontLabel = layerY;
        layerY += labelGap;
        layerY = addSlots(8, layerY, 27, 3) + 4;
        bottomLabel = layerY;
        layerY += labelGap;
        layerY = addSlots(8, layerY, 30, 3) + 4;
        playerLabel = layerY;
        layerY += labelGap;
        guiHeight = addPlayerSlots(8, layerY, 4) + 8;
    }


}
