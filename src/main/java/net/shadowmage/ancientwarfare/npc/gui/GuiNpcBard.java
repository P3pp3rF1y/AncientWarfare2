package net.shadowmage.ancientwarfare.npc.gui;

import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Line;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.core.gui.elements.Text;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.container.ContainerNpcBard;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard.BardTuneData;
import net.shadowmage.ancientwarfare.npc.entity.NpcBard.BardTuneEntry;

public class GuiNpcBard extends GuiContainerBase
{

ContainerNpcBard container;
CompositeScrolled area;
public GuiNpcBard(ContainerBase container)
  {
  super(container, 256, 240, defaultBackground);
  this.container = (ContainerNpcBard)container;
  }

@Override
public void initElements()
  {      
  area = new CompositeScrolled(0, 0, xSize, ySize);
  }

@Override
public void setupElements()
  {  
  clearElements();
  area.clearElements();
  addGuiElement(area);
  
  int totalHeight = 8;
  final BardTuneData data = container.data;
  
  Checkbox playerEntry = new Checkbox(8, totalHeight, 16, 16, "guistrings.play_on_player_entry")
    {
    @Override
    public void onToggled()
      {
      data.setPlayOnPlayerEntry(checked());
      }
    };
  playerEntry.setChecked(data.getPlayOnPlayerEntry());
  area.addGuiElement(playerEntry);
  totalHeight+=16;
  
  Checkbox random = new Checkbox(8, totalHeight, 16, 16, "guistrings.random")
    {
    @Override
    public void onToggled()
      {
      data.setRandom(checked());
      }
    };
  random.setChecked(data.getIsRandom());
  area.addGuiElement(random);
  totalHeight+=16;
  
  NumberInput minDelay = new NumberInput(8, totalHeight, 55, data.getMinDelay(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      data.setMinDelay((int)value);
      }
    };
  minDelay.setIntegerValue();
  area.addGuiElement(minDelay);
  totalHeight+=12;
  
  NumberInput maxDelay = new NumberInput(8, totalHeight, 55, data.getMaxDelay(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      data.setMaxDelay((int)value);
      }
    };
  maxDelay.setIntegerValue();
  area.addGuiElement(maxDelay);
  totalHeight+=12;
  
  area.addGuiElement(new Line(0, totalHeight+2, xSize, totalHeight+2, 1, 0x000000ff));
  totalHeight+=5;
    
  totalHeight = addTuneEntries(data, totalHeight);
    
  Button newTuneButton = new Button(8, totalHeight, 120, 12, "guistrings.new_tune")
    {
    @Override
    protected void onPressed()
      {
      data.addNewEntry();
      refreshGui();
      }
    };
  area.addGuiElement(newTuneButton);
  totalHeight+=12;
  
  area.setAreaSize(totalHeight);
  }

private int addTuneEntries(final BardTuneData data, int startHeight)
  {
  for(int i = 0; i < data.size(); i++)
    {
    startHeight = addTuneEntry(data.get(i), i, startHeight);
    }
  return startHeight;
  }

private int addTuneEntry(final BardTuneEntry entry, final int index, int startHeight)
  {
  Text input = new Text(8, startHeight, 120, entry.name(), this)
    {
    @Override
    public void onTextUpdated(String oldText, String newText)
      {
      super.onTextUpdated(oldText, newText);
      entry.setName(newText);
      }
    };
  area.addGuiElement(input);
  
  //TODO add up/down/delete box
  //TODO add length and volume input boxes
  
  startHeight+=12;
  area.addGuiElement(new Line(0, startHeight+2, xSize, startHeight+2, 1, 0x000000ff));
  startHeight+=5;
  return startHeight;
  }

@Override
protected boolean onGuiCloseRequested()
  {  
  NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_NPC_INVENTORY, container.npc.getEntityId(), 0, 0);
  return false;
  }

}
