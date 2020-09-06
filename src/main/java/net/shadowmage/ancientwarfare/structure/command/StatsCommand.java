package net.shadowmage.ancientwarfare.structure.command;

import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.shadowmage.ancientwarfare.core.command.ParentCommand;
import net.shadowmage.ancientwarfare.core.command.SimpleSubCommand;
import net.shadowmage.ancientwarfare.core.config.ModConfiguration;
import net.shadowmage.ancientwarfare.core.util.FileUtils;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.worldgen.TerritoryManager;
import net.shadowmage.ancientwarfare.structure.worldgen.stats.WorldGenStatistics;

import javax.annotation.Nullable;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatsCommand extends ParentCommand {
	StatsCommand() {
		registerSubCommand(new SimpleSubCommand("start", (server, sender, args) -> {
			WorldGenStatistics.startCollectingStatistics();
			sendToChat(sender, "Started collecting worldgen statistics");
		}));
		registerSubCommand(new SimpleSubCommand("stop", (server, sender, args) -> {
			WorldGenStatistics.stopCollectingStatistics();
			sendToChat(sender, "Stopped collecting worldgen statistics");
		}));
		registerSubCommand(new SimpleSubCommand("clear", (server, sender, args) -> {
			WorldGenStatistics.clearStatistics();
			sendToChat(sender, "Cleared worldgen statistics");
		}));
		registerSubCommand(new SimpleSubCommand("structures", (server, sender, args) -> {
			String filePath = ModConfiguration.configPathForFiles + "structures/stats/";
			File file = new File(filePath, "structures.txt");
			exportStructureStats(file);
			sendToChat(sender, "Exported statistics for all structures to: " + file.getAbsolutePath());
		}));
		registerSubCommand(new SimpleSubCommand("territories", (server, sender, args) -> {
			String filePath = ModConfiguration.configPathForFiles + "structures/stats/";
			File file = new File(filePath, "territories.txt");
			exportTerritoryStats(file);
			sendToChat(sender, "Exported statistics for all structures to: " + file.getAbsolutePath());
		}));
		registerSubCommand(new SimpleSubCommand("structure", (server, sender, args) -> printStructureStats(sender, args[0])) {
			@Override
			public int getMaxArgs() {
				return 1;
			}

			@Override
			public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
				if (args.length < 1) {
					return Collections.emptyList();
				}
				return StructureTemplateManager.getTemplates().stream().filter(t -> t.startsWith(args[0])).collect(Collectors.toList());
			}

			@Override
			public String getUsage(ICommandSender sender) {
				return getName() + " <structureName>";
			}
		});
		registerSubCommand(new SimpleSubCommand("territory", (server, sender, args) -> printTerritoryStats(sender, args[0])) {
			@Override
			public int getMaxArgs() {
				return 1;
			}

			@Override
			public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
				if (args.length < 1) {
					return Collections.emptyList();
				}
				return TerritoryManager.getTerritoryNames().stream().filter(t -> t.startsWith(args[0])).collect(Collectors.toList());
			}

			@Override
			public String getUsage(ICommandSender sender) {
				return getName() + " <territoryName>";
			}
		});
	}

	private void exportTerritoryStats(File file) {
		List<String> rows = new ArrayList<>();
		for (WorldGenStatistics.TerritoryRecord territoryRecord : WorldGenStatistics.getTerritories().stream()
				.sorted((rec1, rec2) -> Integer.compare(rec2.getTimesGenerated(), rec1.getTimesGenerated())).collect(Collectors.toList())) {
			rows.addAll(getTerritoryReport(territoryRecord));
			rows.add("");
		}
		FileUtils.exportToFile(file, rows);
	}

	private void exportStructureStats(File file) {
		List<String> rows = new ArrayList<>();
		for (WorldGenStatistics.StructureRecord structureRecord : WorldGenStatistics.getStructures().stream()
				.sorted((rec1, rec2) -> Integer.compare(rec2.getTimesGenerated(), rec1.getTimesGenerated())).collect(Collectors.toList())) {
			rows.addAll(getStructureReport(structureRecord));
			rows.add("");
		}
		FileUtils.exportToFile(file, rows);
	}

	@Override
	public String getName() {
		return "stats";
	}

	private void printTerritoryStats(ICommandSender sender, String territoryName) {
		Optional<WorldGenStatistics.TerritoryRecord> territory = WorldGenStatistics.getTerritory(territoryName);
		if (territory.isPresent()) {
			getTerritoryReport(territory.get()).forEach(row -> sendToChat(sender, row));
		} else {
			sendToChat(sender, "Territory \"" + territoryName + "\" has no statistics recorded");
		}
	}

	private void printStructureStats(ICommandSender sender, String structureName) {
		Optional<WorldGenStatistics.StructureRecord> structure = WorldGenStatistics.getStructure(structureName);
		if (structure.isPresent()) {
			getStructureReport(structure.get()).forEach(row -> sendToChat(sender, row));
		} else {
			sendToChat(sender, "Structure \"" + structureName + "\" has no statistics recorded");
		}
	}

	private List<String> getStructureReport(WorldGenStatistics.StructureRecord structureRecord) {
		String header = "Worldgen statistics for \"" + structureRecord.getName() + "\"";
		List<String> rows = new ArrayList<>();
		rows.add(Strings.repeat('-', header.length() + 1));
		rows.add(header);
		rows.add(Strings.repeat('-', header.length() + 1));
		rows.add("Number of times generated: " + structureRecord.getTimesGenerated());
		rows.add("Number of times considered in random: " + structureRecord.getTimesConsideredInRandom());
		rows.addAll(getCollectionStats("Biome generation chances:", structureRecord.getBiomeChances(), entry ->
				String.format("%1$4s x %2$s | average chance: %3$.1f%%", entry.getValue().getNumberOfGenerations(), entry.getKey(), entry.getValue().getAverageChance() * 100)
		));
		rows.addAll(getCollectionStats("Generated in biomes: ", structureRecord.getBiomeGenerations()));
		rows.addAll(getCollectionStats("Territory generation chances:", structureRecord.getTerritoryChances(), entry ->
				String.format("%1$4s x %2$s | average chance: %3$.1f%%", entry.getValue().getNumberOfGenerations(), entry.getKey(), entry.getValue().getAverageChance() * 100)
		));
		rows.addAll(getCollectionStats("Generated in territories: ", structureRecord.getTerritoryGenerations()));
		rows.addAll(getCollectionStats("Failed template validation for reasons: ", structureRecord.getValidationRejectionReasons()));
		rows.addAll(getCollectionStats("Failed placement in world for reasons: ", structureRecord.getPlacementRejectionReasons()));
		return rows;
	}

	private List<String> getTerritoryReport(WorldGenStatistics.TerritoryRecord territoryRecord) {
		String header = "Worldgen statistics for territory \"" + territoryRecord.getName() + "\"";
		List<String> rows = new ArrayList<>();
		rows.add(Strings.repeat('-', header.length() + 1));
		rows.add(header);
		rows.add(Strings.repeat('-', header.length() + 1));
		//noinspection ConstantConditions
		rows.add("Biomes generates in / expands into: " + TerritoryManager.getTerritoryBiomes(territoryRecord.getName()).map(biomes -> biomes.stream().map(biome -> biome.getRegistryName().toString()).collect(Collectors.joining(","))).orElse(""));
		rows.add("Number of times generated: " + territoryRecord.getTimesGenerated());
		rows.add(String.format("Average territory cluster value: %.1f", territoryRecord.getAverageClusterValue()));
		rows.addAll(getCollectionStats("Generated in biomes: ", territoryRecord.getBiomeGenerations(),
				entry -> String.format("%1$4s x %2$s | average cluster value: %3$.1f | chance to start generating in biome: %4$.1f%%", entry.getValue(), entry.getKey(),
						territoryRecord.getAverageBiomeClusterValue(entry.getKey()),
						TerritoryManager.getTerritoryChanceInBiome(territoryRecord.getName(), entry.getKey()) * 100)
		));
		return rows;
	}

	private <T> List<String> getCollectionStats(String header, Map<T, Integer> collection) {
		return getCollectionStats(header, collection, entry -> String.format("%1$4s x %2$s", entry.getValue(), entry.getKey()));
	}

	private <K, V> List<String> getCollectionStats(String header, Map<K, V> collection, Function<Map.Entry<K, V>, String> getEntryStat) {
		List<String> ret = new ArrayList<>();
		ret.add(header);
		for (Map.Entry<K, V> entry : collection.entrySet()) {
			ret.add(getEntryStat.apply(entry));
		}
		return ret;
	}

	private static void sendToChat(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}
}
