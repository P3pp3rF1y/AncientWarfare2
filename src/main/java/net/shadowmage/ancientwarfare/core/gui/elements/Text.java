package net.shadowmage.ancientwarfare.core.gui.elements;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;

/**
 * Text input field
 * height = 12px
 * @author Shadowmage
 *
 */
public class Text extends GuiElement
{

String text;
int cursorIndex;
FontRenderer fr;
boolean charInput;
boolean numInput;
boolean charSymbolInput;
boolean numSymbolInput;

public Text(int topLeftX, int topLeftY, int width, String defaultText)
  {
  super(topLeftX, topLeftY, width, 12);
  fr = Minecraft.getMinecraft().fontRenderer;
  this.text = defaultText;
  this.addDefaultListeners();
  }

protected void addDefaultListeners()
  {  
  this.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      if(enabled && visible && isMouseOverElement(evt.mx, evt.my))
        {
        setSelected(true);
        cursorIndex = text.length();
        }
      else
        {
        setSelected(false);
        }
      return true;
      }
    });
  
  this.addNewListener(new Listener(Listener.KEY_DOWN)
    {
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      if(enabled && visible && selected)
        {
        handleKeyInput(evt.key, evt.ch);
        }
      return true;
      }
    });
  }

protected void handleKeyInput(int keyCode, char ch)
  {
  boolean handled = false;
  switch(keyCode)
  {
  case Keyboard.KEY_LEFT:
    {
    handled = true;
    cursorIndex--;
    if(cursorIndex<0)
      {
      cursorIndex = 0;
      }
    }
    break;
  case Keyboard.KEY_RIGHT:
    {
    handled = true;
    cursorIndex++;
    if(cursorIndex > text.length())
      {
      cursorIndex = text.length();
      }
    }
    break;
  case Keyboard.KEY_RETURN:
    {
    handled = true;
    //TODO figure out a good callback mechanism for on-return pressed??
    }
    break;
  case Keyboard.KEY_BACK:
    {
    handled = true;
    handleBackspaceAction();
    }
    break;
  case Keyboard.KEY_DELETE:
    {
    handled = true;
    handleDeleteAction();
    }
    break;
  case Keyboard.KEY_HOME:
    {
    handled = true;
    cursorIndex = 0;
    }
    break;
  case Keyboard.KEY_END:
    {
    handled = true;
    cursorIndex = text.length();
    }
    break;
  }  
  if(!handled)
    {
    handleCharacter(ch);
    }
  }

protected void handleDeleteAction()
  {
  if(cursorIndex < text.length())
    {
    String newText = "";
    for(int i = 0; i< text.length(); i++)
      {
      if(i==cursorIndex)
        {
        continue;
        }
      newText = newText + text.charAt(i);
      }
    text = newText;
    }
  }

protected void handleBackspaceAction()
  {
  if(cursorIndex>0)
    {
    String newText = "";
    for(int i = 0; i < text.length(); i++)
      {
      if(i==cursorIndex-1)
        {
        continue;
        }
      newText = newText + text.charAt(i);
      }
    text = newText;
    cursorIndex--;
    }
  }

protected void handleCharacter(char ch)
  {  
  boolean allowed = false;
  for(char ch1 : allowedChars)
    {
    if(ch1==ch)
      {
      allowed = true;
      break;
      }
    }
  if(allowed)//is allowed character
    {
    String newText = "";
    for(int i = 0; i <= text.length(); i++)
      {
      if(i==cursorIndex)
        {
        newText = newText + ch;
        }
      if(i<text.length())
        {
        newText = newText + text.charAt(i);        
        }
      }
    text = newText;
    cursorIndex++;
    }
  }

protected char[] allowedChars = new char[]
      {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
       'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
       };

protected char[] allowedCharSymbols = new char[]
      {' ', '!','#','$','%','^','&','*','(',')','_','-','+','=',
       '{','}','[',']',':', ';','"','\'',',','<','.', '>', '/', '?'};

protected char[] allowedNums = new char[]{'1','2','3','4','5','6','7','8','9','0'};
protected char[] allowedNumSymbols = new char[]{'.','-'};


@Override
public void render(int mouseX, int mouseY, float partialTick)
  {
  GL11.glDisable(GL11.GL_LIGHTING);
  GL11.glDisable(GL11.GL_TEXTURE_2D);
  GL11.glColor4f(0.9f, 0.9f, 0.9f, 1.f);//lt-grey, for outline box
  
  GL11.glBegin(GL11.GL_QUADS);
  GL11.glVertex2f(renderX, renderY);
  GL11.glVertex2f(renderX, renderY+height);
  GL11.glVertex2f(renderX+width, renderY+height);
  GL11.glVertex2f(renderX+width, renderY);
  GL11.glEnd();

  GL11.glColor4f(0.0f, 0.0f, 0.0f, 1.f);//black, for the input box
  GL11.glBegin(GL11.GL_QUADS);
  GL11.glVertex2f(renderX+1, renderY+1);
  GL11.glVertex2f(renderX+1, renderY+height-1);
  GL11.glVertex2f(renderX+width-1, renderY+height-1);
  GL11.glVertex2f(renderX+width-1, renderY+1);
  GL11.glEnd();   

  GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.f);
  GL11.glEnable(GL11.GL_TEXTURE_2D);
  
  fr.drawString(text, renderX+2, renderY+2, 0xffffffff);

  if(selected)
    {
    int w = 0;
    for(int i = 0; i < cursorIndex; i++)
      {
      w+=fr.getCharWidth(text.charAt(i));
      }
    fr.drawString("_", renderX+2+w, renderY+3, 0xffff0000);
    }

  GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.f);
  }

public void setText(String text)
  {
  String in = text;
  text = "";
  for(int i = 0; i < in.length(); i++)
    {
    if(isAllowedCharacter(in.charAt(i)))
      {
      text = text + in.charAt(i);
      }
    }
  this.text = text;
  }

protected boolean isAllowedCharacter(char ch)
  {
  for(char ch1 : allowedChars)
    {
    if(ch==ch1)
      {
      return true;
      }
    }
  return false;
  }

}
