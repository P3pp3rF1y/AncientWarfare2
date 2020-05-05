package net.shadowmage.ancientwarfare.structure.registry;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

public class EntitySpawnNBTRegistry {
	private EntitySpawnNBTRegistry() {}

	private static final Map<Class, Set<String>> entityNBT = new HashMap<>();

	static {
		entityNBT.put(EntityVillager.class, ImmutableSet.of("Offers", "Profession", "ProfessionName", "Career", "CareerLevel"));
		entityNBT.put(EntityHorse.class, Collections.singleton("Variant"));
		entityNBT.put(EntityLiving.class, ImmutableSet.of("HandItems", "HandDropChances", "ArmorItems", "ArmorDropChances", "CustomName"));
		entityNBT.put(Entity.class, Collections.singleton("CustomName"));
	}

	public static NBTTagCompound getEntitySpawnNBT(Entity entity) {
		NBTTagCompound ret = new NBTTagCompound();
		NBTTagCompound fullEntityNbt = new NBTTagCompound();
		entity.writeToNBT(fullEntityNbt);

		for (Map.Entry<Class, Set<String>> entry : entityNBT.entrySet()) {
			if (entry.getKey().isInstance(entity)) {
				for (String tag : entry.getValue()) {
					if (fullEntityNbt.hasKey(tag)) {
						ret.setTag(tag, fullEntityNbt.getTag(tag));
					}
				}
			}
		}
		return ret;
	}

	public static class Parser implements IRegistryDataParser {
		@Override
		public String getName() {
			return "entity_spawn_nbt";
		}

		@Override
		public void parse(JsonObject json) {
			entityNBT.putAll(JsonHelper.mapFromJson(json, "entity_nbt",
					entry -> getClass(entry.getKey()),
					entry -> JsonHelper.setFromJson(entry.getValue(), element -> JsonUtils.getString(element, ""))));
		}

		private Class getClass(String className) {
			try {
				return Class.forName(className);
			}
			catch (ClassNotFoundException e) {
				throw new MissingResourceException("Unable to find class for class name: " + className, Class.class.toString(), className);
			}
		}
	}
}
