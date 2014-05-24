package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.core.config.ClientOptions;
import net.shadowmage.ancientwarfare.npc.item.AWNpcItemLoader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderWorkLines
{

public static final RenderWorkLines INSTANCE = new RenderWorkLines();

@SubscribeEvent
public void renderLastEvent(RenderWorldLastEvent evt)
  {
  boolean render = ClientOptions.INSTANCE.getBooleanValue(ClientOptions.OPTION_RENDER_WORK_POINTS);
  if(!render){return;}
  Minecraft mc = Minecraft.getMinecraft();
  if(mc==null){return;}
  EntityPlayer player = mc.thePlayer;
  if(player==null){return;}  
  ItemStack stack = player.getCurrentEquippedItem();
  if(stack==null || stack.getItem()==null){return;}
  Item item = stack.getItem();
  if(item==AWNpcItemLoader.upkeepOrder)
    {
    //TODO all of this...
    }
  else if(item==AWNpcItemLoader.workOrder)
    {
    
    }
  //combat orders
  //routing slip
  }

public void renderUpkeepList()
  {
  
  }

public void renderWorkList()
  {
  
  }

public void renderCourierList()
  {
  
  }

public void renderCombatList()
  {
  
  }
}
