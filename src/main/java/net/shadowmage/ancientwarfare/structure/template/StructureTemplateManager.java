package net.shadowmage.ancientwarfare.structure.template;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureRemove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class StructureTemplateManager {
	private StructureTemplateManager() {
	}

	private static final String SINGLE_STRUCTURE_TAG = "singleStructure";
	private static final String SYNC_TEMPLATE_TAG = "syncTemplate";
	private static final String STRUCTURE_LIST_TAG = "structureList";
	private static final Cache<String, String> requestedTemplates = CacheBuilder.newBuilder().expireAfterAccess(10, TimeUnit.SECONDS).build();
	private static HashMap<String, StructureTemplate> loadedTemplates = new HashMap<>();

	//used on client side - only these get synced on log on and then get displayed in structure selection guis, subsequent calls to getTemplate will trigger syncing of the full template detail
	private static Set<String> allTemplateNames = new HashSet<>();
	private static Set<String> survivalTemplateNames = new HashSet<>();

	private static Set<ITemplateObserver> observers = new HashSet<>();

	public static void addTemplate(StructureTemplate template) {
		if (template.getValidationSettings() == null) {
			return;
		}
		if (template.getValidationSettings().isWorldGenEnabled()) {
			WorldGenStructureManager.INSTANCE.registerWorldGenStructure(template);
		}
		if (loadedTemplates.keySet().contains(template.name)) {
			AncientWarfareStructure.proxy.clearTemplatePreviewCache();
		}
		loadedTemplates.put(template.name, template);

		syncTemplateToClient(template);
	}

	private static void syncTemplateToClient(StructureTemplate template) {
		//noinspection ConstantConditions
		if (FMLCommonHandler.instance().getSide() == Side.SERVER && FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList() != null) {
			PacketStructure pkt = new PacketStructure();
			pkt.packetData.setTag(SINGLE_STRUCTURE_TAG, template.serializeNBT());
			NetworkHandler.sendToAllPlayers(pkt);
		}
	}

	public static void onPlayerConnect(EntityPlayerMP player) {
		Set<String> survivalTemplates = loadedTemplates.entrySet().stream()
				.filter(e -> e.getValue().getValidationSettings().isSurvival())
				.map(Map.Entry::getKey).collect(Collectors.toSet());
		if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
			PacketStructure pkt = new PacketStructure();
			pkt.packetData.setTag(STRUCTURE_LIST_TAG, NBTHelper.getNBTStringList(loadedTemplates.keySet()));
			pkt.packetData.setTag("survivalStructures", NBTHelper.getNBTStringList(survivalTemplates));
			NetworkHandler.sendToPlayer(player, pkt);
		} else {
			survivalTemplateNames = survivalTemplates;
			allTemplateNames = loadedTemplates.keySet();
		}
	}

	public static boolean removeTemplate(String name) {
		if (loadedTemplates.containsKey(name)) {
			loadedTemplates.remove(name);
			if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
				NetworkHandler.sendToAllPlayers(new PacketStructureRemove(name));
			}
			return true;
		}
		return false;
	}

	public static void removeAll() {
		//creating a new list because otherwise we run into concurrent modification exception as the collection is both queried and modified
		new ArrayList<>(loadedTemplates.keySet()).forEach(StructureTemplateManager::removeTemplate);
	}

	public static Optional<StructureTemplate> getTemplate(String name) {
		StructureTemplate template = loadedTemplates.get(name);
		if (template == null && FMLCommonHandler.instance().getSide() == Side.CLIENT && allTemplateNames.contains(name) && requestedTemplates.getIfPresent(name) == null) {
			requestedTemplates.put(name, name);
			PacketStructure pkt = new PacketStructure();
			pkt.packetData.setString(SYNC_TEMPLATE_TAG, name);
			NetworkHandler.sendToServer(pkt);
		}
		return Optional.ofNullable(template);
	}

	public static Map<String, StructureTemplate> getSurvivalStructures() {
		return loadedTemplates.entrySet().stream().filter(e -> e.getValue().getValidationSettings().isSurvival()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public static void onTemplateData(NBTTagCompound tag) {
		if (tag.hasKey(SINGLE_STRUCTURE_TAG)) {
			StructureTemplate template = StructureTemplate.deserializeNBT(tag.getCompoundTag(SINGLE_STRUCTURE_TAG));
			addTemplate(template);
			observers.forEach(observer -> observer.notifyTemplateChange(template));
		} else if (tag.hasKey(SYNC_TEMPLATE_TAG)) {
			getTemplate(tag.getString(SYNC_TEMPLATE_TAG)).ifPresent(StructureTemplateManager::syncTemplateToClient);
		} else if (tag.hasKey(STRUCTURE_LIST_TAG)) {
			loadedTemplates.clear();
			allTemplateNames = NBTHelper.getStringSet(tag.getTagList(STRUCTURE_LIST_TAG, Constants.NBT.TAG_STRING));
			survivalTemplateNames = NBTHelper.getStringSet(tag.getTagList("survivalStructures", Constants.NBT.TAG_STRING));
		}
	}

	public static Set<String> getTemplates() {
		return allTemplateNames;
	}

	public static Set<String> getSurvivalTemplates() {
		return survivalTemplateNames;
	}

	public static boolean templateExists(String name) {
		return loadedTemplates.keySet().contains(name);
	}

	public static void registerObserver(ITemplateObserver observer) {
		observers.add(observer);
	}

	public static void unregisterObserver(ITemplateObserver observer) {
		observers.remove(observer);
	}

	public interface ITemplateObserver {
		void notifyTemplateChange(StructureTemplate template);
	}
}
