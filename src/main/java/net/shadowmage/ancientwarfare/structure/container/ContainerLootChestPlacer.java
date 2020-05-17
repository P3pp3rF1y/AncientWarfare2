package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.item.ItemLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContainerLootChestPlacer extends ContainerBase {
	private static final String LOOT_CONTAINER_NAME_TAG = "lootContainerName";
	private final ItemStack placer;

	public ContainerLootChestPlacer(EntityPlayer player, int x, int y, int z) {
		super(player);
		placer = EntityTools.getItemFromEitherHand(player, ItemLootChestPlacer.class);
	}

	public List<String> getLootTableNames() {
		return LootTableList.getAll().stream().map(ResourceLocation::toString).filter(rl -> !AWStructureStatics.lootTableExclusions.contains(rl))
				.collect(Collectors.toList());
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(LOOT_CONTAINER_NAME_TAG)) {
			setContainer(tag.getString(LOOT_CONTAINER_NAME_TAG));
			return;
		}

		setLootSettings(LootSettings.deserializeNBT(tag));
	}

	public void setLootSettings(LootSettings lootSettings) {
		if (player.world.isRemote) {
			sendDataToServer(lootSettings.serializeNBT());
		}

		ItemLootChestPlacer.setLootSettings(placer, lootSettings);
	}

	public Optional<LootSettings> getLootSettings() {
		return ItemLootChestPlacer.getLootSettings(placer);
	}

	public ItemLootChestPlacer.LootContainerInfo getLootContainerInfo() {
		return ItemLootChestPlacer.getLootContainerInfo(placer);
	}

	public void setContainer(String blockName) {
		if (player.world.isRemote) {
			sendDataToServer(new NBTBuilder().setString(LOOT_CONTAINER_NAME_TAG, blockName).build());
		}

		ItemLootChestPlacer.setContainerName(placer, blockName);
	}
}
