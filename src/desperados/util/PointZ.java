package desperados.util;

import java.io.IOException;

public class PointZ extends Point {

	public short z1;
	public short z2;

	public PointZ(short x, short y, short z1, short z2) {
		super(x, y);
		this.z1 = z1;
		this.z2 = z2;
	}

	public String toString() {
		return String.format("%d,%d,%d,%d", x, y, z1, z2);
	}

	public byte[] toByteArray() throws IOException {
		LittleEndianOuputStream stream = new LittleEndianOuputStream();
		stream.writeShort(x);
		stream.writeShort(y);
		stream.writeShort(z1);
		stream.writeShort(z2);
		return stream.getBytes();
	}
}
