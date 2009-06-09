package cameras;

import engine.gtypes.Mat4;
import engine.gtypes.Plane3;
import engine.gtypes.Vec3;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.media.opengl.GL;
import javax.vecmath.Matrix3f;
import javax.vecmath.Vector3f;

import components.Viewport;

public class Camera implements MouseMotionListener, MouseListener,
		MouseWheelListener {
	public Viewport viewport;
	public Mat4 viewMatrix;
	public Mat4 transform = new Mat4();
	public Mat4 projMatrix;
	float fovy;
	public Frustum frustum = new Frustum();
	private boolean orthogonal;

	int oX, oY;

	public class Frustum {
		Plane3 planes[] = new Plane3[6];
		Plane3 near, far, bottom, top, left, right;
		Mat4 m = new Mat4();
		float[][] m_Frustum = new float[6][4];;

		public Frustum() {
			near = planes[0] = new Plane3();
			far = planes[1] = new Plane3();
			bottom = planes[2] = new Plane3();
			top = planes[3] = new Plane3();
			left = planes[4] = new Plane3();
			right = planes[5] = new Plane3();
		}

		public boolean isInside(Vector3f point) {
			for (int i = 0; i < 6; i++) {
				if (planes[i].distance(point) < 0) {
					System.out.println(i);
					return false;
				}
			}
			return true;
		}

		public void update() {
			m.mul(viewMatrix, projMatrix);
			near
					.set(m.m20 + m.m30, m.m21 + m.m31, m.m22 + m.m32, m.m23
							+ m.m33);
			far.set(-m.m20 + m.m30, -m.m21 + m.m31, -m.m22 + m.m32, -m.m23
					+ m.m33);
			bottom.set(m.m10 + m.m30, m.m11 + m.m31, m.m12 + m.m32, m.m13
					+ m.m33);
			top.set(-m.m10 + m.m30, -m.m11 + m.m31, -m.m12 + m.m32, -m.m13
					+ m.m33);
			left
					.set(m.m00 + m.m30, m.m01 + m.m31, m.m02 + m.m32, m.m03
							+ m.m33);
			right.set(-m.m00 + m.m30, -m.m01 + m.m31, -m.m03 + m.m33, -m.m03
					+ m.m33);
		}

		// http://www.markmorley.com/opengl/frustumculling.html
		private void updateFrustum() {

			float clip[] = new float[16], proj[], modl[], t;

			modl = viewMatrix.getGL().array();
			proj = projMatrix.getGL().array();

			/* Combine the two matrices (multiply projection by modelview) */
			clip[0] = modl[0] * proj[0] + modl[1] * proj[4] + modl[2] * proj[8]
					+ modl[3] * proj[12];
			clip[1] = modl[0] * proj[1] + modl[1] * proj[5] + modl[2] * proj[9]
					+ modl[3] * proj[13];
			clip[2] = modl[0] * proj[2] + modl[1] * proj[6] + modl[2]
					* proj[10] + modl[3] * proj[14];
			clip[3] = modl[0] * proj[3] + modl[1] * proj[7] + modl[2]
					* proj[11] + modl[3] * proj[15];

			clip[4] = modl[4] * proj[0] + modl[5] * proj[4] + modl[6] * proj[8]
					+ modl[7] * proj[12];
			clip[5] = modl[4] * proj[1] + modl[5] * proj[5] + modl[6] * proj[9]
					+ modl[7] * proj[13];
			clip[6] = modl[4] * proj[2] + modl[5] * proj[6] + modl[6]
					* proj[10] + modl[7] * proj[14];
			clip[7] = modl[4] * proj[3] + modl[5] * proj[7] + modl[6]
					* proj[11] + modl[7] * proj[15];

			clip[8] = modl[8] * proj[0] + modl[9] * proj[4] + modl[10]
					* proj[8] + modl[11] * proj[12];
			clip[9] = modl[8] * proj[1] + modl[9] * proj[5] + modl[10]
					* proj[9] + modl[11] * proj[13];
			clip[10] = modl[8] * proj[2] + modl[9] * proj[6] + modl[10]
					* proj[10] + modl[11] * proj[14];
			clip[11] = modl[8] * proj[3] + modl[9] * proj[7] + modl[10]
					* proj[11] + modl[11] * proj[15];

			clip[12] = modl[12] * proj[0] + modl[13] * proj[4] + modl[14]
					* proj[8] + modl[15] * proj[12];
			clip[13] = modl[12] * proj[1] + modl[13] * proj[5] + modl[14]
					* proj[9] + modl[15] * proj[13];
			clip[14] = modl[12] * proj[2] + modl[13] * proj[6] + modl[14]
					* proj[10] + modl[15] * proj[14];
			clip[15] = modl[12] * proj[3] + modl[13] * proj[7] + modl[14]
					* proj[11] + modl[15] * proj[15];

			/* Extract the numbers for the RIGHT plane */
			m_Frustum[0][0] = clip[3] - clip[0];
			m_Frustum[0][1] = clip[7] - clip[4];
			m_Frustum[0][2] = clip[11] - clip[8];
			m_Frustum[0][3] = clip[15] - clip[12];

			/* Normalize the result */
			t = (float) (Math.sqrt(m_Frustum[0][0] * m_Frustum[0][0]
					+ m_Frustum[0][1] * m_Frustum[0][1] + m_Frustum[0][2]
					* m_Frustum[0][2]));
			m_Frustum[0][0] /= t;
			m_Frustum[0][1] /= t;
			m_Frustum[0][2] /= t;
			m_Frustum[0][3] /= t;

			/* Extract the numbers for the LEFT plane */
			m_Frustum[1][0] = clip[3] + clip[0];
			m_Frustum[1][1] = clip[7] + clip[4];
			m_Frustum[1][2] = clip[11] + clip[8];
			m_Frustum[1][3] = clip[15] + clip[12];

			/* Normalize the result */
			t = (float) (Math.sqrt(m_Frustum[1][0] * m_Frustum[1][0]
					+ m_Frustum[1][1] * m_Frustum[1][1] + m_Frustum[1][2]
					* m_Frustum[1][2]));
			m_Frustum[1][0] /= t;
			m_Frustum[1][1] /= t;
			m_Frustum[1][2] /= t;
			m_Frustum[1][3] /= t;

			/* Extract the BOTTOM plane */
			m_Frustum[2][0] = clip[3] + clip[1];
			m_Frustum[2][1] = clip[7] + clip[5];
			m_Frustum[2][2] = clip[11] + clip[9];
			m_Frustum[2][3] = clip[15] + clip[13];

			/* Normalize the result */
			t = (float) (Math.sqrt(m_Frustum[2][0] * m_Frustum[2][0]
					+ m_Frustum[2][1] * m_Frustum[2][1] + m_Frustum[2][2]
					* m_Frustum[2][2]));
			m_Frustum[2][0] /= t;
			m_Frustum[2][1] /= t;
			m_Frustum[2][2] /= t;
			m_Frustum[2][3] /= t;

			/* Extract the TOP plane */
			m_Frustum[3][0] = clip[3] - clip[1];
			m_Frustum[3][1] = clip[7] - clip[5];
			m_Frustum[3][2] = clip[11] - clip[9];
			m_Frustum[3][3] = clip[15] - clip[13];

			/* Normalize the result */
			t = (float) (Math.sqrt(m_Frustum[3][0] * m_Frustum[3][0]
					+ m_Frustum[3][1] * m_Frustum[3][1] + m_Frustum[3][2]
					* m_Frustum[3][2]));
			m_Frustum[3][0] /= t;
			m_Frustum[3][1] /= t;
			m_Frustum[3][2] /= t;
			m_Frustum[3][3] /= t;

			/* Extract the FAR plane */
			m_Frustum[4][0] = clip[3] - clip[2];
			m_Frustum[4][1] = clip[7] - clip[6];
			m_Frustum[4][2] = clip[11] - clip[10];
			m_Frustum[4][3] = clip[15] - clip[14];

			/* Normalize the result */
			t = (float) (Math.sqrt(m_Frustum[4][0] * m_Frustum[4][0]
					+ m_Frustum[4][1] * m_Frustum[4][1] + m_Frustum[4][2]
					* m_Frustum[4][2]));
			m_Frustum[4][0] /= t;
			m_Frustum[4][1] /= t;
			m_Frustum[4][2] /= t;
			m_Frustum[4][3] /= t;

			/* Extract the NEAR plane */
			m_Frustum[5][0] = clip[3] + clip[2];
			m_Frustum[5][1] = clip[7] + clip[6];
			m_Frustum[5][2] = clip[11] + clip[10];
			m_Frustum[5][3] = clip[15] + clip[14];

			/* Normalize the result */
			t = (float) (Math.sqrt(m_Frustum[5][0] * m_Frustum[5][0]
					+ m_Frustum[5][1] * m_Frustum[5][1] + m_Frustum[5][2]
					* m_Frustum[5][2]));
			m_Frustum[5][0] /= t;
			m_Frustum[5][1] /= t;
			m_Frustum[5][2] /= t;
			m_Frustum[5][3] /= t;

			for (int i = 0; i < 6; i++) {
				planes[i].set(m_Frustum[i][0], m_Frustum[i][1],
						m_Frustum[i][2], m_Frustum[i][3]);
			}

		}
	}

	public Camera(Viewport vp) {
		this.viewport = vp;
		vp.addMouseListener(this);
		vp.addMouseMotionListener(this);
		vp.addMouseWheelListener(this);
		viewMatrix = new Mat4();
		viewMatrix.setIdentity();
		projMatrix = new Mat4();
		fovy = 45;
		update_projection_matrix();
		// setOrthogonal(true);
	}

	public void apply(GL gl) {
		gl.glViewport(0, 0, viewport.getWidth(), viewport.getHeight());
		gl.glMatrixMode(GL.GL_PROJECTION);
		gl.glLoadMatrixf(projMatrix.getGL());
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glMultMatrixf(viewMatrix.getGL());
	}

	public void update_view_matrix() {
		viewport.repaint();
		transform.set(viewMatrix);
		transform.invert();
		frustum.updateFrustum();
	}

	public void update_projection_matrix() {
		if (!orthogonal) {
			float aspect = ((float) viewport.getWidth()) / viewport.getHeight();
			projMatrix.perspective(fovy, aspect, 1.0f, 10000.0f);
		} else
			projMatrix.orthogonal(0, viewport.getWidth() / 30.0f, 0, viewport
					.getHeight() / 30.0f, 0, 10000);
	}

	public Mat4 getInverseViewMatrix() {
		Mat4 ret = viewMatrix.clone();
		ret.invert();
		return ret;
	}

	public void onMouseDragged(MouseEvent e, int dx, int dy) {
	}

	public Vec3 pan(float dx, float dy) {
		Matrix3f mat = new Matrix3f();
		Vec3 dis = new Vec3(dx, dy, 0);
		viewMatrix.get(mat);
		mat.transpose();
		mat.transform(dis);
		return dis;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int dx, dy;
		dx = e.getX() - oX;
		dy = e.getY() - oY;
		oX += dx;
		oY += dy;
		onMouseDragged(e, dx, dy);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		oX = e.getX();
		oY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

	}

	public void setOrthogonal(boolean orthogonal) {
		this.orthogonal = orthogonal;
		update_projection_matrix();
	}

	public boolean isOrthogonal() {
		return orthogonal;
	}
}