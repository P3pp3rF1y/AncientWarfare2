package net.shadowmage.ancientwarfare.vehicle.input;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicleInput;
import org.lwjgl.input.Keyboard;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VehicleInputHandler {
	public static final String CATEGORY = "keybind.category.awVehicles";
	public static final KeyBinding FORWARD = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_FORWARD, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_W, CATEGORY);
	public static final KeyBinding REVERSE = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_REVERSE, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_S, CATEGORY);
	public static final KeyBinding LEFT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_LEFT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_A, CATEGORY);
	public static final KeyBinding RIGHT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_RIGHT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_D, CATEGORY);
	public static final KeyBinding ASCEND_AIM_UP = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_ASCEND_AIM_UP, VehicleKeyConflictContext.INSTANCE,
			Keyboard.KEY_R, CATEGORY);
	public static final KeyBinding DESCEND_AIM_DOWN = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_DESCEND_AIM_DOWN, VehicleKeyConflictContext.INSTANCE,
			Keyboard.KEY_F, CATEGORY);
	public static final KeyBinding FIRE = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_FIRE, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_SPACE, CATEGORY);
	public static final KeyBinding AMMO_PREV = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_PREV, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_T,
			CATEGORY);
	public static final KeyBinding AMMO_NEXT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_NEXT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_G,
			CATEGORY);
	public static final KeyBinding TURRET_LEFT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_TURRET_LEFT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_Z,
			CATEGORY);
	public static final KeyBinding TURRET_RIGHT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_TURRET_RIGHT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_X,
			CATEGORY);
	public static final KeyBinding MOUSE_AIM = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_MOUSE_AIM, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_C,
			CATEGORY);
	public static final KeyBinding AMMO_SELECT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_SELECT, VehicleKeyConflictContext.INSTANCE, Keyboard.KEY_V,
			CATEGORY);

	private static final Map<KeyBinding, InputCallbackDispatcher> keybindingCallbacks = Maps.newHashMap();
	private static final Set<Integer> releaseableKeys = new HashSet<>();
	private static boolean trackedKeyReleased = false;

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
		releaseableKeys.add(FORWARD.getKeyCode());
		releaseableKeys.add(REVERSE.getKeyCode());
		releaseableKeys.add(LEFT.getKeyCode());
		releaseableKeys.add(RIGHT.getKeyCode());
		releaseableKeys.add(ASCEND_AIM_UP.getKeyCode());
		releaseableKeys.add(DESCEND_AIM_DOWN.getKeyCode());
		releaseableKeys.add(TURRET_LEFT.getKeyCode());
		releaseableKeys.add(TURRET_RIGHT.getKeyCode());
	}

	//TODO move this logic to core
	private static void registerCallBack(KeyBinding keyBinding, InputHandler.IInputCallback callback) {
		if (keybindingCallbacks.containsKey(keyBinding)) {
			keybindingCallbacks.get(keyBinding).addInputCallback(callback);
		} else {
			keybindingCallbacks.put(keyBinding, new InputCallbackDispatcher(callback));
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent evt) {
		boolean state = Keyboard.getEventKeyState();

		if (state) {
			for (Map.Entry<KeyBinding, InputCallbackDispatcher> keybindingCallback : keybindingCallbacks.entrySet()) {
				if (keybindingCallback.getKey().isKeyDown()) {
					keybindingCallback.getValue().onKeyPressed();
				}
			}
		} else {
			trackReleasedKeys(evt);
		}
	}

	private static void trackReleasedKeys(InputEvent.KeyInputEvent evt) {
		int key = Keyboard.getEventKey();
		if (releaseableKeys.contains(key)) {
			trackedKeyReleased = true;
		}
	}

	private static void initCallbacks() {
		registerCallBack(MOUSE_AIM,
				() -> AWVehicleStatics.enableMouseAim = !AWVehicleStatics.enableMouseAim); //TODO add code to update config once mouseAim is made into config setting
		registerCallBack(FIRE, new VehicleCallback(VehicleInputHandler::handleFireAction));
		registerCallBack(ASCEND_AIM_UP, new VehicleCallback(v -> v.firingHelper.handleAimKeyInput(-1, 0)));
		registerCallBack(DESCEND_AIM_DOWN, new VehicleCallback(v -> v.firingHelper.handleAimKeyInput(1, 0)));
		registerCallBack(TURRET_LEFT, new VehicleCallback(v -> v.firingHelper.handleAimKeyInput(0, -1)));
		registerCallBack(TURRET_RIGHT, new VehicleCallback(v -> v.firingHelper.handleAimKeyInput(0, 1)));
		registerCallBack(AMMO_NEXT, new VehicleCallback(v -> v.ammoHelper.setNextAmmo()));
		registerCallBack(AMMO_PREV, new VehicleCallback(v -> v.ammoHelper.setPreviousAmmo()));
		registerCallBack(AMMO_SELECT, new VehicleCallback(vehicle -> {
			if (!vehicle.vehicleType.getValidAmmoTypes().isEmpty()) {
				NetworkHandler.INSTANCE.openGui(Minecraft.getMinecraft().player, NetworkHandler.GUI_VEHICLE_AMMO_SELECTION, vehicle.getEntityId());
			}
		}));
	}

	private static void handleFireAction(VehicleBase vehicle) {
		Minecraft mc = Minecraft.getMinecraft();
		if (vehicle.isAimable()) {
			RayTraceResult pos = getPlayerLookTargetClient(mc.player, 140, vehicle);
			if (pos != null) {
				vehicle.firingHelper.handleFireInput(pos.hitVec);
			} else {
				vehicle.firingHelper.handleFireInput(null);
			}
		}
	}

	private static RayTraceResult getPlayerLookTargetClient(EntityPlayer player, float range, Entity excludedEntity) {
		Vec3d playerPos = player.getPositionVector();
		Vec3d lookVector = player.getLook(0);
		Vec3d endVector = playerPos.addVector(lookVector.x * range, lookVector.y * range, lookVector.z * range);
		RayTraceResult blockHit = player.world.rayTraceBlocks(playerPos, endVector);

		double var9 = 1.f;

		float closestFound = 0.f;
		if (blockHit != null) {
			closestFound = (float) blockHit.hitVec.distanceTo(playerPos);
		}
		Minecraft mc = Minecraft.getMinecraft();
		List<Entity> possibleHitEntities = mc.world.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(),
				mc.getRenderViewEntity().getEntityBoundingBox().expand(lookVector.x * range, lookVector.y * range, lookVector.z * range)
						.grow(var9, var9, var9));
		Entity hitEntity = null;
		for (Entity currentExaminingEntity : possibleHitEntities) {
			if (currentExaminingEntity == excludedEntity) {
				continue;
			}
			if (currentExaminingEntity.canBeCollidedWith()) {
				float borderSize = currentExaminingEntity.getCollisionBorderSize();
				AxisAlignedBB entBB = currentExaminingEntity.getEntityBoundingBox().grow((double) borderSize, (double) borderSize, (double) borderSize);
				RayTraceResult var17 = entBB.calculateIntercept(playerPos, endVector);

				if (entBB.contains(playerPos)) {
					if (0.0D < closestFound || closestFound == 0.0D) {
						hitEntity = currentExaminingEntity;
						closestFound = 0.0f;
					}
				} else if (var17 != null) {
					double var18 = playerPos.distanceTo(var17.hitVec);

					if (var18 < closestFound || closestFound == 0.0D) {
						hitEntity = currentExaminingEntity;
						closestFound = (float) var18;
					}
				}
			}
		}
		if (hitEntity != null) {
			blockHit = new RayTraceResult(hitEntity, new Vec3d(hitEntity.posX, hitEntity.posY + hitEntity.height * 0.65d, hitEntity.posZ));
		}
		return blockHit;
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
			if (AWVehicleStatics.enableMouseAim) {
				handleMouseAimUpdate(vehicle);
			}
		}
	}

	private int inputUpdateTicks = 0;

	private void handleMouseAimUpdate(VehicleBase vehicle) {
		inputUpdateTicks--; //TODO can this be replaced with vehicle.ticksExisted % 5?
		if (inputUpdateTicks > 0) {
			return;
		}
		inputUpdateTicks = 5;
		Minecraft mc = Minecraft.getMinecraft();
		RayTraceResult pos = getPlayerLookTargetClient(mc.player, 140, vehicle);
		if (pos != null) {
			vehicle.firingHelper.handleAimInput(pos.hitVec);
		}
	}

	private static void handleTickInput(VehicleBase vehicle) {
		if (trackedKeyReleased || vehicle.ticksExisted % 20 == 0) {
			trackedKeyReleased = false;

			PacketVehicleInput pkt = new PacketVehicleInput(vehicle);
			if (FORWARD.isKeyDown() && !REVERSE.isKeyDown()) {
				pkt.setForwardInput((byte) 1);
			} else if (REVERSE.isKeyDown() && !FORWARD.isKeyDown()) {
				pkt.setForwardInput((byte) -1);
			}

			if (LEFT.isKeyDown() && !RIGHT.isKeyDown()) {
				pkt.setTurnInput((byte) -1);
			} else if (RIGHT.isKeyDown() && !LEFT.isKeyDown()) {
				pkt.setTurnInput((byte) 1);
			}

			if (ASCEND_AIM_UP.isKeyDown() && !DESCEND_AIM_DOWN.isKeyDown()) {
				pkt.setPowerInput((byte) 1);
			} else if (DESCEND_AIM_DOWN.isKeyDown() && !ASCEND_AIM_UP.isKeyDown()) {
				pkt.setPowerInput((byte) -1);
			}

			if (TURRET_LEFT.isKeyDown() && !TURRET_RIGHT.isKeyDown()) {
				pkt.setRotationInput((byte) -1);
			} else if (TURRET_RIGHT.isKeyDown() && !TURRET_LEFT.isKeyDown()) {
				pkt.setRotationInput((byte) 1);
			}

			NetworkHandler.sendToServer(pkt);
		}
	}
}
