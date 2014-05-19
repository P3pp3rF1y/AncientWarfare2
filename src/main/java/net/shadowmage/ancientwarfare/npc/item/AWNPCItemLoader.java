package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AWNPCItemLoader
{

public static final CreativeTabs npcTab = new CreativeTabs("tabs.npc")
  {    
  @Override
  @SideOnly(Side.CLIENT)
  public Item getTabIconItem()
    {
    return Items.stick;
    }
  };
  
public static final ItemNpcSpawner npcSpawner = new ItemNpcSpawner("npc_spawner");
  
public static void load()
  {
  GameRegistry.registerItem(npcSpawner, "npc_spawner");  
  }

}
