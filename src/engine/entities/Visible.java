package engine.entities;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import cameras.Camera;
import engine.gtypes.BBox;
import engine.gtypes.Mat4;
import engine.gtypes.Vec3;

public class Visible extends Entity {
	public Mat4 transform = Mat4.identity();
	public Mat4 worldTransform = Mat4.identity();
	public BBox bbox = new BBox();
	public List<TransformListener> transformListeners = new ArrayList<TransformListener>();

	public void updateWorldTransform() {
		worldTransform = getTransform().clone();
		if (parent instanceof Visible)
			worldTransform.mul(((Visible) parent).worldTransform,
					worldTransform);
		for (Entity entity : entities) {
			if (entity instanceof Visible)
				((Visible) entity).updateWorldTransform();
		}
		updateBBox();
		for (TransformListener t : transformListeners) {
			t.update();
		}
	}

	public void updateBBox() {
		bbox.setTransform(worldTransform);
	}

	public void render(GL gl, Camera camera) {
		gl.glPushMatrix();
		gl.glMultMatrixf(worldTransform.getGL());
		draw(gl, camera);
		gl.glPopMatrix();
		for (Entity e : entities) {
			if (e instanceof Visible)
				((Visible) e).render(gl, camera);
		}
	}

	public void draw(GL gl, Camera camera) {

	}

	public void setTransform(Mat4 transform) {
		this.transform.set(transform);
		updateWorldTransform();
	}

	public Mat4 getTransform() {
		return transform;
	}

	@Override
	public void setParent(Entity parent) {
		super.setParent(parent);
		updateWorldTransform();
	}

	public void movePolar(double angleX, double angleY, float radius) {
		Mat4 rotate = new Mat4(), translation = Mat4.identity(), transform = getTransform();
		rotate.rotate((float) angleX, (float) angleY, 0);
		translation.setTranslation(new Vec3(0, 0, radius));
		transform.mul(rotate);
		transform.mul(translation);
		setTransform(transform);
	}

	static public interface TransformListener {
		void update();
	}

	@Override
	public void setContext(Context context) {
		super.setContext(context);
	}
}
