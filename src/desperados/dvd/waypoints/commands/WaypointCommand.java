package desperados.dvd.waypoints.commands;

import desperados.dvd.chunk.Waypoints;

public abstract class WaypointCommand {

	protected int code;

	public String getString() {
		return String.format("%s()", getName());
	}

	public byte[] getData() {
		return new byte[]{ (byte)code };
	}

	public String getName() {
		return Waypoints.NAMES[code % 117]; // codes jump from ...9,10 to 128,129...
	}
}
