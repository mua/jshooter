package engine.gtypes;

import javax.vecmath.*;;

public class Vec2 extends Vector2f {

	public Vec2(float f, float g) {
		super();
		x = f;
		y = g;
	}

	public Vec2() {
		super();
	}

	public Vec2(Vec2 swingTarget) {
		super(swingTarget);
	}

	public float[] getArr() {
		return new float[] { x, y };
	}
}
