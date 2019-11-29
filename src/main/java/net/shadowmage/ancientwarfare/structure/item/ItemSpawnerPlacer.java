package net.shadowmage.ancientwarfare.structure.item;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.interfaces.IItemKeyInterface;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.WorldTools;
import net.shadowmage.ancientwarfare.structure.init.AWStructureBlocks;
import net.shadowmage.ancientwarfare.structure.registry.EntitySpawnNBTRegistry;
import net.shadowmage.ancientwarfare.structure.tile.SpawnerSettings;
import net.shadowmage.ancientwarfare.structure.tile.TileAdvancedSpawner;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ItemSpawnerPlacer extends ItemBaseStructure implements IItemKeyInterface {
	private static final String SPAWNER_DATA_TAG = "spawnerData";

	public ItemSpawnerPlacer(String name) {
		super(name);
		setMaxStackSize(1);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
		tooltip.add(I18n.format("guistrings.selected_mob") + ":");
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && hasSpawnerData(stack)) {
			SpawnerSettings settings = new SpawnerSettings();
			settings.readFromNBT(getSpawnerData(stack));
			if (!settings.getSpawnGroups().isEmpty() && !settings.getSpawnGroups().get(0).getEntitiesToSpawn().isEmpty()) {
				tooltip.add(I18n.format(settings.getSpawnGroups().get(0).getEntitiesToSpawn().get(0).getEntityName()));
			}
		} else {
			tooltip.add(I18n.format("guistrings.no_selection"));
		}
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);
		if (player.world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		if (stack.isEmpty()) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		Optional<BlockPos> placementPosition = getPlacementPosition(world, player);
		if (placementPosition.isPresent()) {
			if (hasSpawnerData(stack)) {
				placeSpawner(player, stack, placementPosition.get());
			} else {
				player.sendMessage(new TextComponentTranslation("guistrings.spawner.nodata"));
			}
		} else {
			player.sendMessage(new TextComponentTranslation("guistrings.spawner.noblock"));
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

	private void placeSpawner(EntityPlayer player, ItemStack stack, BlockPos placePos) {
		if (player.world.setBlockState(placePos, AWStructureBlocks.ADVANCED_SPAWNER.getDefaultState())) {
			WorldTools.getTile(player.world, placePos, TileAdvancedSpawner.class)
					.ifPresent(t -> {
						SpawnerSettings settings = new SpawnerSettings();
						settings.readFromNBT(getSpawnerData(stack));
						t.setSettings(settings);
					});
		}
	}

	public static NBTTagCompound getSpawnerData(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.getTagCompound().getCompoundTag(SPAWNER_DATA_TAG);
	}

	public static boolean hasSpawnerData(ItemStack stack) {
		//noinspection ConstantConditions
		return stack.hasTagCompound() && stack.getTagCompound().hasKey(SPAWNER_DATA_TAG);
	}

	private Optional<BlockPos> getPlacementPosition(World world, EntityPlayer player) {
		RayTraceResult traceResult = rayTrace(player.world, player, !player.isSneaking());

		//noinspection ConstantConditions
		if (traceResult == null || traceResult.typeOfHit != RayTraceResult.Type.BLOCK) {
			return Optional.empty();
		}

		BlockPos placementPos = traceResult.getBlockPos().offset(traceResult.sideHit);
		if (!world.getBlockState(placementPos).getBlock().isReplaceable(world, placementPos)) {
			EnumFacing offset;
			if (traceResult.sideHit.getAxis().isHorizontal()) {
				offset = player.rotationPitch < 0 ? EnumFacing.DOWN : EnumFacing.UP;
			} else {
				offset = player.getHorizontalFacing().getOpposite();
			}
			placementPos = placementPos.offset(offset);
			if (!world.getBlockState(placementPos).getBlock().isReplaceable(world, placementPos)) {
				return Optional.empty();
			}
		}
		return Optional.of(placementPos);
	}

	@SubscribeEvent
	public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
		EntityPlayer player = event.getEntityPlayer();
		ItemStack spawnerPlacer = EntityTools.getItemFromEitherHand(player, ItemSpawnerPlacer.class);
		if (!spawnerPlacer.isEmpty()) {
			event.setCanceled(true);
			event.setCancellationResult(EnumActionResult.SUCCESS);

			if (player.world.isRemote) {
				return;
			}

			Entity entity = event.getTarget();

			SpawnerSettings settings = new SpawnerSettings();
			SpawnerSettings.EntitySpawnGroup group = new SpawnerSettings.EntitySpawnGroup(settings);
			SpawnerSettings.EntitySpawnSettings spawnSettings = new SpawnerSettings.EntitySpawnSettings(group);
			if (hasSpawnerData(spawnerPlacer)) {
				settings.readFromNBT(getSpawnerData(spawnerPlacer));
				spawnSettings = getFirstEntitySpawnSettings(settings);
			} else {
				setDefaultEntitySpawnSettings(spawnSettings);
				group.addSpawnSetting(spawnSettings);
				settings.addSpawnGroup(group);
				settings.setSpawnDelay(0);
				settings.setMinDelay(10);
				settings.setMaxDelay(10);
				settings.setSpawnRange(0);
				settings.setPlayerRange(16);
				settings.toggleTransparent();
			}

			spawnSettings.setEntityToSpawn(entity);
			spawnSettings.setCustomSpawnTag(getCustomSpawnTag(entity));
			setSpawnerData(spawnerPlacer, settings);
			entity.setDead();

			event.getEntityPlayer().sendMessage(new TextComponentTranslation("guistrings.spawner.entity_set", entity.getName()));
		}
	}

	private void setDefaultEntitySpawnSettings(SpawnerSettings.EntitySpawnSettings spawnSettings) {
		spawnSettings.setSpawnLimitTotal(1);
		spawnSettings.setSpawnCountMin(1);
		spawnSettings.setSpawnCountMax(1);
	}

	private SpawnerSettings.EntitySpawnSettings getFirstEntitySpawnSettings(SpawnerSettings settings) {
		SpawnerSettings.EntitySpawnSettings spawnSettings;
		SpawnerSettings.EntitySpawnGroup group;
		if (settings.getSpawnGroups().isEmpty()) {
			group = new SpawnerSettings.EntitySpawnGroup(settings);
			settings.addSpawnGroup(group);
		} else {
			group = settings.getSpawnGroups().iterator().next();
		}

		if (group.getEntitiesToSpawn().isEmpty()) {
			spawnSettings = new SpawnerSettings.EntitySpawnSettings(group);
			setDefaultEntitySpawnSettings(spawnSettings);
			group.addSpawnSetting(spawnSettings);
		} else {
			spawnSettings = group.getEntitiesToSpawn().iterator().next();
		}
		return spawnSettings;
	}

	private static void setSpawnerData(ItemStack spawnerPlacer, SpawnerSettings settings) {
		setSpawnerData(spawnerPlacer, settings.writeToNBT(new NBTTagCompound()));
	}

	public static void setSpawnerData(ItemStack spawnerPlacer, NBTTagCompound settingsNbt) {
		spawnerPlacer.setTagInfo(SPAWNER_DATA_TAG, settingsNbt);
	}

	private NBTTagCompound getCustomSpawnTag(Entity entity) {
		NBTTagCompound entityTag = new NBTTagCompound();
		entity.writeToNBT(entityTag);

		return EntitySpawnNBTRegistry.getEntitySpawnNBT(entity, entityTag);
	}

	@Override
	public boolean onKeyActionClient(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		return altFunction == ItemAltFunction.ALT_FUNCTION_1;
	}

	@Override
	public void onKeyAction(EntityPlayer player, ItemStack stack, ItemAltFunction altFunction) {
		if (!hasSpawnerData(stack)) {
			player.sendMessage(new TextComponentString("Must have an entity set first!"));
			return;
		}

		if (player.isSneaking()) {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED_INVENTORY, 0, 0, 0);
		} else {
			NetworkHandler.INSTANCE.openGui(player, NetworkHandler.GUI_SPAWNER_ADVANCED, 0, 0, 0);
		}
	}
}
