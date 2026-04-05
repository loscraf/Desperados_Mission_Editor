package desperados.dvf;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DvfHeader {

	//private short unknown; // always 00 02, magic word?
	private int numSprites;
	private short maxWidth;	// bounding box?
	private short maxHeight;
	// private byte padding[20]; // always 0
	
	private List<DvfSprite> sprites;
	
	private Map<String, DvfObject> objects;

	public DvfHeader(short unknown, int numSprites, short maxWidth, short maxHeight) {
		//this.unknown = unknown;
		this.numSprites = numSprites;
		this.maxWidth = maxWidth;
		this.maxHeight = maxHeight;
		
		sprites = new ArrayList<DvfSprite>(numSprites);
		objects = new LinkedHashMap<>();
	}

	public int getNumSprites() {
		return numSprites;
	}

	public void addSprite(DvfSprite sprite) {
		sprites.add(sprite);
	}

	public DvfSprite getSprite(int index) {
		return sprites.get(index);
	}

	public void addObject(DvfObject obj) {
		objects.put(obj.getName(), obj);
	}

	public DvfObject getObject(String name) {
		if (!objects.containsKey(name)) {
			return null;
		}
		return objects.get(name);
	}

	public short getMaxWidth() {
		return maxWidth;
	}

	public short getMaxHeight() {
		return maxHeight;
	}
}
