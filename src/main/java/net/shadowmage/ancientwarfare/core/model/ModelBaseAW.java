/**
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

import net.shadowmage.ancientwarfare.core.util.StringTools;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ModelBaseAW {

    int textureWidth = 256;
    int textureHeight = 256;

    HashMap<String, ModelPiece> pieces = new HashMap<String, ModelPiece>();
    private List<ModelPiece> basePieces = new ArrayList<ModelPiece>();

    protected int iterationNum;

    public void renderModel() {
        for (ModelPiece piece : this.getBasePieces()) {
            piece.render(textureWidth, textureHeight);
        }
    }

    public void renderForSelection() {
        iterationNum = 0;
        for (ModelPiece piece : this.getBasePieces()) {
            piece.renderForSelection(textureWidth, textureHeight, this);
        }
    }

    public void renderForEditor(ModelPiece selectedPiece, Primitive selectedPrimitive, List<ModelPiece> selectedPieceParents) {
        for (ModelPiece piece2 : this.getBasePieces()) {
            piece2.renderForEditor(selectedPiece, selectedPrimitive, selectedPieceParents, textureWidth, textureHeight);
        }
        GL11.glColor4f(1.f, 1.f, 1.f, 1.f);
    }

    public void setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
        this.recompilePrimitives();
    }

    public void parseFromLines(List<String> lines) {
        String[] bits;
        for (String line : lines) {
            if (line.toLowerCase().startsWith("#")) {
                continue;
            } else if (line.toLowerCase().startsWith("texturesize=")) {
                bits = line.split("=")[1].split(",");
                textureWidth = StringTools.safeParseInt(bits[0]);
                textureHeight = StringTools.safeParseInt(bits[1]);
            } else if (line.startsWith("part=")) {
                ModelPiece piece = new ModelPiece(this, line.split("=")[1]);
                addPiece(piece);
            } else if (line.startsWith("box=")) {
                bits = line.split("=")[1].split(",");
                //parse old-style x,y,z, w,h,l
                String parentName = bits[0];
                ModelPiece piece = getPiece(parentName);
                if (piece == null) {
                    throw new IllegalArgumentException("could not construct model, improper piece reference for: " + parentName);
                }
                PrimitiveBox box = new PrimitiveBox(piece);
                box.readFromLine(bits);
                piece.addPrimitive(box);
            } else if (line.toLowerCase().startsWith("quad")) {
                bits = line.split("=")[1].split(",");
                //parse old-style x,y,z, w,h,l
                String parentName = bits[0];
                ModelPiece piece = getPiece(parentName);
                if (piece == null) {
                    throw new IllegalArgumentException("could not construct model, improper piece reference for: " + parentName);
                }
                PrimitiveQuad box = new PrimitiveQuad(piece);
                box.readFromLine(bits);
                piece.addPrimitive(box);
            } else if (line.toLowerCase().startsWith("triangle")) {
                bits = line.split("=")[1].split(",");
                //parse old-style x,y,z, w,h,l
                String parentName = bits[0];
                ModelPiece piece = getPiece(parentName);
                if (piece == null) {
                    throw new IllegalArgumentException("could not construct model, improper piece reference for: " + parentName);
                }
                PrimitiveTriangle box = new PrimitiveTriangle(piece);
                box.readFromLine(bits);
                piece.addPrimitive(box);
            }
        }
    }

    public List<String> getModelLines() {
        ArrayList<String> lines = new ArrayList<String>();
        lines.add("textureSize=" + textureWidth + "," + textureHeight);
        for (ModelPiece piece : this.basePieces) {
            piece.addPieceLines(lines);
        }
        return lines;
    }

    public void addPiece(ModelPiece piece) {
        pieces.put(piece.getName(), piece);
        if (piece.getParent() == null) {
            basePieces.add(piece);
        }
    }

    public void getPieces(List<ModelPiece> input) {
        for (ModelPiece piece : this.basePieces) {
            piece.getPieces(input);
        }
    }

    public void setPieceRotation(String name, float x, float y, float z) {
        ModelPiece piece = this.getPiece(name);
        if (piece == null) {
            return;
        }
        piece.setRotation(x, y, z);
    }

    public ModelPiece getPiece(String name) {
        return this.pieces.get(name);
    }

    public void removePiece(String name) {
        ModelPiece piece = this.getPiece(name);
        removePiece(piece);
    }

    public void removePiece(ModelPiece piece) {
        this.pieces.remove(piece.getName());
        this.basePieces.remove(piece);
    }

    public List<ModelPiece> getBasePieces() {
        return basePieces;
    }

    public Primitive getPrimitive(int num) {
        Primitive prim;
        this.iterationNum = 0;
        for (ModelPiece p : basePieces) {
            prim = p.getPickedPrimitive(num, this);
            if (prim != null) {
                return prim;
            }
        }
        return null;
    }

    public void recompilePrimitives() {
        for (ModelPiece p : this.basePieces) {
            p.recompilePiece();
        }
    }

    public int textureWidth() {
        return textureWidth;
    }

    public int textureHeight() {
        return textureHeight;
    }

}
