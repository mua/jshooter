package ms3d;

import java.io.DataInput;
import java.io.IOException;

/**
 * a Milshape 3D Triangle
 *
 * @author Nikolaj Ougaard
 */
public class MS3DTriangle {
    public int flags;                                   // SELECTED | SELECTED2 | HIDDEN
    public int[] vertexIndices = new int[3];
    public float[][] vertexNormals = new float[3][3];
    public float[] s = new float[3];
    public float[] t = new float[3];
    public int smoothingGroup;                          // 1 - 32
    public int groupIndex;

    public static MS3DTriangle decodeMS3DTriangle(DataInput input) throws IOException {
        MS3DTriangle t = new MS3DTriangle();
        t.flags = input.readUnsignedShort();
        t.vertexIndices[0] = input.readUnsignedShort();
        t.vertexIndices[1] = input.readUnsignedShort();
        t.vertexIndices[2] = input.readUnsignedShort();
        t.vertexNormals[0][0] = input.readFloat();
        t.vertexNormals[1][0] = input.readFloat();
        t.vertexNormals[2][0] = input.readFloat();
        t.vertexNormals[0][1] = input.readFloat();
        t.vertexNormals[1][1] = input.readFloat();
        t.vertexNormals[2][1] = input.readFloat();
        t.vertexNormals[0][2] = input.readFloat();
        t.vertexNormals[1][2] = input.readFloat();
        t.vertexNormals[2][2] = input.readFloat();
        t.s[0] = input.readFloat();
        t.s[1] = input.readFloat();
        t.s[2] = input.readFloat();
        t.t[0] = 1.0f - input.readFloat();
        t.t[1] = 1.0f - input.readFloat();
        t.t[2] = 1.0f - input.readFloat();
        t.smoothingGroup = input.readUnsignedByte();
        t.groupIndex = input.readUnsignedByte();
        return t;
    }
}
