package engine.gtypes;

import javax.media.opengl.GL;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Tuple3f;

import engine.Meshes.Line;

public class BBox {
	public Vec3 min = new Vec3(), max = new Vec3();
	public Vec3 mMin = new Vec3(), mMax = new Vec3();
	boolean initialized = false;
	boolean updateNeeded = true;
	private Mat4 transform = Mat4.identity();
	Line[] lines;

	public BBox() {
	}

	public void set(BBox bbox) {
		min.set(bbox.min);
		max.set(bbox.max);
		mMin.set(bbox.mMin);
		mMax.set(bbox.mMax);
	}

	public boolean add(Vec3 v) {
		Vec3 nMin = new Vec3(min), nMax = new Vec3(max);
		if (initialized) {
			min.x = Math.min(v.x, min.x);
			min.y = Math.min(v.y, min.y);
			min.z = Math.min(v.z, min.z);
			max.x = Math.max(v.x, max.x);
			max.y = Math.max(v.y, max.y);
			max.z = Math.max(v.z, max.z);
		} else {
			min.set(v);
			max.set(v);
			initialized = true;
		}
		mMin.set(min);
		mMax.set(max);
		if (!(nMin.equals(min) && nMax.equals(max))) {
			updateNeeded = true;
			return true;
		}
		return false;
	}

	public boolean isInside(Tuple3f vec) {
		return (vec.x > min.x) && (vec.y > min.y) && (vec.z > min.z)
				&& (vec.x < max.x) && (vec.y < max.y) && (vec.z < max.z);
	}

	public boolean isInside(BBox bbox) {
		return isInside(bbox.min) && isInside(bbox.max);
	}

	public void add(BBox bbox) {
		add(bbox, null);
	}

	public boolean equals(BBox bbox) {
		return bbox.min.equals(min) && bbox.max.equals(max);
	}

	public boolean add(BBox bbox, Mat4 transform) {
		Point3f point = new Point3f();
		boolean updated = false;
		Vec3 vec = new Vec3();
		for (int i = 0; i < 2; i++) {
			point.set(bbox.get(i));
			if (transform != null)
				transform.transform(point);
			vec.set(point);
			updated = add(vec) || updated;
		}
		return updated;
	}

	public Vec3 get(int i) {
		return i == 0 ? min : max;
	}

	public Vec3 get(int i, boolean transformed) {
		if (!transformed)
			return get(i);
		Point3f ret = new Point3f(get(i));
		getTransform().transform(ret);
		return new Vec3(ret);
	}

	public Vec3[] getVertices() {
		Vec3[] vertices = new Vec3[8];
		Vec3[] mvs = new Vec3[] { min, max };
		for (int i = 0; i < 8; i++)
			vertices[i] = new Vec3(mvs[i & 1].x, mvs[(i & 2) >> 1].y,
					mvs[(i & 4) >> 2].z);
		return vertices;
	}

	public void updateShape() {
		Vec3[] v = getVertices();
		lines = new Line[] { new Line(v[0], v[1]), new Line(v[2], v[3]),
				new Line(v[1], v[3]), new Line(v[2], v[0]),
				new Line(v[4], v[5]), new Line(v[6], v[7]),
				new Line(v[5], v[7]), new Line(v[6], v[4]),
				new Line(v[0], v[4]), new Line(v[2], v[6]),
				new Line(v[1], v[5]), new Line(v[3], v[7]) };
		updateNeeded = false;
	}

	public void clear() {
		initialized = false;
	}

	public void render(GL gl) {
		if (updateNeeded)
			updateShape();
		gl.glPushAttrib(GL.GL_ENABLE_BIT);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glPushMatrix();
		gl.glLineWidth(2);
		gl.glBegin(GL.GL_LINES);
		for (int i = 0; i < lines.length; i++) {
			lines[i].render(gl);
		}
		gl.glEnd();
		gl.glLineWidth(1);
		gl.glPopMatrix();
		gl.glPopAttrib();
	}

	public Vec3 intersectRay(Vec3 vP, Vec3 vD) {
		vP = new Vec3(vP);
		vD = new Vec3(vD);
		float tmin = 0.0f;
		float tmax = 10000;
		float t1, t2, tmp;
		float[] min = this.min.getArr();
		float[] max = this.max.getArr();
		float[] d = vD.getArr();
		float[] p = vP.getArr();
		float ood;

		for (int i = 0; i < 3; i++) {
			if (Math.abs(d[i]) < 0.001f) {
				if (p[i] < min[i] || p[i] > max[i])
					return null;
			} else {
				ood = 1.0f / d[i];
				t1 = (min[i] - p[i]) * ood;
				t2 = (max[i] - p[i]) * ood;
				if (t1 > t2) {
					tmp = t1;
					t1 = t2;
					t2 = tmp;
				}
				tmin = Math.max(tmin, t1);
				tmax = Math.min(tmax, t2);
				if (tmin > tmax)
					return null;
			}
		}
		vD.scale(tmin);
		vP.add(vD);
		Point3f pos = new Point3f(vP);
		getTransform().transform(pos);
		vP.set(pos);
		return vP;
	}

	public boolean intersectBBox(BBox bbox) {
		return Math.max(min.x, bbox.min.x) < Math.min(max.x, bbox.max.x)
				&& Math.max(min.y, bbox.min.y) < Math.min(max.y, bbox.max.y)
				&& Math.max(min.z, bbox.min.z) < Math.min(max.z, bbox.max.z);
	}

	/*
	 * Transforming Axis-Aligned Bounding Boxes from "Graphics Gems", Academic
	 * Press, 1990
	 */
	public void setTransform(Mat4 transform) {
		this.transform = transform;
		Vec3 translate = new Vec3();
		Matrix3f rotate = new Matrix3f();
		transform.get(translate);
		transform.getRotationScale(rotate);
		float[] minA = mMin.getArr();
		float[] maxA = mMax.getArr();
		float[] max = translate.getArr();
		float[] min = translate.getArr();
		float a, b;
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++) {
				a = rotate.getElement(i, j) * minA[j];
				b = rotate.getElement(i, j) * maxA[j];
				if (a < b) {
					min[i] += a;
					max[i] += b;
				} else {
					min[i] += b;
					max[i] += a;
				}
			}
		this.min.x = min[0];
		this.max.x = max[0];
		this.min.y = min[1];
		this.max.y = max[1];
		this.min.z = min[2];
		this.max.z = max[2];
		updateNeeded = true;
	}

	public Mat4 getTransform() {
		return transform;
	}

}
