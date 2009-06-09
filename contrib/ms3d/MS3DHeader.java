package ms3d;

import java.io.DataInput;
import java.io.IOException;

/**
 * A Milshape 3D Header.
 *
 * @author Nikolaj Ougaard
 */
public class MS3DHeader {
    private static final String MAGIC_NUMBER = "MS3D000000";

    public final int version;

    public static MS3DHeader decodeMS3DHeader(DataInput input) throws IOException {
        String header = MS3DModel.decodeZeroTerminatedString(input, 10);
        if (!MAGIC_NUMBER.equals(header)) {
            throw new IOException();
        }

        int version = input.readInt();
        return new MS3DHeader(version);
    }

    private MS3DHeader(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }
}
