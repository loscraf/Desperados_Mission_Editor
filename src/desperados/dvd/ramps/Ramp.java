package desperados.dvd.ramps;

import desperados.util.Point;

public class Ramp {

	private Point point1, point2;
	private short unk1, unk2, unk3;

	public Ramp(short x1, short y1, short x2, short y2, short unk1, short unk2, short unk3) {
		point1 = new Point(x1, y1);
		point2 = new Point(x2, y2);
		this.unk1 = unk1;
		this.unk2 = unk2;
		this.unk3 = unk3;
	}

	public String toString() {
		return String.format("%s\n%s\t%d, %d, %d", point1, point2, unk1, unk2, unk3);
	}
}
