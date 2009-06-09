package entities;

import javax.media.opengl.GL;

import cameras.Camera;
import engine.Engine;
import engine.entities.Model;
import engine.entities.Object;
import engine.entities.ParticleEngine;
import engine.entities.Visible;
import engine.gtypes.Mat4;
import engine.gtypes.Vec3;

class Interpolator {
	static float epsilon2 = 0.0001f;

	Vec3 current, target;
	public float synchSpeed = 4.0f;

	public Interpolator(Vec3 current, Vec3 target) {
		this.current = current;
		this.target = target;
	}

	void act(float deltaTime) {
		Vec3 dif = new Vec3();
		dif.sub(target, current);
		if (dif.dot(dif) > epsilon2) {
			dif.scale(deltaTime * synchSpeed);
			current.add(dif);
		}
	}
}

public class Ship extends Object {
	ParticleEngine engineL, engineR;
	Vec3 angularVelocity = new Vec3(0.5f, 0.5f, 0);
	Vec3 angularVelocityTarget = new Vec3(0, 0, 0);
	Vec3 velocity = new Vec3(0, 0, -60f);
	Gun gun;
	Model model;
	Interpolator angleInterpolator = new Interpolator(angularVelocity,
			angularVelocityTarget);

	public Ship() {
		model = new Model("models/ship/fighter1.ms3d");
		addChild(model);
		Mat4 mat = Mat4.identity();
		Engine.instance.sounds.getClipByFileName("sounds/jet.wav", true).clip
				.loop(-1);
		engineL = new ParticleEngine();
		mat.setTranslation(new Vec3(-7.5f, -.4f, 15));
		engineL.setTransform(mat);
		model.addChild(engineL);
		engineR = new ParticleEngine();
		mat.setTranslation(new Vec3(7.5f, -.4f, 15));
		engineR.setTransform(mat);
		model.addChild(engineR);
		gun = new Gun();
		gun.ship = this;
		model.addChild(gun);
	}

	@Override
	public void act(float deltaTime) {
		angleInterpolator.act(deltaTime);
		Vec3 move = velocity.clone();
		Mat4 deltaPos = Mat4.identity();
		move.scale(deltaTime);
		deltaPos.setTranslation(move);

		Mat4 deltaRot = Mat4.identity();
		deltaRot.rotate(angularVelocity.x * deltaTime, angularVelocity.y
				* deltaTime, 0);

		Mat4 transform = getTransform().clone();
		transform.mul(deltaPos);
		transform.mul(deltaRot);

		setTransform(transform);

		transform.setIdentity();
		transform.rotate(0, 0, angularVelocity.y);
		model.setTransform(transform);

		super.act(deltaTime);
		for (Visible e : context.tree.queryEntities(bbox.min, bbox.max, model)) {
				System.out.format("add(%1s, %2s)\n", e, e.bbox.max);
		}
	}

	@Override
	public void render(GL gl, Camera camera) {
		super.render(gl, camera);
	}
}
