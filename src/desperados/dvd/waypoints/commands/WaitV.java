package desperados.dvd.waypoints.commands;

public class WaitV extends WaypointCommand {

	int ticks;

	public WaitV(int code, int ticks) {
		this.code = code;
		this.ticks = ticks;
	}

	@Override
	public String getString() {
		return String.format("%s(%d)", getName(), ticks);
	}

	@Override
	public byte[] getData() {
		return new byte[]{ (byte)code, (byte)ticks, (byte)(ticks >> 8) };
	}
}
