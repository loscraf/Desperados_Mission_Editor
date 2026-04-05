package desperados.dvd.waypoints.commands;

import java.util.Locale;

public class AdjustSpeed extends WaypointCommand {

	float speed;
	int arg2;

	public AdjustSpeed(int code, float speed, int arg2) {
		this.code = code;
		this.speed = speed;
		this.arg2 = arg2;
	}

	@Override
	public String getString() {
		return String.format(Locale.US, "%s(%.2f,%d)", getName(), speed, arg2);
	}

	@Override
	public byte[] getData() {
		int intBits =  Float.floatToIntBits(speed);
		return new byte[]{ (byte)code, (byte)intBits, (byte) (intBits >> 8), (byte) (intBits >> 16), (byte) (intBits >> 24), (byte)arg2, (byte)(arg2 >> 8) };
	}
}
