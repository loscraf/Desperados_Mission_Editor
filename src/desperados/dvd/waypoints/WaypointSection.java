package desperados.dvd.waypoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.util.LittleEndianOuputStream;

public class WaypointSection {

	private byte type;	// 0,1,2
	private List<WaypointSubsection> subsections;

	public WaypointSection(byte type) {
		this.type = type;
		subsections = new ArrayList<WaypointSubsection>();
	}

	public int getType() {
		return type;
	}

	public void addSubsection(byte v1) {
		WaypointSubsection subsection = new WaypointSubsection(v1);
		addSubsection(subsection);
	}

	public void addSubsection(WaypointSubsection subsection) {
		subsections.add(subsection);
	}

	public WaypointSubsection getSubsection(int index) {
		return subsections.get(index);
	}

	public String toString() {
		String str = String.format("    Section(%d) {\n", type);
		for (WaypointSubsection sub : subsections) {
			str += sub.toString();
		}
		str += "    }\n";
		return str;
	}

	public int getSize() {
		int size = 2;
		for (WaypointSubsection s : subsections) {
			size += s.getSize() + 3;
		}
		return size;
	}

	public void writeDataToStream(LittleEndianOuputStream stream, int offset) throws IOException {
		stream.writeShort(subsections.size());
		
		offset += 2 + subsections.size() * 3;
		
		for (WaypointSubsection s : subsections) {
			stream.writeByte(s.getChance());
			stream.writeShort(offset);
			offset += s.getSize();
		}
		
		for (WaypointSubsection s : subsections) {
			s.writeDataToStream(stream);
		}
	}
}
