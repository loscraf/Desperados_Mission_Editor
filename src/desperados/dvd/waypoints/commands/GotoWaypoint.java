package desperados.dvd.waypoints.commands;

public class GotoWaypoint extends WaypointCommand {

	int waypointIndex;

	public GotoWaypoint(int code, int index) {
		this.code = code;
		this.waypointIndex = index;
		// TODO: lookup waypoint by id
	}

	@Override
	public String getString() {
		return String.format("%s(%d)", getName(), waypointIndex);
	}

	@Override
	public byte[] getData() {
		return new byte[]{ (byte)code, (byte)waypointIndex, (byte)(waypointIndex >> 8) };
	}
}
