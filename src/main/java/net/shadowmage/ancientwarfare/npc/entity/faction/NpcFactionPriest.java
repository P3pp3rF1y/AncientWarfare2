package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class NpcFactionPriest extends NpcFactionBase
{

public NpcFactionPriest(World par1World)
  {
  super(par1World);
  setCurrentItemOrArmor(0, new ItemStack(Items.book));
  //TODO set in-hand item to...a cross? (or other holy symbol...an ankh?)
  }

}
