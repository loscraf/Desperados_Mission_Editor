package desperados.dvd.waypoints.commands;

import java.util.Locale;

public class SetSpeed extends WaypointCommand {

	float speed;

	public SetSpeed(int code, float speed) {
		this.code = code;
		this.speed = speed;
	}

	@Override
	public String getString() {
		return String.format(Locale.US, "%s(%.2f)", getName(), speed);
	}

	@Override
	public byte[] getData() {
		int intBits =  Float.floatToIntBits(speed);
		return new byte[]{ (byte)code, (byte)intBits, (byte) (intBits >> 8), (byte) (intBits >> 16), (byte) (intBits >> 24) };
	}
}
