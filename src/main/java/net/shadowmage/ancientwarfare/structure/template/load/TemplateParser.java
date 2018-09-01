package net.shadowmage.ancientwarfare.structure.template.load;

import net.minecraft.util.math.Vec3i;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate.Version;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;
import net.shadowmage.ancientwarfare.structure.template.datafixes.FixResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TemplateParser {

	public static final TemplateParser INSTANCE = new TemplateParser();

	private TemplateParser() {
	}

	FixResult<StructureTemplate> parseTemplate(String fileName, List<String> templateLines) {
		try {
			return parseTemplateLines(fileName, templateLines);
		}
		catch (TemplateParsingException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private FixResult<StructureTemplate> parseTemplateLines(String fileName, List<String> lines) throws TemplateParsingException {
		Iterator<String> it = lines.iterator();
		String line;

		List<TemplateRule> parsedRules = new ArrayList<>();
		List<TemplateRuleEntity> parsedEntities = new ArrayList<>();
		TemplateRule[] ruleArray;
		TemplateRuleEntity[] entityRuleArray;
		StructureValidator validation = null;
		List<String> groupedLines = new ArrayList<>();

		int parsedLayers = 0;

		String name = null;
		Version version = Version.NONE;
		Vec3i size = new Vec3i(0, 0, 0);
		Vec3i offset = new Vec3i(0, 0, 0);
		short[] templateData = null;
		boolean[] initData = new boolean[4];
		int highestParsedRule = 0;
		FixResult.Builder<StructureTemplate> resultBuilder = new FixResult.Builder<>();
		while (it.hasNext()) {
			line = it.next();
			if (line.startsWith("#") || line.equals("")) {
				continue;
			}
			if (line.startsWith("header:")) {
				while (it.hasNext()) {
					line = it.next();
					if (line.startsWith(":endheader")) {
						break;
					}
					if (line.startsWith("version=")) {
						initData[0] = true;
						version = new Version(StringTools.safeParseString("=", line));
					}
					if (line.startsWith("name=")) {
						name = StringTools.safeParseString("=", line);
						initData[1] = true;
					}
					if (line.startsWith("size=")) {
						int[] sizes = StringTools.safeParseIntArray("=", line);
						size = new Vec3i(sizes[0], sizes[1], sizes[2]);
						initData[2] = true;
					}
					if (line.startsWith("offset=")) {
						int[] offsets = StringTools.safeParseIntArray("=", line);
						offset = new Vec3i(offsets[0], offsets[1], offsets[2]);
						initData[3] = true;
					}
				}
				for (int i = 0; i < 4; i++) {
					if (!initData[i]) {
						throw new TemplateParsingException("Could not parse template for " + fileName + " -- template was missing header or header data.");
					}
				}
				templateData = new short[size.getX() * size.getY() * size.getZ()];
			}

			/*
			 * parse out validation data
             */
			if (line.startsWith("validation:")) {
				groupedLines.add(line);
				while (it.hasNext()) {
					line = it.next();
					groupedLines.add(line);
					if (line.startsWith(":endvalidation")) {
						break;
					}
				}
				validation = StructureValidator.parseValidator(groupedLines);
				groupedLines.clear();
			}

            /*
			 * parse out rule data
             */
			if (line.startsWith("rule:")) {
				groupedLines.add(line);
				while (it.hasNext()) {
					line = it.next();
					groupedLines.add(line);
					if (line.startsWith(":endrule")) {
						break;
					}
				}
				try {
					TemplateRule parsedRule = resultBuilder.updateAndGetData(StructurePluginManager.getRule(version, groupedLines, "rule"));
					parsedRules.add(parsedRule);
					if (parsedRule.ruleNumber > highestParsedRule) {
						highestParsedRule = parsedRule.ruleNumber;
					}
				}
				catch (TemplateRuleParsingException e) {
					StringBuilder data = new StringBuilder(e.getMessage() + "\n");
					for (String line1 : groupedLines) {
						data.append(line1).append("\n");
					}
					TemplateRuleParsingException e1 = new TemplateRuleParsingException(data.toString(), e);
					AncientWarfareStructure.LOG.error("Caught exception parsing template rule for structure: " + name);
					AncientWarfareStructure.LOG.error(e1.getMessage());
				}
				groupedLines.clear();
			}

            /*
			 * parse out rule data
             */
			if (line.startsWith("entity:")) {
				groupedLines.add(line);
				while (it.hasNext()) {
					line = it.next();
					groupedLines.add(line);
					if (line.startsWith(":endentity")) {
						break;
					}
				}
				try {
					parsedEntities.add(resultBuilder.updateAndGetData(StructurePluginManager.getRule(version, groupedLines, "entity")));
				}
				catch (TemplateRuleParsingException e) {
					StringBuilder data = new StringBuilder(e.getMessage() + "\n");
					for (String line1 : groupedLines) {
						data.append(line1).append("\n");
					}
					TemplateRuleParsingException e1 = new TemplateRuleParsingException(data.toString(), e);
					AncientWarfareStructure.LOG.error("Caught exception parsing template rule for structure: " + name);
					AncientWarfareStructure.LOG.error(e1.getMessage());
				}
				groupedLines.clear();
			}

            /*
			 * parse out layer data
             */
			if (line.startsWith("layer:")) {
				groupedLines.add(line);
				while (it.hasNext()) {
					line = it.next();
					groupedLines.add(line);
					if (line.startsWith(":endlayer")) {
						break;
					}
				}
				parseLayer(groupedLines, parsedLayers, size, templateData);
				parsedLayers++;
				groupedLines.clear();
			}
		}

        /*
		 * initialze data for construction of template -- put rules into array
         */
		ruleArray = new TemplateRule[highestParsedRule + 1];
		for (TemplateRule rule : parsedRules) {
			if (rule != null && rule.ruleNumber > 0) {
				ruleArray[rule.ruleNumber] = rule;
			}
		}

		entityRuleArray = new TemplateRuleEntity[parsedEntities.size()];
		int ruleNumber = 0;
		for (TemplateRuleEntity rule : parsedEntities) {
			entityRuleArray[ruleNumber] = rule;
			ruleNumber++;
		}

		return resultBuilder.build(constructTemplate(name, version, size, offset, templateData, ruleArray, entityRuleArray, validation));
	}

	private StructureTemplate constructTemplate(String name, Version version, Vec3i size, Vec3i offset, short[] templateData, TemplateRule[] rules, TemplateRuleEntity[] entityRules, StructureValidator validation) {
		StructureTemplate template = new StructureTemplate(name, version, size, offset);
		template.setRuleArray(rules);
		template.setEntityRules(entityRules);
		template.setTemplateData(templateData);
		template.setValidationSettings(validation);
		return template;
	}

	/*
	 * should parse layer and insert direcly into templateData
	 */
	private void parseLayer(List<String> templateLines, int yLayer, Vec3i size, short[] templateData) {
		int z = 0;
		for (String st : templateLines) {
			if (st.startsWith("layer:") || st.startsWith(":endlayer")) {
				continue;
			}
			short[] data = StringTools.parseShortArray(st);
			for (int x = 0; x < size.getX() && x < data.length; x++) {
				templateData[StructureTemplate.getIndex(new Vec3i(x, yLayer, z), size)] = data[x];
			}
			z++;
		}
	}

}
