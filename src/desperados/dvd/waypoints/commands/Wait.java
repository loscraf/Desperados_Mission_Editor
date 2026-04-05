package desperados.dvd.waypoints.commands;

public class Wait extends WaypointCommand {

	int ticks;

	public Wait(int code, int ticks) {
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
