package desperados.dvf;

import java.util.ArrayList;
import java.util.List;

public class DvfFrameset {

	private String name;
	// private short numFrames;
	private short unknown3, unknown4;
	private int originX, originY;
	private short direction;
	private short animID;
	
	private List<DvfFrame> frames;

	public DvfFrameset(short numFrames, short unknown3, short unknown4, int originX, int originY, short direction, short animID, String name) {
		this.unknown3 = unknown3;
		this.unknown4 = unknown4;
		this.originX = originX;
		this.originY = originY;
		this.direction = direction;
		this.animID = animID;
		this.name = name;
		
		frames = new ArrayList<>(numFrames);
	}

	public void addFrame(DvfFrame frame) {
		frames.add(frame);
	}

	public DvfFrame getFrame(int index) {
		return frames.get(index);
	}

	public int getNumFrames() {
		return frames.size();
	}

	public String toString() {
		String str = "  Name: \"" + name + "\"\n";
		str += "  Num Frames: " + frames.size() + "\n";
		str += "  Direction:  " + direction + "\n";
		str += "  Unknown 3:  " + unknown3 + "\n";
		str += "  Unknown 4:  " + unknown4 + "\n";
		str += "  Origin:     " + originX + "," + originY + "\n";
		str += "  AnimID:     " + animID + "\n\n";
		
		for (DvfFrame frame : frames) {
			str += frame.toString() + "\n";
		}
		
		return str;
	}
}
