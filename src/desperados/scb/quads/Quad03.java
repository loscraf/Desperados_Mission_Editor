package desperados.scb.quads;

import desperados.scb.ScbFunction;

public class Quad03 extends Quad {

	public Quad03() {
		data[0] = 3;
	}

	public void update(ScbFunction function) {
		int operand1 = function.getNumVolsVars() * 4;
		int operand2 = function.getNumTempVars() * 4;
		data[1] = (byte) (operand1 & 0xFF);
		data[2] = (byte) ((operand1 >> 8) & 0xFF);
		data[3] = (byte) (operand2 & 0xFF);
		data[4] = (byte) ((operand2 >> 8) & 0xFF);
	}
}
