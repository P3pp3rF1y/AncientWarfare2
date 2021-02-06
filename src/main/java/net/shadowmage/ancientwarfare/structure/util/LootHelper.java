package net.shadowmage.ancientwarfare.structure.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.items.CapabilityItemHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.InventoryTools;
import net.shadowmage.ancientwarfare.structure.tile.ISpecialLootContainer;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

import javax.annotation.Nullable;

import static net.shadowmage.ancientwarfare.npc.event.EventHandler.NO_SPAWN_PREVENTION_TAG;

public class LootHelper {
	private LootHelper() {}

	public static <T extends TileEntity & ISpecialLootContainer> boolean fillWithLootAndCheckIfGoodToOpen(T te, @Nullable EntityPlayer player) {
		return processLootAndCheckIfGoodToOpen(te, player, new ILootTableProcessor() {
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
		processLootAndCheckIfGoodToOpen(te, player, new ILootTableProcessor() {
			@Override
			public <U extends TileEntity & ISpecialLootContainer> void processLootTable(U te,
					@Nullable EntityPlayer player, LootSettings lootSettings, ResourceLocation lootTable) {
				for (int roll = 0; roll < lootSettings.getLootRolls(); roll++) {
					InventoryTools.dropItemsInWorld(te.getWorld(), InventoryTools.getLootStacks(te.getWorld(), player, te.getWorld().rand, lootTable), te.getPos().add(getPlayerOffset(te, player)));
				}
			}
		});
	}

	private static Vec3i getPlayerOffset(TileEntity te, @Nullable EntityPlayer player) {
		if (player == null) {
			return new Vec3i(0, 0, 0);
		}
		Vec3i playerVector = player.getPosition().subtract(te.getPos());

		int offsetX = Math.max(Math.min(playerVector.getX(), 1), -1);
		int offsetY = Math.max(Math.min(playerVector.getY(), 1), 0);
		int offsetZ = Math.max(Math.min(playerVector.getZ(), 1), -1);

		return new Vec3i(offsetX, offsetY, offsetZ);
	}

	public static <T extends TileEntity & ISpecialLootContainer> boolean processLootAndCheckIfGoodToOpen(T te,
			@Nullable EntityPlayer player, ILootTableProcessor lootTableProcessor) {
		LootSettings lootSettings = te.getLootSettings();
		boolean goodToOpen = true;
		if (!te.getWorld().isRemote) {
			if (lootSettings.getSplashPotion()) {
				lootSettings.setSplashPotion(false);
				ItemStack potion = new ItemStack(Items.SPLASH_POTION);
				PotionUtils.appendEffects(potion, lootSettings.getEffects());
				BlockPos startPos = te.getPos().add(getPlayerOffset(te, player));
				EntityPotion potionEntity = new EntityPotion(te.getWorld(), startPos.getX() + 0.5, startPos.getY() + 0.5, startPos.getZ() + 0.5, potion);
				Vec3d playerPos = player == null ? new Vec3d(startPos.getX() + 0.5, startPos.getY() + 1.5, startPos.getZ() + 0.5) :
						new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);

				potionEntity.shoot(playerPos.x - (startPos.getX() + 0.5), playerPos.y - (startPos.getY() + 0.5), playerPos.z - (startPos.getZ() + 0.5), 0.5F, 1.0F);
				te.getWorld().spawnEntity(potionEntity);
				goodToOpen = false;
			}
			if (lootSettings.getSpawnEntity()) {
				lootSettings.setSpawnEntity(false);
				EntityTools.spawnEntity(te.getWorld(), lootSettings.getEntity(), lootSettings.getEntityNBT(), te.getPos().add(getPlayerOffset(te, player)), NO_SPAWN_PREVENTION_TAG);
				goodToOpen = false;
			}
			if (lootSettings.hasLoot()) {
				lootSettings.setHasLoot(false);
				lootSettings.getLootTableName().ifPresent(lootTable -> {
					lootTableProcessor.processLootTable(te, player, lootSettings, lootTable);
					BlockTools.notifyBlockUpdate(te);
				});
				lootSettings.removeLoot();
			}
			if (lootSettings.hasMessage() && player != null) {
				lootSettings.setHasMessage(false);
				player.sendMessage(new TextComponentTranslation(lootSettings.getPlayerMessage()));
			}
		}
		return goodToOpen;
	}

	public interface ILootTableProcessor {
		<T extends TileEntity & ISpecialLootContainer> void processLootTable(T te,
				@Nullable EntityPlayer player, LootSettings lootSettings, ResourceLocation lootTable);
	}
}
