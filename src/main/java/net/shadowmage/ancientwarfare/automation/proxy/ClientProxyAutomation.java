package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.automation.AutomationInputHandler;
import net.shadowmage.ancientwarfare.automation.block.BlockWaterwheelGenerator;
import net.shadowmage.ancientwarfare.automation.block.TorqueTier;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteBoundsAdjust;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteCropFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFruitFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteQuarry;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteReedFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.render.AutoCraftingRenderer;
import net.shadowmage.ancientwarfare.automation.render.FlywheelControllerAnimationRenderer;
import net.shadowmage.ancientwarfare.automation.render.FlywheelControllerRenderer;
import net.shadowmage.ancientwarfare.automation.render.FlywheelStorageAnimationRenderer;
import net.shadowmage.ancientwarfare.automation.render.FlywheelStorageRenderer;
import net.shadowmage.ancientwarfare.automation.render.HandCrankedGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.render.StirlingGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.render.TorqueAnimationRenderer;
import net.shadowmage.ancientwarfare.automation.render.TorqueDistributorRenderer;
import net.shadowmage.ancientwarfare.automation.render.TorqueJunctionRenderer;
import net.shadowmage.ancientwarfare.automation.render.TorqueShaftAnimationRenderer;
import net.shadowmage.ancientwarfare.automation.render.TorqueShaftRenderer;
import net.shadowmage.ancientwarfare.automation.render.TorqueTransportAnimationRenderer;
import net.shadowmage.ancientwarfare.automation.render.WarehouseStockViewerRenderer;
import net.shadowmage.ancientwarfare.automation.render.WaterwheelGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.render.WindmillBladeAnimationRenderer;
import net.shadowmage.ancientwarfare.automation.render.WindmillBladeRenderer;
import net.shadowmage.ancientwarfare.automation.render.WindmillGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.render.WorksiteRenderer;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileDistributor;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileFlywheelController;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileHandCrankedGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileStirlingGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueShaft;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueSidedCell;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWaterwheelGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWindmillController;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileFlywheelStorage;
import net.shadowmage.ancientwarfare.automation.tile.torque.multiblock.TileWindmillBlade;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseBase;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiVehicleStats;

import java.util.ArrayList;
import java.util.List;

public class ClientProxyAutomation extends ClientProxyBase {

	public ClientProxyAutomation() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public void preInit() {
		super.preInit();

		registerClientOptions();

		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, GuiWorksiteInventorySideSelection.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, GuiWorksiteAnimalControl.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_FISH_CONTROL, GuiWorksiteFishControl.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_CONTROL, GuiWarehouseControl.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_QUARRY, GuiWorksiteQuarry.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_TREE_FARM, GuiWorksiteTreeFarm.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_CROP_FARM, GuiWorksiteCropFarm.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_FRUIT_FARM, GuiWorksiteFruitFarm.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_VEHICLE_STATS, GuiVehicleStats.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_MUSHROOM_FARM, GuiWorksiteMushroomFarm.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, GuiWorksiteAnimalFarm.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_REED_FARM, GuiWorksiteReedFarm.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_FISH_FARM, GuiWorksiteFishFarm.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_BOUNDS, GuiWorksiteBoundsAdjust.class);

		ClientRegistry.bindTileEntitySpecialRenderer(TileWorksiteBase.class, new WorksiteRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWarehouseBase.class, new WorksiteRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileWarehouseStockViewer.class, new WarehouseStockViewerRenderer());

		//********************************************CONDUIT / TRANSPORT RENDERS***************************************************************//

		ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueSidedCell.class, new TorqueTransportAnimationRenderer(TorqueJunctionRenderer.INSTANCE));
		ClientRegistry.bindTileEntitySpecialRenderer(TileDistributor.class, new TorqueTransportAnimationRenderer(TorqueDistributorRenderer.INSTANCE));
		ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueShaft.class, new TorqueShaftAnimationRenderer());

		//********************************************STORAGE RENDERS***************************************************************//
		ClientRegistry.bindTileEntitySpecialRenderer(TileFlywheelController.class, new FlywheelControllerAnimationRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileFlywheelStorage.class, new FlywheelStorageAnimationRenderer());

		//********************************************GENERATOR RENDERS***************************************************************//
		ClientRegistry.bindTileEntitySpecialRenderer(TileStirlingGenerator.class, new TorqueAnimationRenderer<>(StirlingGeneratorRenderer.INSTANCE));
		ClientRegistry.bindTileEntitySpecialRenderer(TileWaterwheelGenerator.class, new TorqueAnimationRenderer<TileWaterwheelGenerator>(WaterwheelGeneratorRenderer.INSTANCE) {
			@Override
			protected IExtendedBlockState updateAdditionalProperties(IExtendedBlockState state, TileTorqueBase te) {
				if (te instanceof TileWaterwheelGenerator) {
					return (IExtendedBlockState) state.withProperty(BlockWaterwheelGenerator.VALID_SETUP, ((TileWaterwheelGenerator) te).validSetup);
				}
				return state;
			}
		});
		ClientRegistry.bindTileEntitySpecialRenderer(TileHandCrankedGenerator.class, new TorqueAnimationRenderer<>(HandCrankedGeneratorRenderer.INSTANCE));
		ClientRegistry.bindTileEntitySpecialRenderer(TileWindmillController.class, new TorqueAnimationRenderer<>(WindmillGeneratorRenderer.INSTANCE));
		ClientRegistry.bindTileEntitySpecialRenderer(TileWindmillBlade.class, new WindmillBladeAnimationRenderer());
	}

	@Override
	public void init() {
		super.init();

		AutomationInputHandler.initKeyBindings();
	}

	private void registerClientOptions() {
		ConfigManager.registerConfigCategory(new AutomationCategory("awconfig.automation_config"));
	}

	public static final class AutomationCategory extends DummyCategoryElement {
		public static final ConfigElement renderWorkBounds = new ConfigElement(AWAutomationStatics.renderWorkBounds);

		AutomationCategory(String name) {
			super(name, name, getElementList());
		}

		private static List<IConfigElement> getElementList() {
			ArrayList<IConfigElement> list = new ArrayList<>();
			list.add(renderWorkBounds);
			return list;
		}
	}

	@SubscribeEvent
	public void onPreTextureStitch(TextureStitchEvent.Pre evt) {
		AutoCraftingRenderer.INSTANCE.setSprite(registerSprite(evt, "tile_auto_crafting"));

		StirlingGeneratorRenderer.INSTANCE.setSprite(registerSprite(evt, "stirling_generator"));

		HandCrankedGeneratorRenderer.INSTANCE.setSprite(registerSprite(evt, "hand_cranked_generator"));

		WaterwheelGeneratorRenderer.INSTANCE.setSprite(registerSprite(evt, "waterwheel_generator"));

		WindmillGeneratorRenderer.INSTANCE.setSprite(registerSprite(evt, "windmill_generator"));
		WindmillBladeRenderer.INSTANCE.setSprite(registerSprite(evt, "windmill_blade"));
		WindmillBladeRenderer.INSTANCE.setCubeSprite(registerSprite(evt, "windmill_blade_cube"));

		TorqueShaftRenderer.INSTANCE.setSprite(TorqueTier.HEAVY, registerSprite(evt, "torque_shaft_heavy"));
		TorqueShaftRenderer.INSTANCE.setSprite(TorqueTier.MEDIUM, registerSprite(evt, "torque_shaft_medium"));
		TorqueShaftRenderer.INSTANCE.setSprite(TorqueTier.LIGHT, registerSprite(evt, "torque_shaft_light"));

		TorqueJunctionRenderer.INSTANCE.setSprite(TorqueTier.HEAVY, registerSprite(evt, "torque_junction_heavy"));
		TorqueJunctionRenderer.INSTANCE.setSprite(TorqueTier.MEDIUM, registerSprite(evt, "torque_junction_medium"));
		TorqueJunctionRenderer.INSTANCE.setSprite(TorqueTier.LIGHT, registerSprite(evt, "torque_junction_light"));

		TorqueDistributorRenderer.INSTANCE.setSprite(TorqueTier.HEAVY, registerSprite(evt, "torque_distributor_heavy"));
		TorqueDistributorRenderer.INSTANCE.setSprite(TorqueTier.MEDIUM, registerSprite(evt, "torque_distributor_medium"));
		TorqueDistributorRenderer.INSTANCE.setSprite(TorqueTier.LIGHT, registerSprite(evt, "torque_distributor_light"));

		FlywheelControllerRenderer.INSTANCE.setSprite(TorqueTier.HEAVY, registerSprite(evt, "flywheel_controller_heavy"));
		FlywheelControllerRenderer.INSTANCE.setSprite(TorqueTier.MEDIUM, registerSprite(evt, "flywheel_controller_medium"));
		FlywheelControllerRenderer.INSTANCE.setSprite(TorqueTier.LIGHT, registerSprite(evt, "flywheel_controller_light"));

		FlywheelStorageRenderer.INSTANCE.setSprite(false, TorqueTier.HEAVY, registerSprite(evt, "flywheel_small_heavy"));
		FlywheelStorageRenderer.INSTANCE.setSprite(false, TorqueTier.MEDIUM, registerSprite(evt, "flywheel_small_medium"));
		FlywheelStorageRenderer.INSTANCE.setSprite(false, TorqueTier.LIGHT, registerSprite(evt, "flywheel_small_light"));
		FlywheelStorageRenderer.INSTANCE.setSprite(true, TorqueTier.HEAVY, registerSprite(evt, "flywheel_large_heavy"));
		FlywheelStorageRenderer.INSTANCE.setSprite(true, TorqueTier.MEDIUM, registerSprite(evt, "flywheel_large_medium"));
		FlywheelStorageRenderer.INSTANCE.setSprite(true, TorqueTier.LIGHT, registerSprite(evt, "flywheel_large_light"));
	}

	private TextureAtlasSprite registerSprite(TextureStitchEvent.Pre evt, String spriteName) {
		return evt.getMap().registerSprite(new ResourceLocation(AncientWarfareCore.modID + ":model/automation/" + spriteName));
	}
}
