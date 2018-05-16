package net.shadowmage.ancientwarfare.core.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandUtils extends CommandBase {

	private final Map<String, ISubCommand> subCommands = new HashMap<>();

	public CommandUtils() {
		subCommands.put("exportentities", new EntityListCommand());
		subCommands.put("exportbiomes", new BiomeListCommand());
		subCommands.put("exportblocks", new BlockListCommand());
	}

	@Override
	public String getName() {
		return "awutils";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "command.aw.utils.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 2 || args.length == 0) {
			throw new WrongUsageException(getUsage(sender));
		}

		String[] subArgs = new String[args.length - 1];
		System.arraycopy(args, 1, subArgs, 0, args.length - 1);
		subCommands.get(args[0]).execute(server, sender, subArgs);
	}

	private interface ISubCommand {
		void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException;
	}

	private abstract static class ExportCommand implements ISubCommand {
		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			ArrayList<String> lines = getLines();
			String fileName = args.length > 0 ? args[0] : getDefaultFileName();
			File file = new File(AWCoreStatics.utilsExportPath, fileName);
			exportToFile(file, getHeader(), lines);
			notifyPlayer(sender, file);
		}

		protected abstract String getHeader();

		protected abstract String getDefaultFileName();

		protected abstract ArrayList<String> getLines();

		private static void exportToFile(File exportFile, String header, ArrayList<String> rows) {

			if (!exportFile.exists()) {
				try {
					if (!exportFile.getParentFile().mkdirs()) {
						AncientWarfareCore.log.error("Unable to create folders for file : " + exportFile.getAbsolutePath());
					}
					if (!exportFile.createNewFile()) {
						AncientWarfareCore.log.error("Unable to open new file : " + exportFile.getAbsolutePath());
					}
				}
				catch (IOException e) {
					AncientWarfareCore.log.error("Error opening file : " + exportFile.getAbsolutePath(), e);
					return;
				}
			}
			try (FileWriter fileWriter = new FileWriter(exportFile); BufferedWriter writer = new BufferedWriter(fileWriter)) {
				writer.write(header);
				writer.newLine();
				for (String row : rows) {
					writer.write(row);
					writer.newLine();
				}
			}
			catch (IOException e) {
				AncientWarfareCore.log.error("Error exporting file: " + exportFile.getAbsolutePath(), e);
			}
		}

		private static void notifyPlayer(ICommandSender sender, File exportFile) {
			sender.sendMessage(new TextComponentString("File exported to " + exportFile.getAbsoluteFile()));
		}
	}

	private static class EntityListCommand extends ExportCommand {
		@Override
		protected String getHeader() {
			return "Registry Name,Entity Name,Entity Class";
		}

		@Override
		protected String getDefaultFileName() {
			return "entitylist.csv";
		}

		@Override
		protected ArrayList<String> getLines() {
			//noinspection ConstantConditions
			return ForgeRegistries.ENTITIES.getValuesCollection().stream()
					.map(e -> String.join(",", e.getRegistryName().toString(), e.getName(), e.getEntityClass().toString()))
					.sorted(Comparator.naturalOrder()).collect(Collectors.toCollection(ArrayList::new));
		}
	}

	private class BiomeListCommand extends ExportCommand {
		@Override
		protected String getHeader() {
			return "Registry Name,Biome Name,Temperature Category,High Humidity,Height Variation,Top Block,Biome Types,Biome Class";
		}

		@Override
		protected String getDefaultFileName() {
			return "biomelist.csv";
		}

		@Override
		protected ArrayList<String> getLines() {
			//noinspection ConstantConditions
			return ForgeRegistries.BIOMES.getValuesCollection().stream()
					.map(b -> String.join(",", b.getRegistryName().toString(), b.getBiomeName(), b.getTempCategory().name()
							, Boolean.toString(b.isHighHumidity()), Float.toString(b.getHeightVariation()), b.topBlock.getBlock().getRegistryName().toString()
							, BiomeDictionary.getTypes(b).stream().map(BiomeDictionary.Type::getName).collect(Collectors.joining("|")),
							b.getBiomeClass().toString()))
					.sorted(Comparator.naturalOrder()).collect(Collectors.toCollection(ArrayList::new));
		}
	}

	private class BlockListCommand extends ExportCommand {

		@Override
		protected String getHeader() {
			return "Registry Name,Block Name";
		}

		@Override
		protected String getDefaultFileName() {
			return "blocklist.csv";
		}

		@Override
		protected ArrayList<String> getLines() {
			//noinspection ConstantConditions
			return ForgeRegistries.BLOCKS.getValuesCollection().stream()
					.map(b -> String.join(",", b.getRegistryName().toString(), b.getLocalizedName()))
					.sorted(Comparator.naturalOrder()).collect(Collectors.toCollection(ArrayList::new));
		}

	}
}
