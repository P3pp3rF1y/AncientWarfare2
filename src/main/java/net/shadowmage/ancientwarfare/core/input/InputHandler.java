package net.shadowmage.ancientwarfare.core.input;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.options.GuiOptions;
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

private HashMap<String, Keybind> keybindMap = new HashMap<String, Keybind>();
private HashMap<Integer, Set<Keybind>> bindsByKey = new HashMap<Integer, Set<Keybind>>();

Configuration config;
private static final String keybinds = "e_keybinds";

public void loadConfig(Configuration config)
  {
  this.config = config;
  registerKeybind("keybind.options", Keyboard.KEY_F7);
  registerKeybind("keybind.alt_item_use", Keyboard.KEY_Z);
  
  InputCallback optionsCB = new InputCallback()
    {
    @Override
    public void onKeyReleased()
      {
     
      }
    @Override
    public void onKeyPressed()
      {
      Minecraft minecraft = Minecraft.getMinecraft();
      if(minecraft==null || minecraft.thePlayer==null || minecraft.currentScreen!=null)
        {       
        return;
        }
      minecraft.displayGuiScreen(new GuiOptions(new ContainerBase(minecraft.thePlayer, 0, 0, 0))); 
      }
    };
  addInputCallback("keybind.options", optionsCB);
  
  InputCallback itemUseCB = new InputCallback()
    {
    @Override
    public void onKeyReleased()
      {

      }
    @Override
    public void onKeyPressed()
      {
      Minecraft minecraft = Minecraft.getMinecraft();
      if(minecraft==null || minecraft.thePlayer==null || minecraft.currentScreen!=null)
        {       
        return;
        }
      ItemStack stack = minecraft.thePlayer.inventory.getCurrentItem();
      if(stack!=null && stack.getItem() instanceof IItemKeyInterface)
        {
        PacketItemInteraction pkt = new PacketItemInteraction();
        NetworkHandler.sendToServer(pkt);
        }
      }
    };
  addInputCallback("keybind.alt_item_use", itemUseCB);
  }

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
  boolean state = Keyboard.getEventKeyState();
  
  if(bindsByKey.containsKey(key))
    {
    Set<Keybind> keys = bindsByKey.get(key);
    for(Keybind k : keys)
      {
      if(state)
        {
        k.onKeyPressed();
        }
      else
        {
        k.onKeyReleased();
        }
      }
    }
  
//  if(state && key==Keyboard.KEY_Z)
//    {
//    ItemStack stack = player.inventory.getCurrentItem();
//    if(stack!=null && stack.getItem() instanceof IItemKeyInterface)
//      {
//      PacketItemInteraction pkt = new PacketItemInteraction();
//      NetworkHandler.sendToServer(pkt);
//      }
//    }
//  else if(key==Keyboard.KEY_F7)
//    {
//    minecraft.displayGuiScreen(new GuiOptions(new ContainerBase(player, 0, 0, 0)));
//    }
  }

public void registerKeybind(String name, int keyCode)
  {
  if(!keybindMap.containsKey(name))
    {    
    int key = config.get(keybinds, name, keyCode).getInt(keyCode);
    Keybind k = new Keybind(name, key);
    keybindMap.put(name, k);
    if(!bindsByKey.containsKey(key))
      {
      bindsByKey.put(key, new HashSet<Keybind>());      
      }
    bindsByKey.get(key).add(k);
    }
  config.save();  
  }

public void reassignKeybind(String name, int newKey)
  {
  Keybind k = keybindMap.get(name);
  if(k==null){return;}
  
  config.get(keybinds, name, 0).set(newKey);
  config.save();
  
  reassignKeyCode(k, newKey);  
  }

private void reassignKeyCode(Keybind k, int newKey)
  {
  bindsByKey.get(k.key).remove(k);
  k.key = newKey;
  
  if(!bindsByKey.containsKey(newKey))
    {
    bindsByKey.put(newKey, new HashSet<Keybind>());      
    }
  bindsByKey.get(newKey).add(k);
  }

public void addInputCallback(String name, InputCallback cb)
  {
  keybindMap.get(name).inputHandlers.add(cb);
  }

public Collection<Keybind> getKeybinds()
  {
  return keybindMap.values();
  }

public static final class Keybind
{
List<InputCallback> inputHandlers = new ArrayList<InputCallback>();

private int key;
private String name;

private Keybind(String name, int key)
  {
  this.name = name;
  this.key = key;
  }

public String getName()
  {
  return name;
  }

public int getKeyCode()
  {
  return key;
  }

public void onKeyPressed()
  {
  for(InputCallback c : inputHandlers)
    {
    c.onKeyPressed();
    }
  }

public void onKeyReleased()
  {
  for(InputCallback c : inputHandlers)
    {
    c.onKeyReleased();
    }
  }
}

public static abstract class InputCallback
{
public abstract void onKeyPressed();
public abstract void onKeyReleased();
}

}
