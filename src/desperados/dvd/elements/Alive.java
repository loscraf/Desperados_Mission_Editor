package desperados.dvd.elements;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import desperados.exception.JsonParseException;
import desperados.service.AnimationService;
import desperados.util.LittleEndianOuputStream;
import desperados.util.Point;

@JsonPropertyOrder({ "subtype" })
public abstract class Alive extends Element {

	protected enum Subtype { };
	protected Subtype subtype;
	
	protected String dvfAlternative;
	protected String spriteAlternative;
	
	protected byte p00;
	protected short p01, p02, p03, p04;
	protected byte p10;
	protected short p11, p12, p13, p14;
	
	protected short x, y;
	protected short u1, u2, u3;
	
	protected byte u4;
	protected byte direction;
	
	protected String className;

	public enum Stance {
		STANDING(0), SITTING(1), CHATTING(2), CROUCHING(6), PRONE(7), MOUNTED(16), DEAD(33), SLEEPING(84);
		
		private final int value;
		Stance(int value) { this.value = value; }
	    public int getValue() { return value; }
	}
	protected Stance stance;

	public String toString() {
		String alt = "0";
		if (dvfAlternative != null) {
			alt = String.format("1,\"%s\",\"%s\"", dvfAlternative, spriteAlternative);
		}
		return String.format("%s,%s,%d,%d,%d,%d,%d,%d,%d,%d,%d,%d,x=%d,y=%d,%d,%d,%d,%d,%d,%s", super.toString(), alt,
				p00, p01, p02, p03, p04, p10, p11, p12, p13, p14, x, y, u1, u2, u3, u4, direction, stance.toString());
	}

	public boolean hasAltProfile() {
		return (dvfAlternative != null);
	}

	public String getDvfAlternative() {
		return dvfAlternative;
	}

	public void setDvfAlternative(String dvfAlternative) {
		this.dvfAlternative = dvfAlternative;
	}

	public String getSpriteAlternative() {
		return spriteAlternative;
	}

	public void setSpriteAlternative(String spriteAlternative) {
		this.spriteAlternative = spriteAlternative;
	}

	public byte getP00() {
		return p00;
	}

	public void setP00(byte p00) {
		this.p00 = p00;
	}

	public byte getP10() {
		return p10;
	}

	public void setP10(byte p10) {
		this.p10 = p10;
	}

	public short getP01() {
		return p01;
	}

	public void setP01(short p01) {
		this.p01 = p01;
	}

	public short getP02() {
		return p02;
	}

	public void setP02(short p02) {
		this.p02 = p02;
	}

	public short getP03() {
		return p03;
	}

	public void setP03(short p03) {
		this.p03 = p03;
	}

	public short getP04() {
		return p04;
	}

	public void setP04(short p04) {
		this.p04 = p04;
	}

	public short getP11() {
		return p11;
	}

	public void setP11(short p11) {
		this.p11 = p11;
	}

	public short getP12() {
		return p12;
	}

	public void setP12(short p12) {
		this.p12 = p12;
	}

	public short getP13() {
		return p13;
	}

	public void setP13(short p13) {
		this.p13 = p13;
	}

	public short getP14() {
		return p14;
	}

	public void setP14(short p14) {
		this.p14 = p14;
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

	public short getU2() {
		return u2;
	}

	public void setU2(short u2) {
		this.u2 = u2;
	}

	public short getU3() {
		return u3;
	}

	public void setU3(short u3) {
		this.u3 = u3;
	}

	public byte getU4() {
		return u4;
	}

	public void setU4(byte u4) {
		this.u4 = u4;
	}

	public byte getDirection() {
		return direction;
	}

	public void setDirection(byte direction) {
		this.direction = direction;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Stance getStance() {
		return stance;
	}

	public void setStance(Stance stance) {
		this.stance = stance;
	}

	@JsonIgnore
	public void setOrigin(short x, short y) {
		this.x += x;
		this.y += y;
	}

	public void initStance(short stance) {
		switch(stance) {
		case 0:
			this.stance = Stance.STANDING;
			break;
		case 1:
			this.stance = Stance.SITTING;
			break;
		case 2:
			this.stance = Stance.CHATTING;
			break;
		case 6:
			this.stance = Stance.CROUCHING;
			break;
		case 7:
			this.stance = Stance.PRONE;
			break;
		case 16:
			this.stance = Stance.MOUNTED;
			break;
		case 33:
			this.stance = Stance.DEAD;
			break;
		case 84:
			this.stance = Stance.SLEEPING;
			break;
		default:
			throw new AssertionError(stance);
		}
	}

	@Override
	public void checkIntegrity() throws JsonParseException {
		super.checkIntegrity();
		if (stance == null) {
			throw new JsonParseException("Error: Element " + super.toString() + " has no stance!");
		}
	}

	public void writeToStream(LittleEndianOuputStream stream, AnimationService animService) throws IOException {
		super.writeToStream(stream, animService);
		
		if (dvfAlternative == null) {
			stream.writeByte(0);
		} else {
			stream.writeByte(1);
			stream.writeString(dvfAlternative);
			stream.writeString(spriteAlternative);
		}
		
		stream.writeByte(p00);
		stream.writeShort(p01);
		stream.writeShort(p02);
		stream.writeShort(p03);
		stream.writeShort(p04);
		
		stream.writeByte(p10);
		stream.writeShort(p11);
		stream.writeShort(p12);
		stream.writeShort(p13);
		stream.writeShort(p14);
		
		Point origin = animService.getOrigin(getDvf(), getSprite());
		stream.writeShort(x - origin.x);
		stream.writeShort(y - origin.y);
		stream.writeShort(u1);
		stream.writeShort(u2);
		stream.writeShort(u3);
		
		stream.writeByte(u4);
		stream.writeByte(direction);
		
		if (className == null) {
			stream.writeShort(0);
		} else {
			stream.writeString(className);
		}
	}
}
