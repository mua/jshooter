package engine.structures;

import java.util.HashMap;
import java.util.Map;

import engine.gtypes.Vec3;

public class Grid extends SpacePartitioner {
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
