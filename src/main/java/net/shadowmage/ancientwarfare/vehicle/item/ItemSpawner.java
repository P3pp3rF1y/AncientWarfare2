package net.shadowmage.ancientwarfare.vehicle.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.AncientWarfareCore;
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.IVehicleType;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.types.VehicleType;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemSpawner extends ItemBaseVehicle {
	private static final String LEVEL_TAG = "level";
	private static final String HEALTH_TAG = "health";
	private static final String SPAWN_DATA_TAG = "spawnData";

	public ItemSpawner() {
		super("spawner");
		setHasSubtypes(true);
		maxStackSize = 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if (world.isRemote) {
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}

		if (stack.isEmpty()) {
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}

		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(SPAWN_DATA_TAG)) {
			if (rayTraceAndSpawnVehicle(world, player, hand, stack))
				return new ActionResult<>(EnumActionResult.FAIL, stack);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		AncientWarfareVehicles.LOG.error("Vehicle spawner item was missing NBT data, something may have corrupted this item");
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}

	private boolean rayTraceAndSpawnVehicle(World world, EntityPlayer player, EnumHand hand, ItemStack stack) {
		//noinspection ConstantConditions
		NBTTagCompound tag = stack.getTagCompound().getCompoundTag(SPAWN_DATA_TAG);
		int level = tag.getInteger(LEVEL_TAG);
		Optional<VehicleBase> v = VehicleType.getVehicleForType(world, stack.getItemDamage(), level);
		if (!v.isPresent()) {
			return true;
		}
		VehicleBase vehicle = v.get();
		if (tag.hasKey(HEALTH_TAG)) {
			vehicle.setHealth(tag.getFloat(HEALTH_TAG));
		}
		RayTraceResult rayTrace = rayTrace(world, player, true);
		//noinspection ConstantConditions
		if (rayTrace == null || rayTrace.typeOfHit != RayTraceResult.Type.BLOCK) {
			return true;
		}
		spawnVehicle(world, player, vehicle, rayTrace);
		updateSpawnerStackCount(player, hand, stack);
		return false;
	}

	private void updateSpawnerStackCount(EntityPlayer player, EnumHand hand, ItemStack stack) {
		if (!player.capabilities.isCreativeMode) {
			stack.shrink(1);
			if (stack.getCount() <= 0) {
				player.setHeldItem(hand, ItemStack.EMPTY);
			}
		}
	}

	private void spawnVehicle(World world, EntityPlayer player, VehicleBase vehicle, RayTraceResult rayTrace) {
		Vec3d hitVec = rayTrace.hitVec;
		if (rayTrace.sideHit.getAxis().isHorizontal()) {
			Vec3i dirVec = rayTrace.sideHit.getDirectionVec();
			float halfWidth = vehicle.width / 2f;
			hitVec = hitVec.addVector(dirVec.getX() * halfWidth, 0, dirVec.getZ() * halfWidth);
		}

		vehicle.setPosition(hitVec.x, hitVec.y, hitVec.z);
		vehicle.prevRotationYaw = vehicle.rotationYaw = -player.rotationYaw + 180;
		vehicle.localTurretDestRot = vehicle.localTurretRotation = vehicle.localTurretRotationHome = vehicle.rotationYaw;
		if (AWVehicleStatics.useVehicleSetupTime) {
			vehicle.setSetupState(true, 100);
		}
		world.spawnEntity(vehicle);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, world, tooltip, flagIn);
		//noinspection ConstantConditions
		if (stack.hasTagCompound() && stack.getTagCompound().hasKey(SPAWN_DATA_TAG)) {
			NBTTagCompound tag = stack.getTagCompound().getCompoundTag(SPAWN_DATA_TAG);
			int level = tag.getInteger(LEVEL_TAG);
			tooltip.add("Material Level: " + level);//TODO additional translations
			if (tag.hasKey(HEALTH_TAG)) {
				tooltip.add("Vehicle Health: " + tag.getFloat(HEALTH_TAG));
			}

			Optional<VehicleBase> v = VehicleType.getVehicleForType(world, stack.getItemDamage(), level);
			if (!v.isPresent()) {
				return;
			}
			tooltip.addAll(v.get().vehicleType.getDisplayTooltip().stream().map(I18n::format).collect(Collectors.toSet()));
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		IVehicleType vehicle = VehicleType.vehicleTypes[stack.getItemDamage()];
		return vehicle == null ? "" : vehicle.getDisplayName();
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (!isInCreativeTab(tab)) {
			return;
		}
		items.addAll(VehicleType.getCreativeDisplayItems());
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerClient() {
		ResourceLocation baseLocation = new ResourceLocation(AncientWarfareCore.MOD_ID, "vehicle/" + getRegistryName().getResourcePath());
		String modelPropString = "variant=%s";

		ModelLoader.setCustomMeshDefinition(this, stack -> {
			if (stack.hasTagCompound()) {
				//noinspection ConstantConditions
				int level = stack.getTagCompound().getCompoundTag(SPAWN_DATA_TAG).getInteger(LEVEL_TAG);
				return new ModelResourceLocation(baseLocation, String.format(modelPropString, VehicleType.getVehicleType(stack.getMetadata()).getConfigName() + "_" + level));
			}
			return new ModelResourceLocation(baseLocation, String.format(modelPropString, "catapult_stand_0"));
		});

		for (IVehicleType type : VehicleType.vehicleTypes) {
			if (type == null || type.getMaterialType() == null || !type.isEnabled()) {
				continue;
			}
			for (int level = 0; level < type.getMaterialType().getNumOfLevels(); level++) {
				ModelLoader.registerItemVariants(this,
						new ModelResourceLocation(baseLocation, String.format(modelPropString, type.getConfigName() + "_" + level)));
			}
		}
	}
}
