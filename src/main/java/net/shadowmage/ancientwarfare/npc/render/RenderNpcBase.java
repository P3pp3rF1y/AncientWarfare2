package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderNpcBase extends RenderBiped
{

ResourceLocation testLoc;
//func_147906_a---drawLabel

public RenderNpcBase()
  {
  super(new ModelBiped(), 0.6f);
  testLoc = new ResourceLocation("ancientwarfare:textures/entity/npc/npcDefault.png");
  }

@Override
public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
  {
  super.doRender(par1Entity, par2, par4, par6, par8, par9);
  }

@Override
protected ResourceLocation getEntityTexture(Entity par1Entity)
  {
  return testLoc;
  }

}
