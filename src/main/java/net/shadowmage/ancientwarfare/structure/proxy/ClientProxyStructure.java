package net.shadowmage.ancientwarfare.structure.proxy;

import codechicken.lib.util.ResourceUtils;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.structure.client.AWStructureBlockColors;
import net.shadowmage.ancientwarfare.structure.client.AWStructureItemColors;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.event.StructureBoundingBoxRenderer;
import net.shadowmage.ancientwarfare.structure.gui.GuiGateControl;
import net.shadowmage.ancientwarfare.structure.gui.GuiGateControlCreative;
import net.shadowmage.ancientwarfare.structure.render.DraftingStationRenderer;
import net.shadowmage.ancientwarfare.structure.render.ParticleDummyModel;
import net.shadowmage.ancientwarfare.structure.render.PreviewRenderer;
import net.shadowmage.ancientwarfare.structure.render.RenderGateInvisible;
import net.shadowmage.ancientwarfare.structure.sounds.SoundLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class ClientProxyStructure extends CommonProxyStructure {
	private Set<IClientRegister> clientRegisters = Sets.newHashSet();
	private Map<BlockPos, PositionedSoundRecord> currentSounds = new HashMap<>();

	public ClientProxyStructure() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void registerModels(ModelRegistryEvent event) {
		for (IClientRegister register : clientRegisters) {
			register.registerClient();
		}
	}

	@Override
	public void addClientRegister(IClientRegister register) {
		clientRegisters.add(register);
	}

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

		AWStructureBlockColors.init();
		AWStructureItemColors.init();
	}

	@SubscribeEvent
	public void onPreTextureStitch(TextureStitchEvent.Pre evt) {
		DraftingStationRenderer.INSTANCE.setSprite(evt.getMap().registerSprite(new ResourceLocation(AncientWarfareCore.MOD_ID + ":model/structure/tile_drafting_station")));
	}

	@Override
	public void clearTemplatePreviewCache() {
		PreviewRenderer.clearCache();
	}

	@Override
	public void resetSoundAt(BlockPos pos) {
		currentSounds.remove(pos);
	}

	@Override
	public void setSoundAt(BlockPos pos, SoundEvent currentTune) {
		currentSounds.put(pos, PositionedSoundRecord.getRecordSoundRecord(currentTune, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ()));
	}

	@Override
	public void stopSoundAt(BlockPos pos) {
		if (currentSounds.containsKey(pos)) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(currentSounds.get(pos));
		}
	}

	@Override
	public boolean hasSoundAt(BlockPos pos) {
		return currentSounds.containsKey(pos);
	}

	@Override
	public boolean isSoundPlayingAt(BlockPos pos) {
		if (!hasSoundAt(pos)) {
			return false;
		}
		ISound positionedsoundrecord = currentSounds.get(pos);
		return Minecraft.getMinecraft().getSoundHandler().isSoundPlaying(positionedsoundrecord);
	}

	@Override
	public void playSoundAt(BlockPos pos) {
		if (hasSoundAt(pos)) {
			Minecraft.getMinecraft().getSoundHandler().playSound(currentSounds.get(pos));
		}
	}

	@Override
	public double getClientPlayerDistanceTo(BlockPos pos) {
		return Minecraft.getMinecraft().player.getDistance(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	@Override
	public Optional<EntityPlayer> getPlayer() {
		return Optional.of(Minecraft.getMinecraft().player);
	}
}
