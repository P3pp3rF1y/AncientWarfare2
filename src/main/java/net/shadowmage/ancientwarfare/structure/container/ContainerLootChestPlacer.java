package net.shadowmage.ancientwarfare.structure.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.storage.loot.LootTableList;
import net.shadowmage.ancientwarfare.core.container.ContainerBase;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.structure.config.AWStructureStatics;
import net.shadowmage.ancientwarfare.structure.item.ItemLootChestPlacer;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ContainerLootChestPlacer extends ContainerBase {
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
		setLootParameters(tag.getString("setTable"), tag.getByte("rolls"), tag.getBoolean("basket"));
	}

	public void setLootParameters(String lootTableName, byte rolls, boolean basket) {
		if (player.world.isRemote) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setString("setTable", lootTableName);
			tag.setByte("rolls", rolls);
			tag.setBoolean("basket", basket);
			sendDataToServer(tag);
			return;
		}

		ItemLootChestPlacer.setLootParameters(placer, lootTableName, rolls, basket);
	}

	public Optional<Tuple<ResourceLocation, Byte>> getLootParameters() {
		return ItemLootChestPlacer.getLootParameters(placer);
	}

	public boolean getPlaceBasket() {
		return ItemLootChestPlacer.getPlaceBasket(placer);
	}

}
