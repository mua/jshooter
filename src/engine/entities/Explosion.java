package engine.entities;


import engine.gtypes.Vec3;

public class Explosion extends ParticleEngine {
	public Explosion() {
		maxLife = 5f;
	}
	@Override
	void initParticle(Particle p) {
		p.life = maxLife * random.nextFloat();
		p.setTransform(worldTransform.getTranslation4());
		p.velocity = new Vec3(random.nextFloat()-.5f,random.nextFloat()-.5f,random.nextFloat()-.5f);
		p.velocity.scale(5);
		worldTransform.transform(p.velocity);
	}
}
