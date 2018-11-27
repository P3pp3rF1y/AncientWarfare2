package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.json;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.RuleDataFixerBase;

public class JsonSimplificationFixer extends RuleDataFixerBase {
	@Override
	public StructureTemplate.Version getVersion() {
		return new StructureTemplate.Version(2, 3);
	}

	@Override
	public boolean isForRule(String ruleName) {
		return true; //so many rules that use JSON so just check it for every single one
	}

	@Override
	protected FixResult<String> fixData(String ruleName, String data) {
		if (!Json.isSerializedJSON(data)) {
			return new FixResult.NotModified<>(data);
		}

		NBTTagCompound tag = JsonTagReader.parseTagCompound(data);

		return new FixResult.Modified<>(TemplateRule.JSON_PREFIX + tag.toString(), "JsonSimplificationFixer");
	}
}
