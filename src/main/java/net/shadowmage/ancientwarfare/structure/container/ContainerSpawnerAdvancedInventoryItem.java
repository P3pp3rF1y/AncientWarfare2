package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.ItemBlockAdvancedSpawner;
import net.shadowmage.ancientwarfare.structure.item.ItemSpawnerPlacer;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;

public class ContainerSpawnerAdvancedInventoryItem extends ContainerSpawnerAdvancedInventoryBase {
	private static final String SPAWNER_SETTINGS_TAG = "spawnerSettings";

	public ContainerSpawnerAdvancedInventoryItem(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);

		settings = new SpawnerSettings();
		ItemStack item = EntityTools.getItemFromEitherHand(player, ItemBlockAdvancedSpawner.class);
		if (!item.isEmpty()) {
			//noinspection ConstantConditions
			if (!item.hasTagCompound() || !item.getTagCompound().hasKey(SPAWNER_SETTINGS_TAG)) {
				throw new IllegalArgumentException("stack must have correct data!!");
			}
			settings.readFromNBT(item.getTagCompound().getCompoundTag(SPAWNER_SETTINGS_TAG));
		} else {
			item = EntityTools.getItemFromEitherHand(player, ItemSpawnerPlacer.class);
			if (!ItemSpawnerPlacer.hasSpawnerData(item)) {
				player.sendMessage(new TextComponentString("Must have an entity set first!"));
				settings.readFromNBT(ItemSpawnerPlacer.getSpawnerData(item));
			}
		}
		inventory = settings.getInventory();

		addSettingsInventorySlots();
		addPlayerSlots(8, 70, 8);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey(SPAWNER_SETTINGS_TAG)) {
			ItemStack item = EntityTools.getItemFromEitherHand(player, ItemBlockAdvancedSpawner.class);
			if (!item.isEmpty()) {
				item.setTagInfo(SPAWNER_SETTINGS_TAG, tag.getCompoundTag(SPAWNER_SETTINGS_TAG));
			} else {
				item = EntityTools.getItemFromEitherHand(player, ItemSpawnerPlacer.class);
				ItemSpawnerPlacer.setSpawnerData(item, tag.getCompoundTag(SPAWNER_SETTINGS_TAG));
			}
		}
	}
}
