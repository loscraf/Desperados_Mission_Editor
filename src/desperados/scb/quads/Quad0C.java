package desperados.scb.quads;

public class Quad0C extends Quad {

	public Quad0C(int operand) {
		data[0] = 0xC;
		data[1] = (byte) (operand & 0xFF);
		data[2] = (byte) ((operand >> 8) & 0xFF);
		data[3] = (byte) ((operand >> 16) & 0xFF);
		data[4] = (byte) ((operand >> 24) & 0xFF);
	}
}
