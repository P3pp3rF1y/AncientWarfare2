package net.shadowmage.ancientwarfare.core.input;

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
  
  }

}
