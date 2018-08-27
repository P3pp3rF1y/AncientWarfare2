package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

public class StructureTemplate {
	public static final Version CURRENT_VERSION = new Version(2, 2);

	/*
	 * base datas
	 */
	public final String name;
	public final int xSize, ySize, zSize;
	public final int xOffset, yOffset, zOffset;
	private Version version;

	/*
	 * stored template data
	 */
	private TemplateRule[] templateRules;
	private TemplateRuleEntity[] entityRules;
	private short[] templateData;
	NonNullList<ItemStack> resourceList;

	/*
	 * world generation placement validation settings
	 */
	private StructureValidator validator;

	public StructureTemplate(String name, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset) {
		this(name, CURRENT_VERSION, xSize, ySize, zSize, xOffset, yOffset, zOffset);
	}

	public StructureTemplate(String name, Version version, int xSize, int ySize, int zSize, int xOffset, int yOffset, int zOffset) {
		if (name == null) {
			throw new IllegalArgumentException("cannot have null name for structure");
		}
		this.version = version;
		this.name = name;
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.zOffset = zOffset;
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

	public TemplateRule getRuleAt(int x, int y, int z) {
		int index = getIndex(x, y, z, xSize, ySize, zSize);
		int ruleIndex = index >= 0 && index < templateData.length ? templateData[index] : -1;
		return ruleIndex >= 0 && ruleIndex < templateRules.length ? templateRules[ruleIndex] : null;
	}

	public static int getIndex(int x, int y, int z, int xSize, int ySize, int zSize) {
		return (y * xSize * zSize) + (z * xSize) + x;
	}

	@Override
	public String toString() {
		return "name: " + name + "\n" + "size: " + xSize + ", " + ySize + ", " + zSize + "\n" + "buildKey: " + xOffset + ", " + yOffset + ", " + zOffset;
	}

	public NonNullList<ItemStack> getResourceList() {
		if (resourceList == null) {
			TemplateRule rule;
			NonNullList<ItemStack> stacks = NonNullList.create();
			for (int x = 0; x < this.xSize; x++) {
				for (int y = 0; y < this.ySize; y++) {
					for (int z = 0; z < this.zSize; z++) {
						rule = getRuleAt(x, y, z);
						if (rule != null) {
							rule.addResources(stacks);
						}
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
		return xSize == that.xSize && ySize == that.ySize && zSize == that.zSize && xOffset == that.xOffset && yOffset == that.yOffset && zOffset == that.zOffset && name.equals(that.name);
	}

	@Override
	public int hashCode() {
		int result = xSize;
		result = 31 * result + ySize;
		result = 31 * result + zSize;
		result = 31 * result + xOffset;
		result = 31 * result + yOffset;
		result = 31 * result + zOffset;
		result = 31 * result + name.hashCode();
		return result;
	}

	public Version getVersion() {
		return version;
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
