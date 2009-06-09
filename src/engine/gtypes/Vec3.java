package engine.gtypes;

import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

public class Vec3 extends Vector3f {
	public Vec3() {

	}

	public Vec3(float x, float y, float z) {
		super(x, y, z);
	}
	
	public Vec3(Tuple3f vec) {
		super(vec);
	}

	public FloatBuffer getGL() {
		return FloatBuffer.wrap(getArr());
	}
	
	public float[] getArr() {
		return new float[] { x, y, z };	
	}
	
	@Override
	public Vec3 clone() {
		return new Vec3(this.x, this.y, this.z);
	}
	
	public void draw(GL gl, Vec3 to) {
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glBegin(GL.GL_LINES);
		gl.glVertex3fv(getGL());
		gl.glVertex3fv(to.getGL());
		gl.glEnd();
	}
}
