package game;
import java.awt.Dimension;

import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;

import com.sun.opengl.util.FPSAnimator;

import components.Viewport;
import engine.Engine;

class GameDisplay {
	public FPSAnimator animator;
	
	public GameDisplay() {
		JFrame frame = new JFrame("Hello");
		frame.setVisible(true);
		GLJPanel panel = new Viewport();
		frame.getContentPane().add(panel);
		frame.setSize(new Dimension(800, 600));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        animator = new FPSAnimator( panel, 60 );
        animator.setRunAsFastAsPossible(false);
        animator.start();
	}
}

public class Game extends Engine {
	public static void main(String[] args) {
		new Engine();
		new GameDisplay();
	}
}
