package engine.entities;

import java.util.ArrayList;
import java.util.List;

public class Entity {
	public Entity parent = null;
	public Context context;
	public List<Entity> entities = new ArrayList<Entity>();

	public Entity getParent() {
		return parent;
	}

	public void setParent(Entity parent) {
		setContext(parent.context);
		this.parent = parent;
	}

	public void setContext(Context context) {
		this.context = context;
		for (Entity e : entities)
			e.setContext(context);
	}

	public void addChild(Entity entity) {
		entities.add(entity);
		entity.setParent(this);
	}

	public void act(float deltaTime) {
		for (Entity e : entities)
			e.act(deltaTime);
	}
}
