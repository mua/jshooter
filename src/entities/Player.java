package entities;

import java.awt.event.MouseEvent;

import javax.media.opengl.GL;
import javax.swing.SwingUtilities;

import components.Viewport;

import cameras.Camera;
import cameras.PointCamera;
import engine.entities.Visible;
import engine.gtypes.Vec2;

public class Player extends Visible {
	public Ship ship;
	public PlayerCam camera;

	public Player() {
		ship = new Ship();
		addChild(ship);
	}

	@Override
	public void act(float deltaTime) {
		camera.act(deltaTime);
		super.act(deltaTime);
	}
	
	@Override
	public void render(GL gl, Camera camera) {
		super.render(gl, camera);
	}

	public class PlayerCam extends PointCamera {
		Vec2 swing = new Vec2();

		public PlayerCam(Viewport vp) {
			super(vp);
			entity = ship;
			position.y = 15;
			distance = 50;
			rotX = (float)Math.PI * -0.01f;
			update_view_matrix();
		}

		void setSwing(float x, float y) {
			rotX -= swing.x;
			rotY -= swing.y;
			swing.x = x;
			swing.y = y;
			rotX += swing.x;
			rotY += swing.y;
		}

		@Override
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			if (SwingUtilities.isLeftMouseButton(e)) {
				float xo = viewport.getWidth() / 2;
				float yo = viewport.getHeight() / 2;
				ship.angularVelocityTarget.y = -(e.getX() - xo) / xo;
				ship.angularVelocityTarget.x = -(e.getY() - yo) / yo;
			}
			if (SwingUtilities.isRightMouseButton(e))
				ship.gun.fire();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e))
				mousePressed(e);
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e))
				ship.gun.stop();
			else {
				ship.angularVelocityTarget.x = 0;
				ship.angularVelocityTarget.y = 0;
			}
			super.mouseReleased(e);
		}

		public void act(float deltaTime) {
			setSwing(ship.angularVelocity.x * -.2f, ship.angularVelocity.y * -.2f);
		}
	}
}
