package net.shadowmage.ancientwarfare.core.interop;

import net.minecraftforge.fml.common.Loader;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.core.compat.ftb.FTBUCompat;
import net.shadowmage.ancientwarfare.core.compat.ftb.FTBUCompatDummy;
import net.shadowmage.ancientwarfare.core.compat.ftb.IFTBUCompat;

public class ModAccessors {
	public static IFTBUCompat FTBU;
	//public static InteropHarderWildlifeInterface HARDER_WILDLIFE;
	public static boolean HARDER_WILDLIFE_LOADED = false;
	//public static InteropEnviromineInterface ENVIROMINE;
	public static boolean ENVIROMINE_LOADED = false;
	//public static InteropTreecapitatorInterface TREECAPITATOR; //TODO implement integration with the new treecapitator port ??
	public static boolean TREECAPITATOR_LOADED = false;

	public static void init() {
		try {
			//TODO move to compatloader
			if (Loader.isModLoaded("ftbu")) {
				AncientWarfareCore.log.info("FTB utils found!");
				FTBU = new FTBUCompat();
			} else {
				AncientWarfareCore.log.info("FTB utils not found.");
				FTBU = new FTBUCompatDummy();
			}

/*
			if (Loader.isModLoaded("HarderWildlife")) {
                AncientWarfareCore.log.info("HarderWildlife found!");
                HARDER_WILDLIFE = Class.forName("net.shadowmage.ancientwarfare.core.interop.InteropHarderWildlife").asSubclass(InteropHarderWildlifeInterface.class).newInstance();
                HARDER_WILDLIFE_LOADED = true;
            } else {
                AncientWarfareCore.log.info("HarderWildlife not found.");
                HARDER_WILDLIFE = Class.forName("net.shadowmage.ancientwarfare.core.interop.InteropHarderWildlifeDummy").asSubclass(InteropHarderWildlifeInterface.class).newInstance();
            }
*/

/* TODO enviromine support
			if (Loader.isModLoaded("enviromine")) {
                AncientWarfareCore.log.info("Enviromine found!");
                ENVIROMINE = Class.forName("net.shadowmage.ancientwarfare.core.interop.InteropEnviromine").asSubclass(InteropEnviromineInterface.class).newInstance();
                ENVIROMINE_LOADED = true;
            } else {
                AncientWarfareCore.log.info("Enviromine not found.");
                ENVIROMINE = Class.forName("net.shadowmage.ancientwarfare.core.interop.InteropEnviromineDummy").asSubclass(InteropEnviromineInterface.class).newInstance();
            }
*/

/* TODO treecapitator support
			if (Loader.isModLoaded("Treecapitator")) {
                AncientWarfareCore.log.info("Treecapitator found!");
                TREECAPITATOR = Class.forName("net.shadowmage.ancientwarfare.core.interop.InteropTreecapitator").asSubclass(InteropTreecapitatorInterface.class).newInstance();
                TREECAPITATOR_LOADED = true;
            } else {
                AncientWarfareCore.log.info("Treecapitator not found.");
                TREECAPITATOR = Class.forName("net.shadowmage.ancientwarfare.core.interop.InteropTreecapitatorDummy").asSubclass(InteropTreecapitatorInterface.class).newInstance();
            }
*/
		}
		catch (Exception e) {
			// shouldn't happen
		}
	}
}
