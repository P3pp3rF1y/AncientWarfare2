package net.shadowmage.ancientwarfare.automation.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.shadowmage.ancientwarfare.automation.tile.TileTorqueGeneratorWaterwheel;

public class RenderTileTorqueGeneratorWaterwheel extends TileEntitySpecialRenderer
{

public RenderTileTorqueGeneratorWaterwheel()
  {
  
  }

@Override
public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTick)
  {
  GL11.glPushMatrix();
  ForgeDirection d = ForgeDirection.getOrientation(te.getBlockMetadata());
  GL11.glTranslated(x+0.5d, y+0.5d, z+0.5d);
  GL11.glRotatef(getRotation(d), 0, 1, 0);
  drawShaftLine(0,0,0,0,0,1);
  GL11.glTranslatef(0, 0, 0.50f);
  TileTorqueGeneratorWaterwheel wheel = (TileTorqueGeneratorWaterwheel)te;
  
  float speed = wheel.rotationSpeed;  
  double angle = wheel.rotationAngle - (1.f-partialTick*speed);
  
  GL11.glRotatef((float)-angle, 0, 0, 1);
  drawBlade();
  for(int i = 0; i < 3; i++)
    {
    GL11.glRotatef(90.f, 0, 0, 1);
    drawBlade();
    }
  GL11.glTranslatef(0, 0.25f, 0);
  drawPointAtCurrentOrigin();
  GL11.glPopMatrix();
  }

private float getRotation(ForgeDirection d)
  {
  float offset = 0.f;
  return offset - (d==ForgeDirection.NORTH ? 0.f : d==ForgeDirection.EAST? 90.f :d==ForgeDirection.SOUTH? 180.f : 270.f);
  }

private void drawBlade()
  {
  drawShaftLine(0, 0, 0, 0, 0.25f, 0);
  drawShaftLine(0, 0, 0.5f, 0, 0.25f, 0.5f);
  drawShaftLine(0, 0.25f, 0, 0, 0.25f, 0.5f);
  }

private void drawShaftLine(float x1, float y1, float z1, float x2, float y2, float z2)
  {
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glDisable(GL11.GL_LIGHTING);
  
  GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
  GL11.glLineWidth(5.f);
  GL11.glBegin(GL11.GL_LINE_LOOP);
  GL11.glVertex3f(x1, y1, z1);
  GL11.glVertex3f(x2, y2, z2);
  GL11.glEnd();

  GL11.glEnable(GL11.GL_TEXTURE_2D);
  GL11.glEnable(GL11.GL_LIGHTING);
  }

private void drawPointAtCurrentOrigin()
  {
  //debug point rendering
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glColor4f(1.f, 0.f, 0.f, 1.f);
  GL11.glPointSize(10.f);
  GL11.glBegin(GL11.GL_POINTS);
  GL11.glVertex3f((float)0, (float)0, (float)0);
  GL11.glEnd();
  GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  GL11.glEnable(GL11.GL_LIGHTING);
  }

}
