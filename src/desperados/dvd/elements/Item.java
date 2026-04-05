package desperados.dvd.elements;

import java.io.IOException;

import desperados.service.AnimationService;
import desperados.util.LittleEndianOuputStream;
import desperados.util.Point;

public class Item extends Animation {

	private final static int TYPE_ID = 8;
	private final static int ID = 0;

	private short x2, y2;
	private short u5, u6, u7;
	private int u8;
	private String className;

	@Override
	public String toString() {
		return String.format("%s,%d,%d,%d,%d,%d,%d,\"%s\"", super.toString(), x2, y2, u5, u6, u7, u8, className);
	}

	public short getX2() {
		return x2;
	}

	public void setX2(short x2) {
		this.x2 = x2;
	}

	public short getY2() {
		return y2;
	}

	public void setY2(short y2) {
		this.y2 = y2;
	}

	public short getU5() {
		return u5;
	}

	public void setU5(short u5) {
		this.u5 = u5;
	}

	public short getU6() {
		return u6;
	}

	public void setU6(short u6) {
		this.u6 = u6;
	}

	public short getU7() {
		return u7;
	}

	public void setU7(short u7) {
		this.u7 = u7;
	}

	public int getU8() {
		return u8;
	}

	public void setU8(int u8) {
		this.u8 = u8;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	@Override
	public void writeToStream(LittleEndianOuputStream stream, AnimationService animService) throws IOException {
		stream.writeByte(ID);
		stream.writeByte(TYPE_ID);
		
		stream.writeString(dvf);
		stream.writeString(sprite);
		
		Point origin = animService.getOrigin(getDvf(), getSprite());
		stream.writeShort(x - origin.x);
		stream.writeShort(y - origin.y);
		stream.writeShort(u1);
		stream.writeByte(u2);
		stream.writeByte(u3);
		stream.writeByte(u4);
		
		stream.writeShort(x2);
		stream.writeShort(y2);
		stream.writeShort(u5);
		stream.writeShort(u6);
		stream.writeShort(u7);
		stream.writeInt(u8);
		stream.writeString(className);
	}
}
