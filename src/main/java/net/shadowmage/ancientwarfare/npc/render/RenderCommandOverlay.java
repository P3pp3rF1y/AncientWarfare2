package net.shadowmage.ancientwarfare.npc.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import net.shadowmage.ancientwarfare.npc.npc_command.NpcCommand;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class RenderCommandOverlay
{
private RenderCommandOverlay(){}
public static final RenderCommandOverlay INSTANCE = new RenderCommandOverlay();
private Gui gui = new Gui();

private List<Entity> targetEntities = new ArrayList<Entity>();
private MovingObjectPosition target = null;

@SubscribeEvent
public void onRenderTick(RenderTickEvent evt)
  {
  Minecraft mc = Minecraft.getMinecraft();
  if(mc==null || mc.theWorld==null || mc.currentScreen!=null)
    {
    return;
    } 
  EntityPlayer player = mc.thePlayer;
  if(player==null || player.getCurrentEquippedItem()==null || !(player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton)){return;}
  ScaledResolution sr = new ScaledResolution(mc.gameSettings, mc.displayWidth, mc.displayHeight);  
  int x = sr.getScaledWidth()-10;
  gui.drawString(mc.fontRenderer, "foo.target:", x-mc.fontRenderer.getStringWidth("foo.target:"), 0, 0xffffffff);
  if(target!=null)
    {
    if(target.typeOfHit==MovingObjectType.ENTITY)
      {
      String t = target.entityHit.getCommandSenderName();
      gui.drawString(mc.fontRenderer, t, x-mc.fontRenderer.getStringWidth(t), 10, 0xffffffff);      
      }
    else if(target.typeOfHit==MovingObjectType.BLOCK)
      {
      String t = target.blockX+","+target.blockY+","+target.blockZ;
      gui.drawString(mc.fontRenderer, t, x-mc.fontRenderer.getStringWidth(t), 10, 0xffffffff);
      }
    }  
  gui.drawString(mc.fontRenderer, "foo.npcs:", 10, 0, 0xffffffff);
  for(int i =0; i < targetEntities.size();i++)
    {
    gui.drawString(mc.fontRenderer, targetEntities.get(i).getCommandSenderName(), 10, 10+10*i, 0xffffffff);
    }
  targetEntities.clear();
  target = null;
  }

@SubscribeEvent
public void onRenderLast(RenderWorldLastEvent evt)
  {
  Minecraft mc = Minecraft.getMinecraft();
  if(mc==null){return;}
  if(mc.currentScreen!=null){return;}
  EntityPlayer player = mc.thePlayer;
  if(player==null){return;}
  if(player.getCurrentEquippedItem()==null || !(player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton)){return;}
  MovingObjectPosition pos = RayTraceUtils.getPlayerTarget(player, 120, 0);//TODO load range from config
  target = pos;
  if(pos!=null)
    {
    AxisAlignedBB bb=null;
    if(pos.typeOfHit==MovingObjectType.BLOCK)
      {
      bb = AxisAlignedBB.getAABBPool().getAABB(pos.blockX, pos.blockY, pos.blockZ, pos.blockX+1.d, pos.blockY+1.d, pos.blockZ+1.d).expand(0.1d, 0.1d, 0.1d);
      }
    else if(pos.typeOfHit==MovingObjectType.ENTITY && pos.entityHit.boundingBox!=null && pos.entityHit instanceof EntityLivingBase)
      {
      bb = pos.entityHit.boundingBox.copy();
      Entity e = pos.entityHit;
      float t = 1.f-evt.partialTicks;
      double dx = e.posX - e.lastTickPosX;
      double dy = e.posY - e.lastTickPosY;
      double dz = e.posZ - e.lastTickPosZ;
      bb.offset(t*-dx, t*-dy, t*-dz);
      }
    if(bb!=null)
      {
      bb = RenderTools.adjustBBForPlayerPos(bb, player, evt.partialTicks);
      RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
      }
    }
  World world = mc.theWorld;
  List<Integer> targets = NpcCommand.INSTANCE.getCommandedNpcs();
  Entity e;
  AxisAlignedBB bb = null;
  Iterator<Integer> it = targets.iterator();
  Integer targetID;
  while(it.hasNext() && (targetID=it.next())!=null)
    {
    e = world.getEntityByID(targetID);
    if(e!=null)
      {
      targetEntities.add(e);
      if(e.boundingBox==null){continue;}
      bb=e.boundingBox.copy();//TODO all this bb-rendering could potentially be moved to the entity itself
      float t = 1.f-evt.partialTicks;
      double dx = e.posX - e.lastTickPosX;
      double dy = e.posY - e.lastTickPosY;
      double dz = e.posZ - e.lastTickPosZ;
      bb.offset(t*-dx, t*-dy, t*-dz);
      bb = RenderTools.adjustBBForPlayerPos(bb, player, evt.partialTicks);
      RenderTools.drawOutlinedBoundingBox(bb, 1.f, 0.f, 0.f);
      }
    else
      {
      it.remove();//TODO validation of list and building of cached entity-list could be moved to a tick-handler instead of render-tick
      }
    }
  }

}
