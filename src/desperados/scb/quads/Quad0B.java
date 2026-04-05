package desperados.scb.quads;

public class Quad0B extends Quad {

	public Quad0B(int operand) {
		data[0] = 0xB;
		data[1] = (byte) (operand & 0xFF);
		data[2] = (byte) ((operand >> 8) & 0xFF);
	}
}
