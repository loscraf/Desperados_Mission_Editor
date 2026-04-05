package desperados.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import desperados.dvd.locations.Location;
import desperados.dvd.locations.Locations;
import desperados.exception.JsonParseException;

public class LocationsJsonReader {

	private static final int FILE_MODE = 0;
	private static final int STRING_MODE = 1;

	public static Location[] readFromFile(String filename) throws JsonParseException {
		return readFromString(filename, FILE_MODE);
	}

	public static Location[] readFromString(String str) throws JsonParseException {
		return readFromString(str, STRING_MODE);
	}

	public static Location[] readFromString(String string, int mode) throws JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		
		Locations locations = null;
		try {
			if (mode == FILE_MODE) {
				locations = mapper.readValue(new File(string), Locations.class);
			} else if (mode == STRING_MODE) {
				locations = mapper.readValue(string, Locations.class);
			} else {
				return null;
			}
		} catch (IOException e) {
			throw new JsonParseException(e.getMessage());
		}
		
		for (Location loc : locations.getLocations()) {
			loc.checkIntegrity();
		}
		
		return locations.getLocations();
	}
}
