package desperados.dvd.buildings;

import java.util.ArrayList;
import java.util.List;

import desperados.util.Point;
import desperados.util.PointZ;

public class Door {

	private byte[] flags;
	private List<Point> selectBox;
	private List<PointZ> doorPath;

	public Door(byte[] flags) {
		this.flags = flags;
		this.selectBox = new ArrayList<Point>();
		this.doorPath = new ArrayList<PointZ>();
	}

	public void addPath(short x, short y, short z1, short z2) {
		doorPath.add(new PointZ(x, y, z1, z2));
	}

	public void addPoint(short x, short y) {
		selectBox.add(new Point(x, y));
	}

	public List<Point> getSelectBox() {
		return selectBox;
	}

	public List<PointZ> getDoorPath() {
		return doorPath;
	}

	public String toString() {
		String str = "Door:\n";
		for (int i = 0; i < flags.length; i++) {
			if (i != 0) str += ", ";
			str += flags[i];
		}
		str += "\n";
		
		for (Point p : selectBox) {
			str += p.toString() + "\n";
		}
		
		str += "\n";
		
		for (PointZ p : doorPath) {
			str += p.toString() + "\n";
		}
		
		return str;
	}
}
