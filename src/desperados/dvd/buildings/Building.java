package desperados.dvd.buildings;

import java.util.ArrayList;
import java.util.List;

import desperados.exception.IndexNotFoundException;
import desperados.service.FileService;

public class Building {

	private boolean isSpecial;
	private byte type;
	private byte type2;
	private List<String> occupants;
	private List<Door> doors;
	private int index;
	private int streamPosition;

	public Building(int streamPosition, boolean isSpecial, byte type, byte type2) {
		this.streamPosition = streamPosition;
		this.isSpecial = isSpecial;
		this.type = type;
		this.type2 = type2;
		this.occupants = new ArrayList<>();
		this.doors = new ArrayList<>();
	}

	public void addOccupant(short elemId) {
		try {
			occupants.add(FileService.lookupElementByIndex(elemId, true).getIdentifier());
		} catch (IndexNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	public void addDoor(Door door) {
		doors.add(door);
	}

	public List<Door> getDoors() {
		return doors;
	}

	public String toString() {
		String str = "Building (@" + Integer.toHexString(streamPosition) + ")";
		if (isSpecial) {
			str += "(special)";
		}
		str += ": " + type + ", " + type2 + "\n";
		
		str += occupants.size() + " occupants:";
		for (String occ : occupants) {
			str += " " + occ;
		}
		str += "\n";
		
		for (int i = 0; i < doors.size(); i++) {
			Door d = doors.get(i);
			str += "ID: " + index + "," + i + "\n";
			str += d.toString() + "\n";
		}
		
		return str;
	}

	public void setIndex(int index) {
		this.index = index;
	}
}
