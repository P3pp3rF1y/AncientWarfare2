package net.shadowmage.ancientwarfare.npc.item;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
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
  
  @Override
  public void displayAllReleventItems(List par1List)
    {
    super.displayAllReleventItems(par1List);
    //TODO sort list
    }
  };
  
public static final ItemNpcSpawner npcSpawner = new ItemNpcSpawner("npc_spawner");
public static final ItemWorkOrder workOrder = new ItemWorkOrder("work_order");
public static final ItemUpkeepOrder upkeepOrder = new ItemUpkeepOrder("upkeep_order");
public static final ItemCombatOrder combatOrder = new ItemCombatOrder("combat_order");
public static final ItemRoutingOrder routingOrder = new ItemRoutingOrder("routing_order");
public static final ItemCommandBaton commandBatonWood = new ItemCommandBaton("wooden_command_baton", ToolMaterial.WOOD);
public static final ItemCommandBaton commandBatonStone = new ItemCommandBaton("stone_command_baton", ToolMaterial.STONE);
public static final ItemCommandBaton commandBatonIron = new ItemCommandBaton("iron_command_baton", ToolMaterial.IRON);
public static final ItemCommandBaton commandBatonGold = new ItemCommandBaton("gold_command_baton", ToolMaterial.GOLD);
public static final ItemCommandBaton commandBatonDiamond = new ItemCommandBaton("diamond_command_baton", ToolMaterial.EMERALD);
public static final ItemBardInstrument bardInstrument = new ItemBardInstrument("bard_instrument");
  
public static void load()
  {
  GameRegistry.registerItem(npcSpawner, "npc_spawner");  
  GameRegistry.registerItem(workOrder, "work_order");
  GameRegistry.registerItem(upkeepOrder, "upkeep_order");
  GameRegistry.registerItem(combatOrder, "combat_order");
  GameRegistry.registerItem(routingOrder, "routing_order");
  GameRegistry.registerItem(commandBatonWood, "wooden_command_baton");
  GameRegistry.registerItem(commandBatonStone, "stone_command_baton");
  GameRegistry.registerItem(commandBatonIron, "iron_command_baton");
  GameRegistry.registerItem(commandBatonGold, "gold_command_baton");
  GameRegistry.registerItem(commandBatonDiamond, "diamond_command_baton");
  GameRegistry.registerItem(bardInstrument, "bard_instrument");
  }

}
