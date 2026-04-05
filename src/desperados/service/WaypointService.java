package desperados.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import desperados.dvd.waypoints.Waypoint;
import desperados.dvd.waypoints.WaypointRoute;

public class WaypointService {

	private List<WaypointRoute> routes;

	public WaypointService() {
		routes = new ArrayList<WaypointRoute>();
	}

	public void setWaypointRoutes(List<WaypointRoute> routes) {
		this.routes = routes;
	}

	public List<WaypointRoute> getWaypointRoutes() {
		return routes;
	}

	public void draw(Display display, PaintEvent e) {
		Font font = new Font(display, "Courier New", 10, SWT.BOLD);
		e.gc.setFont(font);
		
		for (int i = 0; i < routes.size(); i++) {
			WaypointRoute r = routes.get(i);
			List<Waypoint> waypoints = r.getWaypoints();
			
			// draw lines
			e.gc.setForeground(display.getSystemColor(SWT.COLOR_BLUE));
			e.gc.setLineStyle(SWT.LINE_SOLID);
			for (int j = 1; j < waypoints.size(); j++) {
				Waypoint wp = waypoints.get(j);
				Waypoint last = waypoints.get(j-1);
				e.gc.drawLine(last.getX(), last.getY(), wp.getX(), wp.getY());
			}
			
			// draw points
			e.gc.setBackground(display.getSystemColor(SWT.COLOR_RED));
			for (int j = 0; j < waypoints.size(); j++) {
				Waypoint wp = waypoints.get(j);
				e.gc.fillOval(wp.getX() - 2, wp.getY() - 2, 4, 4);
			}
			
			// draw identifier
			e.gc.setForeground(display.getSystemColor(SWT.COLOR_RED));
			Waypoint wp = waypoints.get(0);
			e.gc.drawText(r.getIdentifier(), wp.getX() + 5, wp.getY() + 5, SWT.DRAW_TRANSPARENT);
		}
		font.dispose();
	}
}
