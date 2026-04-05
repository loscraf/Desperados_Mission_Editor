package desperados.scb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import desperados.scb.quads.Quad;
import desperados.util.LittleEndianOuputStream;

public class ScbFunction {

	private final static String LABEL = "label_";
	private final boolean outputLineNumbers = false;

	private ScbClass scbClass;
	private String name;
	private int address;
	private int numParams;
	private int sizeOfRetVal;
	private int sizeOfParams;
	//private int sizeOfVolatile;
	//private int sizeOfTempor;

	private List<ScbQuad> quads;
	private Map<Integer, Integer> labelAddress;
	private List<String> volVariables;
	private List<String> tempVariables;

	private List<Quad> binaryQuads;

	public ScbFunction(ScbClass scbClass, String name) {
		this.scbClass = scbClass;
		this.name = name;
		volVariables = new ArrayList<>();
		tempVariables = new ArrayList<>();
		address = scbClass.getQuadCounter();
		
		int[] values = ScbFunctionSignature.lookUp(scbClass, name);
		numParams = values[0];
		sizeOfRetVal = values[1];
		sizeOfParams = values[2];
	}

	public ScbFunction(ScbClass scbClass, String name, int address, int numParams, int sizeOfRetVal, int sizeOfParams, int sizeOfVolatile, int sizeOfTempor) {
		this.scbClass = scbClass;
		this.name = name;
		this.address = address;
		this.numParams = numParams;
		this.sizeOfRetVal = sizeOfRetVal;
		this.sizeOfParams = sizeOfParams;
		//this.sizeOfVolatile = sizeOfVolatile;
		//this.sizeOfTempor = sizeOfTempor;
		
		quads = new ArrayList<>();
		labelAddress = new HashMap<>();
	}

	public void addQuad(ScbQuad quad) {
		quads.add(quad);
	}

	public void addQuad(Quad quad) {
		if (binaryQuads == null) {
			binaryQuads = new ArrayList<Quad>();
		}
		binaryQuads.add(quad);
		scbClass.increaseQuadCounter();
	}

	public int getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}

	public String addLabel(int address) {
		if (labelAddress.containsKey(address)) {
			return LABEL + labelAddress.get(address);
		}
		int index = scbClass.getNextLabelIndex();
		labelAddress.put(address, index);
		return LABEL + index;
	}

	public String getLabelAddress(int pos) {
		if (labelAddress.containsKey(pos)) {
			return LABEL + labelAddress.get(pos) + ":";
		}
		return null;
	}

	public String getSignature() {
		String functionSignature = name + "(";
		for (int j = 0; j < numParams - 1; j++) {
			functionSignature += "param" + j;
			if (j != numParams - 2) {
				functionSignature += ", ";
			}
		}
		functionSignature += ")";
		return functionSignature;
	}

	public String toString() {
		String str =  "\nFunction " + getSignature() + "\n";
		
		//str += String.format("Function %s, address: %d, numParams: %d, sizeOfRetVal: %d, sizeOfParams: %d, sizeOfVol: %d, sizeOfTemp: %d\n", name, address, numParams, sizeOfRetVal, sizeOfParams, sizeOfVolatile, sizeOfTempor);
		
		for (int i = 1; i < quads.size() - 2; i++) {	// ignore InitFunction, RETURN, EndFunction
			String labelAddress = getLabelAddress(address + i);
			if (labelAddress != null) {
				str += labelAddress + "\n";
			}
			
			if (i < quads.size() - 3) { // don't output final NOP (but we still wan't the label if there's one pointing to the NOP quad)
				if (outputLineNumbers) {
					str += String.format("%04d|", address + i);
				}
				str += "\t" + quads.get(i).toString() + "\n";
			}
		}
		str += "End Function\n";
		
		return str;
	}

	public ScbClass getScbClass() {
		return scbClass;
	}

	public int lookUpVar(String varname) {
		for (int i = 0; i < volVariables.size(); i++) {
			if (volVariables.get(i).equals(varname)) {
				return (i << 2) | 0x8000;
			}
		}
		for (int i = 0; i < tempVariables.size(); i++) {
			if (tempVariables.get(i).equals(varname)) {
				return (i << 2) | 0xC000;
			}
		}
		return -1;
	}

	public void addTempVar(String varname) {
		if (!tempVariables.contains(varname)) {
			tempVariables.add(varname);
		}
	}

	public void addVolVar(String varname) {
		if (!volVariables.contains(varname)) {
			volVariables.add(varname);
		}
	}

	public int getNumVolsVars() {
		return volVariables.size();
	}

	public int getNumTempVars() {
		return tempVariables.size();
	}

	public int lookUpLabel(String label) {
		return scbClass.lookUpLabel(label);
	}

	public int getNumParams() {
		return numParams;
	}

	public int getSizeOfRetVal() {
		return sizeOfRetVal;
	}

	public int getSizeOfParams() {
		return sizeOfParams;
	}

	public byte[] getQuadData() throws IOException {
		LittleEndianOuputStream stream = new LittleEndianOuputStream();
		
		for (Quad q : binaryQuads) {
			q.update(this);
			stream.writeBytes(q.getData());
		}
		
		return stream.getBytes();
	}

	public void setSizeOfRetVal(int i) {
		sizeOfRetVal = i;
	}
}
