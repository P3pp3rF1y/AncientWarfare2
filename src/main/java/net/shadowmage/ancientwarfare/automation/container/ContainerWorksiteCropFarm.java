package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.shadowmage.ancientwarfare.automation.tile.worksite.WorkSiteCropFarm;

public class ContainerWorksiteCropFarm extends ContainerWorksiteBase<WorkSiteCropFarm> {

	public ContainerWorksiteCropFarm(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		int layerY = 78;

		frontLabel = layerY;
		layerY += LABEL_GAP;
		layerY = addSlots(tileEntity.plantableInventory, 8, layerY) + 4;
		bottomLabel = layerY;
		layerY += LABEL_GAP;
		layerY = addSlots(tileEntity.miscInventory, 8, layerY) + 4;
		playerLabel = layerY;
		layerY += LABEL_GAP;
		guiHeight = addPlayerSlots(layerY) + 8;
	}

}
