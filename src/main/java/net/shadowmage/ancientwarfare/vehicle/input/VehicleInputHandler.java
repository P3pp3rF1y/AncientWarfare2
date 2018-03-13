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
import net.shadowmage.ancientwarfare.vehicle.AncientWarfareVehicles;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.gui.GuiIds;
import net.shadowmage.ancientwarfare.vehicle.network.PacketVehicleMove;
import org.lwjgl.input.Keyboard;

import java.util.List;
import java.util.Map;

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

	private static Map<KeyBinding, InputCallbackDispatcher> keybindingCallbacks = Maps.newHashMap();

	private static final VehicleInputHandler INSTANCE = new VehicleInputHandler();

	private VehicleInputHandler() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void initKeyBindings() {
		ClientRegistry.registerKeyBinding(FORWARD);
		ClientRegistry.registerKeyBinding(REVERSE);
		ClientRegistry.registerKeyBinding(LEFT);
		ClientRegistry.registerKeyBinding(RIGHT);
		ClientRegistry.registerKeyBinding(ASCEND_AIM_UP);
		ClientRegistry.registerKeyBinding(DESCEND_AIM_DOWN);
		ClientRegistry.registerKeyBinding(FIRE);

		initCallbacks();
	}

	//TODO move this logic to core
	public static void registerCallBack(KeyBinding keyBinding, InputHandler.IInputCallback callback) {
		if (keybindingCallbacks.containsKey(keyBinding)) {
			keybindingCallbacks.get(keyBinding).addInputCallback(callback);
		} else {
			keybindingCallbacks.put(keyBinding, new InputCallbackDispatcher(callback));
		}
	}

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent evt) {
		boolean state = Keyboard.getEventKeyState();

		for (Map.Entry<KeyBinding, InputCallbackDispatcher> keybindingCallback : keybindingCallbacks.entrySet()) {
			if (keybindingCallback.getKey().isKeyDown()) { //TODO see if there needs to be something to handle only initial key press that is .isPressed()
				if (state) {
					keybindingCallback.getValue().onKeyPressed();
				}
			}
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
				NetworkHandler.INSTANCE.openGui(Minecraft.getMinecraft().player, AncientWarfareVehicles.instance, GuiIds.AMMO_SELECTION);
			}
		}));

	}

	public static void handleFireAction(VehicleBase vehicle) {
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

	public static RayTraceResult getPlayerLookTargetClient(EntityPlayer player, float range, Entity excludedEntity) {
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

	int inputUpdateTicks = 0;

	public void handleMouseAimUpdate(VehicleBase vehicle) {
		inputUpdateTicks--;
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

	protected void handleTickInput(VehicleBase vehicle) {
		NetworkHandler.sendToServer(new PacketVehicleMove(vehicle, ));

		NBTTagCompound tag = null;
		if (vehicle.ticksExisted % 20 == 0) {
			tag = new NBTTagCompound();
			tag.setByte("f", (byte) (forward.isPressed ? 1 : reverse.isPressed ? -1 : 0));
			tag.setByte("s", (byte) (left.isPressed ? -1 : right.isPressed ? 1 : 0));
			tag.setByte("p", (byte) (pitchUp.isPressed ? 1 : pitchDown.isPressed ? -1 : 0));
			tag.setByte("r", (byte) (turretLeft.isPressed ? -1 : turretRight.isPressed ? 1 : 0));
		} else {
			if (changedKeys.isEmpty()) {
				return;
			}
			tag = new NBTTagCompound();
			for (Keybind k : this.changedKeys) {
				if (k == forward) {
					tag.setByte("f", (byte) (forward.isPressed ? 1 : 0));
				} else if (k == reverse) {
					tag.setByte("f", (byte) (reverse.isPressed ? -1 : 0));
				} else if (k == left) {
					tag.setByte("s", (byte) (left.isPressed ? -1 : 0));
				} else if (k == right) {
					tag.setByte("s", (byte) (right.isPressed ? 1 : 0));
				} else if (k == pitchUp) {
					tag.setByte("p", (byte) (pitchUp.isPressed ? 1 : 0));
				} else if (k == pitchDown) {
					tag.setByte("p", (byte) (pitchDown.isPressed ? -1 : 0));
				} else if (k == turretLeft) {
					tag.setByte("r", (byte) (turretLeft.isPressed ? -1 : 0));
				} else if (k == turretRight) {
					tag.setByte("r", (byte) (turretRight.isPressed ? 1 : 0));
				}
			}
		}
		if (tag != null) {
			//    Config.logDebug("sending input packet to server");
			Packet02Vehicle pkt = new Packet02Vehicle();
			pkt.setParams(vehicle);
			pkt.setInputData(tag);
			pkt.sendPacketToServer();
		}
	}

	//	public void onTickEnd()
	//	{
	//		if(mc.thePlayer!=null && mc.thePlayer.ridingEntity instanceof VehicleBase && mc.currentScreen==null)
	//		{
	//			VehicleBase vehicle = (VehicleBase)mc.thePlayer.ridingEntity;
	//			if(vehicle!=null)
	//			{
	//				this.handleTickInput(vehicle);
	//				if(Settings.getMouseAim())
	//				{
	//					this.handleMouseAimUpdate();
	//				}
	//			}
	//		}
	//		this.changedKeys.clear();
	//	}
	//
	//
	//
	//
	//
	//

	//
	//

}
