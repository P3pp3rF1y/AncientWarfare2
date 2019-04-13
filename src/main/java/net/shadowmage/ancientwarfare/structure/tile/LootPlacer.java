package net.shadowmage.ancientwarfare.structure.tile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.items.CapabilityItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;

import javax.annotation.Nullable;

public class LootPlacer {

	public static <T extends TileEntity & ISpecialLootContainer> void fillWithLoot(T te, @Nullable EntityPlayer player) {
		processLoot(te, player, new ILootTableProcessor() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public <U extends TileEntity & ISpecialLootContainer> void processLootTable(U te,
					@Nullable EntityPlayer player, LootSettings lootSettings, ResourceLocation lootTable) {
				if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)) {
					InventoryTools.generateLootFor(te.getWorld(), player, te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null), te.getWorld().rand, lootTable, lootSettings.getLootRolls());
				}
			}
		});
	}

	public static <T extends TileEntity & ISpecialLootContainer> void dropLoot(T te, @Nullable EntityPlayer player) {
		processLoot(te, player, new ILootTableProcessor() {
			@SuppressWarnings("ConstantConditions")
			@Override
			public <U extends TileEntity & ISpecialLootContainer> void processLootTable(U te,
					@Nullable EntityPlayer player, LootSettings lootSettings, ResourceLocation lootTable) {
				InventoryTools.dropItemsInWorld(te.getWorld(), InventoryTools.getLootStacks(te.getWorld(), player, te.getWorld().rand, lootTable), te.getPos().add(getPlayerOffset(te, player)));

			}
		});
	}

	private static Vec3i getPlayerOffset(TileEntity te, @Nullable EntityPlayer player) {
		if (player == null) {
			return new Vec3i(0, 0, 0);
		}
		Vec3i playerVector = player.getPosition().subtract(te.getPos());

		int offsetX = Math.max(Math.min(playerVector.getX(), 1), -1);
		int offsetY = Math.max(Math.min(playerVector.getY(), 1), -1);
		int offsetZ = Math.max(Math.min(playerVector.getZ(), 1), -1);

		return new Vec3i(offsetX, offsetY, offsetZ);
	}

	public static <T extends TileEntity & ISpecialLootContainer> void processLoot(T te, @Nullable EntityPlayer player, ILootTableProcessor lootTableProcessor) {
		LootSettings lootSettings = te.getLootSettings();
		if (!te.getWorld().isRemote && lootSettings.hasLoot()) {
			lootSettings.setHasLoot(false);
			lootSettings.getLootTableName().ifPresent(lootTable -> {
				//noinspection ConstantConditions
				lootTableProcessor.processLootTable(te, player, lootSettings, lootTable);
				BlockTools.notifyBlockUpdate(te);
			});
			lootSettings.removeLoot();
		}
	}

	public interface ILootTableProcessor {
		<T extends TileEntity & ISpecialLootContainer> void processLootTable(T te,
				@Nullable EntityPlayer player, LootSettings lootSettings, ResourceLocation lootTable);
	}
}
