package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.input.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.gui.GuiLootChestPlacer;
import net.shadowmage.ancientwarfare.structure.registry.EntitySpawnNBTRegistry;
import net.shadowmage.ancientwarfare.structure.tile.ISpecialLootContainer;
import net.shadowmage.ancientwarfare.structure.tile.LootSettings;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ItemLootChestPlacer extends ItemBaseStructure implements IItemKeyInterface {
	private static final String LOOT_SETTINGS_TAG = "lootSettings";

	private static final Map<String, LootContainerInfo> LOOT_CONTAINERS = new LinkedHashMap<>();
	private static final String LOOT_CONTAINER_NAME_TAG = "lootContainerName";

	public static Map<String, LootContainerInfo> getLootContainers() {
		return LOOT_CONTAINERS;
	}

	public static void registerLootContainer(String name, ItemStack stack, LootContainerInfo.IPlacementChecker mayPlace) {
		LOOT_CONTAINERS.put(name, new LootContainerInfo(name, stack, mayPlace));
	}

	public ItemLootChestPlacer() {
		super("loot_chest_placer");
		setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if (!player.world.isRemote) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_LOOT_CHEST_PLACER, 0, 0, 0);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack lootChestPlacer = EntityTools.getItemFromEitherHand(player, ItemLootChestPlacer.class);
		if (lootChestPlacer.isEmpty()) {
			return;
		}
		event.setCanceled(true);
		event.setCancellationResult(EnumActionResult.SUCCESS);

		if (player.world.isRemote) {
			return;
		}

		Entity entity = event.getTarget();

		LootSettings lootSettings = getLootSettings(lootChestPlacer).orElse(new LootSettings());

		lootSettings.setSpawnEntity(true);
		//noinspection ConstantConditions
		lootSettings.setEntity(EntityRegistry.getEntry(entity.getClass()).getRegistryName());
		lootSettings.setEntityNBT(EntitySpawnNBTRegistry.getEntitySpawnNBT(entity));
		setLootSettings(lootChestPlacer, lootSettings);
		entity.setDead();

		event.getEntityPlayer().sendMessage(new TextComponentTranslation("guistrings.spawner.entity_set", entity.getName()));
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
		LootContainerInfo lootContainerInfo = getLootContainerInfo(placer);
		ItemStack itemBlockStack = lootContainerInfo.getStack();
		ItemBlock itemBlock = (ItemBlock) itemBlockStack.getItem();
		Block block = itemBlock.getBlock();
		if (lootContainerInfo.canPlace(block, world, placePos, facing, player)) {
			itemBlock.placeBlockAt(itemBlockStack, player, world, placePos, facing, hitX, hitY, hitZ,
					block.getStateForPlacement(world, placePos, facing, hitX, hitY, hitZ, itemBlockStack.getMetadata(), player, hand));
			WorldTools.getTile(world, placePos, ISpecialLootContainer.class).ifPresent(t -> lootSettings.get().transferToContainer(t));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.FAIL;
	}

	public static LootContainerInfo getLootContainerInfo(ItemStack placer) {
		if (placer.hasTagCompound()) {
			NBTTagCompound compound = placer.getTagCompound();
			//noinspection ConstantConditions
			if (compound.hasKey(LOOT_CONTAINER_NAME_TAG)) {
				return LOOT_CONTAINERS.getOrDefault(compound.getString(LOOT_CONTAINER_NAME_TAG), getFirstLootContainer());
			}
		}
		return getFirstLootContainer();
	}

	public static void setContainerName(ItemStack placer, String name) {
		placer.setTagInfo(LOOT_CONTAINER_NAME_TAG, new NBTTagString(name));
	}

	private static LootContainerInfo getFirstLootContainer() {
		return LOOT_CONTAINERS.values().iterator().next();
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

	@SuppressWarnings("ConstantConditions")
	@Override
	public void onKeyAction(EntityPlayer player, ItemStack placer, ItemAltFunction altFunction) {
		BlockPos hit = BlockTools.getBlockClickedOn(player, player.world, false);
		WorldTools.getTile(player.world, hit, ISpecialLootContainer.class).ifPresent(te -> {
			if (altFunction == ItemAltFunction.ALT_FUNCTION_1) {
				IBlockState state = player.world.getBlockState(hit);
				getLootContainerInfoByStack(state.getBlock().getPickBlock(state, new RayTraceResult(new Vec3d(0, 0, 0), EnumFacing.UP, hit), player.world, hit, player))
						.ifPresent(container -> setContainerName(placer, container.getName()));
				getLootSettings(placer).ifPresent(s -> setLootSettings(placer, s.transferFromContainer(te)));
			} else if (altFunction == ItemAltFunction.ALT_FUNCTION_2) {
				getLootSettings(placer).ifPresent(s -> s.transferToContainer(te));
				BlockTools.notifyBlockUpdate(player.world, hit);
			}
		});
	}

	private Optional<LootContainerInfo> getLootContainerInfoByStack(ItemStack stack) {
		for (LootContainerInfo container : LOOT_CONTAINERS.values()) {
			if (ItemStack.areItemStacksEqual(container.getStack(), stack)) {
				return Optional.of(container);
			}
		}
		return Optional.empty();
	}

	public static class LootContainerInfo {
		public static final IPlacementChecker SINGLE_BLOCK_PLACEMENT_CHECKER = (block, world, pos, sidePlacedOn, placer) -> block.canPlaceBlockAt(world, pos);
		private final String name;
		private final ItemStack stack;
		private final IPlacementChecker placementChecker;

		private LootContainerInfo(String name, ItemStack stack, IPlacementChecker placementChecker) {
			this.name = name;
			this.stack = stack;
			this.placementChecker = placementChecker;
		}

		public String getName() {
			return name;
		}

		public ItemStack getStack() {
			return stack;
		}

		private boolean canPlace(Block block, World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer) {
			return placementChecker.mayPlace(block, world, pos, sidePlacedOn, placer);
		}

		public interface IPlacementChecker {
			boolean mayPlace(Block block, World world, BlockPos pos, EnumFacing sidePlacedOn, EntityPlayer placer);
		}
	}
}
