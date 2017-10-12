/*
 Copyright 2012-2013 John Cummens (aka Shadowmage, Shadowmage4513)
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
package net.shadowmage.ancientwarfare.core.model;

import net.minecraft.client.renderer.GlStateManager;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class PrimitiveQuad extends Primitive {

    float x1, y1, x2, y2;//extends of bounding box in local space (post rotation) -- used for w/l/h for boxes

    public PrimitiveQuad(ModelPiece parent) {
        super(parent);
    }

    @Override
    protected void renderPrimitive(float tw, float th) {
        float px = 1.f / tw;
        float py = 1.f / th;
        float w = (x2 - x1) * 16.f;
        float l = 1.f;//TODO fix this for proper texture handling
        float h = (y2 - y1) * 16.f;
        float ty = this.ty();
        float tx = this.tx();

        float tx1, ty1, tx2, ty2;

//render the cube. only called a single time when building the display list for a piece
        if (rx != 0) {
            GlStateManager.rotate(rx, 1, 0, 0);
        }
        if (ry != 0) {
            GlStateManager.rotate(ry, 0, 1, 0);
        }
        if (rz != 0) {
            GlStateManager.rotate(rz, 0, 0, 1);
        }


        GlStateManager.glBegin(GL11.GL_QUADS);

        //front side
        tx1 = (tx + l) * px;
        ty1 = (th - (ty + l + h)) * py;
        tx2 = (tx + l + w) * px;
        ty2 = (th - (ty + l)) * py;

        GlStateManager.glNormal3f(0, 0, 1);
        GlStateManager.glTexCoord2f(tx1, ty1);
        GlStateManager.glVertex3f(x1, y1, 0.f);
        GlStateManager.glTexCoord2f(tx2, ty1);
        GlStateManager.glVertex3f(x2, y1, 0.f);
        GlStateManager.glTexCoord2f(tx2, ty2);
        GlStateManager.glVertex3f(x2, y2, 0.f);
        GlStateManager.glTexCoord2f(tx1, ty2);
        GlStateManager.glVertex3f(x1, y2, 0.f);

        GlStateManager.glEnd();
    }

    @Override
    public Primitive copy() {
        PrimitiveQuad box = new PrimitiveQuad(parent);
        box.setBounds(x1, y1, x2 - x1, y2 - y1);
        box.setOrigin(x, y, z);
        box.setRotation(rx, ry, rz);
        box.setTx(tx());
        box.setTy(ty());
        return box;
    }

    @Override
    public void addPrimitiveLines(ArrayList<String> lines) {
        StringBuilder b = new StringBuilder("quad=" + parent.getName() + ",");
        b.append(x).append(",").append(y).append(",").append(z).append(",").append(rx).append(",").append(ry).append(",").append(rz).append(",").append(tx()).append(",").append(ty()).append(",");
        b.append(x1).append(",").append(y1).append(",").append(x2).append(",").append(y2);
        lines.add(b.toString());
    }

    @Override
    public void readFromLine(String[] lineBits) {
//  String parent = lineBits[0];
        x = StringTools.safeParseFloat(lineBits[1]);
        y = StringTools.safeParseFloat(lineBits[2]);
        z = StringTools.safeParseFloat(lineBits[3]);
        rx = StringTools.safeParseFloat(lineBits[4]);
        ry = StringTools.safeParseFloat(lineBits[5]);
        rz = StringTools.safeParseFloat(lineBits[6]);
        setTx(StringTools.safeParseFloat(lineBits[7]));
        setTy(StringTools.safeParseFloat(lineBits[8]));
        x1 = StringTools.safeParseFloat(lineBits[9]);
        y1 = StringTools.safeParseFloat(lineBits[10]);
        x2 = StringTools.safeParseFloat(lineBits[11]);
        y2 = StringTools.safeParseFloat(lineBits[12]);
    }

    public void setBounds(float x1, float y1, float width, float height) {
        this.x1 = x1;
        this.x2 = x1 + width;
        this.y1 = y1;
        this.y2 = y1 + height;
    }

    public float x1() {
        return x1;
    }

    public float y1() {
        return y1;
    }

    public float width() {
        return x2 - x1;
    }

    public float height() {
        return y2 - y1;
    }

    @Override
    public void addUVMapToImage(BufferedImage image) {
        int w = (int) (x2 - x1) * 16;
        int h = (int) (y2 - y1) * 16;

        for (int x = (int) tx(); x < tx() + w && x < image.getWidth(); x++) {
            for (int y = (int) ty(); y < ty() + h && y < image.getHeight(); y++) {
                image.setRGB(x, y, 0xffff0000);
            }
        }
    }

}
