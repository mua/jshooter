package engine;

public class Engine {
	public static Engine instance = null;
	public Materials materials = new Materials();
	public Meshes meshes = new Meshes();
	public Sounds sounds = new Sounds();
	public Engine() {
		instance = this;
	}
}
