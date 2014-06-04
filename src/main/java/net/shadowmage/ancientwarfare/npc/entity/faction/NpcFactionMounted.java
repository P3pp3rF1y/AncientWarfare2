package net.shadowmage.ancientwarfare.npc.entity.faction;

import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.npc.ai.NpcAIRideHorseFaction;

public abstract class NpcFactionMounted extends NpcFaction
{

protected NpcAIRideHorseFaction horseAI;
public NpcFactionMounted(World par1World)
  {
  super(par1World);
  }

@Override
public void onDeath(DamageSource source)
  {
  if(!worldObj.isRemote)
    {
    if(horseAI!=null)
      {
      horseAI.onKilled();
      }
    }  
  super.onDeath(source);  
  }

}
