package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.block.AWStructuresBlocks;
import net.shadowmage.ancientwarfare.structure.gui.GuiLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedLootChest;

import java.util.Optional;
import java.util.Random;

public class ItemLootChestPlacer extends ItemBaseStructure {
	private static final String LOOT_TABLE_NAME_TAG = "lootTableName";
	private static final String LOOT_ROLLS_TAG = "lootRolls";

	public ItemLootChestPlacer() {
		super("loot_chest_placer");
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote && !player.isSneaking()) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_LOOT_CHEST_PLACER, 0, 0, 0);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return EnumActionResult.SUCCESS;
		}

		ItemStack placer = player.getHeldItem(hand);
		Optional<Tuple<ResourceLocation, Byte>> lt = getLootParameters(placer);
		if (!lt.isPresent() || !lootTableExists(world, lt.get().getFirst())) {
			return EnumActionResult.PASS;
		}

		BlockPos placePos = pos.offset(facing);
		if (Blocks.CHEST.canPlaceBlockAt(world, placePos)) {
			world.setBlockState(placePos, AWStructuresBlocks.advancedLootChest.getDefaultState().withProperty(BlockChest.FACING, player.getHorizontalFacing().getOpposite()));
			WorldTools.getTile(world, placePos, TileAdvancedLootChest.class)
					.ifPresent(t -> {
						t.setLootTable(lt.get().getFirst(), new Random(placePos.toLong()).nextLong());
						t.setLootRolls(lt.get().getSecond());
					});

			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	private boolean lootTableExists(World world, ResourceLocation lootTableName) {
		return world.getLootTableManager().getLootTableFromLocation(lootTableName) != null;
	}

	public static Optional<Tuple<ResourceLocation, Byte>> getLootParameters(ItemStack placer) {
		//noinspection ConstantConditions
		return placer.hasTagCompound() && placer.getTagCompound().hasKey(LOOT_TABLE_NAME_TAG) ?
				Optional.of(new Tuple<>(new ResourceLocation(placer.getTagCompound().getString(LOOT_TABLE_NAME_TAG)), placer.getTagCompound().getByte(LOOT_ROLLS_TAG))) : Optional.empty();
	}

	public static void setLootParameters(ItemStack placer, String lootTableName, byte rolls) {
		placer.setTagInfo(LOOT_TABLE_NAME_TAG, new NBTTagString(lootTableName));
		placer.setTagInfo(LOOT_ROLLS_TAG, new NBTTagByte(rolls));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_LOOT_CHEST_PLACER, GuiLootChestPlacer.class);
	}
}
