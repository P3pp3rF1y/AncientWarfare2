package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

public class ContainerWorksiteReedFarm extends ContainerWorksiteBase {

    public ContainerWorksiteReedFarm(EntityPlayer player, BlockPos pos) {
        super(player, pos);

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
        guiHeight = addPlayerSlots(layerY) + 8;
    }

}
