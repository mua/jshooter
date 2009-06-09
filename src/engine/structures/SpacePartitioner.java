package engine.structures;

import java.util.ArrayList;
import java.util.List;

import engine.entities.Visible;
import engine.entities.Visible.TransformListener;
import engine.gtypes.Vec3;

public abstract class SpacePartitioner {
	int tick = 0;

	class Cell {
		List<Entry> entires = new ArrayList<Entry>();
	}

	class Entry implements TransformListener {
		Visible entity;
		int tick;
		public List<Cell> cells = new ArrayList<Cell>();

		public Entry(Visible entity) {
			this.entity = entity;
			entity.transformListeners.add(this);
		}

		@Override
		public void update() {
			removeEntry(this);
			add(this);
		}
	}

	public abstract Cell[] getCells(Vec3 min, Vec3 max, boolean create);

	void removeEntry(Entry e) {
		for (Cell c : e.cells) {
			c.entires.remove(e);
		}
		e.cells.clear();
	}

	public void add(Entry e) {
		if (e.entity.bbox != null)
			for (Cell c : getCells(e.entity.bbox.min, e.entity.bbox.max, true)) {
				c.entires.add(e);
				e.cells.add(c);
			}
	}

	public void add(Visible e) {
		Entry entry = new Entry(e);
		add(entry);
	}

	public List<Entry> queryEntries(Vec3 min, Vec3 max) {
		tick++;
		List<Entry> entires = new ArrayList<Entry>();
		for (Cell c : getCells(min, max, false)) {
			for (Entry e : c.entires) {
				if (e.tick != tick) {
					entires.add(e);
					e.tick = tick;
				}
			}
		}
		return entires;
	}

	public List<Visible> queryEntities(Vec3 min, Vec3 max, Visible source) {
		tick++;
		final List<Visible> entities = new ArrayList<Visible>();
		for (Cell c : getCells(min, max, false)) {
			if (c != null)
				for (Entry e : c.entires) {
					if (e.tick != tick) {
						if (e.entity != source)
							entities.add(e.entity);
						e.tick = tick;
					}
				}
		}
		return entities;
	}
}
