package desperados.scb.quads;

import desperados.scb.ScbFunction;

public abstract class Quad {

	byte[] data = new byte[]{0,0,0,0,0,0,0,0,0,0x7E};

	public byte[] getData() {
		return data;
	}

	public void update(ScbFunction function) {};
}
