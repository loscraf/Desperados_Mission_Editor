package desperados.dvd.waypoints;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import desperados.dvd.chunk.Waypoints;
import desperados.dvd.waypoints.commands.*;

public class WaypointParser {

	List<WaypointRoute> routes;

	private final static String WAYPOINT = "Waypoint";
	private final static String ROUTE = "Route";
	private final static String IDENTIFIER = "Identifier";
	private final static String GOTOPOS = "GotoPos";
	private final static String CLASSNAME = "Classname";
	private final static String SECTION = "Section";
	private final static String SUBSECTION = "Subsection";
	private final static String NULL = "NULL";

	private enum Token {
		NONE("nothing"),
		KEYWORD("keyword"),
		NUMBER("number"),
		CURLY_OPEN("symbol '{'"),
		CURLY_CLOSE("symbol '}'"),
		ROUND_OPEN("symbol '('"),
		ROUND_CLOSE("symbol ')'"),
		COMMA("symbol ','"),
		STATEMENT_END("symbol ';'");
		
		private String name;
		
		Token(String name) {
			this.name = name;
		}
		String getName() {
			return name;
		}
	}

	private List<Token> tokens;
	private List<String> keywords;
	private List<Float> numbers;
	private List<Integer> lineNumbers;

	private String text;
	private String line;
	private Token token;
	private String value;
	private int pos;

	private boolean error = false;
	private String errorMessage;

	public WaypointParser(String text) {
		routes = new ArrayList<WaypointRoute>();
		tokens = new ArrayList<Token>();
		keywords = new ArrayList<String>();
		numbers = new ArrayList<Float>();
		lineNumbers = new ArrayList<Integer>();
		
		this.text = text;
	}

	int tokenIndex = 0;
	int keywordsIndex = 0;
	int numbersIndex = 0;
	int number;

	int currentLineNumber = 0;

	private Token getNextToken() {
		if (tokenIndex >= tokens.size()) {
			tokenIndex++;
			return Token.NONE;
		}
		currentLineNumber = lineNumbers.get(tokenIndex);
		return tokens.get(tokenIndex++);
	}

	private boolean hasMoreTokens() {
		return tokenIndex < tokens.size();
	}

	private boolean readToken(Token expected) {
		if (error) return false;
		
		Token t = getNextToken();
		if (t == expected) {
			return true;
		}
		error(expected, t);
		return false;
	}

	private boolean readKeyword(String keyword) {
		if (error) return false;
		
		if (readToken(Token.KEYWORD)) {
			String k = getNextString();
			if (k.equals(keyword)) {
				return true;
			}
			error(keyword, k);
		}
		return false;
	}

	private String peakNextKeyword() {
		if (error) return NULL;
		
		Token t = getNextToken();
		tokenIndex--;
		
		boolean success = (t == Token.KEYWORD);
		
		if (success) {
			String k = getNextString();
			keywordsIndex--;
			return k;
		}
		return NULL;
	}

	private String getNextString() {
		if (keywordsIndex >= keywords.size()) {
			return NULL;
		}
		return keywords.get(keywordsIndex++);
	}

	private float getNextNumber() {
		if (numbersIndex >= numbers.size()) {
			return -1;
		}
		return numbers.get(numbersIndex++);
	}

	private List<WaypointRoute> handleTokens() {
		List<WaypointRoute> routes = new ArrayList<WaypointRoute>();
		
		while (hasMoreTokens() && !error) {
			routes.add(readRoute());
		}
		
		if (error) {
			return new ArrayList<WaypointRoute>();
		}
		
		return routes;
	}

	private WaypointRoute readRoute() {
		if (error) return null;
		
		readKeyword(ROUTE);
		readToken(Token.ROUND_OPEN);
		readToken(Token.ROUND_CLOSE);
		readToken(Token.CURLY_OPEN);
		
		WaypointRoute route = new WaypointRoute();
		
		if (peakNextKeyword().equals(IDENTIFIER)) {
			String identifier = readIdentifier();
			route.setIdentifier(identifier);
		}
		
		while (peakNextKeyword().equals(WAYPOINT) && !error) {
			Waypoint wp = readWaypoint();
			route.addWaypoint(wp);
		}
		
		readToken(Token.CURLY_CLOSE);
		
		return route;
	}

	private String readIdentifier() {
		if (error) return null;
		
		readKeyword(IDENTIFIER);
		readToken(Token.ROUND_OPEN);
		readToken(Token.KEYWORD);
		String identifier = getNextString();
		readToken(Token.ROUND_CLOSE);
		readToken(Token.STATEMENT_END);
		
		identifier = identifier.replace("\"", "");
		return identifier;
	}

	private Waypoint readWaypoint() {
		if (error) return null;
		
		readKeyword(WAYPOINT);
		readToken(Token.ROUND_OPEN);
		readToken(Token.ROUND_CLOSE);
		readToken(Token.CURLY_OPEN);
		
		Waypoint waypoint = readGotoPos();
		
		String next = peakNextKeyword();
		if (next.equals(CLASSNAME)) {
			String classname = readClassname();
			waypoint.setClassname(classname);
		} else if (next.equals(SECTION)) {
			do {
				WaypointSection section = readSection();
				waypoint.addSection(section);
			} while (peakNextKeyword().equals(SECTION) && !error);
		} else if (next.equals(NULL)) {
			// do nothing
		} else {
			error(CLASSNAME + " or " + SECTION, next);
		}
		readToken(Token.CURLY_CLOSE);
		
		return waypoint;
	}

	private Waypoint readGotoPos() {
		if (error) return null;
		
		readKeyword(GOTOPOS);
		readToken(Token.ROUND_OPEN);
		
		readToken(Token.NUMBER);
		int x = (int) getNextNumber();
		readToken(Token.COMMA);
		readToken(Token.NUMBER);
		int y = (int) getNextNumber();
		readToken(Token.COMMA);
		readToken(Token.NUMBER);
		int z1 = (int) getNextNumber();
		readToken(Token.COMMA);
		readToken(Token.NUMBER);
		int z2 = (int) getNextNumber();
		
		readToken(Token.ROUND_CLOSE);
		readToken(Token.STATEMENT_END);
		
		return new Waypoint((short)x, (short)y, (short)z1, (short)z2);
	}

	private String readClassname() {
		if (error) return null;
		
		readKeyword(CLASSNAME);
		readToken(Token.ROUND_OPEN);
		readToken(Token.KEYWORD);
		String classname = getNextString();
		readToken(Token.ROUND_CLOSE);
		readToken(Token.STATEMENT_END);
		
		classname = classname.replace("\"", "");
		return classname;
	}

	private WaypointSection readSection() {
		if (error) return null;
		
		readKeyword(SECTION);
		readToken(Token.ROUND_OPEN);
		
		readToken(Token.NUMBER);
		int number = (int) getNextNumber();
		readToken(Token.ROUND_CLOSE);
		readToken(Token.CURLY_OPEN);
		
		WaypointSection section = new WaypointSection((byte)number);
		
		while (peakNextKeyword().equals(SUBSECTION) && !error) {
			WaypointSubsection subsection = readSubsection();
			section.addSubsection(subsection);
		}
		
		readToken(Token.CURLY_CLOSE);
		
		return section;
	}

	private WaypointSubsection readSubsection() {
		if (error) return null;

		readKeyword(SUBSECTION);
		readToken(Token.ROUND_OPEN);
		
		readToken(Token.NUMBER);
		int number = (int) getNextNumber();
		readToken(Token.ROUND_CLOSE);
		readToken(Token.CURLY_OPEN);
		
		WaypointSubsection subsection = new WaypointSubsection((byte)number);
		readCommands(subsection);
		
		readToken(Token.CURLY_CLOSE);
		
		return subsection;
	}

	private void readCommands(WaypointSubsection subsection) {
		
		boolean isValid = true;
		do {
			if (error) return;
			
			String next = peakNextKeyword();
			if (next.equals(Waypoints.NAMES[0])) {
				readKeyword(Waypoints.NAMES[0]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new Null(0));
			} else if (next.equals(Waypoints.NAMES[1])) {
				readKeyword(Waypoints.NAMES[1]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new SkipWaypoint(1));
			} else if (next.equals(Waypoints.NAMES[2])) {
				readKeyword(Waypoints.NAMES[2]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new GotoWaypoint(2, (int)number));
			} else if (next.equals(Waypoints.NAMES[3])) {
				readKeyword(Waypoints.NAMES[3]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new Unused(3, (int)number));
			} else if (next.equals(Waypoints.NAMES[4])) {
				readKeyword(Waypoints.NAMES[4]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new SetAIState(4, (int)number));
			} else if (next.equals(Waypoints.NAMES[5])) {
				readKeyword(Waypoints.NAMES[5]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number1 = getNextNumber();
				readToken(Token.COMMA);
				readToken(Token.NUMBER);
				float number2 = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new FaceToAndStare(5, (int)number1, (int)number2));
			} else if (next.equals(Waypoints.NAMES[6])) {
				readKeyword(Waypoints.NAMES[6]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number1 = getNextNumber();
				readToken(Token.COMMA);
				readToken(Token.NUMBER);
				float number2 = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new GlanceAt(6, (int)number1, (int)number2));
			} else if (next.equals(Waypoints.NAMES[7])) {
				readKeyword(Waypoints.NAMES[7]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new Wait(7, (int)number));
			} else if (next.equals(Waypoints.NAMES[8])) {
				readKeyword(Waypoints.NAMES[8]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number1 = getNextNumber();
				readToken(Token.COMMA);
				readToken(Token.NUMBER);
				float number2 = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new CheckFor(8, (int)number1, (int)number2));
			} else if (next.equals(Waypoints.NAMES[9])) {
				readKeyword(Waypoints.NAMES[9]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number1 = getNextNumber();
				readToken(Token.COMMA);
				readToken(Token.NUMBER);
				float number2 = getNextNumber();
				readToken(Token.COMMA);
				readToken(Token.NUMBER);
				float number3 = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new CheckForSync(9, (int)number1, (int)number2, (int)number3));
			} else if (next.equals(Waypoints.NAMES[10])) {
				readKeyword(Waypoints.NAMES[10]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new FaceTo(10, (int)number));
			} else if (next.equals(Waypoints.NAMES[128 - 117])) {
				readKeyword(Waypoints.NAMES[128 - 117]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new MobileSprite1(128, (int)number));
			} else if (next.equals(Waypoints.NAMES[129 - 117])) { // float
				readKeyword(Waypoints.NAMES[129 - 117]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new SetSpeed(129, number));
			} else if (next.equals(Waypoints.NAMES[130 - 117])) { // float
				readKeyword(Waypoints.NAMES[130 - 117]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number1 = getNextNumber();
				readToken(Token.COMMA);
				readToken(Token.NUMBER);
				float number2 = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new AdjustSpeed(130, number1, (int)number2));
			} else if (next.equals(Waypoints.NAMES[131 - 117])) {
				readKeyword(Waypoints.NAMES[131 - 117]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.NUMBER);
				float number = getNextNumber();
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new WaitV(131, (int)number));
			} else if (next.equals(Waypoints.NAMES[132 - 117])) {
				readKeyword(Waypoints.NAMES[132 - 117]);
				readToken(Token.ROUND_OPEN);
				readToken(Token.ROUND_CLOSE);
				readToken(Token.STATEMENT_END);
				subsection.addCommand(new JumpToStart(132));
			} else {
				isValid = false;
			}
		} while (isValid);
	}

	public List<WaypointRoute> parseText() {
		Scanner scanner = new Scanner(text);
		int lineNumber = 0;
		while (scanner.hasNextLine()) {
			lineNumber++;
			line = scanner.nextLine();
			int p = line.indexOf("//");
			if (p != -1) {
				line = line.substring(0, p);
			}
			line = line.replaceAll("\\s","");
			if (line.length() != 0) {
				pos = 0;
				while (line.length() > pos) {
					token = Token.NONE;
					value = "";
					Token t = readNextToken();
					switch (t) {
					case KEYWORD:
						keywords.add(value);
						break;
					case NUMBER:
						try {
							numbers.add(Float.parseFloat(value));
						} catch (NumberFormatException e) {
							currentLineNumber = lineNumber;
							error("Invalid number found: " + value);
						}
						break;
					default:
						break;
					}
					tokens.add(t);
					lineNumbers.add(lineNumber);
				}
			}
		}
		scanner.close();
		
		List<WaypointRoute> routes = handleTokens();
		
		return routes;
		
	}

	private Token readNextToken() {
		if (pos >= line.length()) {
			return token;
		}
		
		char c = line.charAt(pos++);
		
		if (Character.isDigit(c) || c == '-') {
			if (token == Token.NONE) {
				token = Token.NUMBER;
			}
			value += c;
		} else if (c == '{') {
			if (token != Token.NONE) {
				pos--;
				return token;
			}
			return Token.CURLY_OPEN;
		} else if (c == '}') {
			if (token != Token.NONE) {
				pos--;
				return token;
			}
			return Token.CURLY_CLOSE;
		} else if (c == '(') {
			if (token != Token.NONE) {
				pos--;
				return token;
			}
			return Token.ROUND_OPEN;
		} else if (c == ')') {
			if (token != Token.NONE) {
				pos--;
				return token;
			}
			return Token.ROUND_CLOSE;
		} else if (c == ';') {
			if (token != Token.NONE) {
				pos--;
				return token;
			}
			return Token.STATEMENT_END;
		} else if (c == ',') {
			if (token != Token.NONE) {
				pos--;
				return token;
			}
			return Token.COMMA;
		} else if (c == '.' && token == Token.NUMBER) {
			value += c;
		} else  {
			if (token == Token.NONE) {
				token = Token.KEYWORD;
			}
			value += c;
		}
		return readNextToken();
	}

	public boolean hasErrors() {
		return error;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	private void error(Token expected, Token found) {
		errorMessage = "Error at line " + currentLineNumber + ": " + expected.getName() + " expected, " + found.getName() + " found instead.";
		error = true;
	}

	private void error(String expected, String found) {
		errorMessage = "Error at line " + currentLineNumber + ": Keyword " + expected + " expected, " + found + " found instead.";
		error = true;
	}

	private void error(String message) {
		errorMessage = "Error at line " + currentLineNumber + ": " + message;
		error = true;
	}
}
