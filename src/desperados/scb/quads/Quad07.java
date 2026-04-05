package desperados.scb.quads;

public class Quad07 extends Quad {

	public Quad07(int operand) {
		data[0] = 7;
		data[1] = (byte) (operand & 0xFF);
		data[2] = (byte) ((operand >> 8) & 0xFF);
	}
}
