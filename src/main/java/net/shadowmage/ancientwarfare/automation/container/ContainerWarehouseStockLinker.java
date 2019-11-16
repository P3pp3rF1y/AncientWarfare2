package net.shadowmage.ancientwarfare.automation.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockLinker;
import net.shadowmage.ancientwarfare.automation.tile.warehouse2.TileWarehouseStockLinker.WarehouseStockFilter;
import net.shadowmage.ancientwarfare.core.container.ContainerTileBase;
import net.shadowmage.ancientwarfare.core.util.NBTHelper;

import java.util.ArrayList;
import java.util.List;

public class ContainerWarehouseStockLinker extends ContainerTileBase<TileWarehouseStockLinker> {

	public final List<WarehouseStockFilter> filters = new ArrayList<>();

	public ContainerWarehouseStockLinker(EntityPlayer player, int x, int y, int z) {
		super(player, x, y, z);
		filters.addAll(tileEntity.getFilters());
		tileEntity.addViewer(this);
		addPlayerSlots((256 - (9 * 18)) / 2, 88, 4);
	}

	@Override
	public void sendInitData() {
		NBTTagCompound tag = new NBTTagCompound();
		sendDataToClient(tag);
	}

	/*
	 * should be called from the tile whenever its client-side filters change
	 */
	public void onFiltersChanged() {
		refreshGui();
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		if (tag.hasKey("filterList")) {
			tileEntity.setFilters(NBTHelper.deserializeListFrom(tag, "filterList", WarehouseStockFilter::new));
		}
		refreshGui();
		super.handlePacketData(tag);
	}

	public void sendFiltersToServer() {
		NBTTagCompound tag = new NBTTagCompound();
		NBTHelper.writeSerializablesTo(tag, "filterList", filters);
		sendDataToServer(tag);
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer) {
		tileEntity.removeViewer(this);
		super.onContainerClosed(par1EntityPlayer);
	}

}