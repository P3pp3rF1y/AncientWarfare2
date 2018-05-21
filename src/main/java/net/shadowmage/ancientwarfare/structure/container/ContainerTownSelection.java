package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.ItemTownBuilder;

public class ContainerTownSelection extends ContainerBase {
	public String townName;
	private int width;
	private int length;

	public ContainerTownSelection(EntityPlayer player, int x, int y, int z) {
		super(player);

		ItemStack townBuilder = EntityTools.getItemFromEitherHand(player, ItemTownBuilder.class);
		if (townBuilder.isEmpty()) {
			return;
		}

		townName = ItemTownBuilder.getTownName(townBuilder);
		width = ItemTownBuilder.getWidth(townBuilder);
		length = ItemTownBuilder.getLength(townBuilder);
	}

	public void handleNameSelection(String name) {
		sendDataToServer("townName", new NBTTagString(name));
	}

	public void handleWidthUpdate(int width) {
		sendDataToServer("width", new NBTTagInt(width));
	}

	public void handleLengthUpdate(int length) {
		sendDataToServer("length", new NBTTagInt(length));
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (player.world.isRemote) {
			return;
		}
		ItemStack townBuilder = EntityTools.getItemFromEitherHand(player, ItemTownBuilder.class);
		if (tag.hasKey("townName")) {
			ItemTownBuilder.setTownName(townBuilder, tag.getString("townName"));
		}

		if (tag.hasKey("width")) {
			ItemTownBuilder.setWidth(townBuilder, tag.getInteger("width"));
		}

		if (tag.hasKey("length")) {
			ItemTownBuilder.setLength(townBuilder, tag.getInteger("length"));
		}
	}

	public int getWidth() {
		return width;
	}

	public int getLength() {
		return length;
	}
}
