package entities;

import java.util.Random;

import javax.media.opengl.GL;
import javax.vecmath.Color4f;

import cameras.Camera;
import engine.entities.Sprite;
import engine.entities.Visible;
import engine.gtypes.Mat4;
import engine.gtypes.Vec3;


public class SkyDome extends Visible {
	public SkyDome() {
		Sprite sprite;
		float x, y;
		Random rand = new Random();
		for (int n = 0; n < 400; n++) {
			y = (float) (rand.nextFloat() * 2 * Math.PI);
			x = (float) ((rand.nextFloat() - 0.5) * Math.PI);
			sprite = new Sprite();
			sprite.size = 0.5f * rand.nextFloat();
			place(sprite, x, y, 20);
			sprite.color = new Color4f(
					0.7f+rand.nextFloat()*.3f, 
					0.7f+rand.nextFloat()*.3f, 
					0.7f+rand.nextFloat()*.3f, 
					rand.nextFloat());
			addChild(sprite);
			sprite.bilboard = false;
			sprite.setTexture("textures/star.bmp");
		}
		sprite = new Sprite();
		sprite.setTexture("textures/galaxy.jpg");
		place(sprite, Math.PI/4, -Math.PI/4, 18);
		sprite.size = 10.0f;
		sprite.bilboard = false;
		addChild(sprite);
		
		sprite = new Sprite();
		sprite.setTexture("textures/nebu.jpg");
		place(sprite, Math.PI/4, Math.PI/4, 10);
		sprite.size = 28.0f;
		sprite.bilboard = false;
		addChild(sprite);		
	}

	void place(Visible entity, double angleX, double angleY, float radius) {
		Mat4 translate = Mat4.identity(), rotate = Mat4.identity();
		rotate.rotate((float)angleX, (float)angleY, 0);
		translate.setTranslation(new Vec3(0, 0, radius));
		rotate.mul(translate);
		entity.setTransform(rotate);
	}
	
	@Override
	public void render(GL gl, Camera camera) {
		gl.glPushMatrix();
		gl.glLoadIdentity();
		Visible visible;
		gl.glDisable(GL.GL_DEPTH_TEST);
		gl.glMultMatrixf(camera.viewMatrix.getRotation4().getGL());
		//camera.transform.getTranslation4().transform(move);
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i) instanceof Visible) {
//				move.set(0,0,0);
				visible = (Visible) entities.get(i);
//				visible.getTransform().transform(move);
//				pos.set(move);
//				if (camera.frustum.isInside(pos))
					visible.render(gl, camera);
			}
		}
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glPopMatrix();
	}
	
	@Override
	public void act(float deltaTime) {
	}
}
