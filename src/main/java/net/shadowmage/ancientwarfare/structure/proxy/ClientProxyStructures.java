package net.shadowmage.ancientwarfare.structure.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.core.render.TileCraftingTableRender;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlocks;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.event.StructureBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiDraftingStation;
import net.shadowmage.ancientwarfare.structure.gui.GuiGateControl;
import net.shadowmage.ancientwarfare.structure.gui.GuiSoundBlock;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerAdvanced;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerAdvancedInventory;
import net.shadowmage.ancientwarfare.structure.gui.GuiSpawnerPlacer;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureScanner;
import net.shadowmage.ancientwarfare.structure.gui.GuiStructureSelection;
import net.shadowmage.ancientwarfare.structure.model.ModelDraftingStation;
import net.shadowmage.ancientwarfare.structure.render.RenderGateHelper;
import net.shadowmage.ancientwarfare.structure.render.RenderStructureBuilder;
import net.shadowmage.ancientwarfare.structure.tile.TileDraftingStation;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;
import net.shadowmage.ancientwarfare.structure.tile.TileStructureBuilder;

import javax.annotation.Nullable;

public class ClientProxyStructures extends ClientProxyBase {

    @Override
    public void preInit() {
        super.preInit();

        NetworkHandler.registerGui(NetworkHandler.GUI_SCANNER, GuiStructureScanner.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_BUILDER, GuiStructureSelection.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER, GuiSpawnerPlacer.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED, GuiSpawnerAdvanced.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK, GuiSpawnerAdvanced.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, GuiSpawnerAdvancedInventory.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SPAWNER_ADVANCED_BLOCK_INVENTORY, GuiSpawnerAdvancedInventory.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_GATE_CONTROL, GuiGateControl.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_DRAFTING_STATION, GuiDraftingStation.class);
        NetworkHandler.registerGui(NetworkHandler.GUI_SOUND_BLOCK, GuiSoundBlock.class);
        MinecraftForge.EVENT_BUS.register(StructureBoundingBoxRenderer.INSTANCE);

        RenderingRegistry.registerEntityRenderingHandler(EntityGate.class, RenderGateHelper::new);
        ClientRegistry.bindTileEntitySpecialRenderer(TileStructureBuilder.class, new RenderStructureBuilder());
        TileCraftingTableRender render = new TileCraftingTableRender(new ModelDraftingStation(), "textures/model/structure/tile_drafting_station.png");
        ClientRegistry.bindTileEntitySpecialRenderer(TileDraftingStation.class, render);
        //MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(AWBlocks.draftingStation), render);
    }

    @Override
    public void init() {
        super.init();

        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() {
            @Override
            public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex) {
                TileEntity tileEntity = world.getTileEntity(pos);
                IBlockState disguiseState = Blocks.JUKEBOX.getDefaultState();
                if(tileEntity instanceof TileSoundBlock) {
                    Block block = ((TileSoundBlock) tileEntity).getBlockCache();
                    if (block != null) {
                        disguiseState = block.getDefaultState();
                    }
                }
                return Minecraft.getMinecraft().getBlockColors().colorMultiplier(disguiseState, world, pos, 0);
            }
        }, AWStructuresBlocks.soundBlock);
    }
}
