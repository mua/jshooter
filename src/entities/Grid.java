package entities;

import javax.media.opengl.GL;

import cameras.Camera;
import engine.entities.Visible;

public class Grid extends Visible {
	public int width = 1, size = 100;
	@Override
	public void render(GL gl, Camera camera) {		
		super.render(gl, camera);
		gl.glDisable(GL.GL_TEXTURE_2D);
		gl.glBegin(GL.GL_LINES);
		int min = -size/2, max = size/2;
		for (int x = min; x < max; x+=width) {
			gl.glColor3f(1, 1, 1);
			gl.glVertex3f(x, 0, min);
			gl.glVertex3f(x, 0, max);
			gl.glVertex3f(min, 0, x);
			gl.glVertex3f(max, 0, x);
		}
		gl.glEnd();
		gl.glEnable(GL.GL_TEXTURE_2D);
	}
}
