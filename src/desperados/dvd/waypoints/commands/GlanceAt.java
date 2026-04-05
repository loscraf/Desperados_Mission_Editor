package desperados.dvd.waypoints.commands;

public class GlanceAt extends WaypointCommand {

	int x;
	int y;

	public GlanceAt(int code, int x, int y) {
		this.code = code;
		this.x = x;
		this.y = y;
	}

	@Override
	public String getString() {
		return String.format("%s(%d,%d)", getName(), x, y);
	}

	@Override
	public byte[] getData() {
		return new byte[]{ (byte)code, (byte)x, (byte)(x >> 8), (byte)y, (byte)(y >> 8) };
	}
}
