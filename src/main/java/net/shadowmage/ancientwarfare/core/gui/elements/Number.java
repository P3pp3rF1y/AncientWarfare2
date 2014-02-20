package net.shadowmage.ancientwarfare.core.gui.elements;

import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;

public class Number extends Text
{

boolean allowDecimal = true;
boolean allowNeg;
boolean integerValue;
float value;
int decimalPlaces = 2;

public Number(int topLeftX, int topLeftY, int width, float defaultText)
  {
  super(topLeftX, topLeftY, width, String.format("%.2f", defaultText));
  this.value = defaultText;
  }

public Number setAllowNegative()
  {
  this.allowNeg = true;
  return this;
  }

public Number setIntegerValue()
  {
  this.integerValue = true;
  this.decimalPlaces = 0;
  this.allowDecimal = false;
  this.setText(text);  
  return this;
  }

@Override
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

@Override
public void setText(String text)
  {
  try
    {
    Float fl = Float.parseFloat(text);
    this.text = String.format("%."+decimalPlaces+"f", fl);
    this.value = fl;
    }
  catch(NumberFormatException e)
    {
    this.text = "0";
    }
  }

public Number setValue(float val)
  {
  this.value = val;
  this.setText(String.valueOf(val));
  return this;
  }

protected void handleCharacter(char ch)
  {  
  boolean allowed = false;
  if(ch=='.' && allowDecimal){allowed = true;}
  else if(ch=='-' && allowNeg){allowed = true;}
  if(!allowed)
    {
    for(char ch1 : allowedNums)
      {
      if(ch1==ch)
        {
        allowed = true;
        break;
        }
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
}
