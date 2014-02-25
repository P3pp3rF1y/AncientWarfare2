/**
   Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
   This software is distributed under the terms of the GNU General Public License.
   Please see COPYING for precise license information.

   This file is part of Ancient Warfare.

   Ancient Warfare is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   Ancient Warfare is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package shadowmage.ancient_framework.client.input;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import shadowmage.ancient_framework.common.config.Statics;
import shadowmage.ancient_framework.common.network.GUIHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;

public class KeybindManager extends KeyHandler
{

/**
 * vanilla keybinds setup, used to access/control base mod keybinds for team and options menu
 * options menu has further options to change other AW settings
 */
static KeyBinding options = new KeyBinding("AW-options", Keyboard.KEY_F7);
static KeyBinding teamControl = new KeyBinding("AW-TeamControl", Keyboard.KEY_F6);
private static KeyBinding[] keys = new KeyBinding[]{options, teamControl};
private static boolean[] keyRepeats = new boolean []{false, false};

/**
 * @param keyBindings
 * @param repeatings
 */
public KeybindManager()
  {
  super(keys, keyRepeats);
  }

private static List<Keybind> keybinds = new ArrayList<Keybind>();
private static List<IHandleInput> mouseInputHandlers = new ArrayList<IHandleInput>();
static int mouseX;
static int mouseY;
static boolean[] mouseButtonStates = new boolean[3];//left, right, middle/wheel?
//static int mouseWheelState = 0;

public static void addMouseHandler(IHandleInput mouseHandler)
  {
  mouseInputHandlers.add(mouseHandler);
  }

public static List<Keybind> getKeybinds()
  {
  return keybinds;
  }

public static void addKeybind(Keybind kb)
  {
  keybinds.add(kb);
  }

public static void onTick()
  {
  Iterator<Keybind> it = keybinds.iterator();
  Keybind kb;
  while(it.hasNext())
    {
    kb = it.next();
    kb.changedThisTick = false;
    boolean down = Keyboard.isKeyDown(kb.keyCode);
    if(down && !kb.isPressed)
      {
      kb.isPressed = true;
      kb.owner.onKeyPressed(kb);
      kb.changedThisTick = true;
      }    
    else if(!down && kb.isPressed)
      {
      kb.isPressed = false;
      kb.owner.onKeyUp(kb);
      kb.changedThisTick = true;
      }
    } 
  if(mouseInputHandlers.isEmpty())
    {
    return;
    }
  int x = Mouse.getX();
  int y = Mouse.getY();
  if(x!=mouseX || y!=mouseY)
    {
    mouseX = x;
    mouseY = y;
    for(IHandleInput handle : mouseInputHandlers)
      {
      handle.onMouseMoved(x, y);
      }
    }
  boolean down;
  for(int i = 0; i< 3; i++)
    {
    down = Mouse.isButtonDown(i);
    if(down && !mouseButtonStates[i])
      {
      mouseButtonStates[i] = down;
      for(IHandleInput handle : mouseInputHandlers)
        {
        handle.onMouseButtonPressed(i);
        }
      }
    else if(!down && mouseButtonStates[i])
      {
      mouseButtonStates[i] = down;
      for(IHandleInput handle : mouseInputHandlers)
        {
        handle.onMouseButtonUp(i);
        }
      }
    }
  }

@Override
public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat)
  {
  if(!tickEnd)
    {
    return;
    }
  if(Minecraft.getMinecraft().currentScreen==null && Minecraft.getMinecraft().thePlayer!=null && Minecraft.getMinecraft().theWorld!=null)
    {    
    if(kb==options)
      {
      GUIHandler.instance().openGUI(Statics.guiOptions, Minecraft.getMinecraft().thePlayer,  0, 0, 0);
      }
    else if(kb==teamControl)
      {
      GUIHandler.instance().openGUI(Statics.guiTeamControl, Minecraft.getMinecraft().thePlayer, 0, 0, 0);
      }    
    }
  }

@Override
public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd)
  {
  
  }

@Override
public String getLabel()
  {
  return "Ancient Warfare Keybind Handler";
  }

@Override
public EnumSet<TickType> ticks()
  {
  return EnumSet.of(TickType.CLIENT);
  }

}
