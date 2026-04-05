package desperados.dvd.waypoints.commands;

public class MobileSprite1 extends WaypointCommand {

	int argument;

	public MobileSprite1(int code, int argument) {
		this.code = code;
		this.argument = argument;
	}

	@Override
	public String getString() {
		return String.format("%s(%d)", getName(), argument);
	}

	@Override
	public byte[] getData() {
		return new byte[]{ (byte)code, (byte)argument, (byte)(argument >> 8) };
	}
}
