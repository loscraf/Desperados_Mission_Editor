package desperados.dvd.obstacles;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Obstacle {

	private List<ObstaclePoint> points;
	private float[] minMax;
	private Extra extra;
	private byte[] bytes;
	private float[] floats;
	private int unknown1;
	private byte unknown2;

	public Obstacle() {
		points = new ArrayList<ObstaclePoint>();
		bytes = new byte[4];
		floats = new float[2];
	}

	public void addPoint(float x, float y, float z0, float z1) {
		points.add(new ObstaclePoint(x, y, z0, z1));
	}

	public List<ObstaclePoint> getPoints() {
		return points;
	}

	public void setMinMax(float[] minMax) {
		this.minMax = minMax;
	}

	public void setExtra(short e1, short e2) {
		this.extra = new Extra(e1, e2);
	}

	public void setBytes(byte b0, byte b1, byte b2, byte b3) {
		bytes[0] = b0;
		bytes[1] = b1;
		bytes[2] = b2;
		bytes[3] = b3;
	}

	public void setFloats(float f0, float f1) {
		floats[0] = f0;
		floats[1] = f1;
	}

	public void setUnknownInt(int i) {
		unknown1 = i;
	}

	public void setUnknownByte(byte b) {
		unknown2 = b;
	}

	public boolean hasExtra() {
		if (extra == null) return false;
		if (extra.e1 == 0) return false;
		return true;
	}

	class Extra {
		short e1, e2;
		
		Extra(short e1, short e2) {
			this.e1 = e1;
			this.e2 = e2;
		}
	}

	public String getExtra() {
		if (!hasExtra()) return "";
		return "" + extra.e1 + "," + extra.e2;
	}

	public ObstaclePoint getCenter() {
		return new ObstaclePoint((minMax[0] + minMax[3]) / 2, (minMax[2] + minMax[5]) / 2, minMax[1], minMax[4]);
	}

	public String toString() {
		if (!hasExtra()) return "";
		
		String str = "Obstacle:\n";
		for (ObstaclePoint p : points) {
			str += "\t" + p.toString() + "\n";
		}
		str += String.format(Locale.US, "\n\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\n", minMax[0], minMax[1], minMax[2], minMax[3], minMax[4], minMax[5]);
		
		if (hasExtra()) {
			str += String.format("\n\t%d\t%d\n", extra.e1, extra.e2);
		}
		str += String.format("\n\t%d\t%d\t%d\t%d\n", bytes[0], bytes[1], bytes[2], bytes[3]);
		str += String.format(Locale.US, "\t%.2f\t%.2f\n", floats[0], floats[1]);
		str += String.format("\t%d\t%d\n\n", unknown1, unknown2);
		
		return str;
	}
}
