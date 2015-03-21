package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class AWNPCBlockLoader {

    public static Block townHall;

    public static void load() {
        townHall = AWCoreBlockLoader.INSTANCE.register(new BlockTownHall(), "town_hall", ItemBlockOwnedRotatable.class, TileTownHall.class);
    }
}
