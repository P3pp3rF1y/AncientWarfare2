package net.shadowmage.ancientwarfare.structure.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.shadowmage.ancientwarfare.structure.init.AWStructureItems.*;

@SideOnly(Side.CLIENT)
public class AWStructureItemColors {
	private AWStructureItemColors() {}

	public static void init() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		itemColors.registerItemColorHandler((stack, tintIndex) -> ALTAR_CANDLE.getColor(stack), ALTAR_CANDLE, ALTAR_LONG_CLOTH, ALTAR_SHORT_CLOTH);
	}
}
