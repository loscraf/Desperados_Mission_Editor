package desperados.dvd.waypoints.commands;

public class CheckForSync extends WaypointCommand {

	int arg0;
	int arg1;
	int arg2;

	public CheckForSync(int code, int arg0, int arg1, int arg2) {
		this.code = code;
		this.arg0 = arg0;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}

	@Override
	public String getString() {
		return String.format("%s(%d,%d,%d)", getName(), arg0, arg1, arg2);
	}

	@Override
	public byte[] getData() {
		return new byte[]{ (byte)code, (byte)arg0, (byte)(arg0 >> 8), (byte)arg1, (byte)(arg1 >> 8), (byte)arg2, (byte)(arg2 >> 8) };
	}
}
