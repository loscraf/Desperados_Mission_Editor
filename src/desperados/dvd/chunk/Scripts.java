package desperados.dvd.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.locations.Location;
import desperados.util.Point;

public class Scripts extends Chunk {

	@Override
	public void readChunk() throws IOException {
		
		short numEntries = stream.readShort();
		
		List<Location> locations = new ArrayList<>(numEntries);
		
		for (int i = 0; i < numEntries; i++) {
			Location loc = new Location();
			locations.add(loc);
			loc.setIdentifier("Location_" + i);
			
			short numPoints = stream.readShort();
			Point[] points = new Point[numPoints];
			for (int j = 0; j < numPoints; j++) {
				points[j] = new Point(stream.readShort(), stream.readShort());
			}
			loc.addPoints(points);
			
			loc.setUnknown1(stream.readShort());
			loc.setUnknown2(stream.readShort());
			
			byte classNamePresent = stream.readByte();
			
			if (classNamePresent == 1) {
				String classname = stream.readString();
				loc.setClassname(classname);
			}
		}
		
		dvdContainer.setLocations(locations);
	}
}
