package net.shadowmage.ancientwarfare.core.model;

import com.sun.javafx.geom.Vec3f;

import java.util.List;

public class OBJGroup {
    private final String name;
    private final List<Vec3f> vertices;
    private final List<Vec3f> textureVertices;
    private final List<Vec3f> normals;
    private final List<Face> faces;

    private OBJGroup(String name, List<Vec3f> vertices, List<Vec3f> textureVertices, List<Vec3f> normals, List<Face> faces) {

        this.name = name;
        this.vertices = vertices;
        this.textureVertices = textureVertices;
        this.normals = normals;
        this.faces = faces;
    }

    public String getName() {
        return name;
    }

    public List<Vec3f> getVertices() {
        return vertices;
    }

    public List<Vec3f> getTextureVertices() {
        return textureVertices;
    }

    public List<Vec3f> getNormals() {
        return normals;
    }

    public List<Face> getFaces() {
        return faces;
    }


    public class Face {
        int vertexIndex;
        int textureVertexIndex;
        int normalIndex;

        public Face(int vertexIndex, int textureVertexIndex, int normalIndex) {
            this.vertexIndex = vertexIndex;
            this.textureVertexIndex = textureVertexIndex;
            this.normalIndex = normalIndex;
        }
    }
}
