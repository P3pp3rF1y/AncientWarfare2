package net.shadowmage.ancientwarfare.structure.town;

import com.google.gson.JsonParser;
import net.minecraft.init.Blocks;
import net.shadowmage.ancientwarfare.core.util.CompatUtils;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import net.shadowmage.ancientwarfare.core.util.parsing.JsonHelper;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownStructureEntry;
import net.shadowmage.ancientwarfare.structure.town.TownTemplate.TownWallEntry;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TownTemplateParser {

	public static Optional<TownTemplate> parseTemplate(List<String> lines) {
		TownTemplate template = new TownTemplate();
		Iterator<String> it = lines.iterator();
		String line;

		while (it.hasNext() && (line = it.next()) != null) {
			line = line.toLowerCase(Locale.ENGLISH);
			if (line.startsWith("header:")) {
				parseHeader(it, template);
			} else if (line.startsWith("walls:")) {
				parseWalls(it, template);
			} else if (line.startsWith("wallpatterns:")) {
				parseWallPatterns(it, template);
			} else if (line.startsWith("uniquestructures:")) {
				parseUniqueStructures(it, template);
			} else if (line.startsWith("mainstructures:")) {
				parseMainStructures(it, template);
			} else if (line.startsWith("housestructures:")) {
				parseHouseStructures(it, template);
			} else if (line.startsWith("cosmeticstructures:")) {
				parseCosmetics(it, template);
			} else if (line.startsWith("exteriorstructures:")) {
				parseExteriorStructures(it, template);
			}
		}
		if (template.isValid()) {
			return Optional.of(template);
		}
		return Optional.empty();
	}

	private static void parseHeader(Iterator<String> it, TownTemplate template) {
		String line;
		String lower;
		while (it.hasNext() && (line = it.next()) != null) {
			lower = line.toLowerCase(Locale.ENGLISH);
			if (lower.startsWith(":endheader")) {
				break;
			} else if (lower.startsWith("name")) {
				template.setTownTypeName(StringTools.safeParseString("=", line));
			} else if (line.startsWith("mods=")) {
				if (!CompatUtils.areModsLoaded(StringTools.safeParseString("=", line).split(","))) {
					return;
				}
			} else if (lower.startsWith("minsize")) {
				template.setMinSize(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("maxsize")) {
				template.setMaxSize(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("buildingexpansion")) {
				template.setTownBuildingWidthExpansion(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("selectionweight")) {
				template.setSelectionWeight(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("clustervalue")) {
				template.setClusterValue(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("townblocksize")) {
				template.setTownBlockSize(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("townplotsize")) {
				template.setTownPlotSize(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("wallstyle")) {
				template.setWallStyle(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("wallsize")) {
				template.setWallSize(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("exteriorsize")) {
				template.setExteriorSize(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("exteriorplotsize")) {
				template.setExteriorPlotSize(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("interioremptyplotchance")) {
				template.setInteriorEmtpyPlotChance(StringTools.safeParseInt("=", line));
			} else if (lower.startsWith("randomvillagersperchunk")) {
				template.setRandomVillagersPerChunk(StringTools.safeParseFloat("=", line));
			} else if (lower.startsWith("roadblocks")) {
				String[] roadFillBlocks = StringTools.safeParseString("=", line).split("\\|");
				JsonParser parser = new JsonParser();
				for (String roadFillBlock : roadFillBlocks) {
					template.addRoadFillBlock(JsonHelper.getBlockState(parser.parse(roadFillBlock)));
				}
			} else if (lower.startsWith("biomewhitelist")) {
				template.setBiomeWhiteList(StringTools.safeParseBoolean("=", line));
			} else if (lower.startsWith("biomelist")) {
				template.setBiomeList(parseBiomeList(StringTools.safeParseString("=", line)));
			} else if (lower.startsWith("dimensionwhitelist")) {
				template.setDimensionWhiteList(StringTools.safeParseBoolean("=", line));
			} else if (lower.startsWith("dimensionlist")) {
				template.setDimensionList(parseDimensionList(StringTools.safeParseString("=", line)));
			} else if (lower.startsWith("lamp")) {
				template.setLamp(new TownStructureEntry(StringTools.safeParseString("=", line), 1));
			}
		}
		if (template.getRoadFillBlocks().isEmpty()) {
			template.addRoadFillBlock(Blocks.GRAVEL.getDefaultState());
		}
	}

	private static void parseUniqueStructures(Iterator<String> it, TownTemplate template) {
		String line;
		while (it.hasNext() && (line = it.next()) != null) {
			if (line.toLowerCase(Locale.ENGLISH).startsWith(":enduniquestructures")) {
				break;
			} else {
				TownStructureEntry e = parseStructureName(line);
				template.getUniqueStructureEntries().add(e);
			}
		}
	}

	private static void parseMainStructures(Iterator<String> it, TownTemplate template) {
		String line;
		while (it.hasNext() && (line = it.next()) != null) {
			if (line.toLowerCase(Locale.ENGLISH).startsWith(":endmainstructures")) {
				break;
			} else {
				TownStructureEntry e = parseStructureName(line);
				template.getMainStructureEntries().add(e);
			}
		}
	}

	private static void parseHouseStructures(Iterator<String> it, TownTemplate template) {
		String line;
		while (it.hasNext() && (line = it.next()) != null) {
			if (line.toLowerCase(Locale.ENGLISH).startsWith(":endhousestructures")) {
				break;
			} else {
				TownStructureEntry e = parseStructureWeight(line);
				template.getHouseStructureEntries().add(e);
			}
		}
	}

	private static void parseCosmetics(Iterator<String> it, TownTemplate template) {
		String line;
		while (it.hasNext() && (line = it.next()) != null) {
			if (line.toLowerCase(Locale.ENGLISH).startsWith(":endcosmeticstructures")) {
				break;
			} else {
				TownStructureEntry e = parseStructureWeight(line);
				template.getCosmeticEntries().add(e);
			}
		}
	}

	private static void parseExteriorStructures(Iterator<String> it, TownTemplate template) {
		String line;
		while (it.hasNext() && (line = it.next()) != null) {
			if (line.toLowerCase(Locale.ENGLISH).startsWith(":endexteriorstructures")) {
				break;
			} else {
				TownStructureEntry e = parseStructureWeight(line);
				template.getExteriorStructureEntries().add(e);
			}
		}
	}

	private static void parseWalls(Iterator<String> it, TownTemplate template) {
		String line;
		while (it.hasNext() && (line = it.next()) != null) {
			if (line.toLowerCase(Locale.ENGLISH).startsWith(":endwalls")) {
				break;
			} else {
				TownWallEntry e = parseWall(line);
				template.addWall(e);
			}
		}
	}

	private static void parseWallPatterns(Iterator<String> it, TownTemplate template) {
		String line;
		while (it.hasNext() && (line = it.next()) != null) {
			if (line.toLowerCase(Locale.ENGLISH).startsWith(":endwallpaterns")) {
				break;
			} else {
				String[] bits = line.split(":", -1);
				int size = NumberUtils.toInt(bits[0]);
				int[] pattern = parseWallPattern(bits[1]);
				template.addWallPattern(size, pattern);
			}
		}
	}

	private static List<String> parseBiomeList(String line) {
		String[] bits = line.split(",", -1);
		if (bits.length <= 0) {
			return Collections.emptyList();
		}
		List<String> names = new ArrayList<>();
		for (String bit : bits) {
			names.add(bit.toLowerCase(Locale.ENGLISH));
		}
		return names;
	}

	private static List<Integer> parseDimensionList(String line) {
		String[] bits = line.split(",", -1);
		if (bits.length <= 0) {
			return Collections.emptyList();
		}
		List<Integer> dims = new ArrayList<>();
		for (String bit : bits) {
			dims.add(NumberUtils.toInt(bit.toLowerCase(Locale.ENGLISH)));
		}
		return dims;
	}

	private static TownStructureEntry parseStructureName(String line) {
		String[] bits = line.split(":", -1);
		return new TownStructureEntry(bits[0], 0);
	}

	private static TownStructureEntry parseStructureWeight(String line) {
		String[] bits = line.split(":", -1);
		return new TownStructureEntry(bits[0], NumberUtils.toInt(bits[1]));
	}

	private static TownWallEntry parseWall(String line) {
		String[] bits = line.split(":", -1);
		return new TownWallEntry(bits[0], bits[1], NumberUtils.toInt(bits[2]), NumberUtils.toInt(bits[3]));
	}

	private static int[] parseWallPattern(String line) {
		String[] bits = line.split("-", -1);
		int[] pattern = new int[bits.length];
		for (int i = 0; i < bits.length; i++) {
			pattern[i] = NumberUtils.toInt(bits[i]);
		}
		return pattern;
	}

}
