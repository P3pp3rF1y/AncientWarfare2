package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureScanner;
import net.shadowmage.ancientwarfare.structure.item.ItemStructureSettings;

import javax.annotation.Nonnull;

public class ContainerStructureScanner extends ContainerBase {

	private final ItemStructureSettings settings;

	public ContainerStructureScanner(EntityPlayer player, int x, int y, int z) {
		super(player);
		@Nonnull ItemStack builderItem = EntityTools.getItemFromEitherHand(player, ItemStructureScanner.class);
		if (builderItem.isEmpty()) {
			throw new IllegalArgumentException("No scanner in hand");
		}
		settings = ItemStructureSettings.getSettingsFor(builderItem);
		addPlayerSlots();
		removeSlots();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("export")) {
			boolean include = tag.getBoolean("export");
			String name = tag.getString("name");
			NBTTagCompound validation = tag.getCompoundTag("validation");
			if (ItemStructureScanner.scanStructure(player.world, settings.pos1(), settings.pos2(), settings.buildKey(), settings.face(), name, include, validation))
				settings.clearSettings();
		}
		if (tag.hasKey("reset")) {
			settings.clearSettings();
		}
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		if (player.world.isRemote) {
			return;
		}
		@Nonnull ItemStack builderItem = EntityTools.getItemFromEitherHand(player, ItemStructureScanner.class);
		if (builderItem.isEmpty()) {
			return;
		}
		ItemStructureSettings.setSettingsFor(builderItem, settings);
	}

	public void export(String name, boolean include, NBTTagCompound validation) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("name", name);
		tag.setBoolean("export", include);
		tag.setTag("validation", validation);
		sendDataToServer(tag);
	}
}
