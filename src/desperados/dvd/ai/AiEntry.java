package desperados.dvd.ai;

import java.util.ArrayList;
import java.util.List;

public abstract class AiEntry {

	public short type;
	public short dir = -1; // 0 - 15, 0=South, 4=West, 8=North
	protected List<AiPoint> points;

	public void addPoint(AiPoint point) {
		if (points == null) {
			points = new ArrayList<AiPoint>();
		}
		points.add(point);
	}

	public List<AiPoint> getPoints() {
		return points;
	}

	public String toString() {
		String str = "AI\ttype " + type + "\n";
		for (AiPoint p : points) {
			str += "\t" + p.toString() + "\n";
		}
		if (dir != -1) {
			str += "direction: " + dir + "\n";
		}
		return str;
	}
}
