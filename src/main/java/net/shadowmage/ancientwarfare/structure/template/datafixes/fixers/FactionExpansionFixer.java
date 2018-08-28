package net.shadowmage.ancientwarfare.structure.template.datafixes.fixers;

import net.shadowmage.ancientwarfare.npc.datafixes.FactionExpansionEntityFixer;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;
import net.shadowmage.ancientwarfare.structure.template.datafixes.IDataFixer;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.json.Json;
import net.shadowmage.ancientwarfare.structure.template.datafixes.fixers.json.Json.JsonObject;

import java.util.Optional;

public class FactionExpansionFixer implements IDataFixer {
	private static final Version VERSION = new Version(2, 2);

	@Override
	public FixResult<String> fix(String line) {
		Optional<JsonObject> parsedJson = Json.parseJson(line);

		if (!parsedJson.isPresent()) {
			return new FixResult.NotModified<>(line);
		}

		JsonObject json = parsedJson.get();
		JsonObject entityData = json.getObject("val").getObject("entityData");
		if (entityData.getObject("val").getObject("factionName") != null) {
			Json.JsonValue factionName = entityData.getObject("val").getObject("factionName").getValue("val");
			factionName.setStringValue(FactionExpansionEntityFixer.RENAMES.getOrDefault(factionName.getStringValue(), factionName.getStringValue()));
		}

		return new FixResult.Modified<>(Json.getJsonData(json), "FactionExpansionFixer");
	}

	@Override
	public Version getVersion() {
		return VERSION;
	}

	@Override
	public boolean isForRule(String ruleName) {
		return ruleName.equals("AWNpc");
	}
}
