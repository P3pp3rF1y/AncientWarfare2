package net.shadowmage.ancientwarfare.core.api;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.item.ItemBase;
import net.shadowmage.ancientwarfare.structure.item.ItemGateSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;

@ObjectHolder(AncientWarfareCore.modID)
public class AWItems {

    /*
     * CORE module
     */
    @ObjectHolder("research_book")
    public static Item researchBook;
    @ObjectHolder("research_note")
    public static Item researchNote;
    @ObjectHolder("wooden_hammer")
    public static Item automationHammerWood;
    @ObjectHolder("stone_hammer")
    public static Item automationHammerStone;
    @ObjectHolder("iron_hammer")
    public static Item automationHammerIron;
    @ObjectHolder("gold_hammer")
    public static Item automationHammerGold;
    @ObjectHolder("diamond_hammer")
    public static Item automationHammerDiamond;
    @ObjectHolder("backpack")
    public static Item backpack;
    @ObjectHolder("wooden_quill")
    public static Item quillWood;
    @ObjectHolder("stone_quill")
    public static Item quillStone;
    @ObjectHolder("iron_quill")
    public static Item quillIron;
    @ObjectHolder("gold_quill")
    public static Item quillGold;
    @ObjectHolder("diamond_quill")
    public static Item quillDiamond;
    @ObjectHolder("component")
    public static ItemBase componentItem;
    @ObjectHolder("steel_ingot")
    public static Item steel_ingot;

    /*
     * NPC module
     */
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

    /*
     * AUTOMATION module
     */
    public static ItemBase worksiteUpgrade;

    /*
     * STRUCTURE module
     */
    @ObjectHolder("ancientwarfarestructure:gate_spawner")
    public static ItemGateSpawner gateSpawner;
    @ObjectHolder("ancientwarfarestructure:structure_scanner")
    public static ItemStructureScanner structureScanner;

}
