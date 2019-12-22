package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoHwachaRocket;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleRegistry;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderOverlayAdvanced {

	@SubscribeEvent
	public void renderLast(RenderWorldLastEvent event) {
		EntityPlayer player = Minecraft.getMinecraft().player;
		if (AWVehicleStatics.renderAdvOverlay && player.getRidingEntity() instanceof VehicleBase && Minecraft.getMinecraft().currentScreen == null) {
			RenderOverlayAdvanced.renderAdvancedVehicleOverlay((VehicleBase) player.getRidingEntity(), player, event.getPartialTicks());
		}
	}

	private static void renderAdvancedVehicleOverlay(VehicleBase vehicle, EntityPlayer player, float partialTick) {
		if (vehicle.vehicleType == VehicleRegistry.BATTERING_RAM) {
			renderBatteringRamOverlay(vehicle, player, partialTick);
		} else if (vehicle.ammoHelper.getCurrentAmmoType() != null && vehicle.ammoHelper.getCurrentAmmoType().isRocket()) {
			renderOverlay(vehicle, player, partialTick,
					(rOffset, speed, accVec, gravity) -> drawRocketFlightPath(vehicle, player, rOffset, speed, accVec, gravity));
		} else {
			renderOverlay(vehicle, player, partialTick, (rOffset, speed, accVec, gravity) -> drawNormalTrajectory(vehicle, player, rOffset, gravity, accVec));
		}
	}

	private static void renderOverlay(VehicleBase vehicle, EntityPlayer player, float partialTick, IDynamicOverlayPartRenderer dynamicRenderer) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();
		GlStateManager.color(1, 1, 1, 0.6f);

		Vec3d renderOffset = new Vec3d(vehicle.posX - player.posX, vehicle.posY - player.posY, vehicle.posZ - player.posZ);

		drawStraightLine(vehicle, partialTick, renderOffset);

		drawDynamicPart(vehicle, partialTick, renderOffset, dynamicRenderer);

		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}

	private static void drawStraightLine(VehicleBase vehicle, float partialTick, Vec3d renderOffset) {
		double x2 = renderOffset.x - 20 * Trig.sinDegrees(vehicle.rotationYaw + partialTick * vehicle.moveHelper.getRotationSpeed());
		double z2 = renderOffset.z - 20 * Trig.cosDegrees(vehicle.rotationYaw + partialTick * vehicle.moveHelper.getRotationSpeed());
		GlStateManager.glLineWidth(3f);
		GlStateManager.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(renderOffset.x, renderOffset.y + 0.12d, renderOffset.z);
		GL11.glVertex3d(x2, renderOffset.y + 0.12d, z2);
		GlStateManager.glEnd();
	}

	private static void drawDynamicPart(VehicleBase vehicle, float partialTick, Vec3d renderOffset, IDynamicOverlayPartRenderer dynamicRenderer) {
		GlStateManager.glLineWidth(4f);
		GlStateManager.color(1.f, 0.4f, 0.4f, 0.4f);
		GlStateManager.glBegin(GL11.GL_LINES);

		double gravity = 9.81d * 0.05d * 0.05d;
		double speed = vehicle.localLaunchPower * 0.05d;
		double angle = 90 - vehicle.localTurretPitch - vehicle.rotationPitch;
		double yaw = vehicle.localTurretRotation + partialTick * vehicle.moveHelper.getRotationSpeed();

		double vH = -Trig.sinDegrees((float) angle) * speed;
		Vec3d accelerationVector = new Vec3d(Trig.sinDegrees((float) yaw) * vH, Trig.cosDegrees((float) angle) * speed, Trig.cosDegrees((float) yaw) * vH);

		dynamicRenderer.render(renderOffset, speed, accelerationVector, gravity);
		GlStateManager.glEnd();
	}

	private static void drawRocketFlightPath(VehicleBase vehicle, EntityPlayer player, Vec3d renderOffset, double speed, Vec3d accelerationVector,
			double gravity) {
		int rocketBurnTime = (int) (speed * 20.f * AmmoHwachaRocket.BURN_TIME_FACTOR);

		Vec3d offset = vehicle.getMissileOffset();
		double x2 = renderOffset.x + offset.x;
		double y2 = renderOffset.y + offset.y;
		double z2 = renderOffset.z + offset.z;

		double floorY = renderOffset.y;

		Vec3d adjustedAccelerationVector = accelerationVector;
		if (vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR1 || vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR2) {
			adjustedAccelerationVector = adjustedAccelerationVector.addVector(vehicle.motionX, vehicle.motionY, vehicle.motionZ);
			floorY = -player.posY;
		}

		float xAcc = (float) (adjustedAccelerationVector.x / speed) * AmmoHwachaRocket.ACCELERATION_FACTOR;
		float yAcc = (float) (adjustedAccelerationVector.y / speed) * AmmoHwachaRocket.ACCELERATION_FACTOR;
		float zAcc = (float) (adjustedAccelerationVector.z / speed) * AmmoHwachaRocket.ACCELERATION_FACTOR;
		adjustedAccelerationVector = new Vec3d(xAcc, yAcc, zAcc);

		while (y2 >= floorY) {
			GL11.glVertex3d(x2, y2, z2);
			x2 += adjustedAccelerationVector.x;
			z2 += adjustedAccelerationVector.z;
			y2 += adjustedAccelerationVector.y;
			if (rocketBurnTime > 0) {
				rocketBurnTime--;
				adjustedAccelerationVector = adjustedAccelerationVector.addVector(xAcc, yAcc, zAcc);
			} else {
				adjustedAccelerationVector = adjustedAccelerationVector.addVector(0, -gravity, 0);
			}
			GL11.glVertex3d(x2, y2, z2);
		}
	}

	private static void drawNormalTrajectory(VehicleBase vehicle, EntityPlayer player, Vec3d renderOffset, double gravity, Vec3d accelerationVector) {
		Vec3d offset = vehicle.getMissileOffset();
		double x2 = renderOffset.x + offset.x;
		double y2 = renderOffset.y + offset.y;
		double z2 = renderOffset.z + offset.z;

		double floorY = renderOffset.y;

		Vec3d adjustedAccelerationVector = accelerationVector;
		if (vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR1 || vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR2) {
			adjustedAccelerationVector = adjustedAccelerationVector.addVector(vehicle.motionX, vehicle.motionY, vehicle.motionZ);
			floorY = -player.posY;
		}

		while (y2 >= floorY) {
			GL11.glVertex3d(x2, y2, z2);
			x2 += adjustedAccelerationVector.x;
			z2 += adjustedAccelerationVector.z;
			y2 += adjustedAccelerationVector.y;
			adjustedAccelerationVector = adjustedAccelerationVector.addVector(0, -gravity, 0);
			GL11.glVertex3d(x2, y2, z2);
		}
	}

	private static void renderBatteringRamOverlay(VehicleBase vehicle, EntityPlayer player, float partialTick) {
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.disableTexture2D();
		GlStateManager.color(1, 1, 1, 0.6f);

		double x1 = vehicle.posX - player.posX;
		double y1 = vehicle.posY - player.posY;
		double z1 = vehicle.posZ - player.posZ;

		/*
		 * vectors for a straight line
		 */
		double x2 = x1 - 20 * Trig.sinDegrees(vehicle.rotationYaw);
		double z2 = z1 - 20 * Trig.cosDegrees(vehicle.rotationYaw);
		GlStateManager.glLineWidth(3f);
		GlStateManager.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1 + 0.12d, z1);
		GL11.glVertex3d(x2, y1 + 0.12d, z2);
		GlStateManager.glEnd();

		Vec3d offset = vehicle.getMissileOffset();
		float bx = (float) (vehicle.posX + offset.x);
		float by = (float) (vehicle.posY + offset.y);
		float bz = (float) (vehicle.posZ + offset.z);
		BlockPos blockHit = new BlockPos(bx, by, bz);
		AxisAlignedBB bb = new AxisAlignedBB(blockHit.getX() - 1, blockHit.getY(), blockHit.getZ(), blockHit.getX() + 2, blockHit.getY() + 1,
				blockHit.getZ() + 1);
		bb = adjustBBForPlayerPos(bb, player, partialTick);
		RenderTools.drawOutlinedBoundingBox(bb, 1.f, 0.2f, 0.2f);
		bb = new AxisAlignedBB(blockHit.getX(), blockHit.getY(), blockHit.getZ() - 1, blockHit.getX() + 1, blockHit.getY() + 1, blockHit.getZ() + 2);
		bb = adjustBBForPlayerPos(bb, player, partialTick);
		RenderTools.drawOutlinedBoundingBox(bb, 1.f, 0.2f, 0.2f);
		bb = new AxisAlignedBB(blockHit.getX(), blockHit.getY() - 1, blockHit.getZ(), blockHit.getX() + 1, blockHit.getY() + 2, blockHit.getZ() + 1);
		bb = adjustBBForPlayerPos(bb, player, partialTick);
		RenderTools.drawOutlinedBoundingBox(bb, 1.f, 0.2f, 0.2f);
		GlStateManager.popMatrix();

		GlStateManager.depthMask(true);

		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}

	private static AxisAlignedBB adjustBBForPlayerPos(AxisAlignedBB bb, EntityPlayer player, float partialTick) {
		double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
		double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
		double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;
		return bb.offset(-x, -y, -z);
	}

	private interface IDynamicOverlayPartRenderer {
		void render(Vec3d renderOffset, double speed, Vec3d accelerationVector, double gravity);
	}
}
