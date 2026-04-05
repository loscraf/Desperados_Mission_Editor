package desperados.dvd.elements.animations;

import org.eclipse.swt.graphics.ImageData;

import desperados.dvd.elements.Animation;
import desperados.dvf.DvfFrame;
import desperados.dvf.DvfFrameset;
import desperados.dvf.DvfHeader;
import desperados.dvf.DvfObject;
import desperados.util.Point;

public class BaseAnimation {

	private Animation animation;
	private DvfFrameset dvfFrameset;
	private int currentFrame;
	
	private Point origin;
	private Point offset;
	private Point animPos;
	
	private short maxWidth, maxHeight;

	public BaseAnimation(Animation animation, DvfHeader dvfHeader) {
		this.animation = animation;
		
		DvfObject obj = dvfHeader.getObject(animation.getSprite());
		origin = obj.getOrigin();
		dvfFrameset = obj.getAnimation(0).getFrameset(0);
		
		animation.setOrigin(origin.x, origin.y);
		animPos = new Point(animation.getX(), animation.getY());
		
		maxWidth = dvfHeader.getMaxWidth();
		maxHeight = dvfHeader.getMaxHeight();
	}

	public int getX() {
		return animation.getX();
	}

	public int getY() {
		return animation.getY();
	}

	public int getWidth() {
		return maxWidth;
	}

	public int getHeight() {
		return maxHeight;
	}

	public Point getPosition() {
		return animPos.add(offset).sub(origin);
	}

	public ImageData getFrame() {
		DvfFrame frame = dvfFrameset.getFrame(currentFrame++);
		offset = frame.getOffset();
		
		ImageData imageData = frame.getSprite();
		currentFrame %= dvfFrameset.getNumFrames();
		return imageData;
	}
}
