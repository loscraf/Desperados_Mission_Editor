package desperados.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import desperados.dvd.elements.Accessory;
import desperados.dvd.elements.Alive;
import desperados.dvd.elements.Animation;
import desperados.dvd.elements.Element;

public class ElementService {

	private List<Element> elements;

	public ElementService() {
		elements = new ArrayList<Element>();
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public List<Element> getElements() {
		return elements;
	}

	public List<Animation> getAnimations() {
		List <Animation> anims = new ArrayList<Animation>();
		for (Element e : elements) {
			if (e instanceof Animation) {
				anims.add((Animation) e);
			}
		}
		return anims;
	}

	public List<Element> getCharacters() {
		List <Element> characters = new ArrayList<Element>();
		for (Element e : elements) {
			if (e instanceof Alive) {
				characters.add(e);
			} else if (e instanceof Accessory) {
				if (((Accessory) e).hasExtraInfo()) {
					characters.add(e);
				}
			}
		}
		return characters;
	}

	public void drawElements(Display display, PaintEvent e) {
		Font font = new Font(display, "Arial", 10, SWT.BOLD);
		e.gc.setFont(font);
		e.gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		
		desperados.dvd.elements.Element selectedElement = FileService.getSelectedElement();
		
		for (int i = 0; i < elements.size(); i++) {
			Element elem = elements.get(i);
			
			e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
			
			if (!(elem instanceof Animation)) {
				
				e.gc.drawOval(elem.getX() - 12 , elem.getY() - 8, 24, 16);
				
				if (elem instanceof Alive) {
					double angle = Math.toRadians(((Alive)elem).getDirection());
					int x2 = (int) (Math.sin(angle * 360.0 / 16) * 20);
					int y2 = (int) (Math.cos(angle * 360.0 / 16) * 14);
					e.gc.drawLine(elem.getX(), elem.getY(), elem.getX() + x2, elem.getY() - y2);
				}
				
				
				// draw identifier
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_YELLOW));
				
				// Si es el elemento seleccionado, dibujar fondo negro
				if (selectedElement != null && selectedElement.getIdentifier().equals(elem.getIdentifier())) {
					String identifier = elem.getIdentifier();
					org.eclipse.swt.graphics.Point textExtent = e.gc.textExtent(identifier);
					
					// Dibujar fondo negro con padding
					int bgX = elem.getX() + 15 - 2;
					int bgY = elem.getY() - 15 - 2;
					int bgWidth = textExtent.x + 4;
					int bgHeight = textExtent.y + 4;
					
					e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
					e.gc.fillRectangle(bgX, bgY, bgWidth, bgHeight);
					
					// Dibujar borde blanco
					e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
					e.gc.drawRectangle(bgX, bgY, bgWidth, bgHeight);
				}
				
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_YELLOW));
				e.gc.drawText(elem.getIdentifier(), elem.getX() + 15, elem.getY() - 15, SWT.DRAW_TRANSPARENT);
			}
		}
		font.dispose();
	}
	
	public void drawAnimations(Display display, PaintEvent e) {
		Font font = new Font(display, "Arial", 10, SWT.BOLD);
		e.gc.setFont(font);
		e.gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		
		for (int i = 0; i < elements.size(); i++) {
			Element elem = elements.get(i);
			
			e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
			if (elem instanceof Animation) {
				// TODO
				
				// draw identifier
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.drawText(elem.getSprite(), elem.getX() + 15, elem.getY() - 15, SWT.DRAW_TRANSPARENT);
			}
		}
		font.dispose();
	}
}
