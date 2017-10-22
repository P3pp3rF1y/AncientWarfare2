package net.shadowmage.ancientwarfare.automation.proxy;

import net.minecraft.client.Minecraft;
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
import net.shadowmage.ancientwarfare.automation.KeyHandler;
import net.shadowmage.ancientwarfare.automation.block.BlockWaterwheelGenerator;
import net.shadowmage.ancientwarfare.automation.config.AWAutomationStatics;
import net.shadowmage.ancientwarfare.automation.gui.GuiChunkLoaderDeluxe;
import net.shadowmage.ancientwarfare.automation.gui.GuiMailboxInventory;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseCraftingStation;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseInterface;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseStockViewer;
import net.shadowmage.ancientwarfare.automation.gui.GuiWarehouseStorage;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteAnimalFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteBoundsAdjust;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteCropFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishControl;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteFishFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteInventorySideSelection;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteMushroomFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteQuarry;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteReedFarm;
import net.shadowmage.ancientwarfare.automation.gui.GuiWorksiteTreeFarm;
import net.shadowmage.ancientwarfare.automation.render.AutoCraftingRenderer;
import net.shadowmage.ancientwarfare.automation.render.HandCrankedGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.render.StirlingGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.render.TorqueAnimationRenderer;
import net.shadowmage.ancientwarfare.automation.render.WaterwheelGeneratorRenderer;
import net.shadowmage.ancientwarfare.automation.render.WorksiteRenderer;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileHandCrankedGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileStirlingGenerator;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileTorqueBase;
import net.shadowmage.ancientwarfare.automation.tile.torque.TileWaterwheelGenerator;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseBase;
import net.shadowmage.ancientwarfare.automation.tile.worksite.TileWorksiteBase;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;

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
        MinecraftForge.EVENT_BUS.register(new KeyHandler(Minecraft.getMinecraft()));

        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_INVENTORY_SIDE_ADJUST, GuiWorksiteInventorySideSelection.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_ANIMAL_CONTROL, GuiWorksiteAnimalControl.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_FISH_CONTROL, GuiWorksiteFishControl.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_MAILBOX_INVENTORY, GuiMailboxInventory.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_CONTROL, GuiWarehouseControl.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_STORAGE, GuiWarehouseStorage.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_OUTPUT, GuiWarehouseInterface.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_CRAFTING, GuiWarehouseCraftingStation.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_QUARRY, GuiWorksiteQuarry.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_TREE_FARM, GuiWorksiteTreeFarm.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_CROP_FARM, GuiWorksiteCropFarm.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_MUSHROOM_FARM, GuiWorksiteMushroomFarm.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_ANIMAL_FARM, GuiWorksiteAnimalFarm.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_REED_FARM, GuiWorksiteReedFarm.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_FISH_FARM, GuiWorksiteFishFarm.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_CHUNK_LOADER_DELUXE, GuiChunkLoaderDeluxe.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WAREHOUSE_STOCK, GuiWarehouseStockViewer.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_WORKSITE_BOUNDS, GuiWorksiteBoundsAdjust.class);


        ClientRegistry.bindTileEntitySpecialRenderer(TileWorksiteBase.class, new WorksiteRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWarehouseBase.class, new WorksiteRenderer());
        //ClientRegistry.bindTileEntitySpecialRenderer(TileWarehouseStockViewer.class, new RenderTileWarehouseStockViewer());


        //********************************************CONDUIT / TRANSPORT RENDERS***************************************************************//

        //RenderTileTorqueTransport conduitRender = new RenderTileTorqueTransport(new ResourceLocation("ancientwarfare", "textures/model/automation/torque_conduit_light.png"), new ResourceLocation("ancientwarfare", "textures/model/automation/torque_conduit_medium.png"), new ResourceLocation("ancientwarfare", "textures/model/automation/torque_conduit_heavy.png"));
        //ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueSidedCell.class, conduitRender);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWAutomationBlockLoader.torqueConduit), conduitRender);

        //RenderTileTorqueTransport distributorRender = new RenderTileTorqueTransport(new ResourceLocation("ancientwarfare", "textures/model/automation/torque_distributor_light.png"), new ResourceLocation("ancientwarfare", "textures/model/automation/torque_distributor_medium.png"), new ResourceLocation("ancientwarfare", "textures/model/automation/torque_distributor_heavy.png"));
        //ClientRegistry.bindTileEntitySpecialRenderer(TileDistributor.class, distributorRender);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWAutomationBlockLoader.torqueDistributor), distributorRender);

        //RenderTileTorqueShaft shaftRender = new RenderTileTorqueShaft(new ResourceLocation("ancientwarfare", "textures/model/automation/torque_shaft_light.png"), new ResourceLocation("ancientwarfare", "textures/model/automation/torque_shaft_medium.png"), new ResourceLocation("ancientwarfare", "textures/model/automation/torque_shaft_heavy.png"));
        //ClientRegistry.bindTileEntitySpecialRenderer(TileTorqueShaft.class, shaftRender);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWAutomationBlockLoader.torqueShaft), shaftRender);


        //********************************************STORAGE RENDERS***************************************************************//
        //RenderTileTorqueFlywheelController storageControlRender = new RenderTileTorqueFlywheelController();
        //ClientRegistry.bindTileEntitySpecialRenderer(TileFlywheelControl.class, storageControlRender);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWAutomationBlockLoader.flywheel), storageControlRender);

        //RenderTileFlywheelStorage storageRender = new RenderTileFlywheelStorage();
        //ClientRegistry.bindTileEntitySpecialRenderer(TileFlywheelStorage.class, storageRender);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWAutomationBlockLoader.flywheelStorage), storageRender);

        //********************************************GENERATOR RENDERS***************************************************************//
        ClientRegistry.bindTileEntitySpecialRenderer(TileStirlingGenerator.class, new TorqueAnimationRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileWaterwheelGenerator.class, new TorqueAnimationRenderer() {
            @Override
            protected IExtendedBlockState updateAdditionalProperties(IExtendedBlockState state, TileTorqueBase te) {
                if (te instanceof TileWaterwheelGenerator) {
                    return (IExtendedBlockState) state.withProperty(BlockWaterwheelGenerator.VALID_SETUP, ((TileWaterwheelGenerator) te).validSetup);
                }
                return state;
            }
        });
        ClientRegistry.bindTileEntitySpecialRenderer(TileHandCrankedGenerator.class, new TorqueAnimationRenderer());

        //RenderWindmillControl windmillControlRender = new RenderWindmillControl();
        //ClientRegistry.bindTileEntitySpecialRenderer(TileWindmillController.class, windmillControlRender);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWAutomationBlockLoader.windmillControl), windmillControlRender);

        //RenderWindmillBlades bladeRender = new RenderWindmillBlades();
        //ClientRegistry.bindTileEntitySpecialRenderer(TileWindmillBlade.class, bladeRender);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWAutomationBlockLoader.windmillBlade), bladeRender);
    }

    private void registerClientOptions() {
        ConfigManager.registerConfigCategory(new AutomationCategory("awconfig.automation_config"));
    }

    public static final class AutomationCategory extends DummyCategoryElement {
        public static final ConfigElement renderWorkBounds = new ConfigElement(AWAutomationStatics.renderWorkBounds);

        public AutomationCategory(String name) {
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
    }

    private TextureAtlasSprite registerSprite(TextureStitchEvent.Pre evt, String spriteName) {
        return evt.getMap().registerSprite(new ResourceLocation(AncientWarfareCore.modID + ":model/automation/" + spriteName));
    }
}
