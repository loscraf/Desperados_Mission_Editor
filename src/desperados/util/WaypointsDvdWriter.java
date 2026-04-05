package desperados.util;

import java.io.IOException;
import java.util.List;

import desperados.dvd.waypoints.WaypointRoute;

public class WaypointsDvdWriter {
	
	public static byte[] write(List<WaypointRoute> routes) throws IOException {
		
		LittleEndianOuputStream stream = new LittleEndianOuputStream();
		
		stream.writeInt(1); // version
		stream.writeShort(routes.size());
		
		for (WaypointRoute route : routes) {
			route.writeToStream(stream);
		}
		
		byte[] buffer = stream.getBytes();
		
		stream = new LittleEndianOuputStream();
		stream.writeBytes("WAYS".getBytes());
		stream.writeInt(buffer.length);
		stream.writeBytes(buffer);
		
		buffer = stream.getBytes();
		
		return buffer;
	}
}
