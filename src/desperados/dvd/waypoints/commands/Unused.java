package desperados.dvd.waypoints.commands;

public class Unused extends WaypointCommand {

	int argument;

	public Unused(int code, int argument) {
		this.code = code;
		this.argument = argument;
	}

	@Override
	public String getString() {
		return String.format("%s(%d)", getName(), argument);
	}

	@Override
	public byte[] getData() {
		return new byte[]{ (byte)code, (byte)argument };
	}
}
