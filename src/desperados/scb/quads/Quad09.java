package desperados.scb.quads;

public class Quad09 extends Quad {

	public Quad09(int operand1, int operand2) {
		data[0] = 9;
		data[1] = (byte) (operand2 & 0xFF);
		data[2] = (byte) ((operand2 >> 8) & 0xFF);
		data[5] = (byte) (operand1 & 0xFF);
		data[6] = (byte) ((operand1 >> 8) & 0xFF);
	}
}
