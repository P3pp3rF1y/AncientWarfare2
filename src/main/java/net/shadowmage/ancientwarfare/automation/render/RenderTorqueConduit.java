package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class RenderTorqueConduit implements ISimpleBlockRenderingHandler
{

public static int renderID = -1;

public RenderTorqueConduit()
  {
  if(renderID==-1)
    {
    renderID = RenderingRegistry.getNextAvailableRenderId();
    }
  }

@Override
public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer)
  {
  
  }

@Override
public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
  {
  return false;
  }

@Override
public boolean shouldRender3DInInventory(int modelId)
  {
  // TODO Auto-generated method stub
  return false;
  }

@Override
public int getRenderId()
  {
  return renderID;
  }

}
