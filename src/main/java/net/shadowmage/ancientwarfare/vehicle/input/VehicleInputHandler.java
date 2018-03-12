package net.shadowmage.ancientwarfare.vehicle.input;

import com.google.common.collect.Maps;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.shadowmage.ancientwarfare.core.input.InputHandler;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import org.lwjgl.input.Keyboard;

import java.util.Map;

public class VehicleInputHandler {
	public static final String CATEGORY = "keybind.category.awVehicles";
	public static final KeyBinding FORWARD = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_FORWARD, KeyConflictContext.GUI, Keyboard.KEY_W, CATEGORY);
	public static final KeyBinding REVERSE = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_REVERSE, KeyConflictContext.GUI, Keyboard.KEY_S, CATEGORY);
	public static final KeyBinding LEFT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_LEFT, KeyConflictContext.GUI, Keyboard.KEY_A, CATEGORY);
	public static final KeyBinding RIGHT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_RIGHT, KeyConflictContext.GUI, Keyboard.KEY_D, CATEGORY);
	public static final KeyBinding ASCEND_AIM_UP = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_ASCEND_AIM_UP, KeyConflictContext.GUI, Keyboard.KEY_R, CATEGORY);
	public static final KeyBinding DESCEND_AIM_DOWN = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_DESCEND_AIM_DOWN, KeyConflictContext.GUI, Keyboard.KEY_F,
			CATEGORY);
	public static final KeyBinding FIRE = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_FIRE, KeyConflictContext.GUI, Keyboard.KEY_SPACE, CATEGORY);
	public static final KeyBinding AMMO_PREV = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_PREV, KeyConflictContext.GUI, Keyboard.KEY_T, CATEGORY);
	public static final KeyBinding AMMO_NEXT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_NEXT, KeyConflictContext.GUI, Keyboard.KEY_G, CATEGORY);
	public static final KeyBinding TURRET_LEFT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_TURRET_LEFT, KeyConflictContext.GUI, Keyboard.KEY_Z, CATEGORY);
	public static final KeyBinding TURRET_RIGHT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_TURRET_RIGHT, KeyConflictContext.GUI, Keyboard.KEY_X, CATEGORY);
	public static final KeyBinding MOUSE_AIM = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_MOUSE_AIM, KeyConflictContext.GUI, Keyboard.KEY_C, CATEGORY);
	public static final KeyBinding AMMO_SELECT = new KeyBinding(AWVehicleStatics.KEY_VEHICLE_AMMO_SELECT, KeyConflictContext.GUI, Keyboard.KEY_V, CATEGORY);

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
	}

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

	add tick
	handler for
	continuously pressed
	key?

	//	@Override
	//	public void onKeyPressed(Keybind kb)
	//	{
	//		this.changedKeys.add(kb);
	//		if(mc.currentScreen==null && mc.theWorld != null && mc.thePlayer!=null)
	//		{
	//			if(kb==this.mouseAim )
	//			{
	//				Settings.setMouseAim(!Settings.getMouseAim());
	//			}
	//			else if(kb==forward || kb==left || kb==right || kb==reverse)
	//			{
	//				hasMoveInput = true;
	//			}
	//			else if(kb==fire)
	//			{
	//				this.handleFireAction();
	//			}
	//			else if(kb==pitchUp || kb == pitchDown || kb == turretLeft || kb==turretRight)
	//			{
	//				this.handleAimAction(kb);
	//			}
	//			else if(kb==ammoPrev || kb== ammoNext)
	//			{
	//				this.handleAmmoKeyAction(kb);
	//			}
	//			else if(kb==ammoSelect)
	//			{
	//				this.handleAmmoSelectGui();
	//			}
	//			else if(kb==control)
	//			{
	//				Packet01ModData pkt = new Packet01ModData();
	//				NBTTagCompound tag = new NBTTagCompound();
	//				tag.setString("id", mc.thePlayer.getEntityName());
	//				tag.setBoolean("down", control.isPressed);
	//				pkt.packetData.setCompoundTag("keySynch", tag);
	//				pkt.sendPacketToServer();
	//			}
	//		}
	//	}
	//
	//	List<Keybind> changedKeys = new ArrayList<Keybind>();
	//
	//	@Override
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
	//	protected void handleTickInput(VehicleBase vehicle)
	//	{
	//		NBTTagCompound tag = null;
	//		if(vehicle.ticksExisted%20==0)
	//		{
	//			tag = new NBTTagCompound();
	//			tag.setByte("f", (byte) (forward.isPressed ? 1 : reverse.isPressed? -1: 0));
	//			tag.setByte("s", (byte)(left.isPressed ? -1 : right.isPressed ? 1 : 0));
	//			tag.setByte("p", (byte)(pitchUp.isPressed ? 1 : pitchDown.isPressed ? -1 : 0));
	//			tag.setByte("r", (byte)(turretLeft.isPressed ? -1 : turretRight.isPressed ? 1 : 0));
	//		}
	//		else
	//		{
	//			if(changedKeys.isEmpty())
	//			{
	//				return;
	//			}
	//			tag = new NBTTagCompound();
	//			for(Keybind k : this.changedKeys)
	//			{
	//				if(k==forward)
	//				{
	//					tag.setByte("f", (byte) (forward.isPressed ? 1 : 0));
	//				}
	//				else if(k==reverse)
	//				{
	//					tag.setByte("f", (byte) (reverse.isPressed ? -1 : 0));
	//				}
	//				else if(k==left)
	//				{
	//					tag.setByte("s", (byte)(left.isPressed ? -1 : 0));
	//				}
	//				else if(k==right)
	//				{
	//					tag.setByte("s", (byte)(right.isPressed ? 1 : 0));
	//				}
	//				else if(k==pitchUp)
	//				{
	//					tag.setByte("p", (byte)(pitchUp.isPressed ? 1 : 0));
	//				}
	//				else if(k==pitchDown)
	//				{
	//					tag.setByte("p", (byte)(pitchDown.isPressed ? -1 : 0));
	//				}
	//				else if(k==turretLeft)
	//				{
	//					tag.setByte("r", (byte)(turretLeft.isPressed ? -1 : 0));
	//				}
	//				else if(k==turretRight)
	//				{
	//					tag.setByte("r", (byte)(turretRight.isPressed ? 1 : 0));
	//				}
	//			}
	//		}
	//		if(tag!=null)
	//		{
	//			//    Config.logDebug("sending input packet to server");
	//			Packet02Vehicle pkt = new Packet02Vehicle();
	//			pkt.setParams(vehicle);
	//			pkt.setInputData(tag);
	//			pkt.sendPacketToServer();
	//		}
	//	}
	//
	//	private void handleAmmoSelectGui()
	//	{
	//		if(mc.currentScreen==null && mc.thePlayer!=null && mc.theWorld!=null && mc.thePlayer.ridingEntity instanceof VehicleBase)
	//		{
	//			VehicleBase vehicle = (VehicleBase)mc.thePlayer.ridingEntity;
	//			if(vehicle.vehicleType.getValidAmmoTypes().size()>=1)
	//			{
	//				GUIHandler.instance().openGUI(GUIHandler.VEHICLE_AMMO_SELECT, mc.thePlayer, mc.theWorld, vehicle.entityId, 0, 0);
	//			}
	//		}
	//	}
	//
	//	private void handleAmmoKeyAction(Keybind kb)
	//	{
	//		if(mc.currentScreen==null && mc.thePlayer!=null && mc.theWorld!=null && mc.thePlayer.ridingEntity instanceof VehicleBase)
	//		{
	//			int amt = 0;
	//			if(kb==ammoPrev)
	//			{
	//				amt = -1;
	//			}
	//			else if(kb==ammoNext)
	//			{
	//				amt = 1;
	//			}
	//			((VehicleBase)mc.thePlayer.ridingEntity).ammoHelper.handleAmmoSelectInput(amt);
	//		}
	//	}
	//
	//	int inputUpdateTicks = 0;
	//
	//	public void handleAimAction(Keybind kb)
	//	{
	//		if(mc.thePlayer.ridingEntity instanceof VehicleBase)
	//		{
	//			VehicleBase vehicle = (VehicleBase)mc.thePlayer.ridingEntity;
	//			if(kb==pitchDown)
	//			{
	//				vehicle.firingHelper.handleAimKeyInput(-1, 0);
	//			}
	//			else if(kb==pitchUp)
	//			{
	//				vehicle.firingHelper.handleAimKeyInput(1, 0);
	//			}
	//			else if(kb==turretLeft)
	//			{
	//				vehicle.firingHelper.handleAimKeyInput(0, -1);
	//			}
	//			else if(kb==turretRight)
	//			{
	//				vehicle.firingHelper.handleAimKeyInput(0, 1);
	//			}
	//		}
	//	}
	//
	//	public void handleMouseAimUpdate()
	//	{
	//		inputUpdateTicks--;
	//		if(inputUpdateTicks>0)
	//		{
	//			return;
	//		}
	//		inputUpdateTicks = 5;
	//		MovingObjectPosition pos = getPlayerLookTargetClient(mc.thePlayer, 140, mc.thePlayer.ridingEntity);
	//		if(pos!=null)
	//		{
	//			((VehicleBase)mc.thePlayer.ridingEntity).firingHelper.handleAimInput(pos.hitVec);
	//		}
	//	}
	//
	//	public void handleFireAction()
	//	{
	//		if(mc.thePlayer!=null && mc.thePlayer.ridingEntity instanceof VehicleBase)
	//		{
	//			VehicleBase vehicle = (VehicleBase) mc.thePlayer.ridingEntity;
	//			if(vehicle.isAimable())
	//			{
	//				MovingObjectPosition pos = getPlayerLookTargetClient(mc.thePlayer, 140, mc.thePlayer.ridingEntity);
	//				if(pos!=null)
	//				{
	//					vehicle.firingHelper.handleFireInput(pos.hitVec);
	//				}
	//				else
	//				{
	//					vehicle.firingHelper.handleFireInput(null);
	//				}
	//			}
	//		}
	//	}
	//
	//	public MovingObjectPosition getPlayerLookTargetClient(EntityPlayer player, float range, Entity excludedEntity)
	//	{
	//
	//		/**
	//		 * Vec3 positionVector = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
	//		 Vec3 moveVector = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
	//		 MovingObjectPosition hitPosition = this.worldObj.rayTraceBlocks_do_do(positionVector, moveVector, false, true);
	//		 positionVector = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX, this.posY, this.posZ);
	//		 moveVector = this.worldObj.getWorldVec3Pool().getVecFromPool(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
	//		 */
	//		Vec3 playerPos = player.getPosition(0);
	//		Vec3 lookVector = player.getLook(0);
	//		Vec3 endVector = playerPos.addVector(lookVector.xCoord * range, lookVector.yCoord * range, lookVector.zCoord * range);
	//		MovingObjectPosition blockHit = player.worldObj.clip(playerPos, endVector);
	//
	//		/**
	//		 * reseat vectors, as they get messed with in the rayTrace...
	//		 */
	//		playerPos = player.getPosition(0);
	//		lookVector = player.getLook(0);
	//
	//		float var9 = 1.f;
	//
	//		float closestFound = 0.f;
	//		if(blockHit!=null)
	//		{
	//			closestFound = (float) blockHit.hitVec.distanceTo(playerPos);
	//		}
	//		List possibleHitEntities = this.mc.theWorld.getEntitiesWithinAABBExcludingEntity(this.mc.renderViewEntity, this.mc.renderViewEntity.boundingBox.addCoord(lookVector.xCoord * range, lookVector.yCoord * range, lookVector.zCoord * range).expand((double)var9, (double)var9, (double)var9));
	//		Iterator<Entity> it = possibleHitEntities.iterator();
	//		Entity hitEntity = null;
	//		Entity currentExaminingEntity = null;
	//		while(it.hasNext())
	//		{
	//			currentExaminingEntity = it.next();
	//			if(currentExaminingEntity == excludedEntity)
	//			{
	//				continue;
	//			}
	//			if(currentExaminingEntity.canBeCollidedWith())
	//			{
	//				float borderSize = currentExaminingEntity.getCollisionBorderSize();
	//				AxisAlignedBB entBB = currentExaminingEntity.boundingBox.expand((double)borderSize, (double)borderSize, (double)borderSize);
	//				MovingObjectPosition var17 = entBB.calculateIntercept(playerPos, endVector);
	//
	//				if (entBB.isVecInside(playerPos))
	//				{
	//					if (0.0D < closestFound || closestFound == 0.0D)
	//					{
	//						hitEntity = currentExaminingEntity;
	//						closestFound = 0.0f;
	//					}
	//				}
	//				else if (var17 != null)
	//				{
	//					double var18 = playerPos.distanceTo(var17.hitVec);
	//
	//					if (var18 < closestFound || closestFound == 0.0D)
	//					{
	//						hitEntity = currentExaminingEntity;
	//						closestFound = (float) var18;
	//					}
	//				}
	//			}
	//		}
	//		if(hitEntity!=null)
	//		{
	//			//    Config.logDebug("entity hit!!");
	//			blockHit = new MovingObjectPosition(hitEntity);
	//			blockHit.hitVec.yCoord += hitEntity.height * 0.65f;
	//		}
	//		return blockHit;
	//	}
	//

}
