package net.shadowmage.ancientwarfare.structure.template;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.Vec3i;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.core.util.MathUtils;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntityBase;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class StructureTemplate {
	public static final Version CURRENT_VERSION = new Version(2, 7);

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
	private Map<Integer, TemplateRule> blockRules;
	private Map<Integer, TemplateRuleEntityBase> entityRules;
	private short[] templateData;
	private NonNullList<ItemStack> resourceList;
	private List<ItemStack> remainingStacks;

	/*
	 * world generation placement validation settings
	 */
	private StructureValidator validator;

	public StructureTemplate(String name, Set<String> modDependencies, Vec3i size, Vec3i offset) {
		this(name, modDependencies, CURRENT_VERSION, size, offset);
	}

	public StructureTemplate(String name, Set<String> modDependencies, Version version, Vec3i size, Vec3i offset) {
		if (name == null) {
			throw new IllegalArgumentException("cannot have null name for structure");
		}
		this.modDependencies = modDependencies;
		this.version = version;
		this.name = name;
		this.size = size;
		this.offset = offset;
	}

	public Map<Integer, TemplateRuleEntityBase> getEntityRules() {
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

	public void setEntityRules(Map<Integer, TemplateRuleEntityBase> rules) {
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
			MathUtils.getAllVecsInBox(Vec3i.NULL_VECTOR, new Vec3i(size.getX() - 1, size.getY() - 1, size.getZ() - 1))
					.forEach(pos -> getRuleAt(pos).ifPresent(r -> r.addResources(stacks)));
			resourceList = InventoryTools.compactStackList(stacks);
		}
		return resourceList;
	}

	public List<ItemStack> getRemainingStacks() {
		if (remainingStacks == null) {
			NonNullList<ItemStack> stacks = NonNullList.create();
			MathUtils.getAllVecsInBox(Vec3i.NULL_VECTOR, new Vec3i(size.getX() - 1, size.getY() - 1, size.getZ() - 1))
					.forEach(pos -> getRuleAt(pos).ifPresent(r -> {
						ItemStack stack = r.getRemainingStack();
						if (!stack.isEmpty()) {
							stacks.add(stack);
						}
					}));
			remainingStacks = InventoryTools.compactStackList(stacks);
		}
		return remainingStacks;
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
