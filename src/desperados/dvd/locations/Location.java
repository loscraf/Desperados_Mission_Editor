package desperados.dvd.locations;

import java.io.IOException;

import desperados.dvd.chunk.Identifiable;
import desperados.exception.JsonParseException;
import desperados.util.LittleEndianOuputStream;
import desperados.util.Point;

public class Location extends Identifiable {

	private String classname;
	private Point[] points;
	private short unk1, unk2;

	public void addPoints(Point[] points) {
		this.points = points;
	}

	public void setUnknown1(short unk1) {
		this.unk1 = unk1;
	}

	public void setUnknown2(short unk2) {
		this.unk2 = unk2;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public Point[] getPoints() {
		return points;
	}

	public short getUnknown1() {
		return unk1;
	}

	public short getUnknown2() {
		return unk2;
	}

	public String getClassname() {
		return classname;
	}

	public String toString() {
		String str = "";
		for (Point p : points) {
			str += p.toString() + "\n";
		}
		str += "\t" + unk1 + "," + unk2 + "\n";
		if (classname != null) {
			str += classname + "\n";
		}
		return str;
	}

	public void checkIntegrity() throws JsonParseException {
		if (points == null) {
			throw new JsonParseException("Error: Location has no points!");
		}
	}

	public void writeToStream(LittleEndianOuputStream stream) throws IOException {
		stream.writeShort((short)points.length);
		for (int j = 0; j < points.length; j++) {
			stream.writeBytes(points[j].toByteArray());
		}
		stream.writeShort(unk1);
		stream.writeShort(unk2);
		if (classname == null) {
			stream.writeByte(0);
		} else {
			stream.writeByte(1);
			stream.writeString(classname);
		}
	}
}
