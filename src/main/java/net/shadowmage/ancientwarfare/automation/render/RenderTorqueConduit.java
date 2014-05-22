package net.shadowmage.ancientwarfare.automation.render;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueTransportConduit;
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
  block.setBlockBoundsForItemRender();
  renderer.setRenderBoundsFromBlock(block);

  Tessellator tessellator = Tessellator.instance;
  IIcon icon;
  icon = block.getIcon(0, 2);
  
  tessellator.startDrawingQuads();
  tessellator.setNormal(0.0F, -1F, 0.0F);
  renderer.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, icon);
  tessellator.draw();
  
  tessellator.startDrawingQuads();
  tessellator.setNormal(0.0F, 0.0F, -1F);
  renderer.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, icon);
  tessellator.draw();
  
  tessellator.startDrawingQuads();
  tessellator.setNormal(0.0F, 0.0F, 1.0F);
  renderer.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, icon);
  tessellator.draw();
  
  tessellator.startDrawingQuads();
  tessellator.setNormal(-1F, 0.0F, 0.0F);
  renderer.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, icon);
  tessellator.draw();
  
  tessellator.startDrawingQuads();
  tessellator.setNormal(1.0F, 0.0F, 0.0F);
  renderer.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, icon);
  tessellator.draw();

  icon = block.getIcon(0, 0);
  tessellator.startDrawingQuads();
  tessellator.setNormal(0.0F, 1.0F, 0.0F);
  renderer.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, icon);
  tessellator.draw();
  
//  renderer.renderBlockAsItem(block, 3, 1.f);
  }

@Override
public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer)
  {
  float min  = 0.1875f, max  = 0.8125f;
  float min2 = 0.1250f, max2 = 0.8750f;
  float min3 = 0.0625f, max3 = 0.9375f;
  TileTorqueTransportConduit tile = (TileTorqueTransportConduit) world.getTileEntity(x, y, z);
  boolean[] sides = tile.getConnections();
  block.setBlockBounds(min, min, min, max, max, max);
  renderer.setRenderBoundsFromBlock(block);
  renderer.renderStandardBlock(block, x, y, z);
  int meta = world.getBlockMetadata(x, y, z);  
  
  if(sides[0])//down
    {
    renderer.setOverrideBlockTexture(block.getIcon(0, 2));  
    block.setBlockBounds(min, 0, min, max, min, max);
    renderer.setRenderBoundsFromBlock(block);
    renderer.renderStandardBlock(block, x, y, z);
    if(meta==0)
      {
      renderer.clearOverrideBlockTexture();
      block.setBlockBounds(min2, -0.01f, min2, max2, min3, max2);      
      renderer.setRenderBoundsFromBlock(block);
      renderer.renderStandardBlock(block, x, y, z);
      }//renderoutputlip
    }
  if(sides[1])//up
    {
    renderer.setOverrideBlockTexture(block.getIcon(0, 2));  
    block.setBlockBounds(min, max, min, max, 1, max);
    renderer.setRenderBoundsFromBlock(block);
    renderer.renderStandardBlock(block, x, y, z);
    if(meta==1)
      {
      renderer.clearOverrideBlockTexture();
      block.setBlockBounds(min2, max3, min2, max2, 1.01f, max2);
      renderer.setRenderBoundsFromBlock(block);
      renderer.renderStandardBlock(block, x, y, z);
      }//renderoutputlip
    }
  if(sides[2])//z-
    {
    renderer.setOverrideBlockTexture(block.getIcon(0, 2));  
    block.setBlockBounds(min, min, 0, max, max, min);
    renderer.setRenderBoundsFromBlock(block);
    renderer.renderStandardBlock(block, x, y, z);
    if(meta==2)
      {
      renderer.clearOverrideBlockTexture();
      block.setBlockBounds(min2, min2, -0.01f, max2, max2, min3);
      renderer.setRenderBoundsFromBlock(block);
      renderer.renderStandardBlock(block, x, y, z);
      }//renderoutputlip
    }
  if(sides[3])//z++
    {
    renderer.setOverrideBlockTexture(block.getIcon(0, 2));  
    block.setBlockBounds(min, min, max, max, max, 1);
    renderer.setRenderBoundsFromBlock(block);
    renderer.renderStandardBlock(block, x, y, z);
    if(meta==3)
      {
      renderer.clearOverrideBlockTexture();
      block.setBlockBounds(min2, min2, max3, max2, max2, 1.01f);
      renderer.setRenderBoundsFromBlock(block);
      renderer.renderStandardBlock(block, x, y, z);
      }//renderoutputlip
    }
  if(sides[4])//x--
    {
    renderer.setOverrideBlockTexture(block.getIcon(0, 2));  
    block.setBlockBounds(0, min, min, min, max, max);
    renderer.setRenderBoundsFromBlock(block);
    renderer.renderStandardBlock(block, x, y, z);
    if(meta==4)
      {
      renderer.clearOverrideBlockTexture();
      block.setBlockBounds(-0.01f, min2, min2, min3, max2, max2);
      renderer.setRenderBoundsFromBlock(block);
      renderer.renderStandardBlock(block, x, y, z);
      }//renderoutputlip
    }
  if(sides[5])
    {
    renderer.setOverrideBlockTexture(block.getIcon(0, 2));  
    block.setBlockBounds(max, min, min, 1, max, max);
    renderer.setRenderBoundsFromBlock(block);
    renderer.renderStandardBlock(block, x, y, z);
    if(meta==5)
      {
      renderer.clearOverrideBlockTexture();
      block.setBlockBounds(max3, min2, min2, 1.01f, max2, max2);
      renderer.setRenderBoundsFromBlock(block);
      renderer.renderStandardBlock(block, x, y, z);
      }//renderoutputlip
    }
  renderer.clearOverrideBlockTexture();
  return true;
  }

@Override
public boolean shouldRender3DInInventory(int modelId)
  {
  return true;
  }

@Override
public int getRenderId()
  {
  return renderID;
  }

}
