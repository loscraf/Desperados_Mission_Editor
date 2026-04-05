package desperados.scb.quads;

public class Quad0A extends Quad {

	public Quad0A(int operand) {
		data[0] = 0xA;
		data[1] = (byte) (operand & 0xFF);
		data[2] = (byte) ((operand >> 8) & 0xFF);
	}
}
