package net.shadowmage.ancientwarfare.npc.skin;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.network.PacketEntity;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.NpcBase;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class NpcSkinSettings {
	private static final ResourceLocation BASE_DEFAULT_TEXTURE = new ResourceLocation("ancientwarfare:textures/entity/npc/npc_default.png");
	public static final String PACKET_TAG_NAME = "skinSettings";
	private static final String PLAYER_NAME_TAG = "playerName";
	private static final String RANDOM_TAG = "random";
	private static final String NPC_TYPE_NAME_TAG = "npcTypeName";
	private static final String NPC_TYPE_SKIN_TAG = "npcTypeSkin";
	private static final String IS_ALEX_MODEL_TAG = "isAlexModel";

	private SkinType skinTypeSelected = SkinType.DEFAULT;
	private boolean playerSkinLoaded = false;
	private ResourceLocation playerSkin = null;
	private String playerName = "";
	private boolean random = true;
	private String npcTypeName = "custom";
	private ResourceLocation npcTypeSkin = null;

	public boolean isAlexModel() {
		return isAlexModel;
	}

	public void setAlexModel(boolean alexModel) {
		isAlexModel = alexModel;
	}

	public boolean renderFemaleModel(NpcBase npc) {
		if (skinTypeSelected == SkinType.DEFAULT) {
			return npc.isFemale();
		}
		return isAlexModel;
	}

	private boolean isAlexModel = false;

	public ResourceLocation getTexture(NpcBase npc) {
		switch (skinTypeSelected) {
			case PLAYER:
				loadPlayerSkin();
				return playerSkin == null ? BASE_DEFAULT_TEXTURE : playerSkin;
			case NPC_TYPE:
				return random || npcTypeSkin == null ? NpcSkinManager.getNpcTexture(npcTypeName, npc.getIDForSkin()).orElse(BASE_DEFAULT_TEXTURE) : npcTypeSkin;
			case DEFAULT:
			default:
				return NpcSkinManager.getNpcTexture(npc.getNpcFullType(), npc.getIDForSkin()).orElse(BASE_DEFAULT_TEXTURE);
		}
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("skinTypeSelected", skinTypeSelected.getName());

		if (!playerName.isEmpty()) {
			tag.setString(PLAYER_NAME_TAG, playerName);
		}
		if (!random) {
			tag.setBoolean(RANDOM_TAG, false);
		}
		if (!npcTypeName.isEmpty()) {
			tag.setString(NPC_TYPE_NAME_TAG, npcTypeName);
		}
		if (npcTypeSkin != null) {
			tag.setString(NPC_TYPE_SKIN_TAG, npcTypeSkin.toString());
		}
		if (isAlexModel) {
			tag.setBoolean(IS_ALEX_MODEL_TAG, true);
		}

		return tag;
	}

	public void serializeToBuffer(ByteBuf buffer) {
		PacketBuffer packetBuffer = new PacketBuffer(buffer);
		packetBuffer.writeString(skinTypeSelected.getName());
		packetBuffer.writeString(playerName);
		packetBuffer.writeBoolean(random);
		packetBuffer.writeString(npcTypeName);
		packetBuffer.writeString(npcTypeSkin == null ? "" : npcTypeSkin.toString().substring(0, Math.min(npcTypeSkin.toString().length(), 100)));
		packetBuffer.writeBoolean(isAlexModel);
	}

	public static NpcSkinSettings deserializeFromBuffer(ByteBuf buffer) {
		NpcSkinSettings skinSettings = new NpcSkinSettings();
		PacketBuffer packetBuffer = new PacketBuffer(buffer);
		skinSettings.skinTypeSelected = SkinType.byName(packetBuffer.readString(20));
		if (skinSettings.skinTypeSelected == SkinType.PLAYER) {
			skinSettings.playerName = packetBuffer.readString(40);
		} else {
			packetBuffer.readString(40);
		}
		if (skinSettings.skinTypeSelected == SkinType.NPC_TYPE) {
			skinSettings.random = packetBuffer.readBoolean();
			skinSettings.npcTypeName = packetBuffer.readString(30);
			String skin = packetBuffer.readString(100);
			if (!skinSettings.random) {
				skinSettings.npcTypeSkin = skin.isEmpty() ? null : new ResourceLocation(skin);
			}
		} else {
			packetBuffer.readBoolean();
			packetBuffer.readString(30);
			packetBuffer.readString(60);
		}
		skinSettings.isAlexModel = packetBuffer.readBoolean();
		return skinSettings;
	}

	public static NpcSkinSettings deserializeNBT(NBTTagCompound tag) {
		NpcSkinSettings skinSettings = new NpcSkinSettings();

		skinSettings.skinTypeSelected = SkinType.byName(tag.getString("skinTypeSelected"));

		if (tag.hasKey(PLAYER_NAME_TAG)) {
			skinSettings.playerName = tag.getString(PLAYER_NAME_TAG);
		}
		if (tag.hasKey(RANDOM_TAG)) {
			skinSettings.random = tag.getBoolean(RANDOM_TAG);
		}
		if (tag.hasKey(NPC_TYPE_NAME_TAG)) {
			skinSettings.npcTypeName = tag.getString(NPC_TYPE_NAME_TAG);
		}
		if (tag.hasKey(NPC_TYPE_SKIN_TAG)) {
			skinSettings.npcTypeSkin = new ResourceLocation(tag.getString(NPC_TYPE_SKIN_TAG));
		}
		skinSettings.isAlexModel = tag.getBoolean(IS_ALEX_MODEL_TAG);
		return skinSettings;
	}

	public NpcSkinSettings minimizeData() {
		NpcSkinSettings skinSettings = new NpcSkinSettings();
		skinSettings.skinTypeSelected = skinTypeSelected;
		if (skinTypeSelected == SkinType.PLAYER) {
			skinSettings.playerName = playerName;

		} else if (skinTypeSelected == SkinType.NPC_TYPE) {
			skinSettings.random = random;
			skinSettings.npcTypeName = npcTypeName;
			if (!random) {
				skinSettings.npcTypeSkin = npcTypeSkin;
			}
		}
		if (skinTypeSelected != SkinType.DEFAULT) {
			skinSettings.isAlexModel = isAlexModel;
		}
		return skinSettings;
	}

	private void loadPlayerSkin() {
		if (!playerSkinLoaded) {
			Optional<ResourceLocation> plSkin = AncientWarfareNPC.proxy.getPlayerSkin(playerName);
			if (plSkin.isPresent()) {
				playerSkin = plSkin.get();
				playerSkinLoaded = true;
			}
		}
	}

	private void loadPlayerProfile(NpcBase npc) {
		World world = npc.getEntityWorld();
		if (!world.isRemote) {
			sendEntityPacket(npc, data -> AncientWarfareNPC.proxy.cacheProfile((WorldServer) world, playerName).ifPresent(t -> data.setTag("profile", t)));
		}
	}

	private void sendEntityPacket(NpcBase npc, Consumer<NBTTagCompound> setData) {
		PacketEntity pkt = new PacketEntity(npc);
		NBTTagCompound data = new NBTTagCompound();
		setData.accept(data);
		pkt.packetData.setTag(PACKET_TAG_NAME, data);
		NetworkHandler.sendToAllTracking(npc, pkt);
	}

	public void handlePacketData(NBTTagCompound tag) {
		if (skinTypeSelected == SkinType.PLAYER) {
			Optional.ofNullable(NBTUtil.readGameProfileFromNBT(tag.getCompoundTag(PACKET_TAG_NAME).getCompoundTag("profile"))).ifPresent(AncientWarfareNPC.proxy::cacheProfile);
		}
	}

	@SideOnly(Side.CLIENT)
	public String getDescription() {
		switch (skinTypeSelected) {
			case PLAYER:
				return I18n.format("gui.ancientwarfarenpc.skin_info.player", playerName);
			case NPC_TYPE:
				return random || npcTypeSkin == null ? npcTypeName + " " + I18n.format("gui.ancientwarfarenpc.skin_info.npc_type_random") : npcTypeSkin.toString().replace("ancientwarfare:skinpack/", "").replace(".png", "");
			case DEFAULT:
			default:
				return I18n.format("gui.ancientwarfarenpc.skin_info.default");
		}
	}

	public void setSkinType(SkinType skinType) {
		this.skinTypeSelected = skinType;
	}

	public SkinType getSkinType() {
		return skinTypeSelected;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
		playerSkinLoaded = false;
		playerSkin = null;
	}

	public String getNpcTypeName() {
		return npcTypeName;
	}

	public void setNpcTypeName(String npcTypeName) {
		this.npcTypeName = npcTypeName;
	}

	public Optional<ResourceLocation> getNpcTypeSkin() {
		return Optional.ofNullable(npcTypeSkin);
	}

	public void resetNpcTypeSkin() {
		this.npcTypeSkin = null;
	}

	public void setNpcTypeSkin(ResourceLocation npcTypeSkin) {
		this.npcTypeSkin = npcTypeSkin;
	}

	public void setRandom(boolean random) {
		this.random = random;
	}

	public boolean isRandom() {
		return random;
	}

	public void onNpcSet(NpcBase npc) {
		loadPlayerProfile(npc);
	}

	public enum SkinType implements IStringSerializable {
		DEFAULT,
		PLAYER,
		NPC_TYPE;

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		private static final Map<String, SkinType> NAME_TYPE;

		static {
			ImmutableMap.Builder<String, SkinType> builder = new ImmutableMap.Builder<>();
			for (SkinType type : values()) {
				builder.put(type.getName(), type);
			}
			NAME_TYPE = builder.build();
		}

		public static SkinType byName(String name) {
			return NAME_TYPE.getOrDefault(name, DEFAULT);
		}
	}
}
