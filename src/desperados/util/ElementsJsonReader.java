package desperados.util;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import desperados.dvd.elements.Element;
import desperados.dvd.elements.Elements;
import desperados.exception.JsonParseException;

public class ElementsJsonReader {
	
	private static final int FILE_MODE = 0;
	private static final int STRING_MODE = 1;
	
	public static Element[] readFromFile(String filename) throws JsonParseException {
		return readFromString(filename, FILE_MODE);
	}

	public static Element[] readFromString(String str) throws JsonParseException {
		return readFromString(str, STRING_MODE);
	}

	public static Element[] readFromString(String string, int mode) throws JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		
		Elements elements = null;
		try {
			if (mode == FILE_MODE) {
				elements = mapper.readValue(new File(string), Elements.class);
			} else if (mode == STRING_MODE) {
				elements = mapper.readValue(string, Elements.class);
			} else {
				return null;
			}
		} catch (IOException e) {
			throw new JsonParseException(e.getMessage());
		}
		
		for (Element e : elements.getElements()) {
			e.checkIntegrity();
		}
		
		return elements.getElements();
	}
}
