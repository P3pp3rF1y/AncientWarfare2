package net.shadowmage.ancientwarfare.structure.gui;

import java.util.HashMap;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.StatCollector;
import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.gui.GuiContainerBase;
import net.shadowmage.ancientwarfare.core.gui.elements.Button;
import net.shadowmage.ancientwarfare.core.gui.elements.Checkbox;
import net.shadowmage.ancientwarfare.core.gui.elements.CompositeScrolled;
import net.shadowmage.ancientwarfare.core.gui.elements.Label;
import net.shadowmage.ancientwarfare.core.gui.elements.NumberInput;
import net.shadowmage.ancientwarfare.structure.container.ContainerSpawnerAdvancedBase;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnGroup;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings.EntitySpawnSettings;

public class GuiSpawnerAdvanced extends GuiContainerBase
{

CompositeScrolled area;

ContainerSpawnerAdvancedBase container;

public GuiSpawnerAdvanced(ContainerBase par1Container)
  {
  super(par1Container, 256, 240, defaultBackground);
  container = (ContainerSpawnerAdvancedBase)par1Container;
  }

@Override
protected boolean onGuiCloseRequested()
  {  
  container.sendSettingsToServer();  
  return true;
  }

@Override
public void initElements()
  {
  area = new CompositeScrolled(0, 40, 256, 200);
  addGuiElement(area); 
  
  Button done = new Button(256-8-55, 8, 55, 12, StatCollector.translateToLocal("guistrings.done"))
    {
    @Override
    protected void onPressed()
      {
      closeGui();
      }
    };
  addGuiElement(done);
  
  Label label = new Label(8, 8, StatCollector.translateToLocal("guistrings.spawner.set_spawn_settings"));
  addGuiElement(label);
  }

@Override
public void setupElements()
  {
  area.clearElements();
  groupMapByButton.clear();
  groupMapByInput.clear();
  settingsMapByButton.clear();
  settingsMapByInput.clear();
  
  int totalHeight = 3;  
  Checkbox box;
  NumberInput input;
  Label label;
  Button button;
  
  box = new Checkbox(8, totalHeight, 16, 16, StatCollector.translateToLocal("guistrings.spawner.light_sensitive"))
    {
    @Override
    public void onToggled()
      {
      container.settings.setLightSensitive(checked());
      }
    };
  box.setChecked(container.settings.isLightSensitive());
  area.addGuiElement(box);
  totalHeight+=16;
  
  box = new Checkbox(8, totalHeight, 16, 16, StatCollector.translateToLocal("guistrings.spawner.redstone_sensitive"))
    {
    @Override
    public void onToggled()
      {
      container.settings.setRespondToRedstone(checked());
      }
    };
  box.setChecked(container.settings.isRespondToRedstone());
  area.addGuiElement(box);
  totalHeight+=16;
  
  box = new Checkbox(8, totalHeight, 16, 16, StatCollector.translateToLocal("guistrings.spawner.redstone_mode"))
    {
    @Override
    public void onToggled()
      {
      container.settings.setRedstoneMode(checked());
      }
    };
  box.setChecked(container.settings.getRedstoneMode());
  area.addGuiElement(box);
  totalHeight+=16;
  
  box = new Checkbox(8, totalHeight, 16, 16, StatCollector.translateToLocal("guistrings.spawner.transparent"))
    {
    @Override
    public void onToggled()
      {
      container.settings.setTransparent(checked());
      }
    };
  box.setChecked(container.settings.isTransparent());
  area.addGuiElement(box);
  totalHeight+=16;
  
  box = new Checkbox(8, totalHeight, 16, 16, StatCollector.translateToLocal("guistrings.spawner.debug_mode"))
    {
    @Override
    public void onToggled()
      {
      container.settings.setDebugMode(checked());
      }
    };
  box.setChecked(container.settings.isDebugMode());
  area.addGuiElement(box);
  totalHeight+=16;
  
  totalHeight+=4;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.required_player_range"));
  area.addGuiElement(label);
  input = new NumberInput(180, totalHeight, 50, container.settings.getPlayerRange(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      int val = (int)value;
      container.settings.setPlayerRange(val);
      }
    };
  input.setIntegerValue();  
  area.addGuiElement(input);
  totalHeight+=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.max_nearby_entities"));
  area.addGuiElement(label);
  input = new NumberInput(180, totalHeight, 50, container.settings.getMaxNearbyMonsters(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      int val = (int)value;
      container.settings.setMaxNearbyMonsters(val);
      }
    };
  input.setIntegerValue();  
  area.addGuiElement(input);
  totalHeight+=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.min_spawn_delay"));
  area.addGuiElement(label);
  input = new NumberInput(180, totalHeight, 50, container.settings.getMinDelay(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      int val = (int)value;
      container.settings.setMinDelay(val);
      }
    };
  input.setIntegerValue();  
  area.addGuiElement(input);
  totalHeight+=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.max_spawn_delay"));
  area.addGuiElement(label);
  input = new NumberInput(180, totalHeight, 50, container.settings.getMaxDelay(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      int val = (int)value;
      container.settings.setMaxDelay(val);
      }
    };
  input.setIntegerValue();  
  area.addGuiElement(input);
  totalHeight+=12;
    
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.xp_to_drop"));
  area.addGuiElement(label);
  input = new NumberInput(180, totalHeight, 50, container.settings.getXpToDrop(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      int val = (int)value;
      container.settings.setXpToDrop(val);
      }
    };
  input.setIntegerValue();  
  area.addGuiElement(input);
  totalHeight+=12;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.block_hardness"));
  area.addGuiElement(label);
  input = new NumberInput(180, totalHeight, 50, container.settings.getBlockHardness(), this)
    {
    @Override
    public void onValueUpdated(float value)
      {
      container.settings.setBlockHardness(value);
      }
    };
  area.addGuiElement(input);
  totalHeight+=12;
  
  
  totalHeight+=4;
  
  label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.spawn_groups")+":");
  area.addGuiElement(label);
  totalHeight+=12;
  
  List<EntitySpawnGroup> spawnGroups = container.settings.getSpawnGroups();
  List<EntitySpawnSettings> entitySettings;
  
  for(EntitySpawnGroup group : spawnGroups)
    {
    
    button = new Button(256-16-95, totalHeight, 95, 12, StatCollector.translateToLocal("guistrings.spawner.remove_group"))
      {
      @Override
      protected void onPressed()
        {
        EntitySpawnGroup g = groupMapByButton.get(this);
        container.settings.getSpawnGroups().remove(g);
        groupMapByButton.remove(this);
        refreshGui();
        }
      };
    groupMapByButton.put(button, group);
    area.addGuiElement(button); 
    
    label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.group_weight"));
    area.addGuiElement(label);
    
    input = new NumberInput(100, totalHeight, 30, group.getWeight(), this)
      {
      @Override
      public void onValueUpdated(float value)
        {
        int val = (int)value;
        EntitySpawnGroup g = groupMapByInput.get(this);
        if(g!=null){g.setWeight(val);}
        }
      };
    input.setIntegerValue();
    groupMapByInput.put(input, group);
    area.addGuiElement(input);
    
    totalHeight+=14;
    
    label = new Label(8, totalHeight, StatCollector.translateToLocal("guistrings.spawner.entity_list"));
    area.addGuiElement(label);
    
    label = new Label(130, totalHeight, StatCollector.translateToLocal("guistrings.spawner.min"));
    area.addGuiElement(label);
    label = new Label(160, totalHeight, StatCollector.translateToLocal("guistrings.spawner.max"));
    area.addGuiElement(label);
    label = new Label(190, totalHeight, StatCollector.translateToLocal("guistrings.spawner.total"));
    area.addGuiElement(label);
    
    totalHeight+=12;
    
    entitySettings = group.getEntitiesToSpawn();
    for(EntitySpawnSettings settings : entitySettings)
      {
      if(settings==null){continue;}
      button = new Button(30, totalHeight, 100, 12, settings.getEntityId())
        {
        @Override
        protected void onPressed()
          {
          EntitySpawnSettings set = settingsMapByButton.get(this);
          Minecraft.getMinecraft().displayGuiScreen(new GuiSpawnerAdvancedAddEntity(GuiSpawnerAdvanced.this, groupMapByButton.get(this), set));
          }
        };
      groupMapByButton.put(button, group);
      settingsMapByButton.put(button, settings);
      area.addGuiElement(button);
      
      input = new NumberInput(130, totalHeight, 30, settings.getSpawnMin(), this)
        {
        @Override
        public void onValueUpdated(float value)
          {
          int val = (int)value;
          EntitySpawnSettings set = settingsMapByInput.get(this);
          set.setSpawnCountMin(val);
          }
        };
      settingsMapByInput.put(input, settings);
      area.addGuiElement(input);
      input.setIntegerValue();//for some reason, I have to set this _after_ adding to the setting map, or it NPEs on retrieval...a very large WTF
      
      input = new NumberInput(160, totalHeight, 30, settings.getSpawnMax(), this)
        {
        @Override
        public void onValueUpdated(float value)
          {
          int val = (int)value;
          EntitySpawnSettings set = settingsMapByInput.get(this);
          set.setSpawnCountMax(val);
          }
        };
      settingsMapByInput.put(input, settings);
      area.addGuiElement(input);
      input.setIntegerValue();
      
      input = new NumberInput(190, totalHeight, 30, settings.getSpawnTotal(), this)
        {
        @Override
        public void onValueUpdated(float value)
          {
          int val = (int)value;
          EntitySpawnSettings set = settingsMapByInput.get(this);
          set.setSpawnLimitTotal(val);
          }
        };
      settingsMapByInput.put(input, settings);
      area.addGuiElement(input);
      input.setIntegerValue();
      input.setAllowNegative();

      button = new Button(220, totalHeight, 12, 12, StatCollector.translateToLocal("guistrings.spawner.remove"))
        {
        @Override
        protected void onPressed()
          {
          EntitySpawnSettings set = settingsMapByButton.get(this);
          EntitySpawnGroup g = groupMapByButton.get(this);
          g.getEntitiesToSpawn().remove(set);
          refreshGui();
          }
        };
      settingsMapByButton.put(button, settings);
      groupMapByButton.put(button, group);
      area.addGuiElement(button);
      totalHeight+=12;
      }  
    
    button = new Button(30, totalHeight, 120, 12, StatCollector.translateToLocal("guistrings.spawner.add_entity"))
      {      
      @Override
      protected void onPressed()
        {
        EntitySpawnGroup g = groupMapByButton.get(this);     
        Minecraft.getMinecraft().displayGuiScreen(new GuiSpawnerAdvancedAddEntity(GuiSpawnerAdvanced.this, g, null));
        }
      };
    area.addGuiElement(button);
    groupMapByButton.put(button, group);
    totalHeight+=14;
    
    }
  

  totalHeight+=8;
  
  button = new Button(8, totalHeight, 120, 12, StatCollector.translateToLocal("guistrings.spawner.add_group"))    
    {
    @Override
    protected void onPressed()
      {
      EntitySpawnGroup g = new EntitySpawnGroup();
      g.setWeight(1);
      container.settings.addSpawnGroup(g);
      refreshGui();
      }
    };
  area.addGuiElement(button);
  totalHeight+=12;
  
  area.setAreaSize(totalHeight);
  }

private HashMap<NumberInput, EntitySpawnGroup> groupMapByInput = new HashMap<NumberInput, EntitySpawnGroup>();
private HashMap<Button, EntitySpawnGroup> groupMapByButton = new HashMap<Button, EntitySpawnGroup>();

private HashMap<NumberInput, EntitySpawnSettings> settingsMapByInput = new HashMap<NumberInput, EntitySpawnSettings>();
private HashMap<Button, EntitySpawnSettings> settingsMapByButton = new HashMap<Button, EntitySpawnSettings>();

}
