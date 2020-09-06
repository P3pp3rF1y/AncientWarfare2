package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.util.MathUtils;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityStatueInfo {
	private static final String ENTITY_NAME_TAG = "entityName";
	private static final String STATUE_ENTITY_NAME_TAG = "statueEntityName";
	private static final String OVERALL_TRANSFORM_TAG = "overallTransform";
	private static final String PART_TRANSFORMS_TAG = "partTransforms";

	private RenderType renderType = RenderType.ENTITY;

	private Entity entity = null;
	private ResourceLocation entityName = null;
	private boolean entityOnFire = false;

	private String statueEntityName = "Zombie";
	private Transform overallTransform = new Transform();
	private Map<String, Transform> partTransforms = new HashMap<>();

	Optional<Entity> getRenderEntity(World world) {
		if (entity != null) {
			return Optional.of(entity);
		}
		if (entityName != null && world.isRemote) {
			EntityEntry entityEntry = ForgeRegistries.ENTITIES.getValue(entityName);
			if (entityEntry == null) {
				entityName = null;
				return Optional.empty();
			}
			entity = entityEntry.newInstance(world);
			return Optional.of(entity);
		}
		return Optional.empty();
	}

	boolean isEntityOnFire() {
		return entityOnFire;
	}

	void setEntityOnFire(boolean entityOnFire) {
		this.entityOnFire = entityOnFire;
	}

	void resetEntityName() {
		entityName = null;
		entity = null;
	}

	@Nullable
	public ResourceLocation getEntityName() {
		return entityName;
	}

	void setEntityName(@Nullable ResourceLocation entityName) {
		this.entityName = entityName;
		entity = null;
	}

	public void deserializeNBT(NBTTagCompound tag) {
		renderType = RenderType.byName(tag.getString("renderType"));
		if (tag.hasKey(STATUE_ENTITY_NAME_TAG)) {
			statueEntityName = tag.getString(STATUE_ENTITY_NAME_TAG);
			if (tag.hasKey(OVERALL_TRANSFORM_TAG)) {
				overallTransform = new Transform();
				overallTransform.deserializeNBT(tag.getCompoundTag(OVERALL_TRANSFORM_TAG));
			}
			if (tag.hasKey(PART_TRANSFORMS_TAG)) {
				partTransforms = NBTHelper.getMap(tag.getTagList(PART_TRANSFORMS_TAG, Constants.NBT.TAG_COMPOUND), t -> t.getString("name"), t -> {
					Transform transform = new Transform();
					transform.deserializeNBT(t.getCompoundTag("transform"));
					return transform;
				});
			}
		} else {
			if (tag.hasKey(ENTITY_NAME_TAG)) {
				setEntityName(new ResourceLocation(tag.getString(ENTITY_NAME_TAG)));
				setEntityOnFire(tag.getBoolean("entityOnFire"));
			} else {
				setEntityName(null);
			}
		}

	}

	public NBTTagCompound serializeNBT(NBTTagCompound tag) {
		tag.setString("renderType", renderType.getName());
		if (renderType == RenderType.ENTITY) {
			if (getEntityName() != null) {
				tag.setString(ENTITY_NAME_TAG, getEntityName().toString());
				tag.setBoolean("entityOnFire", isEntityOnFire());
			}
		} else {
			tag.setString(STATUE_ENTITY_NAME_TAG, statueEntityName);
			tag.setTag(OVERALL_TRANSFORM_TAG, overallTransform.serializeNBT());
			tag.setTag(PART_TRANSFORMS_TAG, NBTHelper.mapToCompoundList(partTransforms, (t, name) -> t.setString("name", name),
					(t, transform) -> t.setTag("transform", transform.serializeNBT())));
		}
		return tag;
	}

	public void setRenderType(RenderType renderType) {
		this.renderType = renderType;
	}

	public void setPartTransform(String partName, Transform partTransform) {
		partTransforms.put(partName, partTransform);
	}

	public RenderType getRenderType() {
		return renderType;
	}

	public String getStatueEntityName() {
		return statueEntityName;
	}

	public Map<String, Transform> getPartTransforms() {
		return partTransforms;
	}

	public Transform getOverallTransform() {
		return overallTransform;
	}

	public void setOverallTransform(Transform transform) {
		overallTransform = transform;
	}

	public void setStatueEntityName(String name) {
		statueEntityName = name;
	}

	public enum RenderType implements IStringSerializable {
		ENTITY("entity"),
		MODEL("model");

		private static final Map<String, RenderType> NAME_TYPE = new HashMap<>();

		static {
			for (RenderType type : values()) {
				NAME_TYPE.put(type.getName(), type);
			}
		}

		private String name;

		RenderType(String name) {this.name = name;}

		public static RenderType byName(String name) {
			return NAME_TYPE.getOrDefault(name, ENTITY);
		}

		@Override
		public String getName() {
			return name;
		}
	}

	public static class Transform implements INBTSerializable<NBTTagCompound> {
		private float offsetX = 0;
		private float offsetY = 0;
		private float offsetZ = 0;
		private float rotationX = 0;
		private float rotationY = 0;
		private float rotationZ = 0;
		private float scale = 1;

		public Transform() {}

		Transform(float offsetX, float offsetY, float offsetZ, float rotationX, float rotationY, float rotationZ, float scale) {
			this(offsetX, offsetY, offsetZ, rotationX, rotationY, rotationZ);
			this.scale = scale;
		}
		Transform(float offsetX, float offsetY, float offsetZ, float rotationX, float rotationY, float rotationZ) {
			this.offsetX = offsetX;
			this.offsetY = offsetY;
			this.offsetZ = offsetZ;
			this.rotationX = rotationX;
			this.rotationY = rotationY;
			this.rotationZ = rotationZ;
		}

		public float getOffsetX() {
			return offsetX;
		}

		public void setOffsetX(float offset) {
			offsetX = offset;
		}

		public float getOffsetY() {
			return offsetY;
		}

		public void setOffsetY(float offsetY) {
			this.offsetY = offsetY;
		}

		public float getOffsetZ() {
			return offsetZ;
		}

		public void setOffsetZ(float offsetZ) {
			this.offsetZ = offsetZ;
		}

		public float getRotationX() {
			return rotationX;
		}

		public void setRotationX(float rotationX) {
			this.rotationX = rotationX;
		}

		public float getRotationY() {
			return rotationY;
		}

		public void setRotationY(float rotationY) {
			this.rotationY = rotationY;
		}

		public float getRotationZ() {
			return rotationZ;
		}

		public void setRotationZ(float rotationZ) {
			this.rotationZ = rotationZ;
		}

		public float getScale() {
			return scale;
		}

		public void setScale(float scale) {
			this.scale = scale;
		}

		@Override
		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			setNonDefaultValue(tag, "offsetX", offsetX);
			setNonDefaultValue(tag, "offsetY", offsetY);
			setNonDefaultValue(tag, "offsetZ", offsetZ);

			setNonDefaultValue(tag, "rotationX", rotationX);
			setNonDefaultValue(tag, "rotationY", rotationY);
			setNonDefaultValue(tag, "rotationZ", rotationZ);

			setNonDefaultValue(tag, "scale", scale, 1);

			return tag;
		}

		private void setNonDefaultValue(NBTTagCompound tag, String tagName, float value) {
			setNonDefaultValue(tag, tagName, value, 0);
		}
		private void setNonDefaultValue(NBTTagCompound tag, String tagName, float value, float defaultValue) {
			if (!MathUtils.epsilonEquals(value, defaultValue)) {
				tag.setFloat(tagName, value);
			}
		}

		@Override
		public void deserializeNBT(NBTTagCompound nbt) {
			offsetX = loadValue(nbt, "offsetX").orElse(0f);
			offsetY = loadValue(nbt, "offsetY").orElse(0f);
			offsetZ = loadValue(nbt, "offsetZ").orElse(0f);

			rotationX = loadValue(nbt, "rotationX").orElse(0f);
			rotationY = loadValue(nbt, "rotationY").orElse(0f);
			rotationZ = loadValue(nbt, "rotationZ").orElse(0f);

			scale = loadValue(nbt, "scale").orElse(1f);
		}

		private Optional<Float> loadValue(NBTTagCompound nbt, String name) {
			if (!nbt.hasKey(name)) {
				return Optional.empty();
			}

			return Optional.of(nbt.getFloat(name));
		}
	}
}