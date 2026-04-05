package desperados.scb.quads;

import desperados.scb.ScbFunction;

public class Quad0E extends Quad {

	private String label;

	public Quad0E(String label) {
		data[0] = 0xE;
		this.label = label;
	}

	public void update(ScbFunction function) {
		int operand = function.lookUpLabel(label);
		data[1] = (byte) (operand & 0xFF);
		data[2] = (byte) ((operand >> 8) & 0xFF);
		data[3] = (byte) ((operand >> 16) & 0xFF);
		data[4] = (byte) ((operand >> 24) & 0xFF);
	};
}
