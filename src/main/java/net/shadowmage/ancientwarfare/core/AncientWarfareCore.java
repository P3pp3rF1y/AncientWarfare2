package net.shadowmage.ancientwarfare.core;

import codechicken.lib.CodeChickenLib;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.command.CommandResearch;
import net.shadowmage.ancientwarfare.core.command.CommandUtils;
import net.shadowmage.ancientwarfare.core.compat.CompatLoader;
import net.shadowmage.ancientwarfare.core.compat.ftb.FTBCompat;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.container.ContainerBackpack;
import net.shadowmage.ancientwarfare.core.container.ContainerEngineeringStation;
import net.shadowmage.ancientwarfare.core.container.ContainerInfoTool;
import net.shadowmage.ancientwarfare.core.container.ContainerManual;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchBook;
import net.shadowmage.ancientwarfare.core.container.ContainerResearchStation;
import net.shadowmage.ancientwarfare.core.crafting.AWCraftingManager;
import net.shadowmage.ancientwarfare.core.datafixes.AWDataFixes;
import net.shadowmage.ancientwarfare.core.entity.AWFakePlayer;
import net.shadowmage.ancientwarfare.core.init.AWCoreItems;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase;
import net.shadowmage.ancientwarfare.core.registry.RegistryLoader;
import net.shadowmage.ancientwarfare.core.registry.ResearchRegistry;
import net.shadowmage.ancientwarfare.core.research.ResearchTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(name = "Ancient Warfare Core", modid = AncientWarfareCore.MOD_ID, version = "@VERSION@", guiFactory = "net.shadowmage.ancientwarfare.core.gui.options.OptionsGuiFactory", dependencies = CodeChickenLib.MOD_VERSION_DEP)
public class AncientWarfareCore {

	public static final String MOD_ID = "ancientwarfare";

	public static final CreativeTabs TAB = new AWCoreTab();

	@Instance(value = AncientWarfareCore.MOD_ID)
	public static AncientWarfareCore instance;

	@SidedProxy(clientSide = "net.shadowmage.ancientwarfare.core.proxy.ClientProxy", serverSide = "net.shadowmage.ancientwarfare.core.proxy.CommonProxyBase")
	public static CommonProxyBase proxy;

	public static final Logger LOG = LogManager.getLogger(MOD_ID);

	public static AWCoreStatics statics;

	@EventHandler
	public void preInit(FMLPreInitializationEvent evt) {
		statics = new AWCoreStatics("AncientWarfare");

		NetworkHandler.INSTANCE.registerNetwork();

		MinecraftForge.EVENT_BUS.register(ResearchTracker.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(this);

		NetworkHandler.registerContainer(NetworkHandler.GUI_CRAFTING, ContainerEngineeringStation.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_RESEARCH_STATION, ContainerResearchStation.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_BACKPACK, ContainerBackpack.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_RESEARCH_BOOK, ContainerResearchBook.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_MANUAL, ContainerManual.class);
		NetworkHandler.registerContainer(NetworkHandler.GUI_INFO_TOOL, ContainerInfoTool.class);

		RegistryLoader.registerParser(new ResearchRegistry.ResearchParser());

		CompatLoader.registerCompat(new FTBCompat());

		proxy.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent evt) {

		proxy.init();

		AWCoreItems.load();

		AWCraftingManager.registerIngredients();

		RegistryLoader.load();

		AWCraftingManager.loadRecipes();

		CompatLoader.init();

		AWDataFixes.registerDataFixes();
	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent evt) {
		statics.save();

		proxy.postInit();
	}

	@EventHandler
	public void serverStartingEvent(FMLServerStartingEvent evt) {
		evt.registerServerCommand(new CommandResearch());
		evt.registerServerCommand(new CommandUtils());
	}

	@SubscribeEvent
	public void configChangedEvent(OnConfigChangedEvent evt) {
		if (MOD_ID.equals(evt.getModID())) {
			statics.save();
		}
	}

	@SubscribeEvent
	public void createResearchRecipeRegistry(RegistryEvent.NewRegistry evt) {
		AWCraftingManager.init();
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onDimensionUnload(WorldEvent.Unload event) {
		if (event.getWorld() instanceof WorldServer)
			AWFakePlayer.onWorldUnload();
	}
}
