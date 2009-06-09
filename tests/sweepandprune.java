import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.opengl.impl.mipmap.HalveImage;

import engine.entities.Visible;
import engine.entities.Visible.TransformListener;
import engine.gtypes.BBox;
import engine.gtypes.Vec3;
import junit.framework.TestCase;

abstract class SpacePartitioner {
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

	public List<Visible> queryEntities(Vec3 min, Vec3 max) {
		tick++;
		final List<Visible> entities = new ArrayList<Visible>();
		for (Cell c : getCells(min, max, false)) {
			if (c != null)
				for (Entry e : c.entires) {
					if (e.tick != tick) {
						entities.add(e.entity);
						e.tick = tick;
					}
				}
		}
		return entities;
	}
}

class SPGrid extends SpacePartitioner {
	float size = 10;
	float inverseSize = 1 / size;
	Map<Integer, Cell> cells = new HashMap<Integer, Cell>();

	public int getCellHash(int x, int y, int z) {
		return (x << 20) + (y << 10) + z;
	}

	public Cell getCell(int x, int y, int z) {
		return cells.get(getCellHash(x, y, z));
	}

	public Cell getCell(int x, int y, int z, boolean create) {
		Cell cell = cells.get(getCellHash(x, y, z));
		if (cell == null && create) {
			cells.put(getCellHash(x, y, z), cell = new Cell());
		}
		return cell;
	}

	public Cell[] getCells(Vec3 min, Vec3 max, boolean create) {
		int xmin = (int) (min.x * inverseSize);
		int xmax = (int) (max.x * inverseSize);
		int ymin = (int) (min.y * inverseSize);
		int ymax = (int) (max.y * inverseSize);
		int zmin = (int) (min.z * inverseSize);
		int zmax = (int) (max.z * inverseSize);
		Cell[] ret = new Cell[(Math.abs(xmax - xmin) + 1)
				* (Math.abs(ymax - ymin) + 1) * (Math.abs(zmax - zmin) + 1)];
		int i = 0;
		for (int x = xmin; x <= xmax; x++)
			for (int y = ymin; y <= ymax; y++)
				for (int z = zmin; z <= zmax; z++) {
					ret[i++] = getCell(x, y, z, create);
				}
		return ret;
	}
}

class Octree extends SpacePartitioner {
	Node root = new Node();
	int maxDepth = 3;

	class Node extends Cell {
		Node[] nodes = new Node[8];
		boolean isLeaf = true;
		Vec3 center;
		float halfWidth = 10;
		int depth = 0;

		public int getID(Vec3 point) {
			int ret = 0;
			ret += (point.x < center.x ? 0 : 1) << 2;
			ret += (point.y < center.y ? 0 : 1) << 1;
			ret += (point.z < center.z ? 0 : 1);
			return ret;
		}

		private void createChildren() {
			Vec3 center;
			float halfWidth = 0.5f * this.halfWidth;
			Node node;
			for (int x = 0; x < 2; x++)
				for (int y = 0; y < 2; y++)
					for (int z = 0; z < 2; z++) {
						center = new Vec3((x - 1) * halfWidth, (y - 1)
								* halfWidth, (z - 1) * halfWidth);
						node = new Node();
						nodes[x << 4 + y << 2 + z] = node;
						node.depth = depth + 1;
						node.center = center;
						node.halfWidth = halfWidth;
					}
			isLeaf = false;
		}

		public List<Node> getNodes(Vec3 min, Vec3 max, boolean create) {
			List<Node> ret = new ArrayList<Node>();
			Node node;
			int start = getID(min);
			int end = getID(max);
			if (isLeaf && depth < maxDepth && create) {
				createChildren();
			}			
			for (int x = (start & 4); x <= (end & 4); x++)
				for (int y = (start & 2); y <= (end & 2); y++)
					for (int z = (start & 1); z <= (end & 1); z++) {
						node = nodes[x << 2 + y << 1 + z];
						if (!node.isLeaf) {
							ret.addAll(node.getNodes(min, max, create));
						} else {
							ret.add(node);
						}
					}
			return ret;
		}
	}

	@Override
	public Cell[] getCells(Vec3 min, Vec3 max, boolean create) {
		return (Cell[]) root.getNodes(min, max, create).toArray();
	}
}

public class sweepandprune extends TestCase {
	public void testList() {
		// SortedLinkedList list = new SortedLinkedList();
		System.out.println(2 << 10);
	}
}
