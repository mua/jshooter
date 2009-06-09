package engine.entities;

import engine.structures.Octree;
import engine.structures.SpacePartitioner;

public class Context extends Visible {
	public SpacePartitioner tree = new Octree(); 

	public Context() {
		this.context = this;
	}
}
