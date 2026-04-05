package desperados.dvd.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.buildings.Building;
import desperados.dvd.buildings.Door;

public class Buildings extends Chunk {

	private List<Building> buildings;

	public void readChunk() throws IOException {
		
		short numBuildings = stream.readShort();
		buildings = new ArrayList<>(numBuildings);
		
		for (int i = 0; i < numBuildings; i++) {
			readBuilding();
		}
		
		short numSpecialBuildings = stream.readShort();
		for (int i = 0; i < numSpecialBuildings; i++) {
			readSpecialBuilding();
		}
		
		dvdContainer.setBuildings(buildings);
	}

	private void readBuilding() throws IOException {
		Building building = readBuildingHeader(false);

		short numOccupants = stream.readShort();
		for (int i = 0; i < numOccupants; i++) {
			building.addOccupant(stream.readShort());
		}
		
		short numDoors = stream.readShort();
		for (int i = 0; i < numDoors; i++) {
			readDoor(building, 10);
		}
		
		buildings.add(building);
	}

	private void readSpecialBuilding() throws IOException {
		Building building = readBuildingHeader(true);
		readDoor(building, 8);
		buildings.add(building);
	}

	private Building readBuildingHeader(boolean isSpecial) throws IOException {
		return new Building(stream.getPosition(), isSpecial, stream.readByte(), stream.readByte());
	}

	private void readDoor(Building building, int flagsLength) throws IOException {
		byte[] doorFlags = stream.readBytes(flagsLength);
		Door door = new Door(doorFlags);
		building.addDoor(door);
		
		short numPoints = stream.readShort();
		for (int i = 0; i < numPoints; i++) {
			door.addPoint(stream.readShort(), stream.readShort());
		}
		
		short numDoorPaths = stream.readShort();
		for (int i = 0; i < numDoorPaths; i++) {
			door.addPath(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort());
		}
		
		short unknownId = stream.readShort();
		if (unknownId != -1) {
			stream.readByte();
			stream.readByte();
		}
	}
}
