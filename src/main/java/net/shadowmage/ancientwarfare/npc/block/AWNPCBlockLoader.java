package net.shadowmage.ancientwarfare.npc.block;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwned;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.item.ItemBlockTeleportHub;
import net.shadowmage.ancientwarfare.npc.tile.TileTeleportHub;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

@Mod.EventBusSubscriber(modid = AncientWarfareNPC.modID)
public class AWNPCBlockLoader {

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemBlockOwned(AWNPCBlocks.townHall));
        registry.register(new ItemBlockOwned(AWNPCBlocks.headquarters));
        registry.register(new ItemBlockTeleportHub(AWNPCBlocks.teleportHub));
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockTownHall());
        registry.register(new BlockHeadquarters());
        GameRegistry.registerTileEntity(TileTownHall.class, "town_hall_tile");

        registry.register(new BlockTeleportHub());
        GameRegistry.registerTileEntity(TileTeleportHub.class, "teleport_hub_tile");
    }
}
