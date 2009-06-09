package engine.entities;

import javax.media.opengl.GL;
import javax.vecmath.Color4f;

import cameras.Camera;
import engine.Engine;
import engine.Materials.Texture;

public class Sprite extends Visible {
	public Texture texture;
	public Color4f color = new Color4f(1, 1, 1, 1);
	public float size = 1.0f;
	public boolean bilboard = true; 

	public void setTexture(String fileName) {
		texture = Engine.instance.materials.loadTexture(fileName);
	}
	
//	@Override
//	public void render(GL gl, Camera camera) {
//		if (!bilboard) {
//			super.render(gl, camera);
//			return;
//		}
//		gl.glPushMatrix();		
//		gl.glMultMatrixf(camera.transform.getRotation4().getGL());
//		draw(gl, camera);
//		gl.glPopMatrix();	
//	}

	@Override
	public void draw(GL gl, Camera camera) {
		gl.glPushAttrib(GL.GL_ENABLE_BIT);
		gl.glDepthMask(false);
		if (bilboard)
			gl.glMultMatrixf(camera.transform.getRotation4().getGL());
		// gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_ONE, GL.GL_BLEND);
		texture.glTexture.bind();
		gl.glBegin(GL.GL_QUADS);
		gl.glColor4f(color.x, color.y, color.z, color.w);
		gl.glTexCoord2f(0, 0);
		gl.glVertex3f(-size, -size, 0);
		gl.glTexCoord2f(1, 0);
		gl.glVertex3f(size, -size, 0);
		gl.glTexCoord2f(1, 1);
		gl.glVertex3f(size, size, 0);
		gl.glTexCoord2f(0, 1);
		gl.glVertex3f(-size, size, 0);
		gl.glEnd();
		gl.glPopAttrib();
		gl.glDepthMask(true);
	}

}
