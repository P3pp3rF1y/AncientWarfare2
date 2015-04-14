package net.shadowmage.ancientwarfare.core.item;

import net.shadowmage.ancientwarfare.core.block.AWCoreBlockLoader;

public class ItemComponent extends ItemBase {

    /**
     * automation module
     */
    public static final int WOODEN_GEAR_SET = 0, IRON_GEAR_SET = 1, STEEL_GEAR_SET = 2, WOODEN_BUSHINGS = 3;
    public static final int IRON_BEARINGS = 4, STEEL_BEARINGS = 5;
    public static final int WOODEN_TORQUE_SHAFT = 6, IRON_TORQUE_SHAFT = 7, STEEL_TORQUE_SHAFT = 8;

    /**
     * npc module
     * TODO
     */
    public static int NPC_FOOD_BUNDLE = 100;

    public ItemComponent() {
        this.setCreativeTab(AWCoreBlockLoader.coreTab);
    }
}
