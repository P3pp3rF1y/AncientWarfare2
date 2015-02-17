package net.shadowmage.ancientwarfare.npc.block;

import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwnedRotatable;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class AWNPCBlockLoader {

    public static final BlockTownHall townHall = new BlockTownHall();

    public static void load() {
        AWCoreBlockLoader.INSTANCE.register(townHall, "town_hall", ItemBlockOwnedRotatable.class, TileTownHall.class);
    }

}
