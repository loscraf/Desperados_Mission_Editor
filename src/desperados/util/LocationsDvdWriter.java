package desperados.util;

import java.io.IOException;
import java.util.List;

import desperados.dvd.locations.Location;

public class LocationsDvdWriter {
	
	public static byte[] write(List<Location> locations) throws IOException {
		
		LittleEndianOuputStream stream = new LittleEndianOuputStream();
		
		stream.writeInt(1); // version
		stream.writeShort(locations.size());
		
		for (Location loc : locations) {
			loc.writeToStream(stream);
		}
		
		byte[] buffer = stream.getBytes();
		
		stream = new LittleEndianOuputStream();
		stream.writeBytes("SCRP".getBytes());
		stream.writeInt(buffer.length);
		stream.writeBytes(buffer);
		
		buffer = stream.getBytes();
		
		return buffer;
	}
}
