package net.shadowmage.ancientwarfare.core.item;

import net.shadowmage.ancientwarfare.core.AncientWarfareCore;

public class ItemComponent extends ItemMulti {

	/*
	 * automation module
	 */
	public static final int WOODEN_GEAR_SET = 0;
	public static final int IRON_GEAR_SET = 1;
	public static final int STEEL_GEAR_SET = 2;
	public static final int WOODEN_BEARINGS = 3;
	public static final int IRON_BEARINGS = 4;
	public static final int STEEL_BEARINGS = 5;
	public static final int WOODEN_TORQUE_SHAFT = 6;
	public static final int IRON_TORQUE_SHAFT = 7;
	public static final int STEEL_TORQUE_SHAFT = 8;

	public static final int NPC_FOOD_BUNDLE = 100;

	public ItemComponent() {
		super(AncientWarfareCore.MOD_ID, "component");
		setCreativeTab(AncientWarfareCore.TAB);
	}
}
