package net.shadowmage.ancientwarfare.core.model;

import com.google.common.collect.Lists;
import com.sun.javafx.geom.Vec2f;
import com.sun.javafx.geom.Vec3f;

import java.util.List;

public class OBJGroup {
    private String name;
    private List<Vec3f> vertices;
    private final List<Vec2f> textureVertices;
    private final List<Vec3f> normals;
    private final List<Face> faces;

    private OBJGroup(String name, List<Vec3f> vertices, List<Vec2f> textureVertices, List<Vec3f> normals, List<Face> faces) {

        this.name = name;
        this.vertices = vertices;
        this.textureVertices = textureVertices;
        this.normals = normals;
        this.faces = faces;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Vec3f> getVertices() {
        return vertices;
    }

    public List<Vec2f> getTextureVertices() {
        return textureVertices;
    }

    public List<Vec3f> getNormals() {
        return normals;
    }

    public List<Face> getFaces() {
        return faces;
    }

    public void rotateAndTranslateVertices(float x, float y, float z, float rx, float ry, float rz) {
        List<Vec3f> replace = Lists.newArrayList();

        for (Vec3f vert : vertices) {
            Vec3f rotatedVert = OBJHelper.rotatePoint(vert, new Vec3f(rx, ry, rz));

            replace.add(new Vec3f(x + rotatedVert.x, y + rotatedVert.y, z + rotatedVert.z));
        }

        vertices = replace;
    }


    public static class Builder {
        private List<Vec3f> vertices;
        private List<Vec2f> textureVertices;
        private List<Vec3f> normals;
        private List<Face> faces;
        private String name;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setVertices(List<Vec3f> vertices) {
            this.vertices = vertices;
            return this;
        }

        public Builder setTextureVertices(List<Vec2f> textureVertices) {
            this.textureVertices = textureVertices;
            return this;
        }

        public Builder setNormals(List<Vec3f> normals) {
            this.normals = normals;
            return this;
        }

        public Builder setFaces(List<Face> faces) {
            this.faces = faces;
            return this;
        }

        public OBJGroup build() {
            return new OBJGroup(name, vertices, textureVertices, normals, faces);
        }
    }

    public static class Face {
        int[] vertexIndexes;
        int[] textureVertexIndexes;
        int[] normalIndexes;

        public Face(int[] vertexIndexes, int[] textureVertexIndexes, int[] normalIndexes) {
            if (vertexIndexes.length != textureVertexIndexes.length || textureVertexIndexes.length != normalIndexes.length) {
                throw new IllegalArgumentException("index arrays must have the same length");
            }

            this.vertexIndexes = vertexIndexes;
            this.textureVertexIndexes = textureVertexIndexes;
            this.normalIndexes = normalIndexes;
        }
    }
}
