package desperados.dvd.obstacles;

import java.util.Locale;

public class ObstaclePoint {

	public float x, y, z0, z1;

	public ObstaclePoint(float x, float y, float z0, float z1) {
		this.x = x;
		this.y = y;
		this.z0 = z0;
		this.z1 = z1;
	}

	public String toString() {
		return String.format(Locale.US, "%.2f\t%.2f\t%.2f\t%.2f", x, y, z0, z1);
	}
}
