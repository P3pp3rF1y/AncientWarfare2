package net.shadowmage.ancientwarfare.structure.proxy;

import codechicken.lib.util.ResourceUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.ClientProxyBase;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.event.StructureBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiGateControl;
import net.shadowmage.ancientwarfare.structure.gui.GuiGateControlCreative;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.render.DraftingStationRenderer;
import net.shadowmage.ancientwarfare.structure.render.ParticleDummyModel;
import net.shadowmage.ancientwarfare.structure.render.RenderGateInvisible;
import net.shadowmage.ancientwarfare.structure.sounds.SoundLoader;
import net.shadowmage.ancientwarfare.structure.tile.TileSoundBlock;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxyStructure extends ClientProxyBase {

	@Override
	public void preInit() {
		super.preInit();

		NetworkHandler.registerGui(NetworkHandler.GUI_GATE_CONTROL, GuiGateControl.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_GATE_CONTROL_CREATIVE, GuiGateControlCreative.class);
		MinecraftForge.EVENT_BUS.register(StructureBoundingBoxRenderer.INSTANCE);
		MinecraftForge.EVENT_BUS.register(this);

		RenderingRegistry.registerEntityRenderingHandler(EntityGate.class, RenderGateInvisible::new);

		ResourceUtils.registerReloadListener(ParticleDummyModel.INSTANCE);
	}

	@Override
	public void init() {
		super.init();

		ResourceUtils.registerReloadListener(new SoundLoader());
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
			IBlockState disguiseState = WorldTools.getTile(world, pos, TileSoundBlock.class).filter(t -> t.getDisguiseState() != null)
					.map(TileSoundBlock::getDisguiseState).orElse(Blocks.JUKEBOX.getDefaultState());
			return Minecraft.getMinecraft().getBlockColors().colorMultiplier(disguiseState, world, pos, 0);
		}, AWStructureBlocks.SOUND_BLOCK);
	}

	@SubscribeEvent
	public void onPreTextureStitch(TextureStitchEvent.Pre evt) {
		DraftingStationRenderer.INSTANCE.setSprite(evt.getMap().registerSprite(new ResourceLocation(AncientWarfareCore.MOD_ID + ":model/structure/tile_drafting_station")));
	}
}
