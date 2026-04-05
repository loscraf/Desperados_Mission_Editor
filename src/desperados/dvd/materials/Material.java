package desperados.dvd.materials;

import java.util.List;

import desperados.util.Point;

public class Material {

	private byte type;
	//private float f1, f2;
	//private int unk1;
	//private byte unk2;
	private List<Point> points;

	public Material(byte type, float f1, float f2, int unk1, byte unk2, List<Point> points) {
		this.type = type;
		//this.f1 = f1;
		//this.f2 = f2;
		//this.unk1 = unk1;
		//this.unk2 = unk2;
		this.points = points;
	}

	public List<Point> getPoints() {
		return points;
	}

	public byte getType() {
		return type;
	}
}
