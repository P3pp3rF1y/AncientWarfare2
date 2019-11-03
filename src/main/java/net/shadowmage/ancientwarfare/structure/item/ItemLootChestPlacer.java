package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.gui.GuiLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.tile.ISpecialLootContainer;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemLootChestPlacer extends ItemBaseStructure implements IItemKeyInterface {
	private static final String LOOT_SETTINGS_TAG = "lootSettings";

	private static final List<ItemStack> LOOT_CONTAINERS = new ArrayList<>();
	public static final String BLOCK_STACK_TAG = "blockStack";

	public static List<ItemStack> getLootContainers() {
		return LOOT_CONTAINERS;
	}

	public static void registerLootContainer(ItemStack lootContainer) {
		LOOT_CONTAINERS.add(lootContainer);
	}

	public ItemLootChestPlacer() {
		super("loot_chest_placer");
		setMaxStackSize(1);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_LOOT_CHEST_PLACER, 0, 0, 0);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		String keyText = InputHandler.ALT_ITEM_USE_1.getDisplayName();
		String text = keyText + " = " + I18n.format("guistrings.structure.loot_placer.copy");
		tooltip.add(text);

		keyText = InputHandler.ALT_ITEM_USE_2.getDisplayName();
		text = keyText + " = " + I18n.format("guistrings.structure.loot_placer.paste");
		tooltip.add(text);

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
		ItemStack itemBlockStack = getBlockStack(placer);
		ItemBlock itemBlock = (ItemBlock) itemBlockStack.getItem();
		Block block = itemBlock.getBlock();
		if (block.canPlaceBlockAt(world, placePos)) {
			itemBlock.placeBlockAt(itemBlockStack, player, world, placePos, facing, hitX, hitY, hitZ,
					block.getStateForPlacement(world, placePos, facing, hitX, hitY, hitZ, itemBlockStack.getMetadata(), player, hand));
			WorldTools.getTile(world, placePos, ISpecialLootContainer.class).ifPresent(t -> lootSettings.get().transferToContainer(t));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	@SuppressWarnings("ConstantConditions")
	public static ItemStack getBlockStack(ItemStack placer) {
		return placer.hasTagCompound() && placer.getTagCompound().hasKey(BLOCK_STACK_TAG) ?
				new ItemStack(placer.getTagCompound().getCompoundTag(BLOCK_STACK_TAG)) : LOOT_CONTAINERS.get(0);
	}

	public static void setBlockStack(ItemStack placer, ItemStack blockStack) {
		placer.setTagInfo(BLOCK_STACK_TAG, blockStack.writeToNBT(new NBTTagCompound()));
	}

	public static Optional<LootSettings> getLootSettings(ItemStack placer) {
		//noinspection ConstantConditions
		return placer.hasTagCompound() && placer.getTagCompound().hasKey(LOOT_SETTINGS_TAG) ?
				Optional.of(LootSettings.deserializeNBT(placer.getTagCompound().getCompoundTag(LOOT_SETTINGS_TAG))) : Optional.empty();
	}

	public static void setLootSettings(ItemStack placer, LootSettings lootSettings) {
		placer.setTagInfo(LOOT_SETTINGS_TAG, lootSettings.serializeNBT());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		super.registerClient();

		NetworkHandler.registerGui(NetworkHandler.GUI_LOOT_CHEST_PLACER, GuiLootChestPlacer.class);
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return altFunction == ItemAltFunction.ALT_FUNCTION_1 || altFunction == ItemAltFunction.ALT_FUNCTION_2;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack placer, ItemAltFunction altFunction) {
		BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, false);
		WorldTools.getTile(player.world, hit, ISpecialLootContainer.class).ifPresent(te -> {
			if (altFunction == ItemAltFunction.ALT_FUNCTION_1) {
				IBlockState state = player.world.getBlockState(hit);
				setBlockStack(placer, state.getBlock().getPickBlock(state, new RayTraceResult(new Vec3d(0, 0, 0), EnumFacing.UP, hit), player.world, hit, player));
				getLootSettings(placer).ifPresent(s -> setLootSettings(placer, s.transferFromContainer(te)));
			} else if (altFunction == ItemAltFunction.ALT_FUNCTION_2) {
				getLootSettings(placer).ifPresent(s -> s.transferToContainer(te));
				BlockTools.notifyBlockUpdate(player.world, hit);
			}
		});
	}
}
