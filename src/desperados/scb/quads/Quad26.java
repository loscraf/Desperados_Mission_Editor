package desperados.scb.quads;

public class Quad26 extends Quad {

	public Quad26(int operand1, int operand2, int operand3) {
		data[0] = 0x26;
		data[1] = (byte) (operand1 & 0xFF);
		data[2] = (byte) ((operand1 >> 8) & 0xFF);
		data[3] = (byte) (operand2 & 0xFF);
		data[4] = (byte) ((operand2 >> 8) & 0xFF);
		data[5] = (byte) (operand3 & 0xFF);
		data[6] = (byte) ((operand3 >> 8) & 0xFF);
	}
}
