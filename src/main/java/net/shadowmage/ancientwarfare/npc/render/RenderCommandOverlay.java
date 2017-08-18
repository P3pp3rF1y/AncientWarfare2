package net.shadowmage.ancientwarfare.npc.render;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.RayTraceResult.MovingObjectType;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.shadowmage.ancientwarfare.core.util.RayTraceUtils;
import net.shadowmage.ancientwarfare.core.util.RenderTools;
import net.shadowmage.ancientwarfare.npc.item.ItemCommandBaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class RenderCommandOverlay {
    public static final RenderCommandOverlay INSTANCE = new RenderCommandOverlay();
    /**
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
        if (mc == null || mc.thePlayer == null || mc.currentScreen != null || mc.thePlayer.getCurrentEquippedItem() == null || !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton)) {
            return;
        }
        if (evt.phase == TickEvent.Phase.START) {
            target = RayTraceUtils.getPlayerTarget(mc.thePlayer, 120, 0);
            if (target != null) {
                if (target.typeOfHit == MovingObjectType.BLOCK) {
                    targetString = target.blockX + "," + target.blockY + "," + target.blockZ;
                } else if (target.typeOfHit == MovingObjectType.ENTITY) {
                    targetString = target.entityHit.getCommandSenderName();
                }
            }
            targetEntities = ItemCommandBaton.getCommandedEntities(mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
        } else if (!mc.gameSettings.showDebugInfo) {
            List<String> entityNames = new ArrayList<String>();
            for (Entity e : targetEntities) {
                entityNames.add(e.getCommandSenderName());
            }
            ScaledResolution sr = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
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
        if (mc == null || mc.currentScreen != null || mc.thePlayer == null) {
            return;
        }
        if (mc.thePlayer.getCurrentEquippedItem() == null || !(mc.thePlayer.getCurrentEquippedItem().getItem() instanceof ItemCommandBaton)) {
            return;
        }
        RayTraceResult pos = target;
        if (pos != null) {
            AxisAlignedBB bb = null;
            if (pos.typeOfHit == MovingObjectType.BLOCK) {
                bb = AxisAlignedBB.getBoundingBox(pos.blockX, pos.blockY, pos.blockZ, pos.blockX + 1.d, pos.blockY + 1.d, pos.blockZ + 1.d).expand(0.1d, 0.1d, 0.1d);
            } else if (pos.typeOfHit == MovingObjectType.ENTITY && pos.entityHit.boundingBox != null && pos.entityHit instanceof EntityLivingBase) {
                bb = pos.entityHit.boundingBox.copy();
                Entity e = pos.entityHit;
                float t = 1.f - evt.partialTicks;
                double dx = e.posX - e.lastTickPosX;
                double dy = e.posY - e.lastTickPosY;
                double dz = e.posZ - e.lastTickPosZ;
                bb.offset(t * -dx, t * -dy, t * -dz);
            }
            if (bb != null) {
                bb = RenderTools.adjustBBForPlayerPos(bb, mc.thePlayer, evt.partialTicks);
                RenderTools.drawOutlinedBoundingBox(bb, 1.f, 1.f, 1.f);
            }
        }
        AxisAlignedBB bb = null;
        for (Entity e : targetEntities) {
            if (e.boundingBox == null) {
                continue;
            }
            bb = e.boundingBox.copy();//TODO all this bb-rendering could potentially be moved to the entity itself
            float t = 1.f - evt.partialTicks;
            double dx = e.posX - e.lastTickPosX;
            double dy = e.posY - e.lastTickPosY;
            double dz = e.posZ - e.lastTickPosZ;
            bb.offset(t * -dx, t * -dy, t * -dz);
            bb = RenderTools.adjustBBForPlayerPos(bb, mc.thePlayer, evt.partialTicks);
            RenderTools.drawOutlinedBoundingBox(bb, 1.f, 0.f, 0.f);
        }
    }

}
