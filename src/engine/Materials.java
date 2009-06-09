package engine;

import engine.gtypes.Color4;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import com.sun.opengl.util.texture.TextureIO;

public class Materials {
	List<Texture> textures = new ArrayList<Texture>();
	public List<Material> materials = new ArrayList<Material>();

	public Texture loadTexture(String fileName) {
		Texture tex = getTextureByFileName(fileName);
		if (tex != null)
			return tex;
		tex = new Texture();
		tex.load(fileName);
		if (tex.glTexture != null) {
			textures.add(tex);
			return tex;
		}
		return null;
	}

	private Texture getTextureByFileName(String fileName) {
		for (Texture t : textures)
			if (t.fileName.equals(fileName))
				return t;
		return null;
	}
	
	public void reloadTexture() {
		for (Texture tex : textures)
			tex.reload();
	}

	public class Texture {
		public com.sun.opengl.util.texture.Texture glTexture;
		String fileName;

		public void setGLTexture(com.sun.opengl.util.texture.Texture glTexture) {
			this.glTexture = glTexture;
		}
		
		public void reload() {
			load(fileName);
		}
		
		public void load(String fileName) {
			com.sun.opengl.util.texture.Texture glTexture;
			try {
				glTexture = TextureIO.newTexture(new File(fileName), false);
				glTexture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
				glTexture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
				setGLTexture(glTexture);
				this.fileName = fileName;				
			} catch (Exception e) {
				System.out.println(e.getMessage());
				System.out.println("Error loading texture " + fileName);
			}			
		}
	}

	public class Material {
		public Color4 ambient = new Color4();
		public Color4 diffuse = new Color4();
		public Color4 specular = new Color4();
		public Color4 emissive = new Color4();
		public float shininess;
		Texture texture;

		public void bind(GL gl) {
			// gl.glBlendFunc(GL.GL_ONE, GL.GL_ZERO);
			gl.glDisable(GL.GL_BLEND);
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT, ambient.getGL());
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE, diffuse.getGL());
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR, specular.getGL());
			gl.glMaterialfv(GL.GL_FRONT, GL.GL_EMISSION, emissive.getGL());
			gl.glMaterialf(GL.GL_FRONT, GL.GL_SHININESS, shininess);
			if (texture != null) {
				texture.glTexture.bind();
			}
		}

		public void setTextureByFileName(String fileName) {
			Texture texture = getTextureByFileName(fileName);
			if (texture == null) {
				texture = loadTexture(fileName);
			}
			this.texture = texture;
		}
	}
}
