package desperados.scb.quads;

public class Quad0D extends Quad {

	public Quad0D(int operand) {
		data[0] = 0xD;
		data[1] = (byte) (operand & 0xFF);
		data[2] = (byte) ((operand >> 8) & 0xFF);
	}
}
