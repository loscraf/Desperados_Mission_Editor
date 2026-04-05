package desperados.dvf;

import org.eclipse.swt.graphics.ImageData;

import desperados.util.Point;

public class DvfFrame {

	DvfSprite dvfSprite;
	short animTime;
	short spriteIndex, soundIndex;
	short unknown3, unknown7;
	short offsetHor, offsetVert;
	Point offset;

	public DvfFrame(DvfSprite dvfSprite, short spriteIndex, short animTime, short unknown3, short offsetHor, short offsetVert, short soundIndex, short unknown7) {
		this.dvfSprite = dvfSprite;
		this.spriteIndex = spriteIndex;
		this.soundIndex = soundIndex;
		this.animTime = animTime;
		this.unknown3 = unknown3;
		this.unknown7 = unknown7;
		this.offsetHor = offsetHor;
		this.offsetVert = offsetVert;
		this.offset = new Point(offsetHor, offsetVert);
	}

	public ImageData getSprite() {
		return dvfSprite.getImageData();
	}

	public Point getOffset() {
		return offset;
	}

	public String toString() {
		String str = "    Sprite ID: " + spriteIndex + "\n";
		str += "    Time:      " + animTime + "\n";
		str += "    Offset:    " + offsetHor + "," + offsetVert + "\n";
		str += "    Sound ID:  " + soundIndex + "\n";
		str += "    Unknown 3: " + unknown3 + "\n";
		str += "    Unknown 7: " + unknown7 + "\n";
		return str;
	}
}
