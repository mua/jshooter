package engine.gtypes;

import javax.vecmath.Vector3f;

public class Plane3 {
	public float distance;
	public Vec3 normal = new Vec3();

	public Plane3() {

	}

	public float distance(Vector3f point) {
		return normal.dot(point)-distance;
	}

	public Vec3 intersectRay(Vector3f rayPos, Vector3f rayDir) {
		Vec3 ret = new Vec3();
		float dot = -rayDir.dot(normal);
		if (dot == 0)
			return null;
		ret.scale(distance(rayPos) / dot, rayDir);
		ret.add(rayPos);
		return ret;
	}

	public void set(float f, float g, float h, float i) {
		normal.set(f, g, h);
		float length = normal.length();
		normal.set(f/length, g/length, h/length);
		distance = -i / length;
	}
}
