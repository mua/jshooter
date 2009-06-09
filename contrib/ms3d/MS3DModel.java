package ms3d;

import java.io.*;

import utils.LittleEndianDataInputStream;

/**
 * A Milkshape 3D Model.
 *
 * @author Nikolaj Ougaard
 */
public class MS3DModel {
    public MS3DHeader header;
    public MS3DVertex[] vertices;
    public MS3DTriangle[] triangles;
    public MS3DGroup[] groups;
    public MS3DMaterial[] materials;
    public MS3DJoint[] joints;

    public MS3DModel(MS3DHeader header, MS3DVertex[] vertices, MS3DTriangle[] triangles, MS3DGroup[] groups, MS3DMaterial[] materials, MS3DJoint[] joints) {
        this.header = header;
        this.vertices = vertices;
        this.triangles = triangles;
        this.groups = groups;
        this.materials = materials;
        this.joints = joints;
    }

    public static MS3DModel decodeMS3DModel(InputStream is) throws IOException {
        LittleEndianDataInputStream dataInputStream = new LittleEndianDataInputStream(is);
        MS3DHeader header = decodeMS3DHeader(dataInputStream);
        MS3DVertex[] vertices = decodeMS3DVertices(dataInputStream);
        MS3DTriangle[] triangles = decodeMS3DTriangles(dataInputStream);
        MS3DGroup[] groups = decodeMS3DGroups(dataInputStream);
        MS3DMaterial[] materials = decodeMaterials(dataInputStream);

        MS3DJoint[] joints = decodeJoints(dataInputStream);

        return new MS3DModel(header, vertices, triangles, groups, materials, joints);
    }

    private static MS3DJoint[] decodeJoints(DataInput input) throws IOException {
        int numJoints = input.readUnsignedShort();

        MS3DJoint[] joints = new MS3DJoint[numJoints];

        for (int jc = 0; jc < numJoints; jc++) {
            joints[jc] = MS3DJoint.decodeMS3DJoint(input);
        }
        return joints;
    }

    private static MS3DMaterial[] decodeMaterials(DataInput input) throws IOException {
        int numMaterials = input.readUnsignedShort();

        MS3DMaterial[] materials = new MS3DMaterial[numMaterials];

        for (int mc = 0; mc < numMaterials; mc++) {
            materials[mc] = MS3DMaterial.decodeMS3DMaterial(input);
        }
        return materials;
    }

    private static MS3DGroup[] decodeMS3DGroups(DataInput input) throws IOException {
        int numGroups = input.readUnsignedShort();

        MS3DGroup[] groups = new MS3DGroup[numGroups];

        for (int gc = 0; gc < numGroups; gc++) {
            groups[gc] = MS3DGroup.decodeMS3DGroup(input);
        }
        return groups;
    }

    private static MS3DTriangle[] decodeMS3DTriangles(DataInput input) throws IOException {
        int numTriangles = input.readUnsignedShort();

        MS3DTriangle[] triangles = new MS3DTriangle[numTriangles];

        for (int tc = 0; tc < numTriangles; tc++) {
            triangles[tc] = MS3DTriangle.decodeMS3DTriangle(input);
        }
        return triangles;
    }

    private static MS3DVertex[] decodeMS3DVertices(DataInput input) throws IOException {
        int numVertices = input.readUnsignedShort();

        MS3DVertex[] vertices = new MS3DVertex[numVertices];

        for (int vc = 0; vc < numVertices; vc++) {
            vertices[vc] = MS3DVertex.decodeMS3DVertex(input);
        }
        return vertices;
    }

    private static MS3DHeader decodeMS3DHeader(DataInput input) throws IOException {
        return MS3DHeader.decodeMS3DHeader(input);
    }

    static String decodeZeroTerminatedString(DataInput input, int maximumLength) throws IOException {
        boolean zeroEncountered = false;
        StringBuffer stringBuffer = new StringBuffer();
        for (int c = 0; c < maximumLength; c++) {
            int readByte = input.readUnsignedByte();
            if (!zeroEncountered && readByte != 0) {
                stringBuffer.append((char)readByte);
            } else {
                zeroEncountered = true;
            }
        }

        return stringBuffer.toString();
    }
}
