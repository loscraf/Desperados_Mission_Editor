package desperados.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import desperados.dvd.obstacles.Obstacle;
import desperados.dvd.obstacles.ObstaclePoint;

public class ObstacleService {

	private List<Obstacle> obstacles;

	public ObstacleService() {
		obstacles = new ArrayList<Obstacle>();
	}

	public void setObstacles(List<Obstacle> obstacles) {
		this.obstacles = obstacles;
	}

	public List<Obstacle> getObstacles() {
		return obstacles;
	}

	public int[][] getPolylines(boolean closeLine) {
		int[][] polylines = new int[obstacles.size()][];
		
		for (int i = 0; i < obstacles.size(); i++) {
			Obstacle o = obstacles.get(i);
			polylines[i] = getPolyLine(o, closeLine);
		}
		return polylines;
	}

	private int[] getPolyLine(Obstacle o, boolean closeLine) {
		if (!o.hasExtra()) {
			//return new int[0];
		}
		
		List<ObstaclePoint> points = o.getPoints();
		
		int size = points.size() * 2;
		if (closeLine) size += 2;
		int[] pl = new int[size];
		
		for (int j = 0; j < points.size(); j++) {
			ObstaclePoint p = points.get(j);
			pl[j * 2]     = (int)  p.x;
			pl[j * 2 + 1] = (int) (p.y - p.z1);
		}
		
		// close the polyline by duplication the first point
		if (closeLine) {
			pl[pl.length - 2] = pl[0];
			pl[pl.length - 1] = pl[1];
		}
		
		return pl;
	}

	public void draw(Display display, PaintEvent e) {
		Font font = new Font(display, "Courier New", 10, SWT.BOLD);
		e.gc.setFont(font);
		e.gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
		
		for (int i = 0; i < obstacles.size(); i++) {
			Obstacle o = obstacles.get(i);
			List<ObstaclePoint> points = o.getPoints();
			for (int j = 0; j < points.size(); j++) {
				ObstaclePoint p1 = points.get(j);
				ObstaclePoint p2 = points.get((j + 1) % points.size());
				
				// draw bottom poly
				e.gc.setLineStyle(SWT.LINE_DOT);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
				e.gc.drawLine((int)p1.x, (int)p1.y - (int)p1.z0, (int)p2.x, (int)p2.y - (int)p2.z0);
				
				// draw vertical lines
				e.gc.drawLine((int)p1.x, (int)p1.y - (int)p1.z0, (int)p1.x, (int)p1.y - (int)p1.z1);
				
				// draw top poly
				e.gc.setLineStyle(SWT.LINE_SOLID);
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_MAGENTA));
				e.gc.drawLine((int)p1.x, (int)p1.y - (int)p1.z1, (int)p2.x, (int)p2.y - (int)p2.z1);
			}
			
			if (o.hasExtra()) {
				ObstaclePoint p = o.getCenter();
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
				e.gc.drawText(o.getExtra(), (int)p.x, 15+(int)(p.y - p.z1));
			}
			
			/*
			// draw height
			ObstaclePoint p = o.getCenter();
			e.gc.setForeground(display.getSystemColor(SWT.COLOR_YELLOW));
			e.gc.drawText("" + (int)p.z1, (int)p.x, (int)(p.y - p.z1));
			*/
		}
	}
}
