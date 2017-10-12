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
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.math.BlockPos;
import net.shadowmage.ancientwarfare.core.util.BlockTools;
import net.shadowmage.ancientwarfare.structure.entity.EntityGate;

public final class RenderGateSingle extends RenderGateBasic {
    public RenderGateSingle(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected BlockPos getMin(EntityGate gate) {
        return BlockTools.getMin(gate.pos1, gate.pos2);
    }

    @Override
    protected BlockPos getMax(EntityGate gate) {
        return BlockTools.getMax(gate.pos1, gate.pos2);
    }

    @Override
    protected void postRender(EntityGate gate, int x, float width, int y, float height, boolean wideOnXAxis, float axisRotation, float frame) {
        boolean opensReverse = gate.pos1.getX() > gate.pos2.getX() || gate.pos1.getZ() > gate.pos2.getZ();
        float wallTx = wideOnXAxis ? gate.edgePosition + gate.openingSpeed * (1 - frame) : 0;
        float wallTz = wideOnXAxis ? 0 : gate.edgePosition + gate.openingSpeed * (1 - frame);
        boolean render = false;
        if (opensReverse) {
            if ((wideOnXAxis && x - wallTx > -0.5f) || (!wideOnXAxis && x - wallTz > -0.5f)) {
                render = true;
            }
        } else {
            if ((wideOnXAxis && wallTx + x < gate.edgeMax - 0.5f) || (!wideOnXAxis && wallTz + x < gate.edgeMax - 0.5f)) {
                render = true;
            }
        }
        if (render) {
            if (opensReverse) {
                wallTx *= -1;
                wallTz *= -1;
            }
            GlStateManager.pushMatrix();
            GlStateManager.translate(wallTx, 0, wallTz);
            model.setModelRotation(axisRotation);
            if (gate.getGateType().getModelType() == 0) {
                model.renderSolidWall();
            } else {
                model.renderBars();
            }
            GlStateManager.popMatrix();
        }
    }
}
