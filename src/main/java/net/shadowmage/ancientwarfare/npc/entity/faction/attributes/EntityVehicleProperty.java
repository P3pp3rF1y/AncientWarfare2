package net.shadowmage.ancientwarfare.npc.entity.faction.attributes;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.properties.EntityProperty;
import net.shadowmage.ancientwarfare.npc.AncientWarfareNPC;
import net.shadowmage.ancientwarfare.npc.entity.vehicle.IVehicleUser;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;

import java.util.Random;

public class EntityVehicleProperty implements EntityProperty {
	private final String testVehicle;

	private EntityVehicleProperty(String testVehicle) {
		this.testVehicle = testVehicle;
	}

	public boolean testProperty(Random random, Entity entityIn) {
		if (entityIn instanceof IVehicleUser) {
			IVehicleUser vehicleUserEntity = (IVehicleUser) entityIn;
			if (vehicleUserEntity.isRidingVehicle() && vehicleUserEntity.getVehicle().isPresent()) {
				VehicleBase vehicle = vehicleUserEntity.getVehicle().get();
				String vehicleConfigName = vehicle.vehicleType.getConfigName();
				return vehicleConfigName.contains(testVehicle);
			}
		}
		return false;
	}

	public static class Serializer extends EntityProperty.Serializer<EntityVehicleProperty> {
		public Serializer() {
			super(new ResourceLocation(AncientWarfareNPC.MOD_ID, "rides_vehicle"), EntityVehicleProperty.class);
		}

		public JsonElement serialize(EntityVehicleProperty property, JsonSerializationContext serializationContext) {
			return new JsonPrimitive(property.testVehicle);
		}

		public EntityVehicleProperty deserialize(JsonElement element, JsonDeserializationContext deserializationContext) {
			return new EntityVehicleProperty(JsonUtils.getString(element, AncientWarfareNPC.MOD_ID + ":rides_vehicle"));
		}
	}
}
