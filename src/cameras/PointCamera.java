package cameras;

import engine.entities.Visible;
import engine.gtypes.Mat4;
import engine.gtypes.Vec3;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.SwingUtilities;
import javax.vecmath.Vector3f;

import components.Viewport;

public class PointCamera extends Camera {
	public Visible entity;
	
	public PointCamera(Viewport vp) {
		super(vp);
		update_view_matrix();
		//setOrthogonal(true);
	}

	protected float rotX = (float) Math.PI / -2.0f;
	protected float rotY = 0;
	protected float distance = 10;
	public Vec3 position = new Vec3();
	boolean initialized;

	@Override
	public void update_view_matrix() {
		Mat4 tmp = new Mat4();
		Vector3f eye = new Vector3f(0, 0, distance);
		viewMatrix = new Mat4();
		viewMatrix.rotate(rotX, rotY, 0);
		viewMatrix.setTranslation(position);
		tmp.setTranslation(eye);
		viewMatrix.mul(tmp);
		if (entity != null)
			viewMatrix.mul(entity.worldTransform, viewMatrix);		
		viewMatrix.invert();
		super.update_view_matrix();
	}

	public void mouseMoved(MouseEvent e) {
		// saySomething("Mouse moved", e);
	}

	@Override
	public void onMouseDragged(MouseEvent e, int dx, int dy) {
		if (!SwingUtilities.isRightMouseButton(e)) return;
		super.onMouseDragged(e, dx, dy);
		if (!e.isAltDown()) {
			rotX -= dy / 100.0f;
			rotY -= dx / 100.0f;
		} else {
			position.add(pan(-dx / 10.0f, dy / 10.0f));
		}
		update_view_matrix();
	}

	@Override
	public void update_projection_matrix() {
		if (isOrthogonal()) {
			float w = viewport.getWidth() / Math.abs(distance) * 3.0f / 2;
			float h = viewport.getHeight() / Math.abs(distance) * 3.0f / 2;
			projMatrix.orthogonal(-w, w, -h, h, 0, -100000);
		} else
			super.update_projection_matrix();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		super.mouseWheelMoved(e);
		distance += (e.getUnitsToScroll() / 100.0f)*distance;
		update_view_matrix();
		if (isOrthogonal())
			update_projection_matrix();
	}
}
