package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.json;

import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.IDataFixer;

public class JsonSimplificationFixer implements IDataFixer {
	@Override
	public FixResult<String> fix(String ruleName, String line) {
		if (!Json.isSerializedJSON(line)) {
			return new FixResult.NotModified<>(line);
		}

		NBTTagCompound tag = JsonTagReader.parseTagCompound(line);

		return new FixResult.Modified<>(TemplateRule.JSON_PREFIX + tag.toString(), "JsonSimplificationFixer");
	}

	@Override
	public StructureTemplate.Version getVersion() {
		return new StructureTemplate.Version(2, 3);
	}

	@Override
	public boolean isForRule(String ruleName) {
		return true; //so many rules that use JSON so just check it for every single one
	}
}
