package desperados.dvd;

import java.util.List;

import desperados.dvd.ai.AiEntry;
import desperados.dvd.buildings.Building;
import desperados.dvd.chunk.Identifiable;
import desperados.dvd.elements.Alive;
import desperados.dvd.elements.Element;
import desperados.dvd.locations.Location;
import desperados.dvd.materials.MaterialBlock;
import desperados.dvd.obstacles.Obstacle;
import desperados.dvd.waypoints.WaypointRoute;
import desperados.exception.IdentifierNotFoundException;
import desperados.exception.IndexNotFoundException;

public class DvdContainer {

	private static List<Obstacle> obstacles;
	private static List<Element> elements;
	private static List<WaypointRoute> routes;
	private static List<AiEntry> aiEntries;
	private static List<Location> locations;
	private static List<Building> buildings;
	private static List<MaterialBlock> materials;

	public void setObstacles(List<Obstacle> obs) {
		obstacles = obs;
	}

	public void setWaypointRoutes(List<WaypointRoute> r) {
		routes = r;
	}

	public void setElements(List<Element> elem) {
		elements = elem;
	}

	public void setAI(List<AiEntry> aiEnt) {
		aiEntries = aiEnt;
	}

	public void setLocations(List<Location> loc) {
		locations = loc;
	}

	public void setBuildings(List<Building> buil) {
		buildings = buil;
	}

	public void setMaterials(List<MaterialBlock> mat) {
		materials = mat;
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public List<WaypointRoute> getWaypointRoutes() {
		return routes;
	}

	public List<Element> getElements() {
		return elements;
	}

	public List<AiEntry> getAiEntries() {
		return aiEntries;
	}

	public List<Location> getLocations() {
		return locations;
	}

	public List<Building> getBuildings() {
		return buildings;
	}

	public List<MaterialBlock> getMaterials() {
		return materials;
	}

	public static <T extends Identifiable> short lookUpByIdentifier(List <T> list, String identifier) throws IdentifierNotFoundException {
		if (identifier == null || identifier.equals("")) {
			return -1;
		}
		for (short i = 0; i < list.size(); i++) {
			T elem = list.get(i);
			if (elem.getIdentifier().equals(identifier)) {
				return i;
			}
		}
		throw new IdentifierNotFoundException("identifier \"" + identifier + "\" not found!");
	}

	public static <T extends Identifiable> T lookUpByIndex(List <T> list, int index, boolean aliveOnly) throws IndexNotFoundException {
		if (index < 0 || index > list.size()) {
			throw new IndexNotFoundException("index \"" + index + "\" out of bounds!");
		}
		if (!aliveOnly) {
			return list.get(index);
		}
		
		int counter = 0;
		for (short i = 0; i < list.size(); i++) {
			T elem = list.get(i);
			
			if (elem instanceof Element) {
				Element e = (Element) elem;
				if (e instanceof Alive) {
					if (counter == index) {
						return elem;
					}
					counter++;
				}
			}
		}
		throw new IndexNotFoundException("index \"" + index + "\" not found!");
	}

	public static short lookupElementByIdentifier(String identifier) throws IdentifierNotFoundException {
		return lookUpByIdentifier(elements, identifier);
	}

	public static Element lookupElementByIndex(int index, boolean aliveOnly) throws IndexNotFoundException {
		return lookUpByIndex(elements, index, aliveOnly);
	}

	public static short lookupRouteByIdentifier(String identifier) throws IdentifierNotFoundException {
		return lookUpByIdentifier(routes, identifier);
	}

	public static String lookUpRouteById(short routeId) {
		if (routeId == -1) return "";
		return routes.get(routeId).getIdentifier();
	}
}
