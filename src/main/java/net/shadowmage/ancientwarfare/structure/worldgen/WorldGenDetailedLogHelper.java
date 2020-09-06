package net.shadowmage.ancientwarfare.structure.worldgen;

import net.shadowmage.ancientwarfare.structure.AncientWarfareStructure;
import org.apache.logging.log4j.util.Supplier;

public class WorldGenDetailedLogHelper {
	public static boolean shouldLogValidationMessages = true; //TODO add command that sets these and default to false

	public static void log(String msg, Supplier<?>... paramSuppliers) {
		if (shouldLogValidationMessages) {
			AncientWarfareStructure.LOG.debug(msg, paramSuppliers);
		}
	}
}
