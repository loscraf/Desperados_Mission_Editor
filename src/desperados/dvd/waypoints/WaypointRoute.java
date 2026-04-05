package desperados.dvd.waypoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.chunk.Identifiable;
import desperados.util.LittleEndianOuputStream;

public class WaypointRoute extends Identifiable {

	private List<Waypoint> waypoints;

	public WaypointRoute() {
		this.waypoints = new ArrayList<Waypoint>();
	}

	public void addWaypoint(Waypoint waypoint) {
		waypoints.add(waypoint);
	}

	public List<Waypoint> getWaypoints() {
		return waypoints;
	}

	public String toString() {
		String str = "";
		str += "Route() {\n";
		str += "  Identifier(\"" + identifier + "\");\n";
		int wid = 0;
		for (Waypoint w : waypoints) {
			str += "  Waypoint() { // " + (wid++) + "\n";
			str += w.toString();
			str += "  }\n";
		}
		str += "}";
		return str;
	}

	public void writeToStream(LittleEndianOuputStream stream) throws IOException {
		stream.writeShort(waypoints.size());
		for (Waypoint wp : waypoints) {
			wp.writeToStream(stream);
		}
	}
}
