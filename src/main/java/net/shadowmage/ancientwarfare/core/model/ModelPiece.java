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

import com.google.common.collect.Lists;
import com.sun.javafx.geom.Vec3f;
import net.minecraft.client.renderer.GlStateManager;
import net.shadowmage.ancientwarfare.core.util.StringTools;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * A single piece of a model.  A piece is a discrete static component of the model.  Pieces may be rotated and moved
 * relative to other pieces in the model (in contrast to boxes, which may not be altered).  All animation is done
 * by moving pieces relative to each-other and the model origin.
 * <p/>
 * Each piece has a box list for the boxes of that piece, as well as a children list for sub-pieces
 * (pieces which rotate/move relative to this piece, but may also need to rotate independently)
 * Each piece may have multiple boxes and multiple sub-pieces.
 *
 * @author Shadowmage
 */
public class ModelPiece {

    private String pieceName;
    private boolean visible = true;
    private float x, y, z;//manipulatable coordinates for this piece, relative to either model origin or parent-piece origin (if base piece or has parent)
    private float rx, ry, rz;//manipulatable rotation for this piece, relative to either model rotation or parent-piece rotation (if base piece or has parent)
    private Set<ModelPiece> children = new HashSet<>();//the children of this piece
    private List<Primitive> primitives = new ArrayList<>();//the list of boxes that make up this piece, really only used during first construction of display list
    private ModelPiece parent;

    boolean compiled = false;
    int displayList = -1;

    public ModelPiece(ModelBaseAW model, String line) {
        String[] bits = line.split(",");
        this.pieceName = bits[0];
        String parentName = bits[1];
        float x = StringTools.safeParseFloat(bits[2]);
        float y = StringTools.safeParseFloat(bits[3]);
        float z = StringTools.safeParseFloat(bits[4]);
        float rx = StringTools.safeParseFloat(bits[5]);
        float ry = StringTools.safeParseFloat(bits[6]);
        float rz = StringTools.safeParseFloat(bits[7]);
        ModelPiece parent = parentName.equals("null") ? null : model.getPiece(parentName);
        this.setPosition(x, y, z);
        this.setRotation(rx, ry, rz);
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void setVisible(boolean val) {
        this.visible = val;
    }

    public ModelPiece(String name, float x, float y, float z, float rx, float ry, float rz, ModelPiece parent) {
        this.pieceName = name;
        this.setPosition(x, y, z);
        this.setRotation(rx, ry, rz);
        if (parent != null) {
            parent.addChild(this);
        }
    }

    public void clearParent() {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
    }

    public ModelPiece setParent(ModelPiece parent) {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        if (parent != null) {
            parent.addChild(this);
        }
        return this;
    }

    public ModelPiece copy() {
        ModelPiece piece = new ModelPiece(pieceName + "_copy", x, y, z, rx, ry, rz, parent);
        for (Primitive primitive : this.primitives) {
            piece.addPrimitive(primitive.copy());
        }
        return piece;
    }

    public ModelPiece getParent() {
        return parent;
    }

    public boolean isBasePiece() {
        return getParent() == null;
    }

    public float x() {
        return x;
    }

    public float y() {
        return y;
    }

    public float z() {
        return z;
    }

    public float rx() {
        return rx;
    }

    public float ry() {
        return ry;
    }

    public float rz() {
        return rz;
    }

    public String getName() {
        return pieceName;
    }

    public Collection<ModelPiece> getChildren() {
        return children;
    }

    public void setRotation(float rx, float ry, float rz) {
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
    }

    public void recompilePiece() {
        this.compiled = false;
        for (ModelPiece p : children) {
            p.recompilePiece();
        }
    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setName(String name) {
        this.pieceName = name;
    }

    public void addPrimitive(Primitive primitive) {
        this.primitives.add(primitive);
        primitive.parent = this;
    }

    public void removePrimitive(Primitive primitive) {
        this.primitives.remove(primitive);
        primitive.parent = null;
    }

    public void addChild(ModelPiece piece) {
        this.children.add(piece);
        piece.parent = this;
    }

    public void removeChild(ModelPiece piece) {
        this.children.remove(piece);
        piece.parent = null;
    }

    public void render(float textureWidth, float textureHeight) {
        if (!visible) {
            return;
        }
        GlStateManager.pushMatrix();
        if (x != 0 || y != 0 || z != 0) {
            GlStateManager.translate(x, y, z);
        }
        if (rx != 0) {
            GlStateManager.rotate(rx, 1, 0, 0);
        }
        if (ry != 0) {
            GlStateManager.rotate(ry, 0, 1, 0);
        }
        if (rz != 0) {
            GlStateManager.rotate(rz, 0, 0, 1);
        }
        renderPrimitives(textureWidth, textureHeight);
        for (ModelPiece child : this.children) {
            child.render(textureWidth, textureHeight);
        }
        GlStateManager.popMatrix();

    }

    public void renderForEditor(ModelPiece piece, Primitive prim, List<ModelPiece> highlightedPieces, float tw, float th) {
        GlStateManager.pushMatrix();
        if (x != 0 || y != 0 || z != 0) {
            GlStateManager.translate(x, y, z);
        }
        if (rx != 0) {
            GlStateManager.rotate(rx, 1, 0, 0);
        }
        if (ry != 0) {
            GlStateManager.rotate(ry, 0, 1, 0);
        }
        if (rz != 0) {
            GlStateManager.rotate(rz, 0, 0, 1);
        }

        boolean selected = piece == this;
        boolean colored = selected || highlightedPieces.contains(this);
        if (selected) {
            GlStateManager.disableLighting();
            GlStateManager.disableTexture2D();
            GL11.glEnable(GL11.GL_POINT_SMOOTH);
            GlStateManager.color(1.0f, 0.f, 0.f, 1.f);
            GL11.glPointSize(5.f);
            GlStateManager.glBegin(GL11.GL_POINTS);
            GlStateManager.glVertex3f(0, 0, 0);
            GlStateManager.glEnd();
            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
        }
        if (colored) {
            GlStateManager.color(0.75f, 0.5f, 0.5f, 1.f);
        } else {
            GlStateManager.color(1.f, 1.f, 1.f, 1.f);
        }
        for (Primitive primitive : this.primitives) {
            if (primitive == prim) {
                GlStateManager.disableLighting();
                GlStateManager.disableTexture2D();
                GlStateManager.color(1.0f, 0.f, 0.f, 1.f);
                GlStateManager.glBegin(GL11.GL_POINTS);
                GlStateManager.glVertex3f(prim.x, prim.y, prim.z);
                GlStateManager.glEnd();
                GlStateManager.enableLighting();
                GlStateManager.enableTexture2D();
                GlStateManager.color(1.0f, 0.5f, 0.5f, 1.f);
            } else if (colored) {
                GlStateManager.color(0.75f, 0.5f, 0.5f, 1.f);
            } else {
                GlStateManager.color(1.f, 1.f, 1.f, 1.f);
            }
            primitive.render(tw, th);
        }
        for (ModelPiece child : this.children) {
            child.renderForEditor(piece, prim, highlightedPieces, tw, th);
        }
        GlStateManager.popMatrix();
    }

    public void renderForSelection(float tw, float th, ModelBaseAW model) {
        GlStateManager.pushMatrix();
        if (x != 0 || y != 0 || z != 0) {
            GlStateManager.translate(x, y, z);
        }
        if (rx != 0) {
            GlStateManager.rotate(rx, 1, 0, 0);
        }
        if (ry != 0) {
            GlStateManager.rotate(ry, 0, 1, 0);
        }
        if (rz != 0) {
            GlStateManager.rotate(rz, 0, 0, 1);
        }

        for (Primitive primitive : this.primitives) {
            byte r, g, b;

            r = (byte) ((model.iterationNum >> 14) & 0x7f);
            g = (byte) ((model.iterationNum >> 7) & 0x7f);
            b = (byte) ((model.iterationNum >> 0) & 0x7f);

//    AWLog.logDebug("rendering for selection: "+model.iterationNum+" :: "+r+","+g+","+b);    
            GL11.glColor3b(r, g, b);

            GlStateManager.pushMatrix();
            primitive.render(tw, th);
            GlStateManager.popMatrix();
            model.iterationNum++;
        }

        for (ModelPiece child : this.children) {
            child.renderForSelection(tw, th, model);
        }
        GlStateManager.popMatrix();
    }

    public void getPieces(List<ModelPiece> input) {
        input.add(this);
        for (ModelPiece piece : this.children) {
            piece.getPieces(input);
        }
    }

    public List<Primitive> getPrimitives() {
        return this.primitives;
    }

    public void addPieceLines(ArrayList<String> lines) {
        StringBuilder b = new StringBuilder("part=");
        b.append(this.pieceName).append(",");
        b.append(this.parent == null ? "null" : this.parent.getName()).append(",");
        b.append(x).append(",").append(y).append(",").append(z).append(",").append(rx).append(",").append(ry).append(",").append(rz);
        lines.add(b.toString());
        for (Primitive p : this.primitives) {
            p.addPrimitiveLines(lines);
        }
        for (ModelPiece p : this.children) {
            p.addPieceLines(lines);
        }
    }

    public Primitive getPickedPrimitive(int num, ModelBaseAW model) {
        for (Primitive p : primitives) {
            if (model.iterationNum == num) {
                return p;
            }
            model.iterationNum++;
        }
        Primitive p;
        for (ModelPiece mp : children) {
            p = mp.getPickedPrimitive(num, model);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    protected void renderPrimitives(float tw, float th) {
        if (!compiled) {
            compiled = true;
            if (displayList < 0) {
                displayList = GlStateManager.glGenLists(1);
            }
            GlStateManager.glNewList(displayList, GL11.GL_COMPILE);
            for (Primitive p : primitives) {
                p.render(tw, th);
            }
            GlStateManager.glEndList();
            GlStateManager.callList(displayList);
        } else {
            GlStateManager.callList(displayList);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (displayList >= 0) {
            GlStateManager.glDeleteLists(displayList, 1);
        }
        super.finalize();
    }

    public List<Vec3f> getVertices() {
        List<Vec3f> ret = Lists.newArrayList();

        for (Primitive p : this.primitives) {
            ret.addAll(rotateAndTranslateVertices(p.getVertices()));
        }
        for (ModelPiece p : this.children) {
            ret.addAll(rotateAndTranslateVertices(p.getVertices()));
        }

        return ret;
    }

    private List<Vec3f> rotateAndTranslateVertices(List<Vec3f> vertices) {
        List<Vec3f> ret = Lists.newArrayList();

        for(Vec3f vert : vertices) {
            Vec3f rotatedVert = OBJHelper.rotatePoint(vert, new Vec3f(rx, ry, rz));

            ret.add(new Vec3f(x + (isBasePiece() ? 0.5f : 0.0f) + rotatedVert.x, y + rotatedVert.y, z + (isBasePiece() ? 0.5f : 0.0f) + rotatedVert.z));
        }

        return ret;
    }
}
