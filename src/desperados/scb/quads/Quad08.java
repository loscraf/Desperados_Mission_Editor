package desperados.scb.quads;

public class Quad08 extends Quad {

	public Quad08(int operand1, int operand2) {
		data[0] = 8;
		data[1] = (byte) (operand1 & 0xFF);
		data[2] = (byte) ((operand1 >> 8) & 0xFF);
		data[5] = (byte) (operand2 & 0xFF);
		data[6] = (byte) ((operand2 >> 8) & 0xFF);
	}
}
