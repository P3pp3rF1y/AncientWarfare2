package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.item.AWStructuresItems;
import net.shadowmage.ancientwarfare.structure.item.ItemLootChestPlacer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContainerLootChestPlacer extends ContainerBase {
	private final ItemStack placer;
	private final EnumHand hand;

	public ContainerLootChestPlacer(EntityPlayer player, int x, int y, int z) {
		super(player);
		placer = EntityTools.getItemFromEitherHand(player, ItemLootChestPlacer.class);
		hand = EntityTools.getHandHoldingItem(player, AWStructuresItems.lootChestPlacer);
	}

	public List<String> getLootTableNames() {
		return LootTableList.getAll().stream().map(ResourceLocation::toString).collect(Collectors.toList());
	}

	@Override
	public void handlePacketData(NBTTagCompound tag) {
		setLootTableName(tag.getString("setTable"));
	}

	public void setLootTableName(String lootTableName) {
		if (player.world.isRemote) {
			sendDataToServer("setTable", new NBTTagString(lootTableName));
			return;
		}

		ItemLootChestPlacer.setLootTableName(placer, lootTableName);
	}

	public Optional<ResourceLocation> getLootTable() {
		return ItemLootChestPlacer.getLootTableName(placer);
	}
}
