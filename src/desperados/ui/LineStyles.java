package desperados.ui;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.Bullet;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GlyphMetrics;

public class LineStyles {

	private static final Color COLOR_GREEN = new Color(null, 0, 127, 0);
	private static final Color COLOR_BLUE = new Color(null, 0, 0, 255);
	private static final Color COLOR_PURPLE = new Color(null, 255, 0, 255);
	private static final Color COLOR_PURPLE2 = new Color(null, 127, 0, 127);
	private static final Color COLOR_ORANGE = new Color(null, 255, 127, 0);
	private static final Color COLOR_BLACK = new Color(null, 0, 0, 0);

	static void setLineStyleWAYS(LineStyleEvent e, StyledText text) {
    	e.bulletIndex = text.getLineAtOffset(e.lineOffset);
    	StyleRange style = new StyleRange();
    	style.metrics = new GlyphMetrics(0, 0, Integer.toString(text.getLineCount() + 1).length() * 12);
    	e.bullet = new Bullet(ST.BULLET_NUMBER, style);
		
    	ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
    	String pattern;
		Matcher m;
		
		pattern = "(Route|Identifier|Waypoint|Section|Subsection)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_BLUE, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		pattern = "(GotoPos|FaceToAndStare|FaceTo|GlanceAt|Wait|GotoWaypoint|Classname|CheckFor|CheckForSync|SetAIState|SkipWaypoint)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_PURPLE, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		pattern = "(WaitV|SetSpeed|AdjustSpeed|JumpToStart|MobileSprite1)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_PURPLE2, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		pattern = "(Null|Unused)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_ORANGE, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		pattern = "(Route_)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_BLACK, null, SWT.NONE);
			styleRanges.add(s);
		}
		
		pattern = "//.*";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_GREEN, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		e.styles = styleRanges.toArray(new StyleRange[0]);
	}

	static void setLineStyleSCB(LineStyleEvent e, StyledText text) {
    	e.bulletIndex = text.getLineAtOffset(e.lineOffset);
    	StyleRange style = new StyleRange();
    	style.metrics = new GlyphMetrics(0, 0, Integer.toString(text.getLineCount() + 1).length() * 12);
    	e.bullet = new Bullet(ST.BULLET_NUMBER, style);
		
    	ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();
    	String pattern;
		Matcher m;
		
		pattern = "(NATIVEPARAM|PARAM)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_PURPLE, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		pattern = "(RETURN|GETPARAM|NATIVEGETRETURN|GETRETURN)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_BLUE, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		pattern = "(IF|THEN|GOTO|Function|End Function|Class|End Class)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_PURPLE2, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		pattern = "(NATIVECALL|CALL)";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_ORANGE, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		pattern = "(#|:=I|:=F|:=|==I|==F|==|!=I|!=F|!=|\\(|\\)|>|<|\\+I|-I|\\*I|/I|\\+F|-F|\\*F|/F|\\(INT\\)|\\(FLOAT\\))";
		m = Pattern.compile(pattern).matcher(e.lineText);
		while (m.find()) {
			StyleRange s = new StyleRange(e.lineOffset + m.start(), m.end() - m.start(), COLOR_GREEN, null, SWT.BOLD);
			styleRanges.add(s);
		}
		
		e.styles = styleRanges.toArray(new StyleRange[0]);
	}
}
