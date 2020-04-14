package net.shadowmage.ancientwarfare.vehicle.input;

import codechicken.lib.raytracer.RayTracer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicleInput;
import org.lwjgl.input.Keyboard;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class VehicleInputHandler {
	private static final String CATEGORY = "keybind.category.awVehicles";
	private static final KeyBinding FORWARD = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_FORWARD, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_W,
			CATEGORY);
	private static final KeyBinding REVERSE = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_REVERSE, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_S,
			CATEGORY);
	private static final KeyBinding LEFT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_LEFT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_A, CATEGORY);
	private static final KeyBinding RIGHT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_RIGHT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_D, CATEGORY);
	private static final KeyBinding ASCEND_AIM_UP = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_ASCEND_AIM_UP, VehicleKeyConflictContext.INSTANCE,
			Keyboard.KEY_R, CATEGORY);
	private static final KeyBinding DESCEND_AIM_DOWN = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_DESCEND_AIM_DOWN, VehicleKeyConflictContext.INSTANCE,
			Keyboard.KEY_F, CATEGORY);
	private static final KeyBinding FIRE = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_FIRE, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_SPACE, CATEGORY);
	private static final KeyBinding AMMO_PREV = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_PREV, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_T,
			CATEGORY);
	private static final KeyBinding AMMO_NEXT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_NEXT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_G,
			CATEGORY);
	private static final KeyBinding TURRET_LEFT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_TURRET_LEFT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_Z,
			CATEGORY);
	private static final KeyBinding TURRET_RIGHT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_TURRET_RIGHT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_X,
			CATEGORY);
	private static final KeyBinding MOUSE_AIM = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_MOUSE_AIM, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_C,
			CATEGORY);
	private static final KeyBinding AMMO_SELECT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_SELECT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_V,
			CATEGORY);

	private static final Set<Integer> releaseableKeys = new HashSet<>();
	private static boolean trackedKeyReleased = false;
	private static final Set<IVehicleMovementHandler> vehicleMovementHandlers = new HashSet<>();

	static {
		MinecraftForge.EVENT_BUS.register(new VehicleInputHandler());
	}

	private VehicleInputHandler() {
	}

	public static void initKeyBindings() {
		ClientRegistry.registerKeyBinding(FORWARD);
		ClientRegistry.registerKeyBinding(REVERSE);
		ClientRegistry.registerKeyBinding(LEFT);
		ClientRegistry.registerKeyBinding(RIGHT);
		ClientRegistry.registerKeyBinding(ASCEND_AIM_UP);
		ClientRegistry.registerKeyBinding(DESCEND_AIM_DOWN);
		ClientRegistry.registerKeyBinding(FIRE);
		ClientRegistry.registerKeyBinding(AMMO_PREV);
		ClientRegistry.registerKeyBinding(AMMO_NEXT);
		ClientRegistry.registerKeyBinding(TURRET_LEFT);
		ClientRegistry.registerKeyBinding(TURRET_RIGHT);
		ClientRegistry.registerKeyBinding(MOUSE_AIM);
		ClientRegistry.registerKeyBinding(AMMO_SELECT);

		initCallbacks();
		initReleaseableKeys();
	}

	private static void initReleaseableKeys() {
		vehicleMovementHandlers.add(new IVehicleMovementHandler.Impl(FORWARD, REVERSE, p -> p.setForwardInput((byte) 1)));
		vehicleMovementHandlers.add(new IVehicleMovementHandler.Impl(REVERSE, FORWARD, p -> p.setForwardInput((byte) -1)));
		vehicleMovementHandlers.add(new IVehicleMovementHandler.Impl(RIGHT, LEFT, p -> p.setTurnInput((byte) 1)));
		vehicleMovementHandlers.add(new IVehicleMovementHandler.Impl(LEFT, RIGHT, p -> p.setTurnInput((byte) -1)));
		vehicleMovementHandlers.add(new IVehicleMovementHandler.Impl(ASCEND_AIM_UP, DESCEND_AIM_DOWN, p -> p.setPowerInput((byte) 1)));
		vehicleMovementHandlers.add(new IVehicleMovementHandler.Impl(DESCEND_AIM_DOWN, ASCEND_AIM_UP, p -> p.setPowerInput((byte) -1)));
		vehicleMovementHandlers.add(new IVehicleMovementHandler.Impl(TURRET_RIGHT, TURRET_LEFT, p -> p.setRotationInput((byte) 1)));
		vehicleMovementHandlers.add(new IVehicleMovementHandler.Impl(TURRET_LEFT, TURRET_RIGHT, p -> p.setRotationInput((byte) -1)));

		releaseableKeys.add(FORWARD.getKeyCode());
		releaseableKeys.add(REVERSE.getKeyCode());
		releaseableKeys.add(LEFT.getKeyCode());
		releaseableKeys.add(RIGHT.getKeyCode());
		releaseableKeys.add(ASCEND_AIM_UP.getKeyCode());
		releaseableKeys.add(DESCEND_AIM_DOWN.getKeyCode());
		releaseableKeys.add(TURRET_LEFT.getKeyCode());
		releaseableKeys.add(TURRET_RIGHT.getKeyCode());
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent evt) {
		if (!Keyboard.getEventKeyState()) {
			trackReleasedKeys();
		}
	}

	private static void trackReleasedKeys() {
		int key = Keyboard.getEventKey();
		if (releaseableKeys.contains(key)) {
			trackedKeyReleased = true;
		}
	}

	private static void initCallbacks() {
		InputHandler.registerCallBack(MOUSE_AIM,
				() -> AWVehicleStatics.clientSettings.enableMouseAim = !AWVehicleStatics.clientSettings.enableMouseAim); //TODO add code to update config once mouseAim is made into config setting
		InputHandler.registerCallBack(FIRE, new VehicleCallback(VehicleInputHandler::handleFireAction));
		InputHandler.registerCallBack(ASCEND_AIM_UP, new VehicleCallback(v -> v.firingHelper.handleAimKeyInput(-1, 0)));
		InputHandler.registerCallBack(DESCEND_AIM_DOWN, new VehicleCallback(v -> v.firingHelper.handleAimKeyInput(1, 0)));
		InputHandler.registerCallBack(TURRET_LEFT, new VehicleCallback(v -> v.firingHelper.handleAimKeyInput(0, -1)));
		InputHandler.registerCallBack(TURRET_RIGHT, new VehicleCallback(v -> v.firingHelper.handleAimKeyInput(0, 1)));
		InputHandler.registerCallBack(AMMO_NEXT, new VehicleCallback(v -> v.ammoHelper.setNextAmmo()));
		InputHandler.registerCallBack(AMMO_PREV, new VehicleCallback(v -> v.ammoHelper.setPreviousAmmo()));
		InputHandler.registerCallBack(AMMO_SELECT, new VehicleCallback(VehicleInputHandler::handleAmmoSelectAction));
	}

	private static void handleAmmoSelectAction(VehicleBase vehicle) {
		if (!vehicle.isAmmoLoaded()) {
			Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarevehicles.ammo.no_ammo"), true);
			return;
		}

		if (!vehicle.vehicleType.getValidAmmoTypes().isEmpty()) {
			NetworkHandler.INSTANCE.openGui(Minecraft.getMinecraft().player, NetworkHandler.GUI_VEHICLE_AMMO_SELECTION, vehicle.getEntityId());
		}
	}

	private static void handleFireAction(VehicleBase vehicle) {
		String configName = vehicle.vehicleType.getConfigName();
		if (!vehicle.isAmmoLoaded() && !(configName.equals("battering_ram") || configName.equals("boat_transport") || configName.equals("chest_cart"))) {
			Minecraft.getMinecraft().player.sendStatusMessage(new TextComponentTranslation("gui.ancientwarfarevehicles.ammo.no_ammo"), true);
		}
		if (vehicle.isAimable()) {
			vehicle.firingHelper.handleFireInput();
		}
	}

	private static final float MAX_RANGE = 140;

	private static RayTraceResult getPlayerLookTargetClient(EntityPlayer player, Entity excludedEntity) {
		Vec3d playerEyesPos = RayTracer.getCorrectedHeadVec(player);
		Vec3d lookVector = player.getLook(0);
		Vec3d endVector = playerEyesPos.addVector(lookVector.x * MAX_RANGE, lookVector.y * MAX_RANGE, lookVector.z * MAX_RANGE);
		RayTraceResult blockHit = player.world.rayTraceBlocks(playerEyesPos, endVector);

		Optional<Tuple<Double, Entity>> closestEntityFound = getClosestCollidedEntity(excludedEntity, playerEyesPos, lookVector, endVector);

		if (closestEntityFound.isPresent() && (blockHit == null || closestEntityFound.get().getFirst() < blockHit.hitVec.distanceTo(playerEyesPos))) {
			Entity hitEntity = closestEntityFound.get().getSecond();
			blockHit = new RayTraceResult(hitEntity, new Vec3d(hitEntity.posX, hitEntity.posY + hitEntity.height * 0.65d, hitEntity.posZ));
		}
		return blockHit;
	}

	private static Optional<Tuple<Double, Entity>> getClosestCollidedEntity(Entity excludedEntity, Vec3d playerEyesPos, Vec3d lookVector, Vec3d endVector) {
		Minecraft mc = Minecraft.getMinecraft();

		//noinspection ConstantConditions
		List<Entity> possibleHitEntities = mc.world.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(),
				mc.getRenderViewEntity().getEntityBoundingBox().expand(lookVector.x * MAX_RANGE, lookVector.y * MAX_RANGE, lookVector.z * MAX_RANGE)
						.grow(1, 1, 1));
		return possibleHitEntities.stream().filter(e -> e != excludedEntity && e.canBeCollidedWith())
				.map(e -> new Tuple<>(getDistanceToCollidedEntity(e, playerEyesPos, endVector), e)).filter(t -> t.getFirst() < Double.MAX_VALUE)
				.sorted(Comparator.comparing(Tuple::getFirst)).findFirst();
	}

	private static double getDistanceToCollidedEntity(Entity entity, Vec3d startVector, Vec3d endVector) {
		float borderSize = entity.getCollisionBorderSize();
		AxisAlignedBB entBB = entity.getEntityBoundingBox().grow((double) borderSize, (double) borderSize, (double) borderSize);
		RayTraceResult rayTraceResult = entBB.calculateIntercept(startVector, endVector);

		return rayTraceResult != null ? startVector.distanceTo(rayTraceResult.hitVec) : Double.MAX_VALUE;
	}

	@SubscribeEvent
	public void onTickEnd(TickEvent.ClientTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return;
		}

		Minecraft mc = Minecraft.getMinecraft();
		if (mc.player != null && mc.player.getRidingEntity() instanceof VehicleBase) {
			VehicleBase vehicle = (VehicleBase) mc.player.getRidingEntity();
			handleTickInput(vehicle);
			if (AWVehicleStatics.clientSettings.enableMouseAim) {
				handleMouseAimUpdate(vehicle);
			}
		}
	}

	private void handleMouseAimUpdate(VehicleBase vehicle) {
		if (vehicle.ticksExisted % 5 == 0) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult pos = getPlayerLookTargetClient(mc.player, vehicle);
		if (pos != null) {
			vehicle.firingHelper.handleAimInput(pos.hitVec);
		}
	}

	private static void handleTickInput(VehicleBase vehicle) {
		if (trackedKeyReleased || vehicle.ticksExisted % 20 == 0) {
			trackedKeyReleased = false;

			PacketVehicleInput pkt = new PacketVehicleInput(vehicle);

			vehicleMovementHandlers.stream().filter(h -> h.getKeyBinding().isKeyDown() && !h.getReverseKeyBinding().isKeyDown())
					.forEach(h -> h.updatePacket(pkt));

			NetworkHandler.sendToServer(pkt);
		}
	}

}
