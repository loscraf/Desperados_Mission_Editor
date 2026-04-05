package desperados.dvf;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import desperados.dvd.elements.Element;
import desperados.exception.DvfReadException;
import desperados.ui.EditorWindow;
import desperados.util.LittleEndianInputStream;

public class DvfReader {

	private String gameDir;
	private String outputPath;
	private LittleEndianInputStream stream;
	private boolean outputLog;

	private static enum Type { CHARACTER, ANIMATION, ACCESSORY };
	
	private Map<String, DvfHeader> dvfFiles;

	public DvfReader() {
		this.dvfFiles = new LinkedHashMap<>();
		this.gameDir = EditorWindow.gameDir;
	}

	public DvfReader(String gameDir, String outputPath, boolean outputLog) {
		this.dvfFiles = new LinkedHashMap<>();
		this.gameDir = gameDir;
		this.outputPath = outputPath;
		this.outputLog = outputLog;
	}

	public DvfHeader loadAnimation(Element element) {
		try {
			return readFile(element, Type.ANIMATION);
		} catch (DvfReadException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DvfHeader loadCharacter(Element element) {
		try {
			return readFile(element, Type.CHARACTER);
		} catch (DvfReadException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DvfHeader loadAccessory(Element element) {
		try {
			return readFile(element, Type.ACCESSORY);
		} catch (DvfReadException e) {
			e.printStackTrace();
		}
		return null;
	}

	public DvfHeader readFile(String dvfName, Type type) throws DvfReadException {
		try {
			return read(dvfName, type);
		} catch (IOException e) {
			throw new DvfReadException(e.getMessage());
		} finally {
			if (stream != null) {
				try { stream.close(); } catch (IOException e) {}
			}
		}
	}

	public DvfHeader readFile(Element element, Type type) throws DvfReadException {
		try {
			return read(element.getDvf(), type);
		} catch (IOException e) {
			throw new DvfReadException(e.getMessage());
		} finally {
			if (stream != null) {
				try { stream.close(); } catch (IOException e) {}
			}
		}
	}

	public DvfHeader readFile(File file) throws DvfReadException {
		try {
			return read(file);
		} catch (IOException e) {
			throw new DvfReadException(e.getMessage());
		} finally {
			if (stream != null) {
				try { stream.close(); } catch (IOException e) {}
			}
		}
	}

	private DvfHeader read(String dvfName, Type type) throws IOException {

		if (dvfFiles.containsKey(dvfName)) {
			return dvfFiles.get(dvfName);
		}
		
		String filename;
			
		if (type == Type.ANIMATION) {
			filename = gameDir + "\\data\\animations\\" + dvfName + ".dvf";
		} else { // CHARACTER and ACCESSORY
			filename = gameDir + "\\data\\characters\\" + dvfName + ".dvf";
		}
		
		stream = new LittleEndianInputStream(filename);
		
		DvfHeader header = readHeader();
		readSprites(header);
		readInfo(header);
		
		dvfFiles.put(dvfName, header);
		
		return header;
	}

	private DvfHeader read(File dvfFile) throws IOException {
		stream = new LittleEndianInputStream(dvfFile);
		DvfHeader header = readHeader();
		readSprites(header);
		readInfo(header);
		return header;
	}

	private DvfHeader readHeader() throws IOException {
		DvfHeader header = new DvfHeader(stream.readShort(), stream.readInt(), stream.readShort(), stream.readShort());
		stream.skip(20); // padding, always 0
		return header;
	}

	private void readSprites(DvfHeader header) throws IOException {
		for (int i = 0; i < header.getNumSprites(); i++) {
			int size = stream.readInt();
			DvfSprite sprite = new DvfSprite(stream.readShort(), stream.readShort(), stream.readShort(), stream.readBytes(size));
			header.addSprite(sprite);
		}
	}

	private void extractAllImages(DvfHeader header, String dvfName) {
		for (int i = 0; i < header.getNumSprites(); i++) {
			extractImage(header, dvfName, i);
		}
	}

	private void extractImage(DvfHeader header, String dvfName, int spriteIndex) {
		DvfSprite sprite = header.getSprite(spriteIndex);
		sprite.createPNG(spriteIndex, outputPath, dvfName);
	}

	/*
	private void extractImageData(ImageData imageData, String dvfName, int spriteIndex) {
		String filename = outputPath + "\\" + dvfName + "\\sprite_" + spriteIndex + ".png";
		ImageLoader saver = new ImageLoader();
		saver.data = new ImageData[] { imageData };
		saver.save(filename, SWT.IMAGE_PNG);
	}
	*/

	private void readInfo(DvfHeader header) throws IOException {
		
		int numObjects = stream.readShort();
		
		for (int objIndex = 0; objIndex < numObjects; objIndex++) {
			
			String objName = stream.readString(32);
			short numDirections = stream.readShort();
			stream.skip(32); // always 0
			short numAnimations = stream.readShort();
			stream.skip(16); // always 0
			short width  = stream.readShort();
			short height = stream.readShort();
			int originX = stream.readInt();
			int originY = stream.readInt();
			stream.skip(20); // always 0
			
			DvfObject obj = new DvfObject(objName, numDirections, numAnimations, width, height, originX, originY);
			log(obj.toString());
			
			for (int animIndex = 0; animIndex < numAnimations; animIndex++) {
				log("  Animation " + animIndex + "\n");
				
				DvfAnimation animation = null;
				
				for (int dirIndex = 0; dirIndex < numDirections; dirIndex++) {
					stream.skip(4); // always 0
					short numFrames = stream.readShort();
					short unknown3 = stream.readShort(); // usually numFrames - 1
					short unknown4 = stream.readShort();
					int origX = stream.readInt(); // usually identical to above originX, sometimes a big number
					int origY = stream.readInt(); // usually identical to above originY, sometimes a big number
					short direction = stream.readShort();
					short animID = stream.readShort();
					String animName = stream.readString(32);
				
					if (dirIndex == 0) {
						animation = new DvfAnimation(animName, animID, numDirections);
						obj.addAnimation(animation);
					}
					
					DvfFrameset frameset = new DvfFrameset(numFrames, unknown3, unknown4, origX, origY, direction, animID, animName);
					animation.addFrameset(frameset);
					log(frameset.toString());
					
					for (int m = 0; m < numFrames; m++) {
						short spriteIndex = stream.readShort();
						short animTime = stream.readShort();
						short unk3 = stream.readShort();
						short horOffset = stream.readShort();
						short verOffset = stream.readShort();
						short soundIndex = stream.readShort();
						short unk7 = stream.readShort();
						
						DvfFrame frame = new DvfFrame(header.getSprite(spriteIndex), spriteIndex, animTime, unk3, horOffset, verOffset, soundIndex, unk7);
						frameset.addFrame(frame);
						log(frame.toString());
						
						/*
						// extract the first frame of each "Attendre" sequence for all directions
						if (type == Type.CHARACTER && objectName != null && objName.equals(objectName) && animID == 0 && m == 0) {
							DvfSprite sprite = header.getSprite(spriteIndex);
							header.addFrame(sprite.getImageData());
						}
						
						// extract first animation
						if (type == Type.ANIMATION && objectName != null && objName.equals(objectName) && animIndex == 0) {
							DvfSprite sprite = header.getSprite(spriteIndex);
							header.addFrame(sprite.getImageData());
							
							// extractImageData(sprite.getImageData(), dvfName, spriteIndex);
							// extractImage(header, dvfName, spriteIndex);
						}
						
						if (type == Type.ACCESSORY && objectName != null && objName.equals(objectName) && m == 0) {
							DvfSprite sprite = header.getSprite(spriteIndex);
							header.addFrame(sprite.getImageData());
						}
						*/
					}
				}
			}
			
			header.addObject(obj);
		}
	}

	private void log(String message) {
		if (outputLog) System.out.println(message);
	}

	// just for testing
	public static void main(String[] args) {
		
		String gameDir = "C:\\Games\\Desperados Wanted Dead or Alive";
		String outputPath = "C:\\dvf_output";
		String filename = "accessories"; // without .dvf
		Type type = Type.ACCESSORY;
		
		DvfReader dvfReader = new DvfReader(gameDir, outputPath, false);
		
		try {
			DvfHeader header = dvfReader.readFile(filename, type);
			dvfReader.extractAllImages(header, filename);
			
			/*
			File f = new File(gameDir + "\\data\\animations\\");
			File[] listOfFiles = f.listFiles();
			for (File file : listOfFiles) {
				if (file.isFile() && file.getName().endsWith(".dvf")) {
					dvfReader.readFile(filename, null, type);
				}
			}
			*/
		} catch (DvfReadException e) {
			e.printStackTrace();
		}
	}

	public Map<String, DvfHeader> getDvfFiles() {
		return dvfFiles;
	}

}
