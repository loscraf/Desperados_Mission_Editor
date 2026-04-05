package desperados.dvd.elements;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import desperados.exception.JsonParseException;
import desperados.service.AnimationService;
import desperados.util.LittleEndianOuputStream;
import desperados.util.Point;

public class Animation extends Element {

	private final static int TYPE_ID = 16;
	private final static int ID = 1;

	protected short x, y, u1;
	protected byte u2, u3, u4;
	
	@Override
	public String toString() {
		return String.format("ANIMATION,%s,%d,%d,%d,%d,%d,%d", super.toString(), x, y, u1, u2, u3, u4);
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

	public short getU1() {
		return u1;
	}

	public void setU1(short u1) {
		this.u1 = u1;
	}

	public byte getU2() {
		return u2;
	}

	public void setU2(byte u2) {
		this.u2 = u2;
	}

	public byte getU3() {
		return u3;
	}

	public void setU3(byte u3) {
		this.u3 = u3;
	}

	public byte getU4() {
		return u4;
	}

	public void setU4(byte u4) {
		this.u4 = u4;
	}

	@JsonIgnore
	public void setOrigin(short x, short y) {
		this.x += x;
		this.y += y;
	}

	@Override
	public void checkIntegrity() throws JsonParseException {
		super.checkIntegrity();
		// nothing do do here
	}

	@Override
	public void writeToStream(LittleEndianOuputStream stream, AnimationService animService) throws IOException {
		stream.writeByte(ID);
		stream.writeByte(TYPE_ID);
		
		super.writeToStream(stream, animService);
		
		Point origin = animService.getOrigin(getDvf(), getSprite());
		stream.writeShort(x - origin.x);
		stream.writeShort(y - origin.y);
		stream.writeShort(u1);
		stream.writeByte(u2);
		stream.writeByte(u3);
		stream.writeByte(u4);
	}
}
