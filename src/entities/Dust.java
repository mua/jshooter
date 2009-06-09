package entities;

import java.util.Random;

import engine.entities.Sprite;
import engine.entities.Visible;
import engine.gtypes.Mat4;
import engine.gtypes.Vec3;

import javax.media.opengl.GL;
import javax.vecmath.Point3f;

import cameras.Camera;

public class Dust extends Visible {
	Sprite sprite = new Sprite();
	float radius = 10;
	Random random = new Random();
	Vec3 oldOrigin, camOrigin, camMovement = new Vec3();

	class Colud extends Sprite { 
		boolean initialized = false;
		Vec3 position = new Vec3();
		
		private void placeRandomly(Vec3 origin, float radius) {
			double ax = (random.nextFloat() - 0.5) * Math.PI;
			double ay = random.nextFloat() * Math.PI * 2;
			place(origin, ax, ay, radius);
		}

		@Override
		public void render(GL gl, Camera camera) {
			if (!initialized) {
				placeRandomly(camOrigin, radius * random.nextFloat());
				initialized = true;
			}
			Vec3 pos = getTransform().getOrigin(), distance = new Vec3();
			distance.sub(pos, camOrigin);
			float cd = distance.dot(camMovement);
			float len = distance.length();
			color.w = (len / radius);
			if (len>radius && cd<0) {
				placeRandomly(camOrigin, radius);
			} else // if (camera.frustum.isInside(pos))
				super.render(gl, camera);
		}

		void place(Vec3 origin, double angleX, double angleY, float radius) {
			Mat4 translate = Mat4.identity(), rotate = Mat4.identity(), transform = new Mat4();
			rotate.rotate((float) angleX, (float) angleY, 0);
			translate.setTranslation(new Vec3(0, 0, radius));
			transform.mul(rotate, translate);
			Point3f p = new Point3f(0,0,0);
			transform.transform(p);
			getTransform().setTranslation(new Vec3(p));
			getTransform().move(origin);
			setTransform(getTransform());
		}
	}

	public Dust() {
		super();
		for (int i = 0; i < 100; i++) {
			Sprite sprite = new Colud();
			addChild(sprite);
			sprite.size = .2f;
			sprite.setTexture("textures/star.bmp");
		}
	}

	public boolean isInFrustum(Camera camera, Sprite sprite) {

		return false;
	}
	
	@Override
	public void render(GL gl, Camera camera) {
		camOrigin = camera.transform.getOrigin();
		if (oldOrigin != null) {
			camMovement.sub(camOrigin, oldOrigin);
		}
		oldOrigin = camOrigin;
		super.render(gl, camera);
	}
}
