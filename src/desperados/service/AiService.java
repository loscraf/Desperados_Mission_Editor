package desperados.service;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import desperados.dvd.ai.AiEntry;
import desperados.dvd.ai.AiEntry00;
import desperados.dvd.ai.AiEntry01;
import desperados.dvd.ai.AiPoint;;

public class AiService {

	private List<AiEntry> entries;

	public AiService() {
		entries = new ArrayList<AiEntry>();
	}

	public void setEntries(List<AiEntry> entries) {
		this.entries = entries;
	}

	public List<AiEntry> getEntries() {
		return entries;
	}

	public void draw(Display display, PaintEvent e) {
		Font font = new Font(display, "Courier New", 10, SWT.BOLD);
		e.gc.setFont(font);
		e.gc.setLineStyle(SWT.LINE_SOLID);
		
		for (int i = 0; i < entries.size(); i++) {
			AiEntry entry = entries.get(i);
			
			List<AiPoint> points = entry.getPoints();
			for (int j = 0; j < points.size(); j++) {
				
				AiPoint p = points.get(j);
				
				if (j != 0) {
					AiPoint last = points.get(j-1);
					e.gc.drawLine(last.x, last.y, p.x, p.y);
				} else {
					e.gc.drawText("" + entry.type, p.x + 10, p.y - 10, SWT.DRAW_TRANSPARENT);
				}
				
				if (entry.type == 1) {
					e.gc.setBackground(display.getSystemColor(SWT.COLOR_GREEN));
					e.gc.setForeground(display.getSystemColor(SWT.COLOR_DARK_CYAN));
				
					List<AiEntry00> list = ((AiEntry01)entry).list;
					for (int jj = 0; jj < list.size(); jj++) {
						AiPoint pp = list.get(jj).getPoints().get(0);
						if (jj != 0) {
							AiPoint ll = list.get(jj-1).getPoints().get(0);
							e.gc.drawLine(ll.x, ll.y, pp.x, pp.y);
						}
						e.gc.fillOval(pp.x - 2, pp.y - 2, 4, 4);
					}
				}
				
				e.gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
				e.gc.setForeground(display.getSystemColor(SWT.COLOR_CYAN));
				e.gc.fillOval(p.x - 2, p.y - 2, 4, 4);
				
				e.gc.drawText("" + entry.type, p.x + 10, p.y - 10, SWT.DRAW_TRANSPARENT);
			}
		}
	}
}
