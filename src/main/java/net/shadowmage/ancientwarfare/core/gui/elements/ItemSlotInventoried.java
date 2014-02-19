package net.shadowmage.ancientwarfare.core.gui.elements;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.IItemRenderer;
import net.shadowmage.ancientwarfare.core.gui.Listener;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase.ActivationEvent;
import net.shadowmage.ancientwarfare.core.interfaces.ISlotClickCallback;
import net.shadowmage.ancientwarfare.core.interfaces.ITooltipRenderer;

public class ItemSlotInventoried extends ItemSlot
{

int slotIndex;
IInventory inventory;
ISlotClickCallback containerCallback;

public ItemSlotInventoried(int topLeftX, int topLeftY, IInventory inv, int slot, ISlotClickCallback container, ITooltipRenderer render)
  {
  super(topLeftX, topLeftY, inv.getStackInSlot(slot), render);
  this.inventory = inv;
  this.slotIndex = slot;
  this.containerCallback = container;
  this.addNewListener(new Listener(Listener.MOUSE_UP)
    {
    @Override
    public boolean onEvent(ActivationEvent evt)
      {
      if(visible && enabled && isMouseOverElement(evt.mx, evt.my) && containerCallback!=null)
        {        
        containerCallback.onSlotClicked(inventory, slotIndex);
        }
      return true;
      }
    });
  }

@Override
public void render(int mouseX, int mouseY, float partialTick)
  {  
  this.item = inventory.getStackInSlot(slotIndex);
  super.render(mouseX, mouseY, partialTick);
  }

}
