package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.MathUtils;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import java.util.Map;
import java.util.Optional;

public class StructureTemplate {
	public static final Version CURRENT_VERSION = new Version(2, 5);

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
	private Map<Integer, TemplateRule> blockRules;
	private Map<Integer, TemplateRuleEntity> entityRules;
	private short[] templateData;
	private NonNullList<ItemStack> resourceList;

	/*
	 * world generation placement validation settings
	 */
	private StructureValidator validator;

	public StructureTemplate(String name, Vec3i size, Vec3i offset) {
		this(name, CURRENT_VERSION, size, offset);
	}

	public StructureTemplate(String name, Version version, Vec3i size, Vec3i offset) {
		if (name == null) {
			throw new IllegalArgumentException("cannot have null name for structure");
		}
		this.version = version;
		this.name = name;
		this.size = size;
		this.offset = offset;
	}

	public Map<Integer, TemplateRuleEntity> getEntityRules() {
		return entityRules;
	}

	public Map<Integer, TemplateRule> getBlockRules() {
		return blockRules;
	}

	public short[] getTemplateData() {
		return templateData;
	}

	public StructureValidator getValidationSettings() {
		return validator;
	}

	public void setBlockRules(Map<Integer, TemplateRule> rules) {
		this.blockRules = rules;
	}

	public void setEntityRules(Map<Integer, TemplateRuleEntity> rules) {
		this.entityRules = rules;
	}

	public void setTemplateData(short[] datas) {
		this.templateData = datas;
	}

	public void setValidationSettings(StructureValidator settings) {
		this.validator = settings;
	}

	public Optional<TemplateRule> getRuleAt(Vec3i pos) {
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

	public NonNullList<ItemStack> getResourceList() {
		if (resourceList == null) {
			NonNullList<ItemStack> stacks = NonNullList.create();
			MathUtils.getAllVecsInBox(Vec3i.NULL_VECTOR, size).forEach(pos -> getRuleAt(pos).ifPresent(r -> r.addResources(stacks)));
			resourceList = InventoryTools.compactStackList(stacks);
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
}
