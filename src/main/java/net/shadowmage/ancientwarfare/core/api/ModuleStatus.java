package net.shadowmage.ancientwarfare.core.api;

public class ModuleStatus {

    //set from modules during pre-init
    public static boolean modelerLoaded;
    public static boolean structuresLoaded;
    public static boolean automationLoaded;
    public static boolean npcsLoaded;
    public static boolean vehiclesLoaded;

    //checked in automation module pre-init
    public static boolean buildCraftLoaded;

    //checked...who knows where...
    public static boolean redstoneFluxEnabled;
}
