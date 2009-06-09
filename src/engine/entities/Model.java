package engine.entities;

import javax.media.opengl.GL;

import cameras.Camera;
import engine.Engine;
import engine.Meshes.Mesh;

public class Model extends Object {
	public Mesh mesh;

	public Model(String fileName) {
		mesh = Engine.instance.meshes.getByFileName(fileName);
		if (mesh == null) {
			mesh = Engine.instance.meshes.loadMesh(fileName);
		}
		bbox.set(mesh.bbox);
	}

	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}

	@Override
	public void draw(GL gl, Camera camera) {
		if (mesh == null)
			return;
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glColor3f(1, 1, 1);
		mesh.draw(gl);
	}

	@Override
	public void setContext(Context context) {
		super.setContext(context);
		if (context != null) {
			context.tree.add(this);
		}		
	}
}
