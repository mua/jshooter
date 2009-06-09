package engine.structures;

import java.util.ArrayList;
import java.util.List;

import engine.gtypes.Vec3;

public class Octree extends SpacePartitioner {
	Node root = new Node();
	int maxDepth = 7;

	class Node extends Cell {
		Node[] nodes = new Node[8];
		boolean isLeaf = true;
		Vec3 center;
		float halfWidth = 3000;
		int depth = 0;

		public int getID(Vec3 point) {
			int ret = 0;
			ret += (point.x < center.x ? 0 : 4);
			ret += (point.y < center.y ? 0 : 2);
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
						center = new Vec3((x * 2 - 1) * halfWidth, (y * 2 - 1)
								* halfWidth, (z * 2 - 1) * halfWidth);
						center.add(this.center);
						node = new Node();
						nodes[(x << 2) + (y << 1) + z] = node;
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
			if (isLeaf) {
				ret.add(this);
				return ret;
			}
			for (int x = (start & 4) >> 2; x <= (end & 4) >> 2; x++)
				for (int y = (start & 2) >> 1; y <= (end & 2) >> 1; y++)
					for (int z = (start & 1); z <= (end & 1); z++) {
						node = nodes[(x << 2) + (y << 1) + z];
						ret.addAll(node.getNodes(min, max, create));
					}
			return ret;
		}
	}

	public Octree() {
		root.center = new Vec3(0, 0, 0);
	}

	@Override
	public Cell[] getCells(Vec3 min, Vec3 max, boolean create) {
		List<Node> cells = root.getNodes(min, max, create);
		return cells.toArray(new Cell[cells.size()]);
	}
}
