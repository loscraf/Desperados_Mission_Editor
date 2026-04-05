package desperados.dvd.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AccessoryExtra {
	private short x, y;
	private short s2, s3, s4, s5, s6; // s5 = direction, s6 = animID
	private AccessoryExtraGattling gattlingInfo = null;

	@Override
	public String toString() {
		String gattlingExtra = "";
		if (gattlingInfo != null) gattlingExtra = gattlingInfo.toString();
		return String.format(",%d,%d,%d,%d,%d,%d,%d%s", x, y, s2, s3, s4, s5, s6, gattlingExtra);
	}

	public short getS2() {
		return s2;
	}

	public void setS2(short s2) {
		this.s2 = s2;
	}

	public short getS3() {
		return s3;
	}

	public void setS3(short s3) {
		this.s3 = s3;
	}

	public short getX() {
		return x;
	}

	public void setX(short x) {
		this.x = x;
	}

	public short getY() {
		return y;
	}

	public void setY(short y) {
		this.y = y;
	}

	public short getS4() {
		return s4;
	}

	public void setS4(short s4) {
		this.s4 = s4;
	}

	public short getS5() {
		return s5;
	}

	public void setS5(short s5) {
		this.s5 = s5;
	}

	public short getS6() {
		return s6;
	}

	public void setS6(short s6) {
		this.s6 = s6;
	}

	public AccessoryExtraGattling getGattlingInfo() {
		return gattlingInfo;
	}

	public AccessoryExtraGattling createGattlingInfo() {
		gattlingInfo = new AccessoryExtraGattling();
		return gattlingInfo;
	}

	@JsonIgnore
	public void setOrigin(short x, short y) {
		this.x += x;
		this.y += y;
	}
}
