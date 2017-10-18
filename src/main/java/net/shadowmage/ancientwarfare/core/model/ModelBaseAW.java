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

import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Tuple;
import net.shadowmage.ancientwarfare.core.config.AWCoreStatics;
import net.shadowmage.ancientwarfare.core.util.StringTools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ModelBaseAW {

    int textureWidth = 256;
    int textureHeight = 256;

    HashMap<String, ModelPiece> pieces = new HashMap<>();
    private List<ModelPiece> basePieces = new ArrayList<>();

    protected int iterationNum;

    public void renderModel() {
        for (ModelPiece piece : this.getBasePieces()) {
            piece.render(textureWidth, textureHeight);
        }
    }

    public void exportOBJ(String fileName) throws IOException {
        File dir = new File(AWCoreStatics.configPathForFiles + "model_export");

        File objExport = new File(dir, fileName);
        if(!objExport.exists()) {
            objExport.createNewFile();
        }
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(objExport);
            BufferedWriter writer = new BufferedWriter(fileWriter);

            int baseVertID = 0;
            int baseTextureVertID = 0;
            int baseNormalID = 0;

            for (ModelPiece piece : this.getBasePieces()) {
                for (OBJGroup group : piece.getOBJGroups("", ()-> new Tuple<>(textureHeight, textureWidth))) {
                    exportGroup(group, writer, baseVertID, baseTextureVertID, baseNormalID);

                    baseVertID += group.getVertices().size();
                    baseTextureVertID += group.getTextureVertices().size();
                    baseNormalID += group.getNormals().size();
                }
            }
            writer.close();
        }
        finally {
            if (fileWriter != null)
                fileWriter.close();
        }
    }

    private void exportGroup(OBJGroup group,BufferedWriter writer, int baseVertID, int baseTextureVertID, int baseNormalID) throws IOException {
        writer.write(String.format("g %s", group.getName()));
        writer.newLine();

        exportVertices(group, writer);
        exportTextureVertices(group, writer);
        exportNormals(group, writer);

        writer.write("s off");
        writer.newLine();

        exportFaces(group, writer, baseVertID, baseTextureVertID, baseNormalID);

    }

    private void exportFaces(OBJGroup group, BufferedWriter writer, int baseVertID, int baseTextureVertID, int baseNormalID) throws IOException {
        for (OBJGroup.Face face : group.getFaces()) {
            writer.write("f");
            for (int i = 0; i < face.normalIndexes.length; i++) {
                writer.write(String.format(" %d/%d/%d", face.vertexIndexes[i] + baseVertID, face.textureVertexIndexes[i] + baseTextureVertID, face.normalIndexes[i] + baseNormalID));
            }
            writer.newLine();
        }
    }

    private void exportNormals(OBJGroup group, BufferedWriter writer) throws IOException {
        for(Vec3f vert : group.getNormals()) {
            writer.write(String.format("vn %.6f %.6f %.6f", vert.x, vert.y, vert.z));
            writer.newLine();
        }
    }

    private void exportTextureVertices(OBJGroup group, BufferedWriter writer) throws IOException {
        for(Vec2f vert : group.getTextureVertices()) {
            writer.write(String.format("vt %.6f %.6f", vert.x, vert.y));
            writer.newLine();
        }
    }

    private void exportVertices(OBJGroup group, BufferedWriter writer) throws IOException {
        for(Vec3f vert : group.getVertices()) {
            writer.write(String.format("v %.6f %.6f %.6f", vert.x, vert.y, vert.z));
            writer.newLine();
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
        GlStateManager.color(1.f, 1.f, 1.f, 1.f);
    }

    public void setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
        this.recompilePrimitives();
    }

    public void parseFromLines(List<String> lines) {
        String[] bits;
        for (String line : lines) {
            if (line.toLowerCase(Locale.ENGLISH).startsWith("#")) {
                continue;
            } else if (line.toLowerCase(Locale.ENGLISH).startsWith("texturesize=")) {
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
            } else if (line.toLowerCase(Locale.ENGLISH).startsWith("quad")) {
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
            } else if (line.toLowerCase(Locale.ENGLISH).startsWith("triangle")) {
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
        ArrayList<String> lines = new ArrayList<>();
        lines.add("textureSize=" + textureWidth + "," + textureHeight);
        for (ModelPiece piece : this.basePieces) {
            piece.addPieceLines(lines);
        }
        return lines;
    }

    public void addPiece(ModelPiece piece) {
        pieces.put(piece.getName(), piece);
        if (piece.getParent() == null) {
            getBasePieces().add(piece);
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
