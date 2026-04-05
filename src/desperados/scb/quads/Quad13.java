package desperados.scb.quads;

public class Quad13 extends Quad {

	public Quad13(int operand1, int operand2) {
		data[0] = 0x13;
		data[1] = (byte) (operand1 & 0xFF);
		data[2] = (byte) ((operand1 >> 8) & 0xFF);
		data[5] = (byte) (operand2 & 0xFF);
		data[6] = (byte) ((operand2 >> 8) & 0xFF);
		data[7] = (byte) ((operand2 >> 16) & 0xFF);
		data[8] = (byte) ((operand2 >> 24) & 0xFF);
	}
}
