package desperados.dvd;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import desperados.dvd.chunk.Chunk;
import desperados.exception.DvdReadException;
import desperados.util.LittleEndianInputStream;

public class DvdReader {

	private LittleEndianInputStream stream;
	private DvdContainer dvdContainer;

	public DvdContainer readFile(String filename) throws DvdReadException {
		
		dvdContainer = new DvdContainer();
		
		try {
			return read(filename);
		} catch (IOException e) {
			throw new DvdReadException(e.getMessage());
		} finally {
			if (stream != null) {
				try { stream.close(); } catch (IOException e) {}
			}
		}
	}

	private DvdContainer read(String filename) throws IOException, DvdReadException {
		
		Map<String, Class<?>> classMap = new HashMap<String, Class<?>>();
		try {
			String pkg = "desperados.dvd.chunk.";
			classMap.put("MISC", Class.forName(pkg + "Misc"));
			classMap.put("BGND", Class.forName(pkg + "Background"));
			classMap.put("MOVE", Class.forName(pkg + "Move"));
			classMap.put("SGHT", Class.forName(pkg + "Obstacles"));
			classMap.put("MASK", Class.forName(pkg + "Mask"));
			classMap.put("WAYS", Class.forName(pkg + "Waypoints"));
			classMap.put("ELEM", Class.forName(pkg + "Elem"));
			classMap.put("FXBK", Class.forName(pkg + "Effects"));
			classMap.put("MSIC", Class.forName(pkg + "Music"));
			classMap.put("SND ", Class.forName(pkg + "Sound"));
			classMap.put("PAT ", Class.forName(pkg + "AdditionalSprites"));
			classMap.put("BOND", Class.forName(pkg + "LaddersAndStairs"));
			classMap.put("MAT ", Class.forName(pkg + "Materials"));
			classMap.put("LIFT", Class.forName(pkg + "StairWaypoints"));
			classMap.put("AI  ", Class.forName(pkg + "AI"));
			classMap.put("BUIL", Class.forName(pkg + "Buildings"));
			classMap.put("SCRP", Class.forName(pkg + "Scripts"));
			classMap.put("JUMP", Class.forName(pkg + "JumpZones"));
			classMap.put("CART", Class.forName(pkg + "MovingObjects"));
			classMap.put("DLGS", Class.forName(pkg + "Dialogs"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		stream = new LittleEndianInputStream(filename);
		
		while (!stream.eof()) {
			String chunkId = stream.readString(4);
			
			if (!classMap.containsKey(chunkId)) {
				//throw new DvdReadException("Invalid Chunk ID: " + chunkId + " at position: " + stream.getPosition());
				return dvdContainer;
			}
			
			List<Chunk> chunks = new ArrayList<Chunk>();
			
			try {
				Chunk c = (Chunk) classMap.get(chunkId).getDeclaredConstructor().newInstance();
				c.initialize(stream, filename, dvdContainer, chunkId);
				c.readChunk();
				chunks.add(c);
				
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new DvdReadException(e.getMessage());
			}
		}
		
		return dvdContainer;
	}
}
