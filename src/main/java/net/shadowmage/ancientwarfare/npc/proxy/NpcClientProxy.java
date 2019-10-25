package net.shadowmage.ancientwarfare.npc.proxy;

import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPotion;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.config.ConfigManager;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.proxy.IClientRegister;
import net.shadowmage.ancientwarfare.core.util.TextureImageBased;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.client.NPCItemColors;
import net.shadowmage.ancientwarfare.npc.config.AWNPCStatics;
import net.shadowmage.ancientwarfare.npc.entity.NoFriendlyFirePotion;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;
import net.shadowmage.ancientwarfare.npc.entity.faction.NpcFaction;
import net.shadowmage.ancientwarfare.npc.gui.GuiCombatOrder;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcBard;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcCreativeControls;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcFactionBard;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcFactionTradeSetup;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcFactionTradeView;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcInventory;
import net.shadowmage.ancientwarfare.npc.gui.GuiNpcPlayerOwnedTrade;
import net.shadowmage.ancientwarfare.npc.gui.GuiRoutingOrder;
import net.shadowmage.ancientwarfare.npc.gui.GuiTownHallInventory;
import net.shadowmage.ancientwarfare.npc.gui.GuiTradeOrder;
import net.shadowmage.ancientwarfare.npc.gui.GuiUpkeepOrder;
import net.shadowmage.ancientwarfare.npc.gui.GuiWorkOrder;
import net.shadowmage.ancientwarfare.npc.item.IExtendedReachWeapon;
import net.shadowmage.ancientwarfare.npc.render.RenderCommandOverlay;
import net.shadowmage.ancientwarfare.npc.render.RenderNpcBase;
import net.shadowmage.ancientwarfare.npc.render.RenderNpcFaction;
import net.shadowmage.ancientwarfare.npc.render.RenderWorkLines;
import net.shadowmage.ancientwarfare.npc.skin.NpcSkinManager;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class NpcClientProxy extends NpcCommonProxy {

	private Set<IClientRegister> clientRegisters = Sets.newHashSet();

	public NpcClientProxy() {
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

		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_INVENTORY, GuiNpcInventory.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_FACTION_TRADE_SETUP, GuiNpcFactionTradeSetup.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_FACTION_TRADE_VIEW, GuiNpcFactionTradeView.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_WORK_ORDER, GuiWorkOrder.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_UPKEEP_ORDER, GuiUpkeepOrder.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_COMBAT_ORDER, GuiCombatOrder.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_ROUTING_ORDER, GuiRoutingOrder.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_TOWN_HALL, GuiTownHallInventory.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_BARD, GuiNpcBard.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_CREATIVE, GuiNpcCreativeControls.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_TRADE_ORDER, GuiTradeOrder.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_PLAYER_OWNED_TRADE, GuiNpcPlayerOwnedTrade.class);
		NetworkHandler.registerGui(NetworkHandler.GUI_NPC_FACTION_BARD, GuiNpcFactionBard.class);

		RenderingRegistry.registerEntityRenderingHandler(NpcBase.class, RenderNpcBase::new);
		RenderingRegistry.registerEntityRenderingHandler(NpcFaction.class, RenderNpcFaction::new);
		RenderingRegistry.registerEntityRenderingHandler(NoFriendlyFirePotion.class, renderManager -> new RenderPotion(renderManager, Minecraft.getMinecraft().getRenderItem()));

		MinecraftForge.EVENT_BUS.register(RenderWorkLines.INSTANCE);
		MinecraftForge.EVENT_BUS.register(RenderCommandOverlay.INSTANCE);
		MinecraftForge.EVENT_BUS.register(new IExtendedReachWeapon.MouseClickHandler());

		registerClientOptions();
	}

	@Override
	public void init() {
		super.init();

		NPCItemColors.init();
	}

	private void registerClientOptions() {
		ConfigManager.registerConfigCategory(new NpcCategory("awconfig.npc_config"));
	}

	@Override
	public void loadSkins() {
		NpcSkinManager.loadSkins();
	}

	@Override
	public Optional<ResourceLocation> loadSkinPackImage(String imageName, InputStream is) {
		try {
			BufferedImage image = ImageIO.read(is);
			ResourceLocation loc = new ResourceLocation("ancientwarfare:skinpack/" + imageName);
			TextureImageBased tex = new TextureImageBased(loc, image);
			Minecraft.getMinecraft().renderEngine.loadTexture(loc, tex);
			return Optional.of(loc);
		}
		catch (IOException e) {
			AncientWarfareNPC.LOG.error("Error reading image {}", imageName);
		}
		return Optional.empty();
	}

	@Override
	public Optional<ResourceLocation> getPlayerSkin(String name) {
		Optional<GameProfile> gp = getProfile(name);
		if (gp.isPresent()) {
			SkinManager manager = Minecraft.getMinecraft().getSkinManager();
			Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> map = manager.loadSkinFromCache(gp.get());
			if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
				return Optional.of(manager.loadSkin(map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN));
			}
		}
		return Optional.empty();
	}

	private static final class NpcCategory extends DummyCategoryElement {

		private NpcCategory(String arg0) {
			super(arg0, arg0, getElementList());
		}

		private static List<IConfigElement> getElementList() {
			ArrayList<IConfigElement> list = new ArrayList<>();
			list.add(new ConfigElement(AWNPCStatics.renderAI));
			list.add(new ConfigElement(AWNPCStatics.renderFriendlyNames));
			list.add(new ConfigElement(AWNPCStatics.renderFriendlyHealth));
			list.add(new ConfigElement(AWNPCStatics.renderHostileNames));
			list.add(new ConfigElement(AWNPCStatics.renderHostileHealth));
			list.add(new ConfigElement(AWNPCStatics.renderTeamColors));
			list.add(new ConfigElement(AWNPCStatics.renderWorkPoints));
			return list;
		}
	}

}
