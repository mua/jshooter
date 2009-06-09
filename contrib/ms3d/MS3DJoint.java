package ms3d;

import java.io.DataInput;
import java.io.IOException;

/**
 * A Milshape 3D Joint
 *
 * @author Pepijn Van Eeckhoudt
 */
class MS3DJoint {
    public int flags;                              // SELECTED | DIRTY
    public String name;                           //
    public String parentName;                     //
    public float[] rotation;                        // local reference matrix
    public float[] position;

    public KeyFrameRotation[] keyFramesRot;      // local animation matrices
    public KeyFramePosition[] keyFramesTrans;  // local animation matrices

    public static MS3DJoint decodeMS3DJoint(DataInput input) throws IOException {
        int flags = input.readUnsignedByte();
        String name = MS3DModel.decodeZeroTerminatedString(input, 32);
        String parentName = MS3DModel.decodeZeroTerminatedString(input, 32);

        float[] rotation = new float[]{
                input.readFloat(),
                input.readFloat(),
                input.readFloat()
        };

        float[] position = new float[]{
                input.readFloat(),
                input.readFloat(),
                input.readFloat()
        };

        int numKeyFramesRot = input.readUnsignedShort();
        int numKeyFramesTrans = input.readUnsignedShort();

        KeyFrameRotation[] keyFrameRotations = new KeyFrameRotation[numKeyFramesRot];
        for (int i = 0; i < keyFrameRotations.length; i++) {
            float time = input.readFloat();
            float rotX = input.readFloat();
            float rotY = input.readFloat();
            float rotZ = input.readFloat();
            keyFrameRotations[i] = new KeyFrameRotation(time, rotX, rotY, rotZ);
        }

        KeyFramePosition[] keyFramePositions = new KeyFramePosition[numKeyFramesTrans];
        for (int i = 0; i < keyFramePositions.length; i++) {
            float time = input.readFloat();
            float x = input.readFloat();
            float y = input.readFloat();
            float z = input.readFloat();
            keyFramePositions[i] = new KeyFramePosition(time, x, y, z);
        }

        MS3DJoint joint = new MS3DJoint();
        joint.flags = flags;
        joint.name = name;
        joint.parentName = parentName;
        joint.position = position;
        joint.rotation = rotation;
        joint.keyFramesRot = keyFrameRotations;
        joint.keyFramesTrans = keyFramePositions;

        return joint;
    }

    public static class KeyFrameRotation {
        public float time; // time in seconds
        public float rotationX; // x, y, z angles
        public float rotationY; // x, y, z angles
        public float rotationZ; // x, y, z angles

        public KeyFrameRotation(float time, float rotationX, float rotationY, float rotationZ) {
            this.time = time;
            this.rotationX = rotationX;
            this.rotationY = rotationY;
            this.rotationZ = rotationZ;
        }
    }

    public static class KeyFramePosition {
        public float time; // time in seconds
        public float x; // local position x
        public float y; // local position y
        public float z; // local position z

        public KeyFramePosition(float time, float x, float y, float z) {
            this.time = time;
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
}
