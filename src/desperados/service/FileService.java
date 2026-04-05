package desperados.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Display;

import desperados.MainGUI;
import desperados.dvd.DvdContainer;
import desperados.dvd.DvdReader;
import desperados.dvd.elements.Element;
import desperados.dvd.locations.Location;
import desperados.dvd.waypoints.WaypointRoute;
import desperados.exception.DvdReadException;
import desperados.exception.IdentifierNotFoundException;
import desperados.exception.IndexNotFoundException;
import desperados.exception.JsonParseException;
import desperados.exception.ServiceException;
import desperados.scb.ScbReader;
import desperados.util.BuildingsTextWriter;
import desperados.util.ElementsDvdWriter;
import desperados.util.ElementsJsonReader;
import desperados.util.ElementsJsonWriter;
import desperados.util.LocationsDvdWriter;
import desperados.util.LocationsJsonReader;
import desperados.util.LocationsJsonWriter;
import desperados.util.WaypointsDvdWriter;

public class FileService {

	public static final int MISC = 0;
	public static final int BGND = 1;
	public static final int MOVE = 2;
	public static final int SGHT = 3;
	public static final int MASK = 4;
	public static final int WAYS = 5;
	public static final int ELEM = 6;
	public static final int FXBK = 7;
	public static final int MSIC = 8;
	public static final int SND  = 9;
	public static final int PAT  = 10;
	public static final int BOND = 11;
	public static final int MAT  = 12;
	public static final int LIFT = 13;
	public static final int AI   = 14;
	public static final int BUIL = 15;
	public static final int SCRP = 16;
	public static final int JUMP = 17;
	public static final int CART = 18;
	public static final int DLGS = 19;

	private static MainGUI main;
	// private static String gameDir;
	private static DvdContainer dvdContainer;
	
	private static ElementService elementService;
	private static ObstacleService obstacleService;
	private static WaypointService waypointService;
	private static AiService aiService;
	private static LocationService locationService;
	private static DoorService doorService;
	private static MaterialService materialService;
	private static CoordinateService coordinateService;
	private static AnimationService animationService;
	
	private static ScbReader scbReader;
	
	private static File dvdFile;

	public static void setMain(MainGUI mainGui) {
		main = mainGui;
	}

	/*
	public static void setGameDir(String gd) {
		gameDir = gd;
	}
	*/

	public static void readFile(String filename) throws ServiceException {
		File file = new File(filename);
		if (!file.exists() || !file.isFile()) {
			return;
		}
		
		if (filename.endsWith(".dvd")) {
			dvdFile = file;
			extractDvd(file);
		} else if (filename.endsWith(".json")) {
			writeElementsFromFileToDvd(file);
		}
	}

	public static String readScbFile() {
		if (scbReader == null) {
			String scbFilename = dvdFile.getAbsolutePath();
			scbFilename = scbFilename.substring(0, scbFilename.length() - 4) + ".scb";
			try {
				scbReader = new ScbReader(scbFilename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return scbReader.toString();
	}

	public static void writeElementsFromStringToDvd(String str) throws ServiceException {
		if (!dvdFile.exists()) {
			return;
		}
		
		List<Element> elements = null;
		try {
			elements = Arrays.asList(ElementsJsonReader.readFromString(str));
			writeElementsToDvd(dvdFile, elements);
			dvdContainer.setElements(elements);
			elementService.setElements(elements);
			//animationService = new AnimationService(gameDir, elementService);
		} catch (JsonParseException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	private static void writeElementsFromFileToDvd(File file) throws ServiceException {
		File dvdFile = new File(file.getAbsolutePath().replaceFirst(".json", ".dvd"));
		if (!dvdFile.exists()) {
			return;
		}
		
		List<Element> elements = null;
		try {
			elements = Arrays.asList(ElementsJsonReader.readFromFile(file.getAbsolutePath()));
			writeElementsToDvd(dvdFile, elements);
			dvdContainer.setElements(elements);
			elementService.setElements(elements);
			//animationService = new AnimationService(gameDir, elementService);
		} catch (JsonParseException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public static void writeElementsToDvd(File dvdFile, List<Element> elements) throws ServiceException {
		try {
			byte[] buffer = ElementsDvdWriter.write(elements, animationService);
			patchDvd(dvdFile, ELEM, buffer);
		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public static void writeLocationsFromStringToDvd(String str) throws ServiceException {
		if (!dvdFile.exists()) {
			return;
		}
		
		List<Location> locations = null;
		try {
			locations = Arrays.asList(LocationsJsonReader.readFromString(str));
		} catch (JsonParseException e) {
			throw new ServiceException(e.getMessage());
		}
		writeLocationsToDvd(dvdFile, locations);
		dvdContainer.setLocations(locations);
		locationService.setLocations(locations);
	}

	public static void writeLocationsToDvd(File dvdFile, List<Location> locations) throws ServiceException {
		try {
			byte[] buffer = LocationsDvdWriter.write(locations);
			patchDvd(dvdFile, SCRP, buffer);
		} catch (IOException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	public static void writeWaypointsToDvd(List<WaypointRoute> routes) throws IOException {
		if (!dvdFile.exists()) {
			return;
		}
		byte[] buffer = WaypointsDvdWriter.write(routes);
		patchDvd(dvdFile, WAYS, buffer);
		
		dvdContainer.setWaypointRoutes(routes);
		waypointService.setWaypointRoutes(routes);
	}

	public static void writeScriptToScb(byte[] data) throws IOException {
		String scbFilename = dvdFile.getAbsolutePath();
		scbFilename = scbFilename.substring(0, scbFilename.length() - 4) + ".scb";
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(scbFilename));
		bos.write(data);
		bos.close();
	}

	private static void patchDvd(File dvdFile, int blockIndex, byte[] buffer) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(dvdFile, "rw");
		
		int blockStart = 0;
		int blockEnd = 0;
		int fileLength = (int) raf.length();
		
		for (int i = 0; i <= blockIndex; i++) {
			raf.skipBytes(4);
			int size = raf.readUnsignedByte() | raf.readUnsignedByte() << 8 | raf.readUnsignedByte() << 16 | raf.readUnsignedByte() << 24;
			if (i == blockIndex) {
				blockStart = (int)raf.getFilePointer() - 8;
				blockEnd   = (int)raf.getFilePointer() + size;
			} else {
				raf.skipBytes(size);
			}
		}
		raf.seek(0);
		
		byte[] lastPart = new byte[fileLength - blockEnd];
		raf.seek(blockEnd);
		raf.read(lastPart);
		
		raf.setLength(blockStart);
		raf.seek(blockStart);
		raf.write(buffer);
		raf.write(lastPart);
		raf.close();
	}

	private static void extractDvd(File file) throws ServiceException {
		DvdReader dvdReader = new DvdReader();
		
		try {
			dvdContainer = dvdReader.readFile(file.getAbsolutePath());
		} catch (DvdReadException e) {
			throw new ServiceException(e.getMessage());
		}
		
		if (dvdContainer != null) {
			elementService = new ElementService();
			elementService.setElements(dvdContainer.getElements());
			
			obstacleService = new ObstacleService();
			obstacleService.setObstacles(dvdContainer.getObstacles());
			
			waypointService = new WaypointService();
			waypointService.setWaypointRoutes(dvdContainer.getWaypointRoutes());
			
			aiService = new AiService();
			aiService.setEntries(dvdContainer.getAiEntries());
			
			locationService = new LocationService();
			locationService.setLocations(dvdContainer.getLocations());
			
			doorService = new DoorService();
			doorService.setBuildings(dvdContainer.getBuildings());
			
			materialService = new MaterialService();
			materialService.setMaterials(dvdContainer.getMaterials());
			
			coordinateService = new CoordinateService();
			
			animationService = new AnimationService(elementService);
			
			main.readDvdFile(file.getAbsolutePath());
		}
	}

	public static DvdContainer getDvdContainer() {
		return dvdContainer;
	}

	public static void drawAnimations(Display display, PaintEvent e) {
		animationService.drawAnimations(display, e);
	}

	public static void drawElements(Display display, PaintEvent e) {
		animationService.drawCharacters(display, e);
	}

	public static void drawIdentifier(Display display, PaintEvent e) {
		elementService.drawAnimations(display, e);
		elementService.drawElements(display, e);
	}

	public static void drawObstacles(Display display, PaintEvent e) {
		obstacleService.draw(display, e);
	}

	public static void drawWaypoints(Display display, PaintEvent e) {
		waypointService.draw(display, e);
	}

	public static void drawAI(Display display, PaintEvent e) {
		aiService.draw(display, e);
	}

	public static void drawLocations(Display display, PaintEvent e) {
		locationService.draw(display, e);
	}

	public static void drawDoors(Display display, PaintEvent e) {
		doorService.draw(display, e);
	}

	public static void drawMaterials(Display display, PaintEvent e) {
		materialService.draw(display, e);
	}

	public static void drawCoords(Display display, PaintEvent e, String text) {
		coordinateService.draw(display, e, text);
	}

	public static List<WaypointRoute> getWaypointRoutes() {
		return waypointService.getWaypointRoutes();
	}

	public static String getElementText() {
		return ElementsJsonWriter.writeToString(dvdContainer.getElements());
	}

	public static String getLocationText() {
		return LocationsJsonWriter.writeToString(dvdContainer.getLocations());
	}

	public static String getBuildingsText() {
		return BuildingsTextWriter.writeToString(dvdContainer.getBuildings());
	}

	public static short lookupElementByIdentifier(String identifier) throws IdentifierNotFoundException {
		return DvdContainer.lookupElementByIdentifier(identifier);
	}

	public static Element lookupElementByIndex(int index, boolean aliveOnly) throws IndexNotFoundException {
		return DvdContainer.lookupElementByIndex(index, aliveOnly);
	}

	public static String lookupRouteById(short routeId) {
		return DvdContainer.lookUpRouteById(routeId);
	}

	public static short lookupRouteByIdentifier(String identifier) throws IdentifierNotFoundException {
		return DvdContainer.lookupRouteByIdentifier(identifier);
	}
}
