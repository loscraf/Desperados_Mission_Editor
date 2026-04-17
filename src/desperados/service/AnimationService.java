package desperados.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import desperados.dvd.elements.Accessory;
import desperados.dvd.elements.Animation;
import desperados.dvd.elements.Element;
import desperados.dvd.elements.animations.BaseAnimation;
import desperados.dvd.elements.animations.CharacterAnimation;
import desperados.dvf.DvfHeader;
import desperados.dvf.DvfObject;
import desperados.dvf.DvfReader;
import desperados.util.Point;

public class AnimationService {

	private List<BaseAnimation> anims;
	private List<CharacterAnimation> characterAnims;
	private Map<String, DvfHeader> dvfFiles;

	public AnimationService() {
		dvfFiles = new HashMap<>();
	}

	public AnimationService(ElementService elementService) {
		DvfReader dvfReader = new DvfReader();
		anims = new ArrayList<BaseAnimation>();
		
		for (Animation a : elementService.getAnimations()) {
			DvfHeader header = dvfReader.loadAnimation(a);
			if (header != null) {
				anims.add(new BaseAnimation(a, header));
			}
		}
		
		characterAnims = new ArrayList<CharacterAnimation>();
		
		for (Element e : elementService.getCharacters()) {
			
			DvfHeader header;
			if (e instanceof Accessory) {
				header = dvfReader.loadAccessory(e);
			} else {
				header = dvfReader.loadCharacter(e);
			}
			
			if (header != null) {
				characterAnims.add(new CharacterAnimation(e, header));
			}
		}
		
		dvfFiles = dvfReader.getDvfFiles();
	}

	public void drawAnimations(Display display, PaintEvent e) {
		for (BaseAnimation a : anims) {
			Image image = new Image(display, a.getFrame());
			Point pos = a.getPosition();
			e.gc.drawImage(image, pos.x, pos.y);
			image.dispose();
			//e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLACK));
			//e.gc.drawRectangle(pos.x, pos.y, image.getBounds().width, image.getBounds().height);
		}
	}

	public void drawCharacters(Display display, PaintEvent e) {
		for (CharacterAnimation a : characterAnims) {
			Image image = new Image(display, a.getFrame());
			Point pos = a.getPosition();
			e.gc.drawImage(image, pos.x, pos.y);
			//int width = image.getBounds().width;
			//int height = image.getBounds().height;
	        //e.gc.drawImage(image, a.getX() - width/2, a.getY() - height + 10);
	        image.dispose();
		}
	}

	public Point getOrigin(String dvfName, String objName) {
		Point point = new Point((short)0, (short)0);
		
		if (!dvfFiles.containsKey(dvfName)) {
			return point;
		}
		
		DvfHeader dvfFile = dvfFiles.get(dvfName);
		DvfObject obj = dvfFile.getObject(objName);
		if (obj == null) {
			return point;
		}
		
		return obj.getOrigin();
	}

	public void rebuild(ElementService elementService) {
		DvfReader dvfReader = new DvfReader();

		characterAnims.clear();

		for (Element e : elementService.getCharacters()) {

			DvfHeader header;

			if (e instanceof Accessory) {
				header = dvfReader.loadAccessory(e);
			} else {
				header = dvfReader.loadCharacter(e);
			}

			if (header != null) {
				characterAnims.add(new CharacterAnimation(e, header));
			}
		}
	}
}
