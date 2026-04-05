package desperados.dvd.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.waypoints.*;
import desperados.dvd.waypoints.commands.*;

public class Waypoints extends Chunk  {

	public static final String NAMES[] = new String[]{
			"Null", "SkipWaypoint", "GotoWaypoint", "Unused", "SetAIState",
			"FaceToAndStare", "GlanceAt", "Wait", "CheckFor", "CheckForSync", "FaceTo",
			"MobileSprite1", "SetSpeed", "AdjustSpeed", "WaitV", "JumpToStart"};

	@Override
	public void readChunk() throws IOException {
		
		int numWaypointRoutes = stream.readShort();
		List<WaypointRoute> routes = new ArrayList<WaypointRoute>(numWaypointRoutes);
		
		for (int i = 0; i < numWaypointRoutes; i++) {
			
			WaypointRoute r = new WaypointRoute();
			r.setIdentifier("Route_" + i);
			routes.add(r);
			
			int numWaypoints = stream.readShort();
			
			for (int w = 0; w < numWaypoints; w++) {
				
				Waypoint wp = new Waypoint(stream.readShort(), stream.readShort(), stream.readShort(), stream.readShort());
				r.addWaypoint(wp);
				
				if (stream.readByte() == 1) {
					short nameLength = stream.readShort();
					String name = stream.readString(nameLength);
					wp.setClassname(name);
				} else {
					wp.setClassname("");
					short dataLength = stream.readShort();
					analyzeData(wp, dataLength);
				}
			}
			
		}
		dvdContainer.setWaypointRoutes(routes);
	}

	private void analyzeData(Waypoint wp, int dataLength) throws IOException {
		if (dataLength == 0) {
			return;
		}
		
		short numSections = stream.readShort();
		
		for (int i = 0; i < numSections; i++) {
			wp.addSection(stream.readByte());
			stream.readShort(); // skip offset
		}
		
		for (int j = 0; j < numSections; j++) {
			
			WaypointSection section = wp.getSection(j);
			
			short numSubsections = stream.readShort();
			
			for (int i = 0; i < numSubsections; i++) {
				section.addSubsection(stream.readByte());
				stream.readShort(); // skip offset
			}
			
			for (int k = 0; k < numSubsections; k++) {
				
				WaypointSubsection subsection = section.getSubsection(k);
				
				short length = stream.readShort();
				short pos = 0;
				
				int code;
				int argLength = 0;
				
				while (pos < length) {
					code = stream.readByte() & 0xFF;
					pos++;
					
					switch (code) {
					case 0:
					case 1:
					case 0x84:
						argLength = 0;
						break;
					case 3:
					case 4:
						argLength = 1;
						break;
					case 2:
					case 7:
					case 0x0A:
					case 0x80:
					case 0x83:
						argLength = 2;
						break;
					case 5:
					case 6:
					case 8:
					case 0x81:
						argLength = 4;
						break;
					case 9:
					case 0x82:
						argLength = 6;
						break;
					default:
						System.out.println("Unknown waypoint code: " + code + " at pos: " + stream.getPosition());
						pos = length;
					}
					if (pos + argLength > length) {
						stream.skip(length - pos);
						pos = length;
						break;
					}
					pos += argLength;
					
					switch (code) {
					case 0:
						subsection.addCommand(new Null(code));
						break;
					case 1:
						subsection.addCommand(new SkipWaypoint(code));
						break;
					case 2:
						subsection.addCommand(new GotoWaypoint(code, stream.readShort()));
						break;
					case 3:
						subsection.addCommand(new Unused(code, stream.readByte() & 0xFF));
						break;
					case 4:
						subsection.addCommand(new SetAIState(code, stream.readByte() & 0xFF));
						break;
					case 5:
						subsection.addCommand(new FaceToAndStare(code, stream.readShort(), stream.readShort()));
						break;
					case 6:
						subsection.addCommand(new GlanceAt(code, stream.readShort(), stream.readShort()));
						break;
					case 7:
						subsection.addCommand(new Wait(code, stream.readShort() & 0xFFFF));
						break;
					case 8:
						subsection.addCommand(new CheckFor(code, stream.readShort(), stream.readShort()));
						break;
					case 9:
						subsection.addCommand(new CheckForSync(code, stream.readShort(), stream.readShort(), stream.readShort()));
						break;
					case 0xA:
						subsection.addCommand(new FaceTo(code, stream.readShort()));
						break;
					case 0x80:
						subsection.addCommand(new MobileSprite1(code, stream.readShort()));
						break;
					case 0x81:
						subsection.addCommand(new SetSpeed(code, stream.readFloat()));
						break;
					case 0x82:
						subsection.addCommand(new AdjustSpeed(code, stream.readFloat(), stream.readShort()));
						break;
					case 0x83:
						subsection.addCommand(new WaitV(code, stream.readShort()));
						break;
					case 0x84:
						subsection.addCommand(new JumpToStart(code));
						break;
					default:
						break;
					}
				}
			}
		}
	}
}
