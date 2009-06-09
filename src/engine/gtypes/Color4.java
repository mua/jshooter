package engine.gtypes;

import java.nio.FloatBuffer;

import javax.vecmath.Color4f;

public class Color4 extends Color4f {
	public FloatBuffer getGL() {
		return FloatBuffer.wrap(getArr());
	}
	
	public float[] getArr() {
		return new float[] { x, y, z, w };	
	}		
}
