/*
 Copyright 2012 John Cummens (aka Shadowmage, Shadowmage4513)
 This software is distributed under the terms of the GNU General Public License.
 Please see COPYING for precise license information.

 This file is part of Ancient Warfare.

 Ancient Warfare is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Ancient Warfare is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Ancient Warfare.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.shadowmage.ancientwarfare.structure.render;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.render.gate.RenderGateBasic;
import net.shadowmage.ancientwarfare.structure.render.gate.RenderGateDouble;
import net.shadowmage.ancientwarfare.structure.render.gate.RenderGateRotatingBridge;
import net.shadowmage.ancientwarfare.structure.render.gate.RenderGateSingle;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

public final class RenderGateHelper extends Render<EntityGate> {

    private HashMap<Integer, Render> gateRenders = new HashMap<>();

    public RenderGateHelper(RenderManager renderManager) {
        super(renderManager);
        this.addGateRender(0, new RenderGateBasic(renderManager));
        this.addGateRender(1, new RenderGateBasic(renderManager));
        this.addGateRender(4, new RenderGateSingle(renderManager));
        this.addGateRender(5, new RenderGateSingle(renderManager));
        this.addGateRender(8, new RenderGateDouble(renderManager));
        this.addGateRender(9, new RenderGateDouble(renderManager));
        this.addGateRender(12, new RenderGateRotatingBridge(renderManager));
    }

    protected void addGateRender(int type, Render rend) {
        this.gateRenders.put(type, rend);
    }

    @Override
    public void doRender(EntityGate entity, double d0, double d1, double d2, float f, float f1) {
        GL11.glPushMatrix();
        if(renderManager.isDebugBoundingBox() && !entity.isInvisible()){
            double x = d0 - entity.lastTickPosX, y = d1 - entity.lastTickPosY, z = d2 - entity.lastTickPosZ;
// TODO dual bounding box implementaiton or regular block bounding boxes?
//            if(gate.edgePosition > 0 && entity.getBoundingBox() instanceof DualBoundingBox){
//                renderOffsetAABB(((DualBoundingBox) entity.getBoundingBox()).getTop(), x, y, z);
//                renderOffsetAABB(((DualBoundingBox) entity.getBoundingBox()).getMin(), x, y, z);
//                renderOffsetAABB(((DualBoundingBox) entity.getBoundingBox()).getMax(), x, y, z);
//            }else
                renderOffsetAABB(entity.getEntityBoundingBox(), x, y, z);

            GL11.glPopMatrix();
            return;
        }

        if (entity.hurtAnimationTicks > 0) {
            float percent = ((float) entity.hurtAnimationTicks / 20.f);
            GL11.glColor4f(1.f, 1.f - percent, 1.f - percent, 1.f);
        }
        this.bindEntityTexture(entity);
        GL11.glTranslated(d0, d1, d2);
        GL11.glRotatef(f, 0, 1, 0);
        GL11.glScalef(-1, -1, 1);
        this.gateRenders.get(entity.getGateType().getGlobalID()).doRender(entity, d0, d1, d2, f, f1);
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
        GL11.glPopMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGate entity) {
        return entity.getGateType().getTexture();
    }

}
