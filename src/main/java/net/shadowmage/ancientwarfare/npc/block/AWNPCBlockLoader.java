package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwned;
import net.shadowmage.ancientwarfare.npc.item.ItemBlockTeleportHub;
import net.shadowmage.ancientwarfare.npc.tile.TileTeleportHub;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

public class AWNPCBlockLoader {

    public static Block townHall;
    public static Block headquarters;
    public static Block teleportHub;

    public static void load() {
        townHall = AWCoreBlockLoader.INSTANCE.register(new BlockTownHall(), "town_hall", ItemBlockOwned.class, TileTownHall.class);
        headquarters = AWCoreBlockLoader.INSTANCE.register(new BlockHeadquarters(), "headquarters", ItemBlockOwned.class, TileTownHall.class);
        teleportHub = AWCoreBlockLoader.INSTANCE.register(new BlockTeleportHub(), "teleportHub", ItemBlockTeleportHub.class, TileTeleportHub.class);
    }
}
