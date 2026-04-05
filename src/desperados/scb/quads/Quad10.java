package desperados.scb.quads;

import desperados.scb.ScbFunction;

public class Quad10 extends Quad {

	private String label;

	public Quad10(int operand, String label) {
		data[0] = 0x10;
		data[1] = (byte) (operand & 0xFF);
		data[2] = (byte) ((operand >> 8) & 0xFF);
		this.label = label;
	}

	public void update(ScbFunction function) {
		int operand = function.lookUpLabel(label);
		data[5] = (byte) (operand & 0xFF);
		data[6] = (byte) ((operand >> 8) & 0xFF);
		data[7] = (byte) ((operand >> 16) & 0xFF);
		data[8] = (byte) ((operand >> 24) & 0xFF);
	};
}
