package desperados.util;

import java.util.List;

import desperados.dvd.buildings.Building;

public class BuildingsTextWriter {

	public static String writeToString(List<Building> buildings) {
		String str = "";
		for (int i = 0; i < buildings.size(); i++) {
			Building b = buildings.get(i);
			b.setIndex(i);
			str += b.toString() + "\n";
		}
		return str;
	}

}
