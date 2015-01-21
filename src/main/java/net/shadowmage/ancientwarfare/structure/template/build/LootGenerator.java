package net.shadowmage.ancientwarfare.structure.template.build;

import net.minecraft.inventory.IInventory;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import java.util.Random;

import static net.minecraftforge.common.ChestGenHooks.DUNGEON_CHEST;

public class LootGenerator {

    public static final LootGenerator INSTANCE = new LootGenerator();

    private LootGenerator() {
    }

    public void generateLootFor(IInventory inventory, int lootLevel, Random rng) {
        for (int i = 0; i < lootLevel + 1; i++) {
            WeightedRandomChestContent.generateChestContents(rng, ChestGenHooks.getItems(DUNGEON_CHEST, rng), inventory, ChestGenHooks.getCount(DUNGEON_CHEST, rng));
        }
    }

}
