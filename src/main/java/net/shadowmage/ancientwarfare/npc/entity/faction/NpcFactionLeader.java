package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public abstract class NpcFactionLeader extends NpcFactionBase
{

public NpcFactionLeader(World par1World)
  {
  super(par1World);
  this.setCurrentItemOrArmor(0, new ItemStack(Items.diamond_sword));
  }

}
