package engine.entities;

import engine.gtypes.Mat4;
import engine.gtypes.Vec3;

import java.util.Random;

public class ParticleEngine extends Object {
	Random random = new Random();
	float particleCount = 20;
	float maxLife = 0.5f;
	
	public void start() {
		Particle p;
		for (int i = 0; i < particleCount; i++) {
			p = new Particle();
			p.setTexture("textures/particle.png");
			addParticle(p);
		}
	}
	
	public void addParticle(Particle p) {
		addChild(p);
		p.parent = null;
	}

	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
	}

	void initParticle(Particle p) {
		p.life = maxLife * random.nextFloat();
		p.setTransform(worldTransform.getTranslation4());
		p.velocity = new Vec3(2 * (random.nextFloat() - 0.5f), 2 * (random
				.nextFloat() - 0.5f), random.nextFloat() - 0.5f);
		worldTransform.transform(p.velocity);
	}

	class Particle extends Sprite {
		float life = -1;
		Vec3 velocity;

		@Override
		public void act(float deltaTime) {
			System.out.println(velocity);
			if (life < 0) {
				initParticle(this);
			}
			life -= deltaTime;
			color.w = life / maxLife;
			Mat4 displacement = Mat4.identity();
			Vec3 move = new Vec3(velocity);
			move.scale(deltaTime * 3);
			size = 3 * (1 - color.w) + 0.5f;
			displacement.setTranslation(move);
			displacement.mul(getTransform());
			setTransform(displacement);
		}
	}

}
