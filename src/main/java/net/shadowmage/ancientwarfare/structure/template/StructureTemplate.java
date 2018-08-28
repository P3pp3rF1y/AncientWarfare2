package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import java.util.Optional;

public class StructureTemplate {
	public static final Version CURRENT_VERSION = new Version(2, 3);

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
	private TemplateRule[] templateRules;
	private TemplateRuleEntity[] entityRules;
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

	public TemplateRuleEntity[] getEntityRules() {
		return entityRules;
	}

	public TemplateRule[] getTemplateRules() {
		return templateRules;
	}

	public short[] getTemplateData() {
		return templateData;
	}

	public StructureValidator getValidationSettings() {
		return validator;
	}

	public void setRuleArray(TemplateRule[] rules) {
		this.templateRules = rules;
	}

	public void setEntityRules(TemplateRuleEntity[] rules) {
		this.entityRules = rules;
	}

	public void setTemplateData(short[] datas) {
		this.templateData = datas;
	}

	public void setValidationSettings(StructureValidator settings) {
		this.validator = settings;
	}

	public Optional<TemplateRule> getRuleAt(int x, int y, int z) {
		int index = getIndex(x, y, z, size);
		int ruleIndex = index >= 0 && index < templateData.length ? templateData[index] : -1;
		return ruleIndex >= 0 && ruleIndex < templateRules.length ? Optional.ofNullable(templateRules[ruleIndex]) : Optional.empty();
	}

	public static int getIndex(int x, int y, int z, Vec3i size) {
		return (y * size.getX() * size.getZ()) + (z * size.getX()) + x;
	}

	@Override
	public String toString() {
		return "name: " + name + "\n" + "size: " + size.getX() + ", " + size.getY() + ", " + size.getZ() + "\n" + "buildKey: " + offset.getX() + ", " + offset.getY() + ", " + offset.getZ();
	}

	public NonNullList<ItemStack> getResourceList() {
		if (resourceList == null) {
			NonNullList<ItemStack> stacks = NonNullList.create();
			for (int x = 0; x < size.getX(); x++) {
				for (int y = 0; y < size.getY(); y++) {
					for (int z = 0; z < size.getZ(); z++) {
						getRuleAt(x, y, z).ifPresent(r -> r.addResources(stacks));
					}
				}
			}
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
