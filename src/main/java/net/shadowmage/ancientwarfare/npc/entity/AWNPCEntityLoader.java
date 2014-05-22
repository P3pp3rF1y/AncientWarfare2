package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;


public class AWNPCEntityLoader
{

private static int nextID = 0;

public static void load()
  {
  //TODO fix with proper reg name, fix reg-names in core-entity registry
  EntityDeclaration reg = new EntityDeclaration(NpcCombat.class, AWEntityRegistry.NPC_TEST, nextID++, AncientWarfareNPC.instance, 120, 3, true)
    {
    @Override
    public Entity createEntity(World world)
      {
      return new NpcCombat(world);
      }
    };
  addRegistration(reg);
  }

protected static void addRegistration(EntityDeclaration reg)
  {
  AWEntityRegistry.registerEntity(reg);
  ItemNpcSpawner.npcNames.add(reg.getEntityName());
  }

}
