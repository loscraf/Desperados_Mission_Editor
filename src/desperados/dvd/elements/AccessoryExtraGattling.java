package desperados.dvd.elements;

public class AccessoryExtraGattling {
	private short gattlingInfo1, gattlingInfo2;

	@Override
	public String toString() {
		return String.format(",%d,%d", gattlingInfo1, gattlingInfo2);
	}

	public short getGattlingInfo1() {
		return gattlingInfo1;
	}

	public void setGattlingInfo1(short gattlingInfo1) {
		this.gattlingInfo1 = gattlingInfo1;
	}

	public short getGattlingInfo2() {
		return gattlingInfo2;
	}

	public void setGattlingInfo2(short gattlingInfo2) {
		this.gattlingInfo2 = gattlingInfo2;
	}
}
