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
package net.shadowmage.ancientwarfare.structure.render.gate;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;
import net.shadowmage.ancientwarfare.structure.model.ModelGateBridge;

public final class RenderGateRotatingBridge extends Render {

    private final ModelGateBridge model = new ModelGateBridge();

    public RenderGateRotatingBridge(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    public void doRender(Entity entity, double d0, double d1, double d2, float f, float f1) {
        GlStateManager.pushMatrix();
        EntityGate g = (EntityGate) entity;
        BlockPos min = g.pos1;
        BlockPos max = g.pos2;

        boolean wideOnXAxis = min.getX() != max.getX();

        float rx = wideOnXAxis ? g.edgePosition + g.openingSpeed * (1 - f1) : 0;
        float rz = wideOnXAxis ? 0 : g.edgePosition + g.openingSpeed * (1 - f1);
        boolean invert = g.gateOrientation == EnumFacing.SOUTH || g.gateOrientation == EnumFacing.EAST;
        if (invert) {
            rx *= -1;
            rz *= -1;
        }
//  GlStateManager.rotate(90, 0, 1, 0);
        GlStateManager.translate(0, -0.5f, 0);
        GlStateManager.rotate(rx, 1, 0, 0);
        GlStateManager.rotate(rz, 0, 0, 1);
        float width = wideOnXAxis ? max.getX() - min.getX() + 1 : max.getZ() - min.getZ() + 1;
        float height = max.getY() - min.getY() + 1;
        float xOffset = wideOnXAxis ? width * 0.5f - 0.5f : 0f;
        float zOffset = wideOnXAxis ? 0f : -width * 0.5f + 0.5f;

        float tx = wideOnXAxis ? 1 : 0;
        float ty = -1;
        float tz = wideOnXAxis ? 0 : 1;
        float axisRotation = wideOnXAxis ? 180 : 90;
        if (invert) {
            GlStateManager.rotate(180, 0, 1, 0);
        }
        GlStateManager.translate(-xOffset, 0, zOffset);
        for (int y = 0; y < height; y++) {
            GlStateManager.pushMatrix();
            for (int x = 0; x < width; x++) {
                model.setModelRotation(axisRotation);
                if (y == 0) {
                    model.renderGateBlock();
                }
                if (y == height - 1 && x > 0 && x < width - 1) {
                    model.renderTop();
                } else if (y == height - 1 && x == 0) {
                    model.renderCorner();
                } else if (y == height - 1 && x == width - 1) {
                    model.renderCorner2();
                } else if (x == 0 && y > 0) {
                    model.renderSide1();
                } else if (x == width - 1 && y > 0) {
                    model.renderSide2();
                }
                if (y > 0) {
                    GlStateManager.pushMatrix();
                    model.setModelRotation(axisRotation);
                    model.renderSolidWall();
                    GlStateManager.popMatrix();
                }
                GlStateManager.translate(tx, 0, tz);
            }
            GlStateManager.popMatrix();
            GlStateManager.translate(0, ty, 0);
        }
        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return ((EntityGate) entity).getGateType().getTexture();
    }
}
