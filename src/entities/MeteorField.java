package entities;

import java.util.Random;

import javax.media.opengl.GL;

import cameras.Camera;

import engine.entities.Entity;
import engine.entities.Model;
import engine.entities.Visible;

public class MeteorField extends Visible {
	Random random = new Random();
	Meteor m;

	public MeteorField() {
		for (int i = 0; i < 100; i++) {
			m = new Meteor();
			addChild(m);
			m.moveRandomly(2000);
			m.getTransform().setScale(random.nextFloat() * 4);
			m.updateWorldTransform();
		}
	}

	@Override
	public void addChild(Entity entity) {
		super.addChild(entity);
	}

	class Meteor extends Model {
		public Meteor() {
			super("models/rock/rock1.ms3d");
		}

		void moveRandomly(float radius) {
			double ax = (random.nextFloat() - 0.5) * Math.PI;
			double ay = random.nextFloat() * Math.PI * 2;
			movePolar(ax, ay, radius * random.nextFloat());
		}

		@Override
		public void render(GL gl, Camera camera) {
			bbox.render(gl);
			super.render(gl, camera);
		}

		@Override
		public void draw(GL gl, Camera camera) {
			super.draw(gl, camera);
		}
	}
}
