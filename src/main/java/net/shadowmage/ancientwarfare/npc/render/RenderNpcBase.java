package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

public class RenderNpcBase extends RenderBiped
{

//func_147906_a---drawLabel

public RenderNpcBase()
  {
  super(new ModelBiped(), 0.6f);
  }

@Override
public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
  {
  super.doRender(par1Entity, par2, par4, par6, par8, par9);
  }

@Override
protected ResourceLocation getEntityTexture(Entity par1Entity)
  {
  return ((NpcBase)par1Entity).getTexture();
  }

}
