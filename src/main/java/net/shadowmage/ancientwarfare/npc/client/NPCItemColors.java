package net.shadowmage.ancientwarfare.npc.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.npc.init.AWNPCItems;
import net.shadowmage.ancientwarfare.npc.item.ItemCoin;
import net.shadowmage.ancientwarfare.npc.item.ItemNpcSpawner;
import net.shadowmage.ancientwarfare.npc.registry.FactionRegistry;

@SideOnly(Side.CLIENT)
public class NPCItemColors {
	private NPCItemColors() {}

	public static final int FACTION_TOP_COLOR = 0xEF5757;

	public static void init() {
		ItemColors itemColors = Minecraft.getMinecraft().getItemColors();

		itemColors.registerItemColorHandler(((stack, tintIndex) -> {
			if (tintIndex == 1 || tintIndex == 2) {
				String factionName = ItemNpcSpawner.getFaction(stack).orElse("");
				if (tintIndex == 2) {
					return FactionRegistry.getFactionNames().contains(factionName) ? FACTION_TOP_COLOR : -1;
				} else {
					return FactionRegistry.getFaction(factionName).getColor();
				}
			}

			return -1;

		}), AWNPCItems.NPC_SPAWNER);

		itemColors.registerItemColorHandler(((stack, tintindex) -> ItemCoin.getMetal(stack).getColor()), AWNPCItems.COIN);
	}
}
