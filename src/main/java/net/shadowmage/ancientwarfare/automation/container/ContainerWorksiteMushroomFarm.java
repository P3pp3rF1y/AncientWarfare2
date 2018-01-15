package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteMushroomFarm;

public class ContainerWorksiteMushroomFarm extends ContainerWorksiteBase<WorkSiteMushroomFarm> {

    public ContainerWorksiteMushroomFarm(EntityPlayer player, int x, int y, int z) {
        super(player, x, y, z);

		int layerY = 78;

        frontLabel = layerY;
		layerY += LABEL_GAP;
		layerY = addSlots(tileEntity.plantableInventory, 8, layerY) + 4;
		playerLabel = layerY;
		layerY += LABEL_GAP;
		guiHeight = addPlayerSlots(layerY) + 8;
	}

}
