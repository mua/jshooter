package entities;

import engine.entities.Visible;
import engine.gtypes.Mat4;
import engine.gtypes.Vec3;

public class Body extends Visible {
	Vec3 velocity = new Vec3(0, 0, -13f);

	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		Vec3 move = velocity.clone();
		Mat4 deltaPos = Mat4.identity();
		move.scale(deltaTime);
		deltaPos.setTranslation(move);

		Mat4 transform = getTransform().clone();
		transform.mul(deltaPos);

		setTransform(transform);
		super.act(deltaTime);
	}
}
