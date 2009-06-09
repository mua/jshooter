package entities;

import java.util.Iterator;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;

import cameras.Camera;

import engine.Engine;
import engine.entities.Entity;
import engine.entities.Sprite;
import engine.entities.Visible;
import engine.gtypes.Vec3;

public class Gun extends Visible {
	float charge = 0;
	float cost = .1f;
	boolean firing = false;
	private float range = 2000;
	public Ship ship;

	public Gun() {

	}

	void fire() {
		firing = true;
		Engine.instance.sounds.getClipByFileName("sounds/laser.wav", true).clip
				.loop(-1);
	}

	void stop() {
		firing = false;
		Engine.instance.sounds.getClipByFileName("sounds/laser.wav", true).clip
				.stop();
	}

	void clean() {
		Projectile p;
		Entity e;
		Vec3 origin = worldTransform.getOrigin(), pOrigin;
		Iterator<Entity> it = entities.iterator();
		while (it.hasNext()) {
			e = it.next();
			if (e instanceof Projectile) {
				p = (Projectile) e;
				pOrigin = p.getTransform().getOrigin();
				pOrigin.sub(origin);
				if (pOrigin.length() > range) {
					it.remove();
					// System.out.println("removed");
				}
			}
		}
	}

	@Override
	public void act(float deltaTime) {
		super.act(deltaTime);
		charge += deltaTime;
		if (charge > 1)
			charge = 1;
		if (firing && charge >= cost) {
			Projectile p = new Projectile();
			addChild(p);
			charge -= cost;
			p.parent = null;
			p.transform.set(worldTransform.getTranslation4());
			p.velocity.z = -2550;
			worldTransform.transform(p.velocity);
			clean();
		}
	}

	class Projectile extends Body {
		public Projectile() {
			Sprite s = new Sprite();
			s.setTexture("textures/particle.png");
			s.size = 10;
			addChild(s);
		}

		@Override
		public void act(float deltaTime) {
			Point3f p1 = new Point3f(), p2 = new Point3f();
			worldTransform.transform(p1);
			super.act(deltaTime);
			worldTransform.transform(p2);
			bbox.clear();
			bbox.add(new Vec3(p1));
			bbox.add(new Vec3(p2));
			Vec3 vd = new Vec3();
			vd.sub(p2, p1);
			Vec3 point;
			for (Visible v : context.tree.queryEntities(bbox.min, bbox.max, this)) {
				if (v.bbox.intersectBBox(bbox)) {
					if ((point = v.bbox.intersectRay(new Vec3(p1), vd)) != null) {
						hit(v, point);
						break;
					}
				}
			}
		}

		private void hit(Visible v, Vec3 point) {
			System.out.println("hit!");
		}
	}
}
