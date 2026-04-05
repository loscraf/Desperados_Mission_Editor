package desperados.dvd.waypoints;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.waypoints.commands.WaypointCommand;
import desperados.util.LittleEndianOuputStream;

public class WaypointSubsection {

	private byte chance;	// 0-100
	private List<WaypointCommand> commands;

	public WaypointSubsection(byte chance) {
		this.chance = chance;
		commands = new ArrayList<WaypointCommand>();
	}

	public void addCommand(WaypointCommand command) {
		commands.add(command);
	}

	public int getChance() {
		return chance;
	}

	public String toString() {
		String str = String.format("      Subsection(%d) {\n", chance);
		for (WaypointCommand c : commands) {
			str += "        " + c.getString() + ";\n";
		}
		str += "      }\n";
		return str;
	}

	public int getSize() {
		int size = 2;
		for (WaypointCommand c : commands) {
			size += c.getData().length;
		}
		return size;
	}

	public void writeDataToStream(LittleEndianOuputStream stream) throws IOException {
		if (commands.size() == 0) {
			stream.writeShort(0);
			return;
		}
		
		LittleEndianOuputStream commandsStream = new LittleEndianOuputStream();
		for (WaypointCommand c : commands) {
			commandsStream.writeBytes(c.getData());
		}
		byte[] buffer = commandsStream.getBytes();
		
		stream.writeShort(buffer.length);
		stream.writeBytes(buffer);
	}
}
