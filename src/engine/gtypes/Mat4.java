package engine.gtypes;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.lang.Math;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Mat4 extends Matrix4f {
	public Mat4(float[] glMatrix) {
		super();
	}

	public Mat4() {
		super();
		setIdentity();
	}

	public Mat4(Matrix4f matrix) {
		super(matrix);
	}

	public Mat4(Matrix4d matrix) {
		super(matrix);
	}

	public FloatBuffer getGL() {
		return FloatBuffer.wrap(new float[] { this.m00, this.m10, this.m20,
				this.m30, this.m01, this.m11, this.m21, this.m31, this.m02,
				this.m12, this.m22, this.m32, this.m03, this.m13, this.m23,
				this.m33 });
	}

	public DoubleBuffer getGLd() {
		return DoubleBuffer.wrap(new double[] { this.m00, this.m10, this.m20,
				this.m30, this.m01, this.m11, this.m21, this.m31, this.m02,
				this.m12, this.m22, this.m32, this.m03, this.m13, this.m23,
				this.m33 });
	}

	public void rotate(float x, float y, float z) {
		Matrix3f matX = new Matrix3f(), matY = new Matrix3f(), matZ = new Matrix3f();
		matX.rotX(x);
		matY.rotY(y);
		matZ.rotZ(z);
		matY.mul(matX);
		matY.mul(matZ);
		setRotation(matY);
	}

	public void perspective(float fovy, float aspect, float zNear, float zFar) {
		float f = (float) (1.0 / Math.tan(fovy / 2));
		this.m00 = f / aspect;
		this.m11 = f;
		this.m22 = (zFar + zNear) / (zNear - zFar);
		this.m23 = (2 * zFar * zNear) / (zNear - zFar);
		this.m32 = -1;
	}

	public void orthogonal(float left, float right, float bottom, float top,
			float nearVal, float farVal) {
		float tx = (right + left) / (right - left);
		float ty = (top + bottom) / (top - bottom);
		float tz = (farVal + nearVal) / (farVal - nearVal);
		this.m00 = 2 / (right - left);
		this.m11 = 2 / (top - bottom);
		this.m22 = -2 / (farVal - nearVal);
		this.m33 = 1;
		this.m03 = tx;
		this.m13 = ty;
		this.m23 = tz;
	}
	
	public Mat4 getRotation4() {
		Matrix3f mat = new Matrix3f();
		getRotationScale(mat);
		Mat4 ret = new Mat4();
		ret.set(mat);
		return ret;
	}
	
	public Mat4 getTranslation4() {
		Vector3f vec = new Vector3f();
		get(vec);
		Mat4 ret = new Mat4();
		ret.setTranslation(vec);
		return ret;
	}	

	@Override
	public Mat4 clone() {
		return new Mat4(this);
	}

	static public Mat4 identity() {
		Mat4 mat = new Mat4();
		mat.setIdentity();
		return mat;
	}

	public void transformRay(Vec3 vP, Vec3 vD) {
		Point3f p = new Point3f(vP);
		transform(p);
		vP.set(p);
		transform(vD);
	}

	public Mat4 getInverse() {
		Mat4 mat = new Mat4(this);
		mat.invert();
		return mat;
	}
	
	public void move(Vec3 vec) {
		Mat4 mat = Mat4.identity();
		mat.setTranslation(vec);
		mul(mat);
	}
	
	public Vec3 getOrigin() {
		Point3f p = new Point3f(0,0,0);
		transform(p);
		return new Vec3(p);
	}
}