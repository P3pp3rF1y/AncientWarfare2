package net.shadowmage.ancientwarfare.core.input;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketItemInteraction;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.common.gameevent.InputEvent.MouseInputEvent;

public class InputHandler
{

private static InputHandler instance = new InputHandler();
public static InputHandler instance(){return instance;}
private InputHandler(){}

@SubscribeEvent
public void onMouseInput(MouseInputEvent evt)
  {
  
  }

@SubscribeEvent
public void onKeyInput(KeyInputEvent evt)
  {
  Minecraft minecraft = Minecraft.getMinecraft();
  if(minecraft==null){return;}
  EntityPlayer player = minecraft.thePlayer;
  if(player==null){return;}
  
  int key = Keyboard.getEventKey();
  if(key==Keyboard.KEY_Z)
    {
    ItemStack stack = player.inventory.getCurrentItem();
    if(stack!=null && stack.getItem() instanceof IItemKeyInterface)
      {
      PacketItemInteraction pkt = new PacketItemInteraction();
      NetworkHandler.sendToServer(pkt);
      }
    }  
  }

}
