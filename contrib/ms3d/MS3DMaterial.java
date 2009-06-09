package ms3d;

import java.io.DataInput;
import java.io.IOException;

/**
 * A Milshape 3D Material
 *
 * @author Nikolaj Ougaard
 */
public class MS3DMaterial {
    public String name;
    public float[] ambient = new float[4];
    public float[] diffuse = new float[4];
    public float[] specular = new float[4];
    public float[] emissive = new float[4];
    public float shininess;                             // 0.0f - 128.0f
    public float transparency;                          // 0.0f - 1.0f
    public int mode;                                    // 0, 1, 2 is unused now
    public String textureName;                          // texture.bmp
    public String alphamapName;                         // alpha.bmp

    public int textureId;                               //The only variable not from the spec. Used internally in JOGL to represent a texture

    /**
     * This method creates a new MS3DMaterial.
     * Note: Only textures of image type gif, jpg and bmp are supported
     */
    public static MS3DMaterial decodeMS3DMaterial(DataInput input) throws IOException {
        MS3DMaterial m = new MS3DMaterial();
        m.name = MS3DModel.decodeZeroTerminatedString(input, 32);
        m.ambient[0] = input.readFloat();
        m.ambient[1] = input.readFloat();
        m.ambient[2] = input.readFloat();
        m.ambient[3] = input.readFloat();
        m.diffuse[0] = input.readFloat();
        m.diffuse[1] = input.readFloat();
        m.diffuse[2] = input.readFloat();
        m.diffuse[3] = input.readFloat();
        m.specular[0] = input.readFloat();
        m.specular[1] = input.readFloat();
        m.specular[2] = input.readFloat();
        m.specular[3] = input.readFloat();
        m.emissive[0] = input.readFloat();
        m.emissive[1] = input.readFloat();
        m.emissive[2] = input.readFloat();
        m.emissive[3] = input.readFloat();
        m.shininess = input.readFloat();
        m.transparency = input.readFloat();
        m.mode = input.readUnsignedByte();
        m.textureName = MS3DModel.decodeZeroTerminatedString(input, 128).replace('\\', '/');
        m.alphamapName = MS3DModel.decodeZeroTerminatedString(input, 128).replace('\\', '/');
        return m;
    }
}
