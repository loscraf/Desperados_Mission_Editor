package desperados.service;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Display;

public class CoordinateService {
	
	private int color = SWT.COLOR_CYAN;
	
	public void draw(Display display, PaintEvent e, String text) {
		changeColor(display, e);
		
		String lines[] = text.split("\\r?\\n");
		
		for (int i = 0; i < lines.length; i++) {
			if (lines[i].length() == 0) {
				changeColor(display, e);
				continue;
			}
			
			try {
				String[] arr = lines[i].trim().split("[\\s,]+" );
				
				if (arr.length >= 5 && arr[0].equals("line")) {
					int x1 = (int) Float.parseFloat(arr[1]);
					int y1 = (int) Float.parseFloat(arr[2]);
					int x2 = (int) Float.parseFloat(arr[3]);
					int y2 = (int) Float.parseFloat(arr[4]);
					e.gc.drawLine(x1, y1, x2, y2);
					e.gc.fillOval(x1 - 5, y1 - 5, 10, 10);
					e.gc.fillOval(x2 - 5, y2 - 5, 10, 10);
				} else {
					int x = 0, y = 0, z0 = 0, z1 = 0;
					if (arr.length >= 2) {
						x = (int) Float.parseFloat(arr[0]);
						y = (int) Float.parseFloat(arr[1]);
						if (arr.length >= 3) {
							z0 = (int) Float.parseFloat(arr[2]);
							if (arr.length == 4) {
								z1 = (int) Float.parseFloat(arr[3]);
							}
						}
						e.gc.fillOval(x - 5, y - 5 - Math.max(z0,  z1), 10, 10);
					}
				}
				
			} catch (NumberFormatException nfe) {
				// ignore
			}
		}
	}

	private void changeColor(Display display, PaintEvent e) {
		e.gc.setBackground(display.getSystemColor(color));
		e.gc.setForeground(display.getSystemColor(color));
		color++;
		if (color == SWT.COLOR_GRAY) color = SWT.COLOR_RED;
	}

}
