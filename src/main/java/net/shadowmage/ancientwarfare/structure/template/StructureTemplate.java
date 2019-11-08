package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.MathUtils;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleBlock;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntityBase;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidationType;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.template.datafixes.DataFixManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StructureTemplate {
	/*
	 * base datas
	 */
	public final String name;
	public final Vec3i size;
	public final Vec3i offset;
	private Version version;

	/*
	 * stored template data
	 */
	public final Set<String> modDependencies;
	private Map<Integer, TemplateRuleBlock> blockRules;
	private Map<Integer, TemplateRuleEntityBase> entityRules;
	private short[] templateData;
	private List<BuildResource> resourceList;

	/*
	 * world generation placement validation settings
	 */
	private StructureValidator validator = StructureValidationType.GROUND.getValidator();

	public StructureTemplate(String name, Set<String> modDependencies, Vec3i size, Vec3i offset) {
		this(name, modDependencies, DataFixManager.getCurrentVersion(), size, offset);
	}

	public StructureTemplate(String name, Set<String> modDependencies, Version version, Vec3i size, Vec3i offset) {
		this.modDependencies = modDependencies;
		this.version = version;
		this.name = name;
		this.size = size;
		this.offset = offset;
	}

	public Map<Integer, TemplateRuleEntityBase> getEntityRules() {
		return entityRules;
	}

	public Map<Integer, TemplateRuleBlock> getBlockRules() {
		return blockRules;
	}

	public short[] getTemplateData() {
		return templateData;
	}

	public StructureValidator getValidationSettings() {
		return validator;
	}

	public void setBlockRules(Map<Integer, TemplateRuleBlock> rules) {
		this.blockRules = rules;
	}

	public void setEntityRules(Map<Integer, TemplateRuleEntityBase> rules) {
		this.entityRules = rules;
	}

	public void setTemplateData(short[] datas) {
		this.templateData = datas;
	}

	public void setValidationSettings(StructureValidator settings) {
		this.validator = settings;
	}

	public Optional<TemplateRuleBlock> getRuleAt(Vec3i pos) {
		int index = getIndex(pos, size);
		int ruleIndex = index >= 0 && index < templateData.length ? templateData[index] : -1;
		return Optional.ofNullable(blockRules.get(ruleIndex));
	}

	public static int getIndex(Vec3i pos, Vec3i size) {
		return (pos.getY() * size.getX() * size.getZ()) + (pos.getZ() * size.getX()) + pos.getX();
	}

	@Override
	public String toString() {
		return "name: " + name + "\n" + "size: " + size.getX() + ", " + size.getY() + ", " + size.getZ() + "\n" + "buildKey: " + offset.getX() + ", " + offset.getY() + ", " + offset.getZ();
	}

	public List<BuildResource> getResourceList() {
		if (resourceList == null) {
			List<BuildResource> allResources = new ArrayList<>();

			NonNullList<ItemStack> consumeOnlyResources = NonNullList.create();

			MathUtils.getAllVecsInBox(Vec3i.NULL_VECTOR, new Vec3i(size.getX() - 1, size.getY() - 1, size.getZ() - 1))
					.forEach(pos -> getRuleAt(pos).ifPresent(r -> {
								ItemStack remainingStack = r.getRemainingStack();
								List<ItemStack> resources = r.getResources();
								if (remainingStack.isEmpty() || resources.size() > 1) {
									consumeOnlyResources.addAll(resources);
								} else {
									resources.forEach(res -> allResources.add(new BuildResource(res, remainingStack)));
								}
							})
					);
			InventoryTools.compactStackList(consumeOnlyResources).forEach(res -> allResources.add(new BuildResource(res)));

			resourceList = allResources;
		}
		return resourceList;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof StructureTemplate))
			return false;
		StructureTemplate that = (StructureTemplate) o;
		return size.equals(that.size) && offset.equals(that.offset) && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = size.hashCode();
		result = 31 * result + offset.hashCode();
		result = 31 * result + name.hashCode();
		return result;
	}

	public Version getVersion() {
		return version;
	}

	public Vec3i getSize() {
		return size;
	}

	public Vec3i getOffset() {
		return offset;
	}

	public NBTTagCompound serializeNBT() {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setString("name", name);
		tag.setLong("size", MathUtils.toLong(size));
		tag.setLong("offset", MathUtils.toLong(offset));
		tag.setTag("blockRules", serializeRules(blockRules));
		tag.setTag("entityRules", serializeRules(entityRules));
		tag.setIntArray("templateData", MathUtils.toIntArray(templateData));
		tag.setString("validationType", validator.validationType.getName());
		tag.setTag("validator", validator.serializeToNBT());

		return tag;
	}

	public static StructureTemplate deserializeNBT(NBTTagCompound tag) {
		String name = tag.getString("name");
		Vec3i size = MathUtils.fromLong(tag.getLong("size"));
		Vec3i offset = MathUtils.fromLong(tag.getLong("offset"));

		StructureTemplate template = new StructureTemplate(name, Collections.emptySet(), size, offset);
		template.setBlockRules(deserializeRules(tag.getTagList("blockRules", Constants.NBT.TAG_COMPOUND)));
		template.setEntityRules(deserializeRules(tag.getTagList("entityRules", Constants.NBT.TAG_COMPOUND)));
		template.setTemplateData(MathUtils.toShortArray(tag.getIntArray("templateData")));
		StructureValidationType.getTypeFromName(tag.getString("validationType")).ifPresent(type -> {
			StructureValidator structureValidator = type.getValidator();
			structureValidator.readFromNBT(tag.getCompoundTag("validator"));
			template.setValidationSettings(structureValidator);
		});
		return template;
	}

	private static <T extends TemplateRule> Map<Integer, T> deserializeRules(NBTTagList blockRules) {
		Map<Integer, T> ret = new HashMap<>();
		for (NBTBase data : blockRules) {
			NBTTagCompound ruleData = (NBTTagCompound) data;
			StructurePluginManager.INSTANCE.getRuleByName(ruleData.getString("pluginName")).ifPresent(
					rule -> {
						rule.parseRule(ruleData);
						//noinspection unchecked
						ret.put(ruleData.getInteger("ruleNumber"), (T) rule);
					}
			);

		}
		return ret;
	}

	private NBTTagList serializeRules(Map<Integer, ? extends TemplateRule> templateRules) {
		return templateRules.entrySet().stream().map(this::serializeRule).collect(NBTHelper.NBTLIST_COLLECTOR);
	}

	private NBTTagCompound serializeRule(Map.Entry<Integer, ? extends TemplateRule> blockRule) {
		NBTTagCompound ruleData = new NBTTagCompound();
		ruleData.setInteger("ruleNumber", blockRule.getKey());
		ruleData.setString("pluginName", blockRule.getValue().getPluginName());
		blockRule.getValue().writeRuleData(ruleData);
		return ruleData;
	}

	public static class Version {
		private final int major;
		private final int minor;

		public Version(int major, int minor) {
			this.major = major;
			this.minor = minor;
		}

		public Version(String version) {
			this(Integer.valueOf(version.substring(0, version.indexOf('.'))), Integer.valueOf(version.substring(version.indexOf('.') + 1)));
		}

		public boolean isGreaterThan(Version otherVersion) {
			return getMajor() > otherVersion.getMajor() || getMinor() > otherVersion.getMinor();
		}

		public static final Version NONE = new Version(0, 0);

		public int getMajor() {
			return major;
		}

		public int getMinor() {
			return minor;
		}
	}

	public static class BuildResource implements INBTSerializable<NBTTagCompound> {
		private static final String STACK_TO_RETURN_TAG = "stackToReturn";
		private ItemStack stackRequired;
		private ItemStack stackToReturn = ItemStack.EMPTY;
		private int requiredOriginalCount = 0;

		private BuildResource(ItemStack stackRequired, ItemStack stackToReturn) {
			this.stackRequired = stackRequired;
			this.stackToReturn = stackToReturn;
			requiredOriginalCount = stackRequired.getCount();
		}

		private BuildResource(ItemStack stackRequired) {
			this.stackRequired = stackRequired;
		}

		public BuildResource() {}

		public ItemStack getStackRequired() {
			return stackRequired;
		}

		public BuildResource copy() {
			return new BuildResource(stackRequired.copy(), stackToReturn.copy());
		}

		public ItemStack shrinkStackRequiredAndGetRemaining() {
			stackRequired.shrink(1);
			if (!stackToReturn.isEmpty() && stackRequired.getCount() % requiredOriginalCount == 0) {
				return stackToReturn.copy();
			}
			return ItemStack.EMPTY;
		}

		public boolean isEmpty() {
			return stackRequired.isEmpty();
		}

		public NBTTagCompound serializeNBT() {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag("stackRequired", stackRequired.writeToNBT(new NBTTagCompound()));
			if (!stackToReturn.isEmpty()) {
				tag.setTag(STACK_TO_RETURN_TAG, stackToReturn.writeToNBT(new NBTTagCompound()));
				tag.setInteger("requiredOriginalCount", requiredOriginalCount);
			}
			return tag;
		}

		public void deserializeNBT(NBTTagCompound tag) {
			stackRequired = new ItemStack(tag.getCompoundTag("stackRequired"));
			if (tag.hasKey(STACK_TO_RETURN_TAG)) {
				stackToReturn = new ItemStack(tag.getCompoundTag(STACK_TO_RETURN_TAG));
				requiredOriginalCount = tag.getInteger("requiredOriginalCount");
			}
		}
	}
}
