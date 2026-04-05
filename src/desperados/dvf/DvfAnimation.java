package desperados.dvf;

import java.util.ArrayList;
import java.util.List;

public class DvfAnimation {

	private String animName;
	private int animID;
	
	private List<DvfFrameset> framesets;

	public DvfAnimation(String animName, short animID, short numDirections) {
		this.animName = animName;
		this.animID = animID;
		
		framesets = new ArrayList<>(numDirections);
	}

	public void addFrameset(DvfFrameset frameset) {
		framesets.add(frameset);
	}

	public DvfFrameset getFrameset(int index) {
		return framesets.get(index);
	}

	public int getID() {
		return animID;
	}

	public String getName() {
		return animName;
	}

	public String toString() {
		String str = "";
		for (DvfFrameset fs : framesets) {
			str += fs.toString();
		}
		return str;
	}
}
