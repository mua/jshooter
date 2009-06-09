package engine;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;

import com.sun.opengl.util.BufferUtil;

import ms3d.MS3DGroup;
import ms3d.MS3DMaterial;
import ms3d.MS3DModel;
import ms3d.MS3DTriangle;
import ms3d.MS3DVertex;

import engine.Materials.Material;
import engine.gtypes.BBox;
import engine.gtypes.Vec2;
import engine.gtypes.Vec3;

public class Meshes {
	public List<Mesh> meshes = new ArrayList<Mesh>();	

	public Mesh getByFileName(String fileName) {
		for (Mesh m : meshes)
			if (m.fileName.equals(fileName))
				return m;
		return null;
	}

	public Mesh loadMesh(String fileName) {
		Mesh mesh = new Mesh();
		try {
			mesh.loadFromFile(fileName);
			System.out.println("loaded " + fileName);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		meshes.add(mesh);
		return mesh;
	}

	public class Mesh {
		public List<Section> sections = new ArrayList<Section>();
		public String fileName;
		public BBox bbox = new BBox();

		public void updateBBox() {
			bbox = new BBox();
			for (Section sec : sections)
				for (Face face : sec.faces) {
					bbox.add(face.vertices[0].pos);
					bbox.add(face.vertices[1].pos);
					bbox.add(face.vertices[2].pos);
				}
		}

		public boolean loadFromFile(String fileName)
				throws FileNotFoundException, IOException {
			MS3DModel model = MS3DModel.decodeMS3DModel(new FileInputStream(
					fileName));
			Face face;
			Section section;

			MS3DTriangle mFace;
			MS3DVertex mVertex;

			List<Materials.Material> objectMaterials = new ArrayList<Materials.Material>();
			for (MS3DMaterial mMat : model.materials) {
				Materials.Material mat = Engine.instance.materials.new Material();
				mat.ambient.set(mMat.ambient);
				mat.diffuse.set(mMat.diffuse);
				mat.emissive.set(mMat.emissive);
				mat.specular.set(mMat.specular);
				mat.shininess = mMat.shininess;
				String dirName = fileName.substring(0, fileName
						.lastIndexOf("/") + 1);
				mat.setTextureByFileName(dirName + mMat.textureName);
				objectMaterials.add(mat);
				Engine.instance.materials.materials.add(mat);
			}
			for (MS3DGroup g : model.groups) {
				if (g.triangleIndices.length == 0) continue;
				section = new Section();
				sections.add(section);
				if (g.materialIndex < objectMaterials.size())
					section.material = objectMaterials.get(g.materialIndex);
				for (int t : g.triangleIndices) {
					face = new Face();
					section.faces.add(face);
					mFace = model.triangles[t];
					for (int i = 0; i < 3; i++) {
						mVertex = model.vertices[mFace.vertexIndices[i]];
						face.vertices[i] = new Vertex();
						face.vertices[i].pos.set(mVertex.location[0],
								mVertex.location[1], mVertex.location[2]);
						face.vertices[i].uv = new Vec2(mFace.s[i],
								1 - mFace.t[i]);
					}
				}
			}
			updateBBox();
			this.fileName = fileName;
			return true;
		}

		public void draw(GL gl) {
			for (Section sec : sections)
				if (!sec.allocated)
					sec.allocateBuffers(gl);

			gl.glEnableClientState(GL.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL.GL_TEXTURE_COORD_ARRAY);
			// gl.glDisable(GL.GL_TEXTURE_2D);
			for (Section sec : sections) {
				sec.draw(gl);
			}
			gl.glDisableClientState(GL.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL.GL_TEXTURE_COORD_ARRAY);
		}
	}

	public static class Vertex {
		public Vec3 pos = new Vec3();
		public Vec3 normal = new Vec3();
		public Vec2 uv = new Vec2();
	}

	public static class Face {
		public Vertex[] vertices = new Vertex[3];

		public void render(GL gl) {
			Vertex v;
			v = vertices[0];
			gl.glTexCoord2f(v.uv.x, v.uv.y);
			gl.glVertex3fv(v.pos.getGL());
			v = vertices[2];
			gl.glTexCoord2f(v.uv.x, v.uv.y);
			gl.glVertex3fv(v.pos.getGL());
			v = vertices[1];
			gl.glTexCoord2f(v.uv.x, v.uv.y);
			gl.glVertex3fv(v.pos.getGL());
		}
	}

	public static class Line {
		public Vertex[] vertices = new Vertex[2];

		public Line(Vec3 v1, Vec3 v2) {
			super();
			vertices[0] = new Vertex();
			vertices[1] = new Vertex();
			vertices[0].pos = v1;
			vertices[1].pos = v2;
		}

		public void render(GL gl) {
			gl.glColor3f(1.0f, 1.0f, 1.0f);
			gl.glVertex3fv(vertices[0].pos.getGL());
			gl.glVertex3fv(vertices[1].pos.getGL());
		}
	}

	public class Section {
		public List<Face> faces = new ArrayList<Face>();
		private int[] bufferUV = new int[1];
		private int[] bufferPos = new int[1];
		private int vertexCount = 0;
		boolean allocated = false;
		Material material;

		void allocateBuffers(GL gl) {
			vertexCount = faces.size() * 3;
			FloatBuffer pos = BufferUtil.newFloatBuffer(vertexCount * 3);
			FloatBuffer uv = BufferUtil.newFloatBuffer(vertexCount * 2);
			for (Face f : faces) {
				for (Vertex v : f.vertices) {
					pos.put(v.pos.x);
					pos.put(v.pos.y);
					pos.put(v.pos.z);
					uv.put(v.uv.getArr());
				}
			}
			pos.flip();
			uv.flip();
			gl.glGenBuffersARB(1, bufferPos, 0);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, bufferPos[0]);
			gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 3
					* BufferUtil.SIZEOF_FLOAT, pos, GL.GL_STATIC_DRAW_ARB);

			gl.glGenBuffersARB(1, bufferUV, 0);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, bufferUV[0]);
			gl.glBufferDataARB(GL.GL_ARRAY_BUFFER_ARB, vertexCount * 2
					* BufferUtil.SIZEOF_FLOAT, uv, GL.GL_STATIC_DRAW_ARB);
			allocated = true;
		}

		void draw(GL gl) {
			if (!allocated)
				allocateBuffers(gl);
			if (material != null)
				material.bind(gl);
			else
				System.out.println("no mat");
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, bufferUV[0]);
			gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, 0);
			gl.glBindBufferARB(GL.GL_ARRAY_BUFFER_ARB, bufferPos[0]);
			gl.glVertexPointer(3, GL.GL_FLOAT, 0, 0);

			gl.glDrawArrays(GL.GL_TRIANGLES, 0, vertexCount);
		}
	}
}
