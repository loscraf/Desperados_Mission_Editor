package desperados.util;

import java.io.IOException;
import java.util.List;

import desperados.dvd.elements.Element;
import desperados.service.AnimationService;

public class ElementsDvdWriter {
	
	public static byte[] write(List<Element> elements, AnimationService animService) throws IOException {
		
		LittleEndianOuputStream stream = new LittleEndianOuputStream();
		
		stream.writeInt(28); // version
		stream.writeShort(elements.size());
		
		for (Element e : elements) {
			e.writeToStream(stream, animService);
		}
		
		byte[] buffer = stream.getBytes();
		
		stream = new LittleEndianOuputStream();
		stream.writeBytes("ELEM".getBytes());
		stream.writeInt(buffer.length);
		stream.writeBytes(buffer);
		
		buffer = stream.getBytes();
		
		return buffer;
	}
}
