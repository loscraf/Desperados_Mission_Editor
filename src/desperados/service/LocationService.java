package desperados.service;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import desperados.dvd.locations.Location;
import desperados.util.Point;

public class LocationService {

	private List<Location> locations;
	
	public void setLocations(List<Location> locations) {
		this.locations = locations;
	}

	public void draw(Display display, PaintEvent e) {
		Font font = new Font(display, "Courier New", 10, SWT.BOLD);
		e.gc.setFont(font);
		e.gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
		e.gc.setLineStyle(SWT.LINE_SOLID);
		
		for (int i = 0; i < locations.size(); i++) {
			Location loc = locations.get(i);
			
			Point[] points = loc.getPoints();
			
			for (int j = 0; j < points.length; j++) {
				Point p = points[j];
				
				if (j != 0) {
					Point last = points[j-1];
					e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
					e.gc.drawLine(last.x, last.y, p.x, p.y);
				} else {
					e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
					e.gc.drawText("" + i, p.x - 20, p.y, SWT.DRAW_TRANSPARENT);
				}
				
				e.gc.fillOval(p.x - 2, p.y - 2, 4, 4);
			}
			
			if (points.length != 1) {
				Point p1 = points[0];
				Point p2 = points[points.length - 1];
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.drawLine(p2.x, p2.y, p1.x, p1.y);
			}
			
			String classname = loc.getClassname();
			if (classname != null) {
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.drawText(classname, points[0].x + 10, points[0].y);
			}
		}
		font.dispose();
	}
}
