package desperados.scb.quads;

public class Quad14 extends Quad {

	public Quad14(int operand1, float operand2) {
		int bits = Float.floatToIntBits(operand2);
		data[0] = 0x14;
		data[1] = (byte) (operand1 & 0xFF);
		data[2] = (byte) ((operand1 >> 8) & 0xFF);
		data[5] = (byte) (bits & 0xFF);
		data[6] = (byte) ((bits >> 8) & 0xFF);
		data[7] = (byte) ((bits >> 16) & 0xFF);
		data[8] = (byte) ((bits >> 24) & 0xFF);
	}
}
