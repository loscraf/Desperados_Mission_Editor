package desperados.service;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import desperados.dvd.materials.Material;
import desperados.dvd.materials.MaterialBlock;
import desperados.util.Point;

public class MaterialService {

	private List<MaterialBlock> materialBlocks;
	
	private int[] colors = new int[9];

	public void setMaterials(List<MaterialBlock> materialBlocks) {
		this.materialBlocks = materialBlocks;
		
		colors[0] = SWT.COLOR_YELLOW; // no type?, see level_03
		colors[1] = SWT.COLOR_RED; //wood
		colors[3] = SWT.COLOR_GREEN; // grass
		colors[4] = SWT.COLOR_MAGENTA; // unknown, only used in level_02?
		colors[5] = SWT.COLOR_BLUE; // water 
		colors[6] = SWT.COLOR_DARK_GREEN; // bushes
		colors[8] = SWT.COLOR_GRAY; // shadows (lights in night missions)
	}

	public void draw(Display display, PaintEvent e) {
		Font font = new Font(display, "Courier New", 10, SWT.BOLD);
		e.gc.setFont(font);
		e.gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		e.gc.setLineStyle(SWT.LINE_SOLID);
		
		for (int i = 0; i < materialBlocks.size(); i++) {
			
			MaterialBlock mb = materialBlocks.get(i);
			List<Material> materials = mb.getMaterials();
			
			for (int j = 0; j < materials.size(); j++) {
				Material mat = materials.get(j);
				
				byte type = mat.getType();
				e.gc.setForeground(display.getSystemColor(colors[type]));
				
				List<Point> points = mat.getPoints();
				for (int k = 0; k < points.size(); k++) {
					Point p = points.get(k);
					if (k != 0) {
						Point last = points.get(k - 1);
						e.gc.drawLine(last.x, last.y, p.x, p.y);
					} else {
						e.gc.drawText("" + i + "," + j, p.x + 5, p.y - 30);
					}
				}
				if (points.size() != 0) {
					Point p1 = points.get(0);
					Point p2 = points.get(points.size() - 1);
					e.gc.drawLine(p2.x, p2.y, p1.x, p1.y);
				}
			}
			
			
			/*
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
			*/
			
		}
		font.dispose();
	}
}
