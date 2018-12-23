package net.shadowmage.ancientwarfare.npc.item;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.shadowmage.ancientwarfare.core.network.NetworkHandler;
import net.shadowmage.ancientwarfare.npc.network.PacketExtendedReachAttack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface IExtendedReachWeapon {
	float getReach();

	@SideOnly(Side.CLIENT)
	class MouseClickHandler {
		@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
		public void onMouseClick(MouseEvent event) {
			if (!(event.getButton() == 0 && event.isButtonstate())) {
				return;
			}
			EntityPlayer player = Minecraft.getMinecraft().player;
			if (player == null || !(player.getHeldItemMainhand().getItem() instanceof IExtendedReachWeapon)) {
				return;
			}
			IExtendedReachWeapon weapon = (IExtendedReachWeapon) player.getHeldItemMainhand().getItem();
			Optional<RayTraceResult> result = getMouseOverExtended(weapon.getReach());

			result.ifPresent(r -> {
				if (r.entityHit != null && r.entityHit.hurtResistantTime == 0 && r.entityHit != player) {
					NetworkHandler.sendToServer(new PacketExtendedReachAttack(r.entityHit.getEntityId()));
				}
			});
		}

		//Based on EntityRenderer.getMouseOver
		@SideOnly(Side.CLIENT)
		private Optional<RayTraceResult> getMouseOverExtended(float reach) {
			RayTraceResult ret = null;
			Minecraft mc = Minecraft.getMinecraft();
			Entity renderViewEntity = mc.getRenderViewEntity();

			if (renderViewEntity != null && mc.world != null) {
				mc.mcProfiler.startSection("pick");
				double d0 = reach;
				ret = renderViewEntity.rayTrace(d0, 0);
				Vec3d positionEyes = renderViewEntity.getPositionEyes(0);
				double calcDist = d0;

				if (ret != null) {
					calcDist = ret.hitVec.distanceTo(positionEyes);
				}

				Vec3d vec3d1 = renderViewEntity.getLook(1.0F);
				Vec3d vec3d2 = positionEyes.addVector(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
				List<Entity> list = mc.world.getEntitiesInAABBexcluding(renderViewEntity,
						renderViewEntity.getEntityBoundingBox().expand(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0).grow(1.0D, 1.0D, 1.0D),
						Predicates.and(EntitySelectors.NOT_SPECTATING, e -> e != null && e.canBeCollidedWith()));

				ret = getEntityHit(ret, renderViewEntity, positionEyes, calcDist, vec3d2, list);
			}
			return Optional.ofNullable(ret);
		}

		@Nullable
		private RayTraceResult getEntityHit(
				@Nullable RayTraceResult ret, Entity renderViewEntity, Vec3d positionEyes, double calcDist, Vec3d vec3d2, List<Entity> list) {
			double d = calcDist;
			Entity pointedEntity = null;
			for (Entity entity : list) {
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow((double) entity.getCollisionBorderSize());
				RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(positionEyes, vec3d2);

				if (axisalignedbb.contains(positionEyes)) {
					if (d >= 0.0D) {
						pointedEntity = entity;
						d = 0.0D;
					}
				} else if (raytraceresult != null) {
					double d1 = positionEyes.distanceTo(raytraceresult.hitVec);

					if (d1 < d || d == 0.0D) {
						if (entity.getLowestRidingEntity() == renderViewEntity.getLowestRidingEntity() && !entity.canRiderInteract()) {
							if (d == 0.0D) {
								pointedEntity = entity;
							}
						} else {
							pointedEntity = entity;
							d = d1;
						}
					}
				}
			}

			if (pointedEntity != null && (d < calcDist || ret == null)) {
				ret = new RayTraceResult(pointedEntity);
			}
			return ret;
		}
	}
}
