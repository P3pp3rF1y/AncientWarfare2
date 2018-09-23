package net.shadowmage.ancientwarfare.npc.init;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.item.ItemBlockOwned;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.block.BlockTownHall;
import net.shadowmage.ancientwarfare.npc.tile.TileTownHall;

import static net.shadowmage.ancientwarfare.npc.AncientWarfareNPC.MOD_ID;

@ObjectHolder(MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareNPC.MOD_ID)
public class AWNPCBlocks {
	private AWNPCBlocks() {}

	public static final Block TOWN_HALL = InjectionTools.nullValue();

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(new ItemBlockOwned(AWNPCBlocks.TOWN_HALL));
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		registry.register(new BlockTownHall());
		registerTile(TileTownHall.class, "town_hall_tile");
	}

	@SuppressWarnings("SameParameterValue")
	private static void registerTile(Class<? extends TileEntity> teClass, String teId) {
		GameRegistry.registerTileEntity(teClass, new ResourceLocation(MOD_ID, teId));
	}
}
