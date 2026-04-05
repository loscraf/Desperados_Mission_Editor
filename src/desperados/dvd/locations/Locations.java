package desperados.dvd.locations;

import java.util.List;

public class Locations {
	private Location[] locations;

	public Location[] getLocations() {
		return locations;
	}

	public void setElements(List<Location> list) {
		if (list == null) return;
		this.locations = new Location[list.size()];
		this.locations = list.toArray(this.locations);
	}
}
