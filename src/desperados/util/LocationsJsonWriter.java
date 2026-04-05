package desperados.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import desperados.dvd.locations.*;

public class LocationsJsonWriter {

	public static void writeToFile(List<Location> elements, String filename) {
		ObjectMapper mapper = getMapper();
		Locations elem = arrayToObject(elements);
		
		try {
			mapper.writeValue(new File(filename), elem);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String writeToString(List<Location> list) {
		ObjectMapper mapper = getMapper();
		Locations elem = arrayToObject(list);
		
		try {
			return mapper.writeValueAsString(elem);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enableDefaultTyping();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.setSerializationInclusion(Include.NON_NULL);
		return mapper;
	}

	private static Locations arrayToObject(List<Location> list) {
		Locations elem = new Locations();
		elem.setElements(list);
		return elem;
	}
}
