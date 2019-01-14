package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ScannerCommandTracker {
	private ScannerCommandTracker() {}

	private static int currentId = 0;
	private static Map<Integer, CommandType> issuedGlobalCommands = new LinkedHashMap<>();
	private static Map<BlockPos, Set<Integer>> executedTileCommands = new HashMap<>();
	private static Map<BlockPos, Map<Integer, CommandType>> tileCommandsToExecute = new HashMap<>();
	private static Map<String, Tuple<Integer, BlockPos>> scannerPositions = new HashMap<>();

	public static void issueGlobalCommand(CommandType type) {
		issuedGlobalCommands.put(currentId++, type);
	}

	public static Optional<CommandType> getAndRemoveNextCommand(TileStructureScanner scanner) {
		noteScannerPosition(scanner);
		BlockPos pos = scanner.getPos();
		if (!tileCommandsToExecute.containsKey(pos)) {
			tileCommandsToExecute.put(pos, new LinkedHashMap<>());
		}
		if (tileCommandsToExecute.get(pos).isEmpty()) {
			Set<Integer> executed = executedTileCommands.getOrDefault(pos, new HashSet<>());
			for (Map.Entry<Integer, CommandType> entry : issuedGlobalCommands.entrySet()) {
				if (!executed.contains(entry.getKey())) {
					tileCommandsToExecute.get(pos).put(entry.getKey(), entry.getValue());
				}
			}
		}
		if (tileCommandsToExecute.get(pos).isEmpty()) {
			return Optional.empty();
		}

		Iterator<Map.Entry<Integer, CommandType>> it = tileCommandsToExecute.get(pos).entrySet().iterator();
		Map.Entry<Integer, CommandType> next = it.next();
		it.remove();
		setExecuted(pos, next.getKey());
		return Optional.of(next.getValue());
	}

	private static void noteScannerPosition(TileStructureScanner scanner) {
		ItemStack scannerStack = scanner.getScannerInventory().getStackInSlot(0);
		String name = ItemStructureScanner.getStructureName(scannerStack);
		if (!name.isEmpty() && !scannerPositions.containsKey(name)) {
			scannerPositions.put(name, new Tuple<>(scanner.getWorld().provider.getDimension(), scanner.getPos()));
		}
	}

	private static void setExecuted(BlockPos pos, Integer id) {
		if (!executedTileCommands.containsKey(pos)) {
			executedTileCommands.put(pos, new HashSet<>());
		}
		executedTileCommands.get(pos).add(id);
	}

	public static Set<String> getTrackedScannerNames() {
		return scannerPositions.keySet();
	}

	public static Tuple<Integer, BlockPos> getScannerPosByName(String name) {
		return scannerPositions.get(name);
	}

	public enum CommandType {
		RELOAD_MAIN_SETTINGS,
		REEXPORT
	}
}
