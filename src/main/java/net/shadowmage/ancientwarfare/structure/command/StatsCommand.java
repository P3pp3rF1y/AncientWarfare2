package net.shadowmage.ancientwarfare.structure.command;

import joptsimple.internal.Strings;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.shadowmage.ancientwarfare.core.command.ParentCommand;
import net.shadowmage.ancientwarfare.core.command.SimpleSubCommand;
import net.shadowmage.ancientwarfare.structure.template.StructureTemplateManager;
import net.shadowmage.ancientwarfare.structure.worldgen.TerritoryManager;
import net.shadowmage.ancientwarfare.structure.worldgen.stats.WorldGenStatistics;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

	@Override
	public String getName() {
		return "stats";
	}

	private void printTerritoryStats(ICommandSender sender, String territoryName) {
		Optional<WorldGenStatistics.TerritoryRecord> territory = WorldGenStatistics.getTerritory(territoryName);
		if (territory.isPresent()) {
			WorldGenStatistics.TerritoryRecord territoryRecord = territory.get();
			String header = "Worldgen statistics for territory \"" + territoryRecord.getName() + "\"";
			sendToChat(sender, Strings.repeat('-', header.length() + 1));
			sendToChat(sender, header);
			sendToChat(sender, Strings.repeat('-', header.length() + 1));
			sendToChat(sender, "Number of times generated: " + territoryRecord.getTimesGenerated());
			sendCollectionStatsToChat(sender, "Generated in biomes: ", territoryRecord.getBiomeGenerations());
		} else {
			sendToChat(sender, "Territory \"" + territoryName + "\" has no statistics recorded");
		}
	}

	private void printStructureStats(ICommandSender sender, String structureName) {
		Optional<WorldGenStatistics.StructureRecord> structure = WorldGenStatistics.getStructure(structureName);
		if (structure.isPresent()) {
			WorldGenStatistics.StructureRecord structureRecord = structure.get();
			String header = "Worldgen statistics for \"" + structureRecord.getName() + "\"";
			sendToChat(sender, Strings.repeat('-', header.length() + 1));
			sendToChat(sender, header);
			sendToChat(sender, Strings.repeat('-', header.length() + 1));
			sendToChat(sender, "Number of times generated: " + structureRecord.getTimesGenerated());
			sendCollectionStatsToChat(sender, "Generated in biomes: ", structureRecord.getBiomeGenerations());
			sendCollectionStatsToChat(sender, "Generated in territories: ", structureRecord.getTerritoryGenerations());
			sendCollectionStatsToChat(sender, "Failed template validation for reasons: ", structureRecord.getValidationRejectionReasons());
			sendCollectionStatsToChat(sender, "Failed placement in world for reasons: ", structureRecord.getPlacementRejectionReasons());
		} else {
			sendToChat(sender, "Structure \"" + structureName + "\" has no statistics recorded");
		}
	}

	private <T> void sendCollectionStatsToChat(ICommandSender sender, String header, Map<T, Integer> collection) {
		sendToChat(sender, header);
		for (Map.Entry<T, Integer> entry : collection.entrySet()) {
			sendToChat(sender, String.format("%1$4s", entry.getValue()) + " x " + entry.getKey());
		}
	}

	private static void sendToChat(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}
}
