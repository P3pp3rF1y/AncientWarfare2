package net.shadowmage.ancientwarfare.npc.entity;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry;
import net.shadowmage.ancientwarfare.core.entity.AWEntityRegistry.EntityDeclaration;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;


public class AWNPCEntityLoader
{

private static int nextID = 0;

public static void load()
  {
  EntityDeclaration reg = new EntityDeclaration(NpcBase.class, AWEntityRegistry.NPC_TEST, nextID++, AncientWarfareNPC.instance, 250, 5, true)
    {
    @Override
    public Entity createEntity(World world)
      {
      return new NpcBase(world);
      }
    };
  AWEntityRegistry.registerEntity(reg);
  }

}
