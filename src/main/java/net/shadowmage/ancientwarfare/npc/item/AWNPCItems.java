package net.shadowmage.ancientwarfare.npc.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;

@ObjectHolder(AncientWarfareNPC.modID)
public class AWNPCItems {
	@ObjectHolder("iron_command_baton")
	public static ItemCommandBaton commandBatonIron;
	@ObjectHolder("bard_instrument")
	public static ItemBardInstrument bardInstrument;
	@ObjectHolder("wooden_shield")
	public static ItemShield woodenShield;
	@ObjectHolder("stone_shield")
	public static ItemShield stoneShield;
	@ObjectHolder("iron_shield")
	public static ItemShield ironShield;
	@ObjectHolder("gold_shield")
	public static ItemShield goldShield;
	@ObjectHolder("diamond_shield")
	public static ItemShield diamondShield;
	@ObjectHolder("npc_spawner")
	public static Item npcSpawner;
	@ObjectHolder("work_order")
	public static Item workOrder;
	@ObjectHolder("upkeep_order")
	public static Item upkeepOrder;
	@ObjectHolder("combat_order")
	public static Item combatOrder;
	@ObjectHolder("routing_order")
	public static Item routingOrder;
	@ObjectHolder("trade_order")
	public static Item tradeOrder;
}
