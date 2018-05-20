package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.ItemTownBuilder;

public class ContainerTownSelection extends ContainerBase {
	public String townName;

	public ContainerTownSelection(EntityPlayer player, int x, int y, int z) {
		super(player);

		ItemStack townBuilder = EntityTools.getItemFromEitherHand(player, ItemTownBuilder.class);
		if (townBuilder.isEmpty()) {
			return;
		}

		townName = ItemTownBuilder.getTownName(townBuilder);
	}

	public void handleNameSelection(String name) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setString("townName", name);
		sendDataToServer(tag);
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (!player.world.isRemote && tag.hasKey("townName")) {
			ItemStack townBuilder = EntityTools.getItemFromEitherHand(player, ItemTownBuilder.class);
			ItemTownBuilder.setTownName(townBuilder, tag.getString("townName"));
		}
	}
}
