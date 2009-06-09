package ms3d;

import java.io.DataInput;
import java.io.IOException;

/**
 * A Milshape 3D Group
 *
 * @author Nikolaj Ougaard
 */
public class MS3DGroup {
    public int flags;                                   // SELECTED | HIDDEN
    public String name;                                 //
    public int numTriangles;                            //
    public int[] triangleIndices;                       // the groups group the triangles
    public int materialIndex;

    /**
     * Decodes a MS3DGroup.
     */
    public static MS3DGroup decodeMS3DGroup(DataInput input) throws IOException {
        MS3DGroup g = new MS3DGroup();
        g.flags = input.readUnsignedByte();
        g.name = MS3DModel.decodeZeroTerminatedString(input, 32);
        g.numTriangles = input.readUnsignedShort();
        g.triangleIndices = new int[g.numTriangles];
        for (int t = 0; t < g.numTriangles; t++) {
            g.triangleIndices[t] = input.readUnsignedShort();
        }
        g.materialIndex = input.readUnsignedByte();
        return g;
    }
}
