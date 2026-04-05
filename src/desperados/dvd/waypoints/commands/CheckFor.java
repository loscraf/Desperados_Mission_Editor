package desperados.dvd.waypoints.commands;

public class CheckFor extends WaypointCommand {

	int arg0;
	int arg1;

	public CheckFor(int code, int arg0, int arg1) {
		this.code = code;
		this.arg0 = arg0;
		this.arg1 = arg1;
	}

	@Override
	public String getString() {
		return String.format("%s(%d,%d)", getName(), arg0, arg1);
	}

	@Override
	public byte[] getData() {
		return new byte[]{ (byte)code, (byte)arg0, (byte)(arg0 >> 8), (byte)arg1, (byte)(arg1 >> 8) };
	}
}
