package desperados.scb.quads;

public class Quad15 extends Quad {

	public Quad15(int operand1, int operand2) {
		data[0] = 0x15;
		data[1] = (byte) (operand1 & 0xFF);
		data[2] = (byte) ((operand1 >> 8) & 0xFF);
		data[3] = (byte) (operand2 & 0xFF);
		data[4] = (byte) ((operand2 >> 8) & 0xFF);
	}
}
