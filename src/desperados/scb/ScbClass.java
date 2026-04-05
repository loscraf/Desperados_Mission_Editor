package desperados.scb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import desperados.util.LittleEndianOuputStream;

public class ScbClass {

	private String classname;
	
	private int labelIndex = 0;
	private Integer quadCounter;
	
	private List<String> classVariables;
	private Map<String, ScbFunction> functions;
	private Map<Integer, String> functionAddresses = new LinkedHashMap<>();
	private Map<String, Integer> labels;
	private int address;

	public ScbClass(String classname) {
		this.classname = classname;
		classVariables = new ArrayList<>();
		functions = new LinkedHashMap<>();
		labels = new LinkedHashMap<>();
		quadCounter = 0;
	}

	public ScbClass(String classname, int numVars, int varSize, int numFunctions) {
		this.classname = classname;
		classVariables = new ArrayList<>(numVars);
		functions = new LinkedHashMap<>(numFunctions);
	}

	public int getNumVariables() {
		return classVariables.size();
	}

	public int getVariablesSize() {
		return getNumVariables() * 4;
	}

	public int getNumFunctions() {
		return functions.size();
	}

	public void addFunction(ScbFunction function) {
		functions.put(function.getName(), function);
		functionAddresses.put(function.getAddress(), function.getSignature());
	}

	public String getFunctionName(int address) {
		return functionAddresses.get(address);
	}

	public int getNextLabelIndex() {
		return labelIndex++;
	}

	public Map<String, ScbFunction> getFunctions() {
		return functions;
	}

	public String toString() {
		String str = 
		"##################################################\n" + 
		"Class " + classname + "\n" +
		"##################################################\n";
		
		for (Map.Entry<String, ScbFunction> entry : functions.entrySet()) {
			str += entry.getValue().toString();
		}
		
		str += "\nEnd Class\n";
		
		return str;
	}

	public Integer addLabel(String label) {
		return labels.put(label, quadCounter);
	}

	public void increaseQuadCounter() {
		quadCounter++;
	}

	public int getQuadCounter() {
		return quadCounter;
	}

	public int lookUpVar(String varname) {
		for (int i = 0; i < classVariables.size(); i++) {
			if (classVariables.get(i).equals(varname)) {
				return (i << 2) | 0x4000;
			}
		}
		return -1;
	}

	public void addClassVar(String varname) {
		if (!classVariables.contains(varname)) {
			classVariables.add(varname);
		}
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) { // TODO
		this.address = address;
	}

	public int lookUpLabel(String label) {
		return labels.get(label);
	}

	public byte[] getData() throws IOException {
		LittleEndianOuputStream stream = new LittleEndianOuputStream();
		
		stream.writeString2(String.format("fileName fileName Z:\\Desperados_Mission_Editor\\script.scs , className %s\n", classname));
		stream.writeString2(String.format("nbOfVariables %d, sizeOfVariables %d\n", classVariables.size(), classVariables.size() * 4));
		stream.writeString2(String.format("nbOfFunctions %d\n", functions.size()));
		
		for (Map.Entry<String, ScbFunction> entry : functions.entrySet()) {
			ScbFunction f = entry.getValue();
			
			stream.writeString2(String.format("functionName %s , address %d, nbOfParams %d, sizeOfRetVal %d, sizeOfParams %d\n",
					f.getName(), f.getAddress(), f.getNumParams(), f.getSizeOfRetVal(), f.getSizeOfParams()));
			stream.writeString2("functionParameters\n\n");
			stream.writeString2(String.format(" sizeOfVolatile %d, sizeOfTempor %d\n", f.getNumVolsVars() * 4, f.getNumTempVars() * 4));
		}
		
		stream.writeString2(String.format("nbOfQuads %d\n", quadCounter));
		
		for (Map.Entry<String, ScbFunction> entry : functions.entrySet()) {
			ScbFunction f = entry.getValue();
			stream.writeBytes(f.getQuadData());
		}
		
		return stream.getBytes();
	}

	public String getName() {
		return classname;
	}

	public ScbFunction getFunction(String name) {
		return functions.get(name);
	}
}
