package components;

import java.io.File;
import java.util.Calendar;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;

import cameras.Camera;
import cameras.PointCamera;
import engine.Engine;
import engine.entities.Context;
import engine.entities.Explosion;
import engine.entities.Visible;
import entities.Dust;
import entities.Grid;
import entities.MeteorField;
import entities.Player;
import entities.Ship;
import entities.SkyDome;
import entities.Player.PlayerCam;

class Renderer implements GLEventListener {
	Viewport viewport;
	long time = 0;

	public Renderer(Viewport vp) {
		viewport = vp;
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		long ntime = Calendar.getInstance().getTimeInMillis();
		if (time != 0)
			viewport.context.act(((float) (ntime - time)) / 1000.0f);
		time = ntime;
		final GL gl = drawable.getGL();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		viewport.camera.update_view_matrix();
		viewport.camera.apply(gl);
		viewport.context.render(gl, viewport.camera);
		// System.out.println(1000.0f/(Calendar.getInstance().getTimeInMillis()
		// - ntime));
	}

	@Override
	public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
			boolean deviceChanged) {
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL gl = drawable.getGL();
		gl.glShadeModel(GL.GL_SMOOTH);
		// gl.glClearColor(0.5f, 0.5f, 0.5f, 0.5f);
		gl.glClearDepth(1.0f);
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		// gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL.GL_LINE);
		gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glHint(GL.GL_LINE_SMOOTH_HINT, GL.GL_DONT_CARE);

		gl.glEnable(GL.GL_ALPHA_TEST);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glTexEnvf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);

		viewport.context.addChild(new SkyDome());
		// viewport.context.addChild(new Grid());
		// viewport.context.addChild(new ParticleEngine());

		viewport.context.addChild(viewport.field = new MeteorField());
		Player p = new Player();
		viewport.context.addChild(p);
		viewport.camera = p.camera = p.new PlayerCam(viewport);
		viewport.context.addChild(new Explosion());		
		viewport.context.addChild(new Grid());
		Visible v = new Visible();
		v.addChild(new Explosion());
		viewport.context.addChild(v);
		// viewport.camera = new PointCamera(viewport);
		//viewport.context.addChild(new Dust());
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		viewport.camera.update_projection_matrix();
	}
}

public class Viewport extends GLJPanel {
	Camera camera;
	Visible context;
	MeteorField field;

	public Viewport() {
		super();
		context = new Context();
		addMouseListener(camera);
		addMouseWheelListener(camera);
		addMouseMotionListener(camera);
		this.addGLEventListener(new Renderer(this));
	}
}