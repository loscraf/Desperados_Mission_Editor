package desperados.dvf;

import java.util.LinkedHashMap;
import java.util.Map;

import desperados.util.Point;

public class DvfObject {

	private String name;
	private short numDirections;
	private short numAnimations;
	private short width, height;
	private int originX, originY;
	private Point origin;

	private Map<Integer, DvfAnimation> animations;

	public DvfObject(String name, short numDirections, short numAnimations, short width, short height, int originX, int originY) {
		this.name = name;
		this.numDirections = numDirections;
		this.numAnimations = numAnimations;
		this.width = width; 
		this.height = height;
		this.originX = originX;
		this.originY = originY;
		this.origin = new Point((short)originX, (short)originY);
		
		animations = new LinkedHashMap<>(numAnimations);
	}

	public void addAnimation(DvfAnimation animation) {
		animations.put(animation.getID(), animation);
	}

	public DvfAnimation getAnimation(int animID) {
		if (!animations.containsKey(animID)) {
			return animations.entrySet().iterator().next().getValue(); // get first animation instead
		}
		return animations.get(animID);
	}

	public String getName() {
		return name;
	}

	public Point getOrigin() {
		return origin;
	}

	public String toString() {
		String str = "Name: \"" + name + "\"\n";
		str += "Directions: " + numDirections + "\n";
		str += "Animations: " + numAnimations + "\n";
		str += "Width:  " + width + "\n";
		str += "Height: " + height + "\n";
		str += "Origin: " + originX + "," + originY + "\n\n";
		
		int animIndex = 0;
		for (Map.Entry<Integer, DvfAnimation> entry : animations.entrySet()) {
			str += "  Animation " + (animIndex++) + "\n\n";
		    str += entry.getValue().toString();
		}
		
		return str;
	}
}
