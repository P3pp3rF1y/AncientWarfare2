package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.structure.network.PacketStructure;
import net.shadowmage.ancientwarfare.structure.network.PacketStructureRemove;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class StructureTemplateManager {
	private static final String SINGLE_STRUCTURE_TAG = "singleStructure";
	private HashMap<String, StructureTemplate> loadedTemplates = new HashMap<>();

	private StructureTemplateManager() {
	}

	public static final StructureTemplateManager INSTANCE = new StructureTemplateManager();

	public void addTemplate(StructureTemplate template) {
		if (template.getValidationSettings() == null) {
			return;
		}
		if (template.getValidationSettings().isWorldGenEnabled()) {
			WorldGenStructureManager.INSTANCE.registerWorldGenStructure(template);
		}
		loadedTemplates.put(template.name, template);

		PacketStructure pkt = new PacketStructure();
		pkt.packetData.setTag(SINGLE_STRUCTURE_TAG, template.serializeNBT());
		NetworkHandler.sendToAllPlayers(pkt);
	}

	public void onPlayerConnect(EntityPlayerMP player) {
		NBTTagList list = new NBTTagList();
		for (StructureTemplate template : loadedTemplates.values()) {
			list.appendTag(template.serializeNBT());
		}
		PacketStructure pkt = new PacketStructure();
		pkt.packetData.setTag("structureList", list);
		NetworkHandler.sendToPlayer(player, pkt);
	}

	public boolean removeTemplate(String name) {
		if (loadedTemplates.containsKey(name)) {
			loadedTemplates.remove(name);
			NetworkHandler.sendToAllPlayers(new PacketStructureRemove(name));
			return true;
		}
		return false;
	}

	public void removeAll() {
		//creating a new list because otherwise we run into concurrent modification exception as the collection is both queried and modified
		new ArrayList<>(loadedTemplates.keySet()).forEach(this::removeTemplate);
	}

	public StructureTemplate getTemplate(String name) {
		return this.loadedTemplates.get(name);
	}

	public Map<String, StructureTemplate> getSurvivalStructures() {
		return loadedTemplates.entrySet().stream().filter(e -> e.getValue().getValidationSettings().isSurvival()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public void onTemplateData(NBTTagCompound tag) {
		if (tag.hasKey(SINGLE_STRUCTURE_TAG)) {
			addTemplate(StructureTemplate.deserializeNBT(tag.getCompoundTag(SINGLE_STRUCTURE_TAG)));
		} else {
			loadedTemplates.clear();
			NBTTagList list = tag.getTagList("structureList", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < list.tagCount(); i++) {
				addTemplate(StructureTemplate.deserializeNBT(list.getCompoundTagAt(i)));
			}
		}
	}

	public Collection<StructureTemplate> getTemplates() {
		return loadedTemplates.values();
	}

	public boolean templateExists(String name) {
		return loadedTemplates.keySet().contains(name);
	}
}
