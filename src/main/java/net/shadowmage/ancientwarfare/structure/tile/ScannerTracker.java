package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ScannerTracker {
	private ScannerTracker() {}

	private static Map<String, Tuple<Integer, BlockPos>> scannerPositions = new HashMap<>();

	public static void registerScanner(TileStructureScanner scanner) {
		ItemStack scannerStack = scanner.getScannerInventory().getStackInSlot(0);
		String name = ItemStructureScanner.getStructureName(scannerStack);
		if (!name.isEmpty() && !scannerPositions.containsKey(name)) {
			scannerPositions.put(name, new Tuple<>(scanner.getWorld().provider.getDimension(), scanner.getPos()));
		}
	}

	public static Set<String> getTrackedScannerNames() {
		return scannerPositions.keySet();
	}

	public static Tuple<Integer, BlockPos> getScannerPosByName(String name) {
		return scannerPositions.get(name);
	}

	public static void teleportAboveScannerBlock(EntityPlayer player, Tuple<Integer, BlockPos> pos) {
		if (player.getEntityWorld().provider.getDimension() == pos.getFirst()) {
			BlockPos aboveScanner = pos.getSecond().up();
			player.setPositionAndUpdate(aboveScanner.getX() + 0.5D, aboveScanner.getY(), aboveScanner.getZ() + 0.5D);
		}
	}

	public static void reexportAll(EntityPlayer player, boolean reloadMainSettings) {
		scannerExporter.startExport(player, reloadMainSettings);
	}

	private static ScannerExporter scannerExporter = new ScannerExporter();

	private static class ScannerExporter {
		private boolean exportFinished = true;
		private boolean teleported = false;
		private int timeout = 0;
		private Set<Tuple<Integer, BlockPos>> scanners;
		private Iterator<Tuple<Integer, BlockPos>> exportIterator;
		private Tuple<Integer, BlockPos> currentPos;
		private EntityPlayer player;
		private boolean reloadMainSettings;
		private int current = 0;

		private ScannerExporter() {
			MinecraftForge.EVENT_BUS.register(this);
		}

		public void startExport(EntityPlayer player, boolean reloadMainSettings) {
			this.player = player;
			this.reloadMainSettings = reloadMainSettings;
			exportFinished = false;
			teleported = false;
			timeout = 40;
			current = 0;
			scanners = new HashSet<>(scannerPositions.values());
			exportIterator = scanners.iterator();
		}

		@SubscribeEvent
		public void serverTick(TickEvent.ServerTickEvent evt) {
			if (!exportFinished && evt.phase == TickEvent.Phase.END) {
				if (!teleported) {
					if (!updatePos()) {
						return;
					}
					teleportAboveScannerBlock(player, currentPos);
					teleported = true;
				}
				if (timeout <= 0) {
					WorldTools.getTile(player.getEntityWorld(), currentPos.getSecond(), TileStructureScanner.class).ifPresent(te -> {
						if (reloadMainSettings) {
							te.reloadMainSettings();
						}
						te.export();
						te.getScanner().ifPresent(s ->
								player.sendMessage(new TextComponentString("Exported template for " + ItemStructureScanner.getStructureName(s))));
						player.sendStatusMessage(new TextComponentString(current + " of " + scanners.size() + " templates exported"), true);
					});
					timeout = 40;
					teleported = false;
				} else {
					timeout--;
				}
			}
		}

		private boolean updatePos() {
			if (!exportIterator.hasNext()) {
				exportFinished = true;
				return false;
			}
			currentPos = exportIterator.next();
			current++;
			while (currentPos.getFirst() != player.getEntityWorld().provider.getDimension()) {
				if (!exportIterator.hasNext()) {
					exportFinished = true;
					return false;
				}
				currentPos = exportIterator.next();
				current++;
			}
			return true;
		}
	}

}
