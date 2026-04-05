package desperados.dvd.waypoints.commands;

public class FaceTo extends WaypointCommand {

	int direction; // 0 - 15

	public FaceTo(int code, int direction) {
		this.code = code;
		this.direction = direction;
	}

	@Override
	public String getString() {
		return String.format("%s(%d)", getName(), direction);
	}

	@Override
	public byte[] getData() {
		return new byte[]{ (byte)code, (byte)direction, (byte)(direction >> 8) };
	}
}
