package desperados.util;

import java.io.IOException;

public class Point {
	
	public short x;
	public short y;
	
	public Point() {}

	public Point(short x, short y) {
		this.x = x;
		this.y = y;
	}

	public String toString() {
		return String.format("%d,%d", x, y);
	}

	public byte[] toByteArray() throws IOException {
		LittleEndianOuputStream stream = new LittleEndianOuputStream();
		stream.writeShort(x);
		stream.writeShort(y);
		return stream.getBytes();
	}

	public Point add(Point other) {
		return new Point((short)(this.x + other.x), (short)(this.y + other.y));
	}

	public Point sub(Point other) {
		return new Point((short)(this.x - other.x), (short)(this.y - other.y));
	}
}
