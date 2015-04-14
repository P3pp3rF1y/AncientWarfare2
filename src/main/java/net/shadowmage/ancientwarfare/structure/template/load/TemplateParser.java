/**
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.structure.template.load;

import net.shadowmage.ancientwarfare.core.config.AWLog;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateParsingException.TemplateRuleParsingException;
import net.shadowmage.ancientwarfare.structure.api.TemplateRule;
import net.shadowmage.ancientwarfare.structure.api.TemplateRuleEntity;
import net.shadowmage.ancientwarfare.structure.template.StructurePluginManager;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplate;
import net.shadowmage.ancientwarfare.structure.template.build.validation.StructureValidator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TemplateParser {

    public static final TemplateParser INSTANCE = new TemplateParser();

    private final TemplateFormatConverter converter;

    private TemplateParser() {
        converter = new TemplateFormatConverter();
    }


    public StructureTemplate parseTemplate(String fileName, List<String> templateLines) {
        try {
            return parseTemplateLines(fileName, templateLines);
        } catch (TemplateParsingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * used for debug/error output purposes, to know what line number is currently being iterated over/read through
     */
    public static int lineNumber = -1;

    private StructureTemplate parseTemplateLines(String fileName, List<String> lines) throws IllegalArgumentException, TemplateParsingException {
        lineNumber = -1;
        Iterator<String> it = lines.iterator();
        String line;

        List<TemplateRule> parsedRules = new ArrayList<TemplateRule>();
        List<TemplateRuleEntity> parsedEntities = new ArrayList<TemplateRuleEntity>();
        TemplateRule[] ruleArray = null;
        TemplateRuleEntity[] entityRuleArray = null;
        StructureValidator validation = null;
        List<String> groupedLines = new ArrayList<String>();


        int parsedLayers = 0;

        String name = null;
        int xSize = 0, ySize = 0, zSize = 0, xOffset = 0, yOffset = 0, zOffset = 0;
        short[] templateData = null;
        boolean newVersion = false;
        boolean[] initData = new boolean[4];
        int highestParsedRule = 0;
        while (it.hasNext()) {
            lineNumber++;
            line = it.next();
            if (line.startsWith("#") || line.equals("")) {
                continue;
            }
            if (line.startsWith("header:")) {
                while (it.hasNext()) {
                    line = it.next();
                    lineNumber++;
                    if (line.startsWith(":endheader")) {
                        break;
                    }
                    if (line.startsWith("version=")) {
                        newVersion = true;
                        initData[0] = true;
                    }
                    if (line.startsWith("name=")) {
                        name = StringTools.safeParseString("=", line);
                        initData[1] = true;
                    }
                    if (line.startsWith("size=")) {
                        int[] sizes = StringTools.safeParseIntArray("=", line);
                        xSize = sizes[0];
                        ySize = sizes[1];
                        zSize = sizes[2];
                        initData[2] = true;
                    }
                    if (line.startsWith("offset=")) {
                        int[] offsets = StringTools.safeParseIntArray("=", line);
                        xOffset = offsets[0];
                        yOffset = offsets[1];
                        zOffset = offsets[2];
                        initData[3] = true;
                    }
                }
                for (int i = 0; i < 4; i++) {
                    if (initData[i] == false) {
                        throw new TemplateParsingException("Could not parse template for " + fileName + " -- template was missing header or header data.");
                    }
                }
                templateData = new short[xSize * ySize * zSize];
            }

            if (!newVersion) {
                try {
                    return converter.convertOldTemplate(fileName, lines);
                } catch (Exception e) {
                    throw new TemplateParsingException("Error parsing template: " + fileName + " at line: " + (converter.lineNumber + 1) + " for line: " + lines.get(converter.lineNumber));
                }
            }
            /**
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

            /**
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
                    TemplateRule rule = parseRule(groupedLines, "rule");
                    if (rule != null) {
                        parsedRules.add(rule);
                        if (rule.ruleNumber > highestParsedRule) {
                            highestParsedRule = rule.ruleNumber;
                        }
                    }
                } catch (TemplateRuleParsingException e) {
                    String data = e.getMessage() + "\n";
                    for (String line1 : groupedLines) {
                        data += line1 + "\n";
                    }
                    TemplateRuleParsingException e1 = new TemplateRuleParsingException(data, e);
                    AWLog.logError("Caught exception parsing template rule for structure: " + name);
                    AWLog.logError(e1.getMessage());
                }
                groupedLines.clear();
            }

            /**
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
                    TemplateRuleEntity rule = (TemplateRuleEntity) parseRule(groupedLines, "entity");
                    if (rule != null) {
                        parsedEntities.add(rule);
                    }
                } catch (TemplateRuleParsingException e) {
                    String data = e.getMessage() + "\n";
                    for (String line1 : groupedLines) {
                        data += line1 + "\n";
                    }
                    TemplateRuleParsingException e1 = new TemplateRuleParsingException(data, e);
                    AWLog.logError("Caught exception parsing template rule for structure: " + name);
                    AWLog.logError(e1.getMessage());
                }
                groupedLines.clear();
            }

            /**
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
                parseLayer(groupedLines, parsedLayers, xSize, ySize, zSize, templateData);
                parsedLayers++;
                groupedLines.clear();
            }
        }

        /**
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

        return constructTemplate(name, xSize, ySize, zSize, xOffset, yOffset, zOffset, templateData, ruleArray, entityRuleArray, validation);
    }

    private TemplateRule parseRule(List<String> templateLines, String ruleType) throws TemplateRuleParsingException {
        return StructurePluginManager.getRule(templateLines, ruleType);
    }

    private StructureTemplate constructTemplate(String name, int x, int y, int z, int xo, int yo, int zo, short[] templateData, TemplateRule[] rules, TemplateRuleEntity[] entityRules, StructureValidator validation) {
        StructureTemplate template = new StructureTemplate(name, x, y, z, xo, yo, zo);
        template.setRuleArray(rules);
        template.setEntityRules(entityRules);
        template.setTemplateData(templateData);
        template.setValidationSettings(validation);
        return template;
    }

    /**
     * should parse layer and insert direcly into templateData
     */
    private void parseLayer(List<String> templateLines, int yLayer, int xSize, int ySize, int zSize, short[] templateData) {
        int z = 0;
        for (String st : templateLines) {
            lineNumber++;
            if (st.startsWith("layer:") || st.startsWith(":endlayer")) {
                continue;
            }
            short[] data = StringTools.parseShortArray(st);
            for (int x = 0; x < xSize && x < data.length; x++) {
                templateData[StructureTemplate.getIndex(x, yLayer, z, xSize, ySize, zSize)] = data[x];
            }
            z++;
        }
    }

}
