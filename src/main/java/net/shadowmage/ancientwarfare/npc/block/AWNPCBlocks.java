package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

@ObjectHolder(AncientWarfareNPC.modID)
public class AWNPCBlocks {
	@ObjectHolder("town_hall")
	public static Block townHall;
	@ObjectHolder("headquarters")
	public static Block headquarters;
	@ObjectHolder("teleport_hub")
	public static Block teleportHub;
}
