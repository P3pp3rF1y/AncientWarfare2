package net.shadowmage.ancientwarfare.core.block;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.api.AWBlocks;
import net.shadowmage.ancientwarfare.core.api.AWItems;
import net.shadowmage.ancientwarfare.core.item.ItemBlockRotatableMetaTile;
import net.shadowmage.ancientwarfare.core.tile.TileEngineeringStation;
import net.shadowmage.ancientwarfare.core.tile.TileResearchStation;

@Mod.EventBusSubscriber(modid = AncientWarfareCore.modID)
public class AWCoreBlockLoader {

    public static final AWCoreBlockLoader INSTANCE = new AWCoreBlockLoader();

    private AWCoreBlockLoader() {
    }

    public static final CreativeTabs coreTab = new CreativeTabs("tabs.awcore") {
        @Override
        @SideOnly(Side.CLIENT)
        public ItemStack getTabIconItem() {
            return new ItemStack(AWItems.researchBook);
        }
    };

    @SubscribeEvent
    public static void registerItemBlocks(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        registry.register(new ItemBlockRotatableMetaTile(AWBlocks.engineeringStation));
        registry.register(new ItemBlockRotatableMetaTile(AWBlocks.researchStation));
    }

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        registry.register(new BlockEngineeringStation());
        GameRegistry.registerTileEntity(TileEngineeringStation.class, "engineering_station_tile");
        registry.register(new BlockResearchStation());
        GameRegistry.registerTileEntity(TileResearchStation.class, "research_station_tile");
    }
}
