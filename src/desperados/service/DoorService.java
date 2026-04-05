package desperados.service;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import desperados.dvd.buildings.Building;
import desperados.dvd.buildings.Door;
import desperados.util.Point;
import desperados.util.PointZ;

public class DoorService {

	private List<Building> buildings;

	public void setBuildings(List<Building> buildings) {
		this.buildings = buildings;
	}

	public void draw(Display display, PaintEvent e) {
		Font font = new Font(display, "Courier New", 10, SWT.BOLD);
		e.gc.setFont(font);
		e.gc.setBackground(display.getSystemColor(SWT.COLOR_YELLOW));
		e.gc.setLineStyle(SWT.LINE_SOLID);
		
		for (int i = 0; i < buildings.size(); i++) {
			Building buil = buildings.get(i);
			List<Door> doors = buil.getDoors();
			for (int j = 0; j < doors.size(); j++) {
				Door door = doors.get(j);
				
				e.gc.setLineWidth(2);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
				List<Point> selectBox = door.getSelectBox();
				for (int k = 0; k < selectBox.size(); k++) {
					Point p = selectBox.get(k);
					if (k != 0) {
						Point last = selectBox.get(k - 1);
						e.gc.drawLine(last.x, last.y, p.x, p.y);
					} else {
						e.gc.drawText("" + i + "," + j, p.x + 5, p.y - 30);
					}
					//e.gc.fillOval(p.x - 5, p.y - 5, 10, 10);
				}
				
				if (selectBox.size() != 0) {
					Point p1 = selectBox.get(0);
					Point p2 = selectBox.get(selectBox.size() - 1);
					e.gc.drawLine(p2.x, p2.y, p1.x, p1.y);
				}
				
				e.gc.setLineWidth(1);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_YELLOW));
				List<PointZ> doorPath = door.getDoorPath();
				for (int k = 0; k < doorPath.size(); k++) {
					Point p = doorPath.get(k);
					if (k != 0) {
						Point last = doorPath.get(k - 1);
						e.gc.drawLine(last.x, last.y, p.x, p.y);
					} else {
						//e.gc.drawText("" + i, p.x - 20, p.y);
					}
					e.gc.fillOval(p.x - 2, p.y - 2, 4, 4);
				}
			}
			
		}
		font.dispose();
	}
}
