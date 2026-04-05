package desperados.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import desperados.dvd.elements.*;

public class ElementsJsonWriter {

	public static void writeToFile(List<Element> elements, String filename) {
		ObjectMapper mapper = getMapper();
		Elements elem = arrayToObject(elements);
		
		try {
			mapper.writeValue(new File(filename), elem);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String writeToString(List<Element> list) {
		ObjectMapper mapper = getMapper();
		Elements elem = arrayToObject(list);
		
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

	private static Elements arrayToObject(List<Element> list) {
		Elements elem = new Elements();
		elem.setElements(list);
		return elem;
	}
}
