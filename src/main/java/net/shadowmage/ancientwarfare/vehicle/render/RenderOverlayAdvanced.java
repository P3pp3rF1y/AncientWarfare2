package net.shadowmage.ancientwarfare.vehicle.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.core.util.Trig;
import net.shadowmage.ancientwarfare.vehicle.config.AWVehicleStatics;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleBase;
import net.shadowmage.ancientwarfare.vehicle.entity.VehicleMovementType;
import net.shadowmage.ancientwarfare.vehicle.missiles.AmmoHwachaRocket;
import net.shadowmage.ancientwarfare.vehicle.registry.VehicleRegistry;
import org.lwjgl.opengl.GL11;

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
			renderRocketFlightPath(vehicle, player, partialTick);
		} else if (vehicle.ammoHelper.getCurrentAmmoType() != null && vehicle.ammoHelper.getCurrentAmmoType().isTorpedo()) {
			renderTorpedoPath(vehicle, player, partialTick);
		} else {
			renderNormalVehicleOverlay(vehicle, player, partialTick);
		}
	}

	private static void renderTorpedoPath(VehicleBase vehicle, EntityPlayer player, float partialTick) {
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
		double x2 = x1 - 20 * Trig.sinDegrees(vehicle.rotationYaw + partialTick * vehicle.moveHelper.getRotationSpeed());
		double y2 = y1;
		double z2 = z1 - 20 * Trig.cosDegrees(vehicle.rotationYaw + partialTick * vehicle.moveHelper.getRotationSpeed());
		GlStateManager.glLineWidth(3f);
		GlStateManager.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1 + 0.12d, z1);
		GL11.glVertex3d(x2, y2 + 0.12d, z2);
		GlStateManager.glEnd();

		GlStateManager.glLineWidth(4f);
		GlStateManager.color(1.f, 0.4f, 0.4f, 0.4f);
		GlStateManager.glBegin(GL11.GL_LINES);

		Vec3d offset = vehicle.getMissileOffset();
		x2 = x1 + offset.x;
		y2 = y1 + offset.y;
		z2 = z1 + offset.z;

		double gravity = 9.81d * 0.05d * 0.05d;
		double speed = vehicle.localLaunchPower * 0.05d;
		double angle = 90 - vehicle.localTurretPitch - vehicle.rotationPitch;
		double yaw = vehicle.localTurretRotation + partialTick * vehicle.moveHelper.getRotationSpeed();

		double vH = -Trig.sinDegrees((float) angle) * speed;
		double vY = Trig.cosDegrees((float) angle) * speed;
		double vX = Trig.sinDegrees((float) yaw) * vH;
		double vZ = Trig.cosDegrees((float) yaw) * vH;
		int rocketBurnTime = (int) (speed * 20.f * AmmoHwachaRocket.burnTimeFactor);

		float xAcc = (float) (vX / speed) * AmmoHwachaRocket.accelerationFactor;
		float yAcc = (float) (vY / speed) * AmmoHwachaRocket.accelerationFactor;
		float zAcc = (float) (vZ / speed) * AmmoHwachaRocket.accelerationFactor;
		vX = xAcc;
		vY = yAcc;
		vZ = zAcc;
		float dist = 0;

		while (dist < 100 * 100) {
			GL11.glVertex3d(x2, y2, z2);
			x2 += vX;
			z2 += vZ;
			y2 += vY;
			dist += x2 * x2 + z2 * z2 + y2 * y2;
			GL11.glVertex3d(x2, y2, z2);
		}
		GlStateManager.glEnd();

		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}

	private static void renderRocketFlightPath(VehicleBase vehicle, EntityPlayer player, float partialTick) {
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
		double x2 = x1 - 20 * Trig.sinDegrees(vehicle.rotationYaw + partialTick * vehicle.moveHelper.getRotationSpeed());
		double y2 = y1;
		double z2 = z1 - 20 * Trig.cosDegrees(vehicle.rotationYaw + partialTick * vehicle.moveHelper.getRotationSpeed());
		GlStateManager.glLineWidth(3f);
		GlStateManager.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1 + 0.12d, z1);
		GL11.glVertex3d(x2, y2 + 0.12d, z2);
		GlStateManager.glEnd();

		GlStateManager.glLineWidth(4f);
		GlStateManager.color(1.f, 0.4f, 0.4f, 0.4f);
		GlStateManager.glBegin(GL11.GL_LINES);

		Vec3d offset = vehicle.getMissileOffset();
		x2 = x1 + offset.x;
		y2 = y1 + offset.y;
		z2 = z1 + offset.z;

		double gravity = 9.81d * 0.05d * 0.05d;
		double speed = vehicle.localLaunchPower * 0.05d;
		double angle = 90 - vehicle.localTurretPitch - vehicle.rotationPitch;
		double yaw = vehicle.localTurretRotation + partialTick * vehicle.moveHelper.getRotationSpeed();

		double vH = -Trig.sinDegrees((float) angle) * speed;
		double vY = Trig.cosDegrees((float) angle) * speed;
		double vX = Trig.sinDegrees((float) yaw) * vH;
		double vZ = Trig.cosDegrees((float) yaw) * vH;
		int rocketBurnTime = (int) (speed * 20.f * AmmoHwachaRocket.burnTimeFactor);

		if (vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR1 || vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR2) {
			vY += vehicle.motionY;
			vX += vehicle.motionX;
			vZ += vehicle.motionZ;
			y1 = -player.posY;
		}

		float xAcc = (float) (vX / speed) * AmmoHwachaRocket.accelerationFactor;
		float yAcc = (float) (vY / speed) * AmmoHwachaRocket.accelerationFactor;
		float zAcc = (float) (vZ / speed) * AmmoHwachaRocket.accelerationFactor;
		vX = xAcc;
		vY = yAcc;
		vZ = zAcc;

		while (y2 >= y1) {
			GL11.glVertex3d(x2, y2, z2);
			x2 += vX;
			z2 += vZ;
			y2 += vY;
			if (rocketBurnTime > 0) {
				rocketBurnTime--;
				vX += xAcc;
				vY += yAcc;
				vZ += zAcc;
			} else {
				vY -= gravity;
			}
			GL11.glVertex3d(x2, y2, z2);
		}
		GlStateManager.glEnd();

		GlStateManager.popMatrix();
		GlStateManager.depthMask(true);
		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
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
		AxisAlignedBB bb = new AxisAlignedBB(blockHit.getX() - 1, blockHit.getY(), blockHit.getZ(), blockHit.getX() + 2, blockHit.getY() + 1, blockHit.getZ() + 1);
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

	private static void renderNormalVehicleOverlay(VehicleBase vehicle, EntityPlayer player, float partialTick) {
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
		double x2 = x1 - 20 * Trig.sinDegrees(vehicle.rotationYaw + partialTick * vehicle.moveHelper.getRotationSpeed());
		double y2 = y1;
		double z2 = z1 - 20 * Trig.cosDegrees(vehicle.rotationYaw + partialTick * vehicle.moveHelper.getRotationSpeed());
		GlStateManager.glLineWidth(3f);
		GlStateManager.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(x1, y1 + 0.12d, z1);
		GL11.glVertex3d(x2, y2 + 0.12d, z2);
		GlStateManager.glEnd();

		GlStateManager.glLineWidth(4f);
		GlStateManager.color(1.f, 0.4f, 0.4f, 0.4f);
		GlStateManager.glBegin(GL11.GL_LINES);

		Vec3d offset = vehicle.getMissileOffset();
		x2 = x1 + offset.x;
		y2 = y1 + offset.y;
		z2 = z1 + offset.z;

		double gravity = 9.81d * 0.05d * 0.05d;
		double speed = vehicle.localLaunchPower * 0.05d;
		double angle = 90 - vehicle.localTurretPitch - vehicle.rotationPitch;
		double yaw = vehicle.localTurretRotation + partialTick * vehicle.moveHelper.getRotationSpeed();

		double vH = -Trig.sinDegrees((float) angle) * speed;
		double vY = Trig.cosDegrees((float) angle) * speed;
		double vX = Trig.sinDegrees((float) yaw) * vH;
		double vZ = Trig.cosDegrees((float) yaw) * vH;

		if (vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR1 || vehicle.vehicleType.getMovementType() == VehicleMovementType.AIR2) {
			vY += vehicle.motionY;
			vX += vehicle.motionX;
			vZ += vehicle.motionZ;
			y1 = -player.posY;
		}
		while (y2 >= y1) {
			GL11.glVertex3d(x2, y2, z2);
			x2 += vX;
			z2 += vZ;
			y2 += vY;
			vY -= gravity;
			GL11.glVertex3d(x2, y2, z2);
		}
		GlStateManager.glEnd();

		GlStateManager.popMatrix();

		GlStateManager.depthMask(true);

		GlStateManager.disableBlend();
		GlStateManager.enableTexture2D();
	}
}
