package desperados.dvd.waypoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.util.LittleEndianOuputStream;
import desperados.util.PointZ;

public class Waypoint {

	private PointZ point;
	private String classname;
	private List<WaypointSection> sections;

	public Waypoint(short x, short y, short z1, short z2) {
		point = new PointZ(x, y, z1, z2);
		classname = "";
		sections = new ArrayList<WaypointSection>();
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public short getX() {
		return point.x;
	}

	public short getY() {
		return point.y;
	}

	public String getClassname() {
		return classname;
	}

	public String toString() {
		String str = "    GotoPos(" + point.toString() + ");\n";
		
		if (classname.length() != 0) {
			str += "    Classname(\"" + classname + "\");\n";
		} else {
			for (WaypointSection s : sections) {
				str += s.toString();
			}
		}
		return str;
	}

	public void addSection(byte v1) {
		addSection(new WaypointSection(v1));
	}

	public void addSection(WaypointSection section) {
		sections.add(section);
	}

	public WaypointSection getSection(int index) {
		return sections.get(index);
	}

	public void writeToStream(LittleEndianOuputStream stream) throws IOException {
		stream.writeBytes(point.toByteArray());
		
		if (classname.length() != 0) {
			stream.writeByte(1);
			stream.writeString(classname);
			return;
		}
		stream.writeByte(0);
		
		if (sections.size() > 0) {
			int[] sectionOffsets = new int[sections.size()];
			sectionOffsets[0] = 2 + sections.size() * 3;
			if (sections.size() == 2) {
				sectionOffsets[1] = sectionOffsets[0] + sections.get(0).getSize();
			}
			
			LittleEndianOuputStream sectionStream = new LittleEndianOuputStream();
			
			sectionStream.writeShort(sections.size());
			
			for (int i = 0; i < sections.size(); i++) {
				sectionStream.writeByte(sections.get(i).getType());
				sectionStream.writeShort(sectionOffsets[i]);
			}
			
			for (int i = 0; i < sections.size(); i++) {
				sections.get(i).writeDataToStream(sectionStream, sectionOffsets[i]);
			}
			
			byte[] buffer = sectionStream.getBytes();
			
			stream.writeShort(buffer.length);
			stream.writeBytes(buffer);
			return;
		}
		
		stream.writeShort(0);
	}
}
