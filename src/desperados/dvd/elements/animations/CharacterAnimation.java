package desperados.dvd.elements.animations;

import org.eclipse.swt.graphics.ImageData;

import desperados.dvd.elements.Accessory;
import desperados.dvd.elements.Alive;
import desperados.dvd.elements.Element;
import desperados.dvf.DvfAnimation;
import desperados.dvf.DvfFrame;
import desperados.dvf.DvfFrameset;
import desperados.dvf.DvfHeader;
import desperados.dvf.DvfObject;
import desperados.util.Point;

public class CharacterAnimation {

	private Element character;
	private DvfAnimation dvfAnimation;
	private int currentFrame;

	private Point origin;
	private Point offset;
	private Point charPos;

	public CharacterAnimation(Element character, DvfHeader dvfHeader) {
		this.character = character;
		
		DvfObject obj = dvfHeader.getObject(character.getSprite());
		origin = obj.getOrigin();
	
		int animIndex = 0;
		if (character instanceof Alive) {
			animIndex = ((Alive) character).getStance().getValue();
		} else if (character instanceof Accessory) {
			Element horse = ((Accessory)character).getHorse();
			if (horse == null) {
				animIndex = ((Accessory) character).getAnimID();
			}
		}
		this.dvfAnimation = obj.getAnimation(animIndex);
		
		character.setOrigin(origin.x, origin.y);
		charPos = new Point(character.getX(), character.getY());
	}

	public int getX() {
		return character.getX();
	}

	public int getY() {
		return character.getY();
	}

	public Point getPosition() {
		return charPos.add(offset).sub(origin);
	}

	public ImageData getFrame() {
		DvfFrameset frameset;
		
		if (character instanceof Accessory) {
			frameset = dvfAnimation.getFrameset(((Accessory)character).getDirection());
		} else {
			frameset = dvfAnimation.getFrameset(((Alive) character).getDirection());
		}
		
		DvfFrame frame = frameset.getFrame(currentFrame++);
		currentFrame %= frameset.getNumFrames();
		offset = frame.getOffset();
		return frame.getSprite();
	}
}
