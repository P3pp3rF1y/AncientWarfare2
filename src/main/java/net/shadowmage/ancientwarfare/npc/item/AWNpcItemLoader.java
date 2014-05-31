package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AWNpcItemLoader
{

public static final CreativeTabs npcTab = new CreativeTabs("tabs.npc")
  {    
  @Override
  @SideOnly(Side.CLIENT)
  public Item getTabIconItem()
    {
    return npcSpawner;
    }
  };
  
public static final ItemNpcSpawner npcSpawner = new ItemNpcSpawner("npc_spawner");
public static final ItemWorkOrder workOrder = new ItemWorkOrder("work_order");
public static final ItemUpkeepOrder upkeepOrder = new ItemUpkeepOrder("upkeep_order");
public static final ItemCombatOrder combatOrder = new ItemCombatOrder("combat_order");
public static final ItemRoutingOrder routingOrder = new ItemRoutingOrder("routing_order");
public static final ItemCommandBaton commandBaton = new ItemCommandBaton("command_baton");
  
public static void load()
  {
  GameRegistry.registerItem(npcSpawner, "npc_spawner");  
  GameRegistry.registerItem(workOrder, "work_order");
  GameRegistry.registerItem(upkeepOrder, "upkeep_order");
  GameRegistry.registerItem(combatOrder, "combat_order");
  GameRegistry.registerItem(routingOrder, "routing_order");
  GameRegistry.registerItem(commandBaton, "command_baton");
  }

}
