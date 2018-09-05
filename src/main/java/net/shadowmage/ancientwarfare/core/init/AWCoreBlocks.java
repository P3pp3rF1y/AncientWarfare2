package net.shadowmage.ancientwarfare.core.init;

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
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.block.BlockEngineeringStation;
import net.shadowmage.ancientwarfare.core.block.BlockResearchStation;
import net.shadowmage.ancientwarfare.core.item.ItemBlockRotatableMetaTile;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;
import net.shadowmage.ancientwarfare.core.util.InjectionTools;

@ObjectHolder(AncientWarfareCore.MOD_ID)
@Mod.EventBusSubscriber(modid = AncientWarfareCore.MOD_ID)
public class AWCoreBlocks {
	private AWCoreBlocks() {}

	public static final Block ENGINEERING_STATION = InjectionTools.nullValue();
	public static final Block RESEARCH_STATION = InjectionTools.nullValue();

	@SubscribeEvent
	public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
		IForgeRegistry<Item> registry = event.getRegistry();

		registry.register(new ItemBlockRotatableMetaTile(AWCoreBlocks.ENGINEERING_STATION));
		registry.register(new ItemBlockRotatableMetaTile(AWCoreBlocks.RESEARCH_STATION));
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> registry = event.getRegistry();

		registry.register(new BlockEngineeringStation());
		registerTile(TileEngineeringStation.class, "engineering_station_tile");
		registry.register(new BlockResearchStation());
		registerTile(TileResearchStation.class, "research_station_tile");
	}

	private static void registerTile(Class<? extends TileEntity> teClass, String teId) {
		GameRegistry.registerTileEntity(teClass, new ResourceLocation(AncientWarfareCore.MOD_ID, teId));
	}
}
