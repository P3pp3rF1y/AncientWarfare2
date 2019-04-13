package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.NBTBuilder;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.gui.GuiLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.tile.ISpecialLootContainer;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemLootChestPlacer extends ItemBaseStructure {
	private static final String LOOT_SETTINGS_TAG = "lootSettings";

	private static final List<ItemStack> LOOT_CONTAINERS = new ArrayList<>();

	public static List<ItemStack> getLootContainers() {
		return LOOT_CONTAINERS;
	}

	public static void registerLootContainer(ItemStack lootContainer) {
		LOOT_CONTAINERS.add(lootContainer);
	}

	public ItemLootChestPlacer() {
		super("loot_chest_placer");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_LOOT_CHEST_PLACER, 0, 0, 0);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return EnumActionResult.PASS;
		}
		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}

		ItemStack placer = player.getHeldItem(hand);
		Optional<LootSettings> lootSettings = getLootSettings(placer);
		if (!lootSettings.isPresent()) {
			return EnumActionResult.PASS;
		}

		BlockPos placePos = pos.offset(facing);
		ItemStack itemBlockStack = lootSettings.get().getBlockStack();
		ItemBlock itemBlock = (ItemBlock) itemBlockStack.getItem();
		Block block = itemBlock.getBlock();
		if (block.canPlaceBlockAt(world, placePos)) {
			itemBlock.placeBlockAt(itemBlockStack, player, world, placePos, facing, hitX, hitY, hitZ,
					block.getStateForPlacement(world, placePos, facing, hitX, hitY, hitZ, itemBlockStack.getMetadata(), player, hand));
			WorldTools.getTile(world, placePos, ISpecialLootContainer.class).ifPresent(t -> t.setLootSettings(lootSettings.get()));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	public static Optional<LootSettings> getLootSettings(ItemStack placer) {
		//noinspection ConstantConditions
		return placer.hasTagCompound() && placer.getTagCompound().hasKey(LOOT_SETTINGS_TAG) ?
				Optional.of(LootSettings.deserializeNBT(placer.getTagCompound().getCompoundTag(LOOT_SETTINGS_TAG))) : Optional.empty();
	}

	public static void setLootSettings(ItemStack placer, LootSettings lootSettings) {
		placer.setTagCompound(new NBTBuilder().setTag(LOOT_SETTINGS_TAG, lootSettings.serializeNBT()).build());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_LOOT_CHEST_PLACER, GuiLootChestPlacer.class);
	}
}
