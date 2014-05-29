package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class NpcFactionTrader extends NpcFactionBase
{

public NpcFactionTrader(World par1World)
  {
  super(par1World);
  setCurrentItemOrArmor(0, new ItemStack(Items.book));
  }

}
