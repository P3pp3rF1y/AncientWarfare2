package net.shadowmage.ancientwarfare.structure.registry;

import com.google.gson.JsonObject;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.shadowmage.ancientwarfare.core.registry.IRegistryDataParser;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

public class EntitySpawnNBTRegistry {
	private EntitySpawnNBTRegistry() {}

	private static final Map<Class, Set<String>> entityNBT = new HashMap<>();

	public static NBTTagCompound getEntitySpawnNBT(Entity entity, NBTTagCompound entityTag) {
		NBTTagCompound ret = new NBTTagCompound();

		for (Map.Entry<Class, Set<String>> entry : entityNBT.entrySet()) {
			if (entry.getKey().isInstance(entity)) {
				for (String tag : entry.getValue()) {
					if (entityTag.hasKey(tag)) {
						ret.setTag(tag, entityTag.getTag(tag));
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
