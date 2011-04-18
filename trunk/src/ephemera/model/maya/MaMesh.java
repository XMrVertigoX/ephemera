package ephemera.model.maya;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.NormalGenerator;

/**
 * 
 * @author -LANmower-
 */
public class MaMesh extends MaNode {
    public static Map<String, MaMesh> meshes = new Hashtable<String, MaMesh>();
    public static String[] syntax = { "mesh" };

    public static MaNode createIfType(final String type, final String[] line)
            throws IOException {
        for (final String element : syntax)
            if (type.equals(element)) {
                final MaMesh mesh = new MaMesh(line);
                meshes.put(mesh.name, mesh);
                return mesh;
            }
        return null;
    }

    private TriMesh mesh;
    private List<int[]> faceEdgeIndices = new ArrayList<int[]>();
    private List<int[]> texIndices = new ArrayList<int[]>();

    private int indexSize = 0;
    private IntBuffer texIndexBuf;

    private IntBuffer vertIndices;
    private FloatBuffer texCoords;

    private FloatBuffer vertices;

    private int[] edgeIndices;
    private int totalFaces;

    Node parent;

    /** Creates a new instance of MaMesh */
    public MaMesh(final String[] line) {
        super(line);
        mesh = new TriMesh(name);
        for (int currentWord = 1; currentWord < line.length; currentWord++) {
            final String word = line[currentWord];
            if (word.equals("-parent") || word.equals("-p")) {
                parent = MaTransform.nodes.get(line[currentWord + 1]).getNode();
                parent.attachChild(mesh);
            }
        }
    }

    public void buildMesh() {
        vertIndices = BufferUtils.createIntBuffer(indexSize);
        texIndexBuf = BufferUtils.createIntBuffer(indexSize);
        vertIndices.rewind();
        vertIndices.rewind();
        System.out.println("Building index buffers");
        for (int face = 0; face < faceEdgeIndices.size(); face++) {
            final int length = faceEdgeIndices.get(face).length;
            final int[] verts = new int[3 + (length - 3) * 3];
            final int[] texIndices = new int[3 + (length - 3) * 3];
            for (int vert = 0; vert < length; vert++) {
                final int edgeNum = faceEdgeIndices.get(face)[vert];
                // first find out what vertex are we on by looking up the edge
                // index
                if (edgeNum < 0)
                    verts[vert] = edgeIndices[-edgeNum * 3 - 2];
                // use the second point on reversed edges
                else
                    verts[vert] = edgeIndices[edgeNum * 3];
                // use the first point on normal edges
                // edges only have 2 values, 3 is hard/soft info
                texIndices[vert] = this.texIndices.get(face)[vert];
            }
            splitAddPolygon(verts, vertIndices, length);
            splitAddPolygon(texIndices, texIndexBuf, length);
        }
        totalFaces = vertIndices.limit() / 3;
        mesh.setVertexBuffer(transferValues(vertices, vertIndices, texIndexBuf,
                3));
        mesh.setNormalBuffer(transferValues(computeNormals(), vertIndices,
                texIndexBuf, 3));
        mesh.setTextureCoords(new TexCoords(texCoords));
        mesh.setIndexBuffer(texIndexBuf);
        // generateNormals();
        mesh.updateWorldVectors();
        mesh.setModelBound(new BoundingBox());
        mesh.updateModelBound();
        mesh.setVertexCount(mesh.getVertexBuffer().limit() / 3);
        mesh.setTriangleQuantity(mesh.getIndexBuffer().limit() / 3);
    }

    public void cleanup() {
        mesh = null;
        faceEdgeIndices = null;
        texIndices = null;
        texCoords = null;
        vertices = null;
        vertIndices = null;
        edgeIndices = null;
    }

    private FloatBuffer computeNormals() {
        final Vector3f vector1 = new Vector3f();
        final Vector3f vector2 = new Vector3f();
        final Vector3f vector3 = new Vector3f();

        // Here we allocate all the memory we need to calculate the normals
        final Vector3f[] tempNormals = new Vector3f[totalFaces];
        final Vector3f[] normals = new Vector3f[vertIndices.limit()];

        // Go though all of the faces of this object
        for (int i = 0; i < totalFaces; i++) {
            BufferUtils.populateFromBuffer(vector1, vertices, vertIndices
                    .get(i * 3));
            BufferUtils.populateFromBuffer(vector2, vertices, vertIndices
                    .get(i * 3 + 1));
            BufferUtils.populateFromBuffer(vector3, vertices, vertIndices
                    .get(i * 3 + 2));

            vector1.subtractLocal(vector3);

            tempNormals[i] = vector1.cross(vector3.subtract(vector2))
                    .normalizeLocal();
        }

        final Vector3f sum = new Vector3f();
        int shared = 0;

        for (int i = 0; i < vertices.limit(); i++) {
            for (int j = 0; j < totalFaces; j++)
                if (vertIndices.get(j * 3) == i
                        || vertIndices.get(j * 3 + 1) == i
                        || vertIndices.get(j * 3 + 2) == i) {
                    sum.addLocal(tempNormals[j]);

                    shared++;
                }

            normals[i] = sum.divide((-shared)).normalizeLocal();
            sum.zero(); // Reset the sum
            shared = 0; // Reset the shared
        }

        return BufferUtils.createFloatBuffer(normals);
    }

    private void edges(final String[] line, final int start, final int stop)
            throws IOException {
        int offset = 0;
        while (!line[offset].endsWith("]"))
            ++offset;
        ++offset;
        for (int x = start * 3; x < stop * 3; x++)
            edgeIndices[x] = Integer.valueOf(line[offset + x - start * 3])
                    .intValue();
    }

    private int faceIndexData(final String[] line, int offset,
            final List<int[]> target) throws IOException {
        offset++;
        if (target == texIndices)
            offset++;
        final int length = Integer.valueOf(line[offset]).intValue();
        if (target == faceEdgeIndices)
            indexSize += 3 + (length - 3) * 3;
        offset++;
        final int[] indices = new int[length];
        for (int y = 0; y < length; y++)
            indices[y] = Integer.valueOf(line[offset + y]).intValue();
        target.add(indices);
        return offset + length;
    }

    private void faces(final String[] line, final int start, final int stop)
            throws IOException {
        int offset = 0;
        while (!line[offset].endsWith("]"))
            ++offset;
        if (start == 0)
            offset += 3;
        else
            ++offset;
        for (int x = start; x < stop; x++) {
            offset = faceIndexData(line, offset, faceEdgeIndices);
            offset = faceIndexData(line, offset, texIndices);
        }
        if (totalFaces == faceEdgeIndices.size())
            buildMesh();
    }

    public void generateNormals() {
        final NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(mesh, 1.5f);
    }

    public TriMesh getMesh() {
        return mesh;
    }

    @Override
    public void setAttribute(final String[] line) throws IOException {
        super.setAttribute(line);
        int size = -1;
        final int[] startStop = new int[2];
        for (int currentWord = 1; currentWord < line.length; currentWord++) {
            String word = line[currentWord];
            if (word.equals("-size") || word.equals("-s"))
                size = Integer.valueOf(line[currentWord + 1]);
            else if (word.startsWith(".uvSet") || word.startsWith(".uvst")) {
                if (size != -1)
                    texCoords = BufferUtils.createFloatBuffer(size * 2);
                if (word.endsWith("]")) {
                    word = word.split("\\[")[2];
                    final String[] nums = word.substring(0, word.length() - 1)
                            .split(":");
                    startStop[0] = Integer.valueOf(nums[0]);
                    if (nums.length > 1)
                        startStop[1] = Integer.valueOf(nums[1]);
                    uvData(line, startStop[0], startStop[1] + 1);
                    break;
                }
            } else if (word.startsWith(".vrts") || word.startsWith(".vt")) {
                if (size != -1)
                    vertices = BufferUtils.createFloatBuffer(size * 3);
                if (word.endsWith("]")) {
                    word = word.split("\\[")[1];
                    final String[] nums = word.substring(0, word.length() - 1)
                            .split(":");
                    startStop[0] = Integer.valueOf(nums[0]);
                    if (nums.length > 1)
                        startStop[1] = Integer.valueOf(nums[1]);
                    verts(line, startStop[0], startStop[1] + 1);
                    break;
                }
            } else if (word.startsWith(".edge") || word.startsWith(".ed")) {
                if (size != -1)
                    edgeIndices = new int[size * 3];
                if (word.endsWith("]")) {
                    word = word.split("\\[")[1];
                    final String[] nums = word.substring(0, word.length() - 1)
                            .split(":");
                    startStop[0] = Integer.valueOf(nums[0]);
                    if (nums.length > 1)
                        startStop[1] = Integer.valueOf(nums[1]);
                    edges(line, startStop[0], startStop[1] + 1);
                    break;
                }
            } else if (word.startsWith(".face") || word.startsWith(".fc")) {
                if (size != -1)
                    totalFaces = size;
                if (word.endsWith("]")) {
                    word = word.split("\\[")[1];
                    final String[] nums = word.substring(0, word.length() - 1)
                            .split(":");
                    startStop[0] = Integer.valueOf(nums[0]);
                    if (nums.length > 1)
                        startStop[1] = Integer.valueOf(nums[1]);
                    if (totalFaces == 0)
                        totalFaces = 1;
                    faces(line, startStop[0], startStop[1] + 1);
                    break;
                }
            }
        }
    }

    private void splitAddPolygon(final int[] polyIndices,
            final IntBuffer triangles, final int indexCount) {
        for (int i = 0; i < indexCount - 2; i++) {
            triangles.put(polyIndices[i]);
            triangles.put(polyIndices[i + 1]);
            triangles.put(polyIndices[indexCount - 1]);
        }
    }

    private FloatBuffer transferValues(final FloatBuffer values,
            final IntBuffer fromIndices, final IntBuffer toIndices,
            final int setLength) {
        final int size = texCoords.limit() / 2;
        final FloatBuffer retVal = BufferUtils.createFloatBuffer(size
                * setLength);
        for (int i = 0; i < toIndices.limit(); i++)
            for (int j = 0; j < setLength; j++)
                retVal.put(toIndices.get(i) * setLength + j, values
                        .get(fromIndices.get(i) * setLength + j));
        return retVal;
    }

    private void uvData(final String[] line, final int start, final int stop)
            throws IOException {
        int offset = 0;
        while (!line[offset].endsWith("]"))
            ++offset;
        if (start == 0)
            offset += 3;
        else
            ++offset;
        for (int x = start * 2; x < stop * 2; x++)
            texCoords.put(x, Float.valueOf(line[offset + x - start * 2])
                    .floatValue());
    }

    private void verts(final String[] line, final int start, final int stop)
            throws IOException {
        int offset = 0;
        while (!line[offset].endsWith("]"))
            ++offset;
        // if(start == 0) offset+=3;
        ++offset;
        for (int x = start * 3; x < stop * 3; x++)
            vertices.put(Float.valueOf(line[offset + x - start * 3])
                    .floatValue());
    }
}
