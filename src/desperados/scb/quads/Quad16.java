package desperados.scb.quads;

public class Quad16 extends Quad {

	public Quad16(int operand1, int operand2) {
		data[0] = 0x16;
		data[1] = (byte) (operand1 & 0xFF);
		data[2] = (byte) ((operand1 >> 8) & 0xFF);
		data[3] = (byte) (operand2 & 0xFF);
		data[4] = (byte) ((operand2 >> 8) & 0xFF);
	}
}
