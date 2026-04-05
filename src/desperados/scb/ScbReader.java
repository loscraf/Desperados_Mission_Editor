package desperados.scb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import desperados.util.LittleEndianInputStream;

public class ScbReader {

	private LittleEndianInputStream stream;
	private Scanner scanner;
	private String line;
	private List<ScbClass> classes;

	// just for testing
	public static void main(String[] args) {
		
		String filename = "C:\\Games\\Desperados Wanted Dead or Alive\\data\\levels\\level_00.scb";
		
		try {
			ScbReader reader = new ScbReader(filename);
			System.out.println(reader.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ScbReader(String filename) throws IOException {
		classes = new ArrayList<>();
		
		stream = new LittleEndianInputStream(filename);
		
		int numClasses = parseScbHeader();
		
		for (int i = 0; i < numClasses; i++) {
			parseClassHeader();
		}
		
		stream.close();
	}

	private void resetScanner() throws IOException {
		line = stream.readLine();
		line = line.replace(",", "");
		scanner = new Scanner(line);
	}

	private int parseScbHeader() throws IOException {
		line = stream.readLine(); // skip "version 1.00, debug 0"
		resetScanner();
		scanner.next();
		return scanner.nextInt();
	}

	public void parseClassHeader() throws IOException {
		resetScanner();
		scanner.findInLine("className");
		String classname = scanner.next();
		
		resetScanner();
		scanner.next();
		int numVars = scanner.nextInt();
		scanner.next();
		int varSize = scanner.nextInt();
		
		resetScanner();
		scanner.next();
		int numFuncts = scanner.nextInt();
		
		ScbClass scbClass  = new ScbClass(classname, numVars, varSize, numFuncts);
		classes.add(scbClass);
		
		String[] funcLines = new String[numFuncts];
		
		for (int i = 0; i < numFuncts; i++) {
			funcLines[i] = stream.readLine();
			stream.readLine();
			stream.readLine();
			funcLines[i] += stream.readLine();
			funcLines[i] = funcLines[i].replace(",", "");
		}
		
		resetScanner();
		scanner.next();
		int numQuads = scanner.nextInt();
		
		byte[] data = stream.readBytes(numQuads * 10);
		
		for (int i = 0; i < numFuncts; i++) {
			scanner = new Scanner(funcLines[i]);
			scanner.next();
			String funcName = scanner.next();
			scanner.next();
			int address = scanner.nextInt();
			scanner.next();
			int numParams = scanner.nextInt();
			scanner.next();
			int sizeOfRetVal = scanner.nextInt();
			scanner.next();
			int sizeOfParams = scanner.nextInt();
			scanner.next();
			int sizeOfVolatile = scanner.nextInt();
			scanner.next();
			int sizeOfTempor = scanner.nextInt();
			
			ScbFunction scbFunction = new ScbFunction(scbClass, funcName, address, numParams, sizeOfRetVal, sizeOfParams, sizeOfVolatile, sizeOfTempor);
			scbClass.addFunction(scbFunction);
			
			int pos = address;
			
			ScbQuad quad;
			do {
				byte[] buffer = new byte[10];
				System.arraycopy(data, pos * 10, buffer, 0, 10);
				quad = ScbQuadService.parseQuad(scbFunction, buffer);
				pos++;
				scbFunction.addQuad(quad);
			} while (!quad.getStatement().equals("EndFunction"));
		}
	}

	public String toString() {
		String str = "";
		for (ScbClass c : classes) {
			str += c.toString();
		}
		return str;
	}
}
