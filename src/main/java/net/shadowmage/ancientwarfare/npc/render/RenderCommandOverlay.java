package net.shadowmage.ancientwarfare.npc.render;

import java.util.ArrayList;
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
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.RenderTickEvent;

public class RenderCommandOverlay
{
private RenderCommandOverlay(){}
public static final RenderCommandOverlay INSTANCE = new RenderCommandOverlay();
private Gui gui = new Gui();

/**
 * TODO move this off into separate class for datas, as this is a _render_ class...
 */
private List<Entity> targetEntities = new ArrayList<Entity>();
private List<String> entityNames = new ArrayList<String>();
private MovingObjectPosition target = null;
String targetString = null;

public MovingObjectPosition getClientTarget()
  {
  return target;
  }

@SubscribeEvent
public void onClientTick(ClientTickEvent evt)
  {
  targetEntities.clear();
  target = null;
  entityNames.clear();
  targetString=null;
  Minecraft mc = Minecraft.getMinecraft();
  if(mc==null || mc.thePlayer==null || mc.currentScreen!=null || mc.thePlayer.getCurrentEquippedItem()==null || !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton)){return;}
  target = RayTraceUtils.getPlayerTarget(mc.thePlayer, 120, 0);
  ItemCommandBaton.getCommandedEntities(mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), targetEntities);
  if(target!=null)
    {
    if(target.typeOfHit==MovingObjectType.BLOCK)
      {
      targetString = target.blockX+","+target.blockY+","+target.blockZ;
      }
    else if(target.typeOfHit==MovingObjectType.ENTITY)
      {
      if(target.entityHit instanceof NpcBase)
        {
        targetString = ((NpcBase)target.entityHit).getNpcFullType();
        targetString = StatCollector.translateToLocal("npc."+targetString+".name");
        }
      else
        {
        targetString = StatCollector.translateToLocal(target.entityHit.getCommandSenderName());
        }
      }
    }
  String name;
  NpcBase npc;
  for(Entity e : targetEntities)
    {
    if(e instanceof NpcBase)
      {
      npc = (NpcBase)e;
      name = StatCollector.translateToLocal("npc."+((NpcBase)e).getNpcFullType()+".name");
      if(npc.hasCustomNameTag())
        {
        name = name + " : "+npc.getCustomNameTag();
        }
      }
    else{name = StatCollector.translateToLocal(e.getCommandSenderName());}
    entityNames.add(name);
    }
  }

@SubscribeEvent
public void onRenderTick(RenderTickEvent evt)
  {
  Minecraft mc = Minecraft.getMinecraft();
  if(mc==null || mc.theWorld==null || mc.currentScreen!=null || mc.gameSettings.showDebugInfo)
    {
    return;
    } 
  EntityPlayer player = mc.thePlayer;
  if(player==null || player.getCurrentEquippedItem()==null || !(player.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton)){return;}
  ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);  
  int x = sr.getScaledWidth()-10;
  String header = StatCollector.translateToLocal("guistrings.npc.target");
  gui.drawString(mc.fontRenderer, header, x-mc.fontRenderer.getStringWidth(header), 0, 0xffffffff);
  if(targetString!=null)
    {
    String t = targetString;
    gui.drawString(mc.fontRenderer, t, x-mc.fontRenderer.getStringWidth(t), 10, 0xffffffff);      
    }  
  header = StatCollector.translateToLocal("guistrings.npc.commanded");
  gui.drawString(mc.fontRenderer, header, 10, 0, 0xffffffff);
  for(int i =0; i < entityNames.size();i++)
    {
    gui.drawString(mc.fontRenderer, entityNames.get(i), 10, 10+10*i, 0xffffffff);
    }
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
  MovingObjectPosition pos = target;
  if(pos!=null)
    {
    AxisAlignedBB bb=null;
    if(pos.typeOfHit==MovingObjectType.BLOCK)
      {
      bb = AxisAlignedBB.getBoundingBox(pos.blockX, pos.blockY, pos.blockZ, pos.blockX+1.d, pos.blockY+1.d, pos.blockZ+1.d).expand(0.1d, 0.1d, 0.1d);
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
  AxisAlignedBB bb = null;  
  for(Entity e : targetEntities)
    {
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
  }

}
