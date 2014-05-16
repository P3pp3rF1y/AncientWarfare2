package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;

public class ContainerWorksiteFishFarm extends ContainerWorksiteBase
{

public ContainerWorksiteFishFarm(EntityPlayer player, int x, int y, int z)
  {
  super(player, x, y, z);  
  int layerY = 8;
  int labelGap = 12;
  topLabel = layerY;
  layerY+=labelGap;
  
  layerY = addSlots(8, layerY, 0, 27)+4;
  playerLabel = layerY;
  layerY+=labelGap;  
  guiHeight = addPlayerSlots(player, 8, layerY, 4)+8;  
  }

}
