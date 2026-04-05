package desperados.scb.quads;

public class Quad19 extends Quad {

	public Quad19(int operand1, int operand2, int operand3) {
		data[0] = 0x19;
		data[1] = (byte) (operand1 & 0xFF);
		data[2] = (byte) ((operand1 >> 8) & 0xFF);
		data[3] = (byte) (operand2 & 0xFF);
		data[4] = (byte) ((operand2 >> 8) & 0xFF);
		data[5] = (byte) (operand3 & 0xFF);
		data[6] = (byte) ((operand3 >> 8) & 0xFF);
	}
}
