package net.shadowmage.ancientwarfare.npc.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.shadowmage.ancientwarfare.core.util.EntityTools;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RenderCommandOverlay {
	public static final RenderCommandOverlay INSTANCE = new RenderCommandOverlay();
	/*
	 * TODO move this off into separate class for datas, as this is a _render_ class...
	 */
	private List<Entity> targetEntities;
	private RayTraceResult target;
	private String targetString;

	private RenderCommandOverlay() {
		targetEntities = Collections.emptyList();
	}

	public RayTraceResult getClientTarget() {
		return target;
	}

	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.player == null || mc.currentScreen != null) {
			return;
		}
		ItemStack commandBaton = EntityTools.getItemFromEitherHand(mc.player, ItemCommandBaton.class);
		if (commandBaton.isEmpty()) {
			return;
		}

		if (evt.phase == TickEvent.Phase.START) {
			target = RayTraceUtils.getPlayerTarget(mc.player, 120, 0);
			if (target != null) {
				if (target.typeOfHit == RayTraceResult.Type.BLOCK) {
					targetString = target.getBlockPos().getX() + "," + target.getBlockPos().getY() + "," + target.getBlockPos().getZ();
				} else if (target.typeOfHit == RayTraceResult.Type.ENTITY) {
					targetString = target.entityHit.getName();
				}
			}
			targetEntities = ItemCommandBaton.getCommandedEntities(mc.world, commandBaton);
		} else if (!mc.gameSettings.showDebugInfo) {
			List<String> entityNames = new ArrayList<>();
			for (Entity e : targetEntities) {
				entityNames.add(e.getName());
			}
			ScaledResolution sr = new ScaledResolution(mc);
			int x = sr.getScaledWidth() - 10;
			String header = I18n.format("guistrings.npc.target");
			mc.fontRenderer.drawStringWithShadow(header, x - mc.fontRenderer.getStringWidth(header), 0, 0xffffffff);
			if (targetString != null) {
				mc.fontRenderer.drawStringWithShadow(targetString, x - mc.fontRenderer.getStringWidth(targetString), 10, 0xffffffff);
			}
			mc.fontRenderer.drawStringWithShadow(I18n.format("guistrings.npc.commanded"), 10, 0, 0xffffffff);
			for (int i = 0; i < entityNames.size(); i++) {
				mc.fontRenderer.drawStringWithShadow(entityNames.get(i), 10, 10 + 10 * i, 0xffffffff);
			}
		}
	}

	@SubscribeEvent
	public void onRenderLast(RenderWorldLastEvent evt) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.currentScreen != null || mc.player == null) {
			return;
		}
		ItemStack commandBaton = EntityTools.getItemFromEitherHand(mc.player, ItemCommandBaton.class);
		if (commandBaton.isEmpty()) {
			return;
		}
		RayTraceResult pos = target;
		if (pos != null) {
			AxisAlignedBB bb = null;
			if (pos.typeOfHit == RayTraceResult.Type.BLOCK) {
				bb = new AxisAlignedBB(pos.getBlockPos(), pos.getBlockPos().add(1, 1, 1)).grow(0.1d, 0.1d, 0.1d);
			} else if (pos.typeOfHit == RayTraceResult.Type.ENTITY && pos.entityHit.getEntityBoundingBox() != null && pos.entityHit instanceof EntityLivingBase) {
				Entity e = pos.entityHit;
				float t = 1.f - evt.getPartialTicks();
				double dx = e.posX - e.lastTickPosX;
				double dy = e.posY - e.lastTickPosY;
				double dz = e.posZ - e.lastTickPosZ;
				bb = pos.entityHit.getEntityBoundingBox().offset(t * -dx, t * -dy, t * -dz);
			}
			if (bb != null) {
				bb = RenderTools.adjustBBForPlayerPos(bb, mc.player, evt.getPartialTicks());
				RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
			}
		}
		AxisAlignedBB bb = null;
		for (Entity e : targetEntities) {
			if (e.getEntityBoundingBox() == null) {
				continue;
			}
			float t = 1.f - evt.getPartialTicks();
			double dx = e.posX - e.lastTickPosX;
			double dy = e.posY - e.lastTickPosY;
			double dz = e.posZ - e.lastTickPosZ;
			//TODO all this bb-rendering could potentially be moved to the entity itself
			bb = e.getEntityBoundingBox().offset(t * -dx, t * -dy, t * -dz);
			bb = RenderTools.adjustBBForPlayerPos(bb, mc.player, evt.getPartialTicks());
			RenderTools.drawOutlinedBoundingBox(bb, 1.f, 0.f, 0.f);
		}
	}

}
