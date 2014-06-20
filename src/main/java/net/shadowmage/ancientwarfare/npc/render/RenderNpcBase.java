package net.shadowmage.ancientwarfare.npc.render;

import static net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED;
import static net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.core.util.AWTextureManager;
import net.shadowmage.ancientwarfare.npc.ai.NpcAI;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import org.lwjgl.opengl.GL11;

public class RenderNpcBase extends RenderBiped
{

//func_147906_a---drawLabel
List<Integer> renderTasks = new ArrayList<Integer>();

public RenderNpcBase()
  {
  super(new ModelBiped(), 0.6f);
  }

@Override
public void doRender(Entity par1Entity, double x, double y, double z, float par8, float par9)
  {  
  super.doRender(par1Entity, x, y, z, par8, par9);
  if(ClientOptions.INSTANCE.getBooleanValue(ClientOptions.OPTION_RENDER_NPC_ADDITIONAL_INFO))
    {
    NpcBase npc = (NpcBase)par1Entity;
    if(npc.isHostileTowards(renderManager.livingPlayer))
      {
      if(ClientOptions.INSTANCE.getBooleanValue(ClientOptions.OPTION_RENDER_NPC_HOSTILE_NAMES))
        {
        String name = getNameForRender(npc);        
        renderColoredLabel(npc, name, x, y, z, 64, 0x20ff0000, 0xffff0000);
        }
      }
    else
      {
      if(ClientOptions.INSTANCE.getBooleanValue(ClientOptions.OPTION_RENDER_NPC_FRIENDLY_NAMES))
        {
        String name = getNameForRender(npc);  
        renderColoredLabel(npc, name, x, y, z, 64, 0x20ffffff, 0xffffffff);
        }
      if(npc.canBeCommandedBy(renderManager.livingPlayer.getCommandSenderName()))
        {
        if(ClientOptions.INSTANCE.getBooleanValue(ClientOptions.OPTION_RENDER_NPC_AI))
          {
          renderNpcAITasks(npc, x, y, z, 64);
          }
        }
      }
    }
  }


@Override
protected void func_82420_a(EntityLiving par1EntityLiving, ItemStack par2ItemStack)
  {
  super.func_82420_a(par1EntityLiving, par2ItemStack);
  NpcBase npc = (NpcBase)par1EntityLiving;
  this.field_82423_g.heldItemLeft = this.field_82425_h.heldItemLeft = this.modelBipedMain.heldItemLeft = npc.getShieldStack()!=null ? 1 : 0;
  }

@Override
protected void renderEquippedItems(EntityLiving par1EntityLiving, float par2)
  {
  super.renderEquippedItems(par1EntityLiving, par2);
  NpcBase npc = (NpcBase)par1EntityLiving;
  ItemStack itemstack = npc.getShieldStack();  
  Item item;
  
  //TODO clean up this copypasta code
  float f1;
  if (itemstack != null && itemstack.getItem() != null)
    {
    item = itemstack.getItem();
    GL11.glPushMatrix();
    
        
    if (this.mainModel.isChild)
      {
      f1 = 0.5F;
      GL11.glTranslatef(0.0F, 0.625F, 0.0F);
      GL11.glRotatef(-20.0F, -1.0F, 0.0F, 0.0F);
      GL11.glScalef(f1, f1, f1);
      }
    
    

    this.modelBipedMain.bipedLeftArm.postRender(0.0625F);
    GL11.glTranslatef(0, 0.5625F, 0);//translate down to the 'hand'

    IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
    boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));

    if (item instanceof ItemBlock && (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(item).getRenderType())))
      {
      f1 = 0.5F;
      GL11.glTranslatef(0.0625F, 0.0625f, -0.25F);
      f1 *= 0.75F;
      GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
      GL11.glScalef(-f1, -f1, f1);
      }
    else if (item == Items.bow)
      {
      f1 = 0.625F;
      GL11.glTranslatef(0.125F, 0.0F, 0.375F);
      GL11.glRotatef(20.0F, 0.0F, 1.0F, 0.0F);
      GL11.glScalef(f1, -f1, f1);
      GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
      }
    else if (item.isFull3D())
      {
      f1 = 0.625F;

      if (item.shouldRotateAroundWhenRendering())
        {
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(0.0F, -0.125F, 0.0F);
        }

      GL11.glTranslatef(0.125F, 0.0625f, 0.0625F);
      GL11.glScalef(f1, -f1, f1);
      GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(35.0F, 0.0F, 1.0F, 0.0F);
      }
    else
      {
      f1 = 0.375F;
      GL11.glTranslatef(0.1875F, 0.0F, -0.0625F);
      GL11.glScalef(f1, f1, f1);
      GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(20.f, 0, 1, 0);
      }
    
    float f2;
    float f3;
    int i;

    if (itemstack.getItem().requiresMultipleRenderPasses())
      {
      for (i = 0; i < itemstack.getItem().getRenderPasses(itemstack.getItemDamage()); ++i)
        {
        int j = itemstack.getItem().getColorFromItemStack(itemstack, i);
        f2 = (float)(j >> 16 & 255) / 255.0F;
        f3 = (float)(j >> 8 & 255) / 255.0F;
        float f4 = (float)(j & 255) / 255.0F;
        GL11.glColor4f(f2, f3, f4, 1.0F);
        this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemstack, i);
        }
      }
    else
      {
      i = itemstack.getItem().getColorFromItemStack(itemstack, 0);
      float f5 = (float)(i >> 16 & 255) / 255.0F;
      f2 = (float)(i >> 8 & 255) / 255.0F;
      f3 = (float)(i & 255) / 255.0F;
      GL11.glColor4f(f5, f2, f3, 1.0F);
      this.renderManager.itemRenderer.renderItem(par1EntityLiving, itemstack, 0);
      }

    GL11.glPopMatrix();
    }
  }



protected boolean func_110813_b(EntityLivingBase par1EntityLivingBase)
  {
  return false;
  }

@Override
protected void func_147906_a(Entity p_147906_1_, String p_147906_2_, double p_147906_3_, double p_147906_5_, double p_147906_7_, int p_147906_9_)
  {
  //noop to disable vanilla nameplate rendering, custom label rendering handled through custom rendering
  }

private String getNameForRender(NpcBase npc)
  {
  String customName = npc.hasCustomNameTag() ? npc.getCustomNameTag() : "npc."+npc.getNpcFullType()+".name";
  customName = StatCollector.translateToLocal(customName);
  return customName + " "+getHealthForRender(npc);
  }

private String getHealthForRender(NpcBase npc)
  {
  String health = String.format("%.1f", npc.getHealth());
  return health;
  }

private void renderNpcAITasks(NpcBase entity, double x, double y, double z, int renderDistance)
  {
  double d3 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);

  if (d3 <= (double)(renderDistance * renderDistance) && this.renderManager.livingPlayer.canEntityBeSeen(entity))
    {
    float f = 1.6F;
    float f1 = 0.016666668F * f;
    GL11.glPushMatrix();
    GL11.glTranslatef((float)x + 0.0F, (float)y + entity.height + 0.5F, (float)z);
    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    GL11.glScalef(-f1, -f1, f1);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    int tasks = entity.getAITasks();
    int mask;    
    String icon;
    
    for(int i = 0; i<NpcAI.NUMBER_OF_TASKS; i++)
      {
      mask = 1<<i;
      if((tasks & mask)!=0)
        {
        renderTasks.add(mask);
        }
      }
    
    int offset = (renderTasks.size()*10/2);
    int startX = -offset;
    
    for(int i = 0; i < renderTasks.size();i++)
      {
      icon = getIconFor(renderTasks.get(i));
      renderIcon(icon, 16, 16, startX+i*20, -16);
      }    
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glPopMatrix();
    this.renderTasks.clear();
    }
  }

private void renderColoredLabel(NpcBase entity, String string, double x, double y, double z, int renderDistance, int color1, int color2)
  {
  double d3 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);

  if (d3 <= (double)(renderDistance * renderDistance) && this.renderManager.livingPlayer.canEntityBeSeen(entity))
    {
    FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
    float f = 1.6F;
    float f1 = 0.016666668F * f;
    GL11.glPushMatrix();
    GL11.glTranslatef((float)x + 0.0F, (float)y + entity.height + 0.5F, (float)z);
    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    GL11.glScalef(-f1, -f1, f1);
    GL11.glDisable(GL11.GL_LIGHTING);
    GL11.glDepthMask(false);
    GL11.glDisable(GL11.GL_DEPTH_TEST);
    GL11.glEnable(GL11.GL_BLEND);
    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    Tessellator tessellator = Tessellator.instance;

    GL11.glDisable(GL11.GL_TEXTURE_2D);
    tessellator.startDrawingQuads();
    int j = fontrenderer.getStringWidth(string) / 2;
    tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
    tessellator.addVertex((double)(-j - 1), (double)(-1 ), 0.0D);
    tessellator.addVertex((double)(-j - 1), (double)(8), 0.0D);
    tessellator.addVertex((double)(j + 1), (double)(8), 0.0D);
    tessellator.addVertex((double)(j + 1), (double)(-1), 0.0D);
    tessellator.draw();
    GL11.glEnable(GL11.GL_TEXTURE_2D);
    fontrenderer.drawString(string, -fontrenderer.getStringWidth(string) / 2, 0, color1);
    GL11.glEnable(GL11.GL_DEPTH_TEST);
    GL11.glDepthMask(true);
    fontrenderer.drawString(string, -fontrenderer.getStringWidth(string) / 2, 0, color2);
    GL11.glEnable(GL11.GL_LIGHTING);
    GL11.glDisable(GL11.GL_BLEND);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glPopMatrix();
    }
  }

@Override
protected ResourceLocation getEntityTexture(Entity par1Entity)
  {
  return ((NpcBase)par1Entity).getTexture();
  }

private void renderIcon(String tex, int width, int height, int x, int y)
  {
  Tessellator tess = Tessellator.instance;
  AWTextureManager.instance().bindLocationTexture(tex);
  int halfW = width/2;
  int halfH = height/2;
  tess.startDrawingQuads();
  tess.addVertexWithUV(x-halfW, y-halfH, 0, 0, 0); 
  tess.addVertexWithUV(x-halfW, y+halfH, 0, 0, 1);
  tess.addVertexWithUV(x+halfW, y+halfH, 0, 1, 1);
  tess.addVertexWithUV(x+halfW, y+-halfH, 0, 1, 0);
  tess.draw();
  }

private String getIconFor(int task)
  {
  switch(task)
  {
  case 0:
  return null;
  case NpcAI.TASK_ATTACK:
  return "ancientwarfare:textures/entity/npc/ai/task_attack.png";
  case NpcAI.TASK_UPKEEP:
  return "ancientwarfare:textures/entity/npc/ai/task_upkeep.png";
  case NpcAI.TASK_IDLE_HUNGRY:
  return "ancientwarfare:textures/entity/npc/ai/task_upkeep2.png";
  case NpcAI.TASK_GO_HOME:
  return "ancientwarfare:textures/entity/npc/ai/task_home.png";
  case NpcAI.TASK_WORK:
  return "ancientwarfare:textures/entity/npc/ai/task_work.png";
  case NpcAI.TASK_PATROL:
  return "ancientwarfare:textures/entity/npc/ai/task_patrol.png";
  case NpcAI.TASK_GUARD:
  return "ancientwarfare:textures/entity/npc/ai/task_guard.png";
  case NpcAI.TASK_FOLLOW:
  return "ancientwarfare:textures/entity/npc/ai/task_follow.png";
  case NpcAI.TASK_WANDER:
  return "ancientwarfare:textures/entity/npc/ai/task_wander.png";
  case NpcAI.TASK_MOVE:
  return "ancientwarfare:textures/entity/npc/ai/task_move.png";
  default:
  return null;  
  }
  }

}
