package desperados.scb;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import desperados.scb.quads.*;
import desperados.util.LittleEndianOuputStream;

public class ScbParser {
	
	private Map<String, ScbClass> classes;
	
	private String text;
	private String line;
	private ScbClass currentClass;
	private ScbFunction currentFunction;
	
	private boolean hasErrors;
	private String errorMessage;

	public ScbParser(String text) {
		this.text = text;
		
		classes = new LinkedHashMap<>();
	}

	public void parseText() {
		Scanner scanner = new Scanner(text);
		int lineNumber = 0;
		hasErrors = false;
		
		currentClass = null;
		currentFunction = null;
		
		while (scanner.hasNextLine() && !hasErrors) {
			lineNumber++;
			line = scanner.nextLine();
			
			// remove comments, leading whitespaces, empty lines
			int pos = line.indexOf('#');
			if (pos != -1) {
				line = line.substring(0, pos);
			}
			line = line.trim();
			if (line.length() == 0) {
				continue;
			}
			
			String[] tokens = line.split("\\s+");
			
			if (tokens[0].startsWith("var_") || tokens[0].startsWith("class_var_") || tokens[0].startsWith("vol_var_")) {
				
				if (tokens.length < 3) {
					error(String.format("Invalid expression at line %d", lineNumber));
					continue;
				}
				
				if (tokens[0].startsWith("var_")) {
					currentFunction.addTempVar(tokens[0]);
				} else if (tokens[0].startsWith("class_var_")) {
					currentClass.addClassVar(tokens[0]);
				} else if (tokens[0].startsWith("vol_var_")) {
					currentFunction.addVolVar(tokens[0]);
				}
				
				int addr0 = lookUpVar(tokens[0]);
				
				if (tokens[1].equals(":=I")) {
					if (tokens[2].equals("-")) {
						if (tokens.length != 4) {
							error(String.format("Invalid expression at line %d", lineNumber));
							continue;
						}
						int addr1 = lookUpVar(tokens[3]);
						if (addr1 == -1) {
							error(String.format("Variable \"%s\" at line %d does not exist", tokens[3], lineNumber));
							continue;
						}
						currentFunction.addQuad(new Quad15(addr0, addr1));
					} else {
						int i;
						try {
							i = Integer.parseInt(tokens[2]);
						} catch (NumberFormatException e) {
							error(String.format("\"%s\" at line %d is not a number", tokens[2], lineNumber));
							continue;
						}
						currentFunction.addQuad(new Quad13(addr0, i));
					}
				} else if (tokens[1].equals(":=F")) {
					if (tokens[2].equals("-")) {
						if (tokens.length != 4) {
							error(String.format("Invalid expression at line %d", lineNumber));
							continue;
						}
						int addr1 = lookUpVar(tokens[3]);
						if (addr1 == -1) {
							error(String.format("Variable \"%s\" at line %d does not exist", tokens[3], lineNumber));
							continue;
						}
						currentFunction.addQuad(new Quad16(addr0, addr1));
					} else {
						float f;
						try {
							f = Float.parseFloat(tokens[2]);
						} catch (NumberFormatException e) {
							error(String.format("\"%s\" at line %d is not a number", tokens[2], lineNumber));
							continue;
						}
						currentFunction.addQuad(new Quad14(addr0, f));
					}
				} else if (tokens[1].equals(":=")) {
					if (tokens.length == 5) { // comparisons
						int addr1 = lookUpVar(tokens[2]);
						int addr2 = lookUpVar(tokens[4]);
						if (addr1 == -1) {
							error(String.format("Variable \"%s\" at line %d does not exist", addr1, lineNumber));
							continue;
						}
						if (addr2 == -1) {
							error(String.format("Variable \"%s\" at line %d does not exist", addr2, lineNumber));
							continue;
						}
						if (tokens[3].equals("+I")) {
							currentFunction.addQuad(new Quad19(addr0, addr1, addr2));
						} else if (tokens[3].equals("-I")) {
							currentFunction.addQuad(new Quad1A(addr0, addr1, addr2));
						} else if (tokens[3].equals("*I")) {
							currentFunction.addQuad(new Quad1B(addr0, addr1, addr2));
						} else if (tokens[3].equals("/I")) {
							currentFunction.addQuad(new Quad1C(addr0, addr1, addr2));
						} else if (tokens[3].equals("<=I")) {
							currentFunction.addQuad(new Quad21(addr0, addr1, addr2));
						} else if (tokens[3].equals("<I")) {
							currentFunction.addQuad(new Quad22(addr0, addr1, addr2));
						} else if (tokens[3].equals(">=I")) {
							currentFunction.addQuad(new Quad23(addr0, addr1, addr2));
						} else if (tokens[3].equals(">I")) {
							currentFunction.addQuad(new Quad24(addr0, addr1, addr2));
						} else if (tokens[3].equals("!=I")) {
							currentFunction.addQuad(new Quad25(addr0, addr1, addr2));
						} else if (tokens[3].equals("==I")) {
							currentFunction.addQuad(new Quad26(addr0, addr1, addr2));
						} else {
							error(String.format("Invalid operator \"%s\" at line %d", tokens[3], lineNumber));
							continue;
						}
						
					} else if (tokens[2].equals("GETPARAM")) {
						if (tokens.length != 4) {
							error(String.format("Missing parameter at line %d", lineNumber));
							continue;
						}
						// TODO validate param
						int i;
						try {
							i = Integer.parseInt(tokens[3]);
						} catch (NumberFormatException e) {
							error(String.format("\"%s\" at line %d is not a number", tokens[2], lineNumber));
							continue;
						}
						currentFunction.addQuad(new Quad08(addr0, i << 2));
						
					} else if (tokens[2].equals("GETRETURN")) {
						currentFunction.addQuad(new Quad0A(addr0));
						
					} else if (tokens[2].equals("NATIVEGETRETURN")) {
						currentFunction.addQuad(new Quad0D(addr0));
					} else {
						int addr1 = lookUpVar(tokens[2]);
						if (addr1 == -1) {
							error(String.format("Variable \"%s\" at line %d does not exist", tokens[3], lineNumber));
							continue;
						}
						currentFunction.addQuad(new Quad11(addr0, addr1));
					}
				} else {
					error(String.format("Invalid operator \"%s\" at line %d", tokens[1], lineNumber));
					continue;
				}
				
			} else if (tokens[0].endsWith(":")) { // label
				String label = tokens[0].substring(0, tokens[0].length() - 1);
				if (currentClass.addLabel(label) != null) {
					error(String.format("Label \"%s\" at line %d already exists", label, lineNumber));
					continue;
				}
				
			} else if (tokens[0].equals("NATIVEPARAM")) {
				if (tokens.length == 1) {
					error(String.format("Missing parameter at line %d", lineNumber));
					continue;
				}
				int address = lookUpVar(tokens[1]);
				if (address == -1) {
					error(String.format("Variable \"%s\" at line %d does not exist", tokens[1], lineNumber));
					continue;
				}
				currentFunction.addQuad(new Quad0B(address));
				
			} else if (tokens[0].equals("NATIVECALL")) {
				if (tokens.length == 1) {
					error(String.format("Missing function name at line %d", lineNumber));
					continue;
				}
				String name = tokens[1];
				int idx = name.indexOf('(');
				if (idx != -1) {
					name = name.substring(0, idx);
				}
				Integer code = lookUpNativeFunction(name);
				if (code == null) {
					error(String.format("Unknown function name \"%s\" found at line %d", name, lineNumber));
					continue;
				}
				currentFunction.addQuad(new Quad0C(code));
				
			} else if (tokens[0].equals("IF")) {
				if (tokens.length != 9) {
					error(String.format("line %d: expression does not match \"IF ( %s != 0 ) THEN GOTO %s\"", lineNumber));
					continue;
				}
				int address = lookUpVar(tokens[2]);
				if (address == -1) {
					error(String.format("Variable \"%s\" at line %d does not exist", tokens[2], lineNumber));
					continue;
				}
				if (!tokens[4].equals("0")) {
					// error
				}
				if (tokens[3].equals("!=")) {
					currentFunction.addQuad(new Quad0F(address, tokens[8]));
				} else if (tokens[3].equals("==")) {
					currentFunction.addQuad(new Quad10(address, tokens[8]));
				} else {
					// error
				}
				
			} else if (tokens[0].equals("GOTO")) {
				if (tokens.length == 1) {
					error(String.format("Missing label at line %d", lineNumber));
					continue;
				}
				currentFunction.addQuad(new Quad0E(tokens[1]));
				
			} else if (tokens[0].equals("PARAM")) {
				if (tokens.length == 1) {
					error(String.format("Missing parameter at line %d", lineNumber));
					continue;
				}
				int address = lookUpVar(tokens[1]);
				if (address == -1) {
					error(String.format("Variable \"%s\" at line %d does not exist", tokens[1], lineNumber));
					continue;
				}
				currentFunction.addQuad(new Quad02(address));
				
			} else if (tokens[0].equals("SETPARAM")) {
				if (tokens.length != 4) {
					error(String.format("Misformed express at line %d", lineNumber));
					continue;
				}
				int i;
				try {
					i = Integer.parseInt(tokens[1]);
				} catch (NumberFormatException e) {
					error(String.format("\"%s\" at line %d is not a number", tokens[1], lineNumber));
					continue;
				}
				int addr = lookUpVar(tokens[3]);
				if (addr == -1) {
					error(String.format("Variable \"%s\" at line %d does not exist", tokens[3], lineNumber));
					continue;
				}
				if (!tokens[2].equals(":=")) {
					error(String.format("Invalid operator at line %d", lineNumber));
					continue;
				}
				currentFunction.addQuad(new Quad09(i, addr));
				
			} else if (tokens[0].equals("CALL")) {
				if (tokens.length == 1) {
					error(String.format("Missing function name at line %d", lineNumber));
					continue;
				}
				String name = tokens[1];
				int idx = name.indexOf('(');
				if (idx != -1) {
					name = name.substring(0, idx);
				}
				ScbFunction f = lookUpFunction(name);
				if (f == null) {
					error(String.format("Unknown function name \"%s\" found at line %d", name, lineNumber));
					continue;
				}
				currentFunction.addQuad(new Quad05(f.getAddress()));
				
			} else if (tokens[0].equals("RETURN")) {
				if (tokens.length == 1) {
					currentFunction.addQuad(new Quad06());
				} else {
					int address = lookUpVar(tokens[1]);
					if (address == -1) {
						error(String.format("Variable \"%s\" at line %d does not exist", tokens[1], lineNumber));
						continue;
					}
					currentFunction.addQuad(new Quad07(address));
					currentFunction.setSizeOfRetVal(4);
				}
				
			} else if (tokens[0].equals("NOP")) {
				currentFunction.addQuad(new Quad01());
				
			} else if (tokens[0].equals("Class")) {
				if (currentFunction != null) {
					error(String.format("\"End Function\" expected at line %d", lineNumber));
					continue;
				} else if (currentClass != null) {
					error(String.format("\"End Class\" expected at line %d", lineNumber));
					continue;
				} else if (tokens.length == 1) {
					error(String.format("Missing class name at line %d", lineNumber));
					continue;
				}
				String name = tokens[1];
				int idx = name.indexOf("(");
				if (idx != -1) {
					name = name.substring(0, idx);
				}
				if (classes.containsKey(name)) {
					error(String.format("Duplicate class name \"%s\" at line %d", tokens[1], lineNumber));
					continue;
				}
				newClass(name);
				
			} else if (tokens[0].equals("Function")) {
				if (currentFunction != null) {
					error(String.format("\"End Function\" expected at line %d", lineNumber));
					continue;
				} else if (tokens.length == 1) {
					error(String.format("Missing function name at line %d", lineNumber));
					continue;
				}
				String name = tokens[1];
				int idx = name.indexOf("(");
				if (idx != -1) {
					name = name.substring(0, idx);
				}
				if (currentClass.getFunctions().containsKey(name)) {
					error(String.format("Duplicate function name \"%s\" at line %d", tokens[1], lineNumber));
					continue;
				}
				newFunction(name);
				
			} else if (tokens[0].equals("End")) {
				if (tokens.length == 1) {
					error(String.format("Missing keyword at line %d", lineNumber));
					continue;
				} else if (tokens[1].equals("Class")) {
					endClass();
				} else if (tokens[1].equals("Function")) {
					endFunction();
				} else {
					error(String.format("Unknown keyword \"%s\" at line %d", tokens[1], lineNumber));
					continue;
				}
			} else {
				error(String.format("Unknown keyword \"%s\" at line %d", tokens[0], lineNumber));
			}
		}
		scanner.close();
		
		if (currentClass != null) {
			error(String.format("\"End Class\" expected at line %d", lineNumber));
		}
	}

	private void newClass(String classname) {
		ScbClass c = new ScbClass(classname);
		classes.put(classname, c);
		currentClass = c;
	}

	private void newFunction(String functionName) {
		ScbFunction f = new ScbFunction(currentClass, functionName);
		currentClass.getFunctions().put(functionName, f);
		currentFunction = f;
		currentFunction.addQuad(new Quad03());
	}

	private void endFunction() {
		currentFunction.addQuad(new Quad01());
		currentFunction.addQuad(new Quad06());
		currentFunction.addQuad(new Quad04());
		currentFunction = null;
	}

	private void endClass() {
		currentClass = null;
	}

	private Integer lookUpNativeFunction(String name) {
		return ScbExternalFunction.functionNames.get(name);
	}

	private ScbFunction lookUpFunction(String name) {
		return currentClass.getFunction(name);
	}

	private int lookUpVar(String varname) {
		int address = currentClass.lookUpVar(varname); 
		if (address != -1) {
			return address;
		}
		return currentFunction.lookUpVar(varname);
	}

	private void error(String errorMessage) {
		this.errorMessage = errorMessage;
		hasErrors = true;
	}

	public byte[] getData() throws IOException {
		LittleEndianOuputStream stream = new LittleEndianOuputStream();
		
		stream.writeString2("version 1.00, debug 0\n");
		stream.writeString2(String.format("nbOfClasses %d\n", classes.size()));
		
		for (Map.Entry<String, ScbClass> entry : classes.entrySet()) {
			ScbClass c = entry.getValue();
			stream.writeBytes(c.getData());
		}
		
		return stream.getBytes();
	}

	public boolean hasErrors() {
		return hasErrors;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
