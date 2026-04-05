package desperados.dvd.elements;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import desperados.exception.IdentifierNotFoundException;
import desperados.exception.IndexNotFoundException;
import desperados.exception.JsonParseException;
import desperados.service.AnimationService;
import desperados.service.FileService;
import desperados.util.LittleEndianOuputStream;
import desperados.util.Point;

@JsonPropertyOrder({ "subtype" })
public class Accessory extends Element {

	private final static int TYPE_ID = 17;
	private final static byte B1 = 0;

	private enum Subtype {
		DYNAMITE(1), SCARECROW(2), TEQUILA(3), SNAKE(4), WATCH(5), CARD(6), KNIFE(7), TNT(8), FLASKS(9), PEANUTS(10),
		SADDLE(11), GATTLING(12), STONE(13), FLASH(14), ITEM_DYNAMITE(16), ITEM_GAS(17), ITEM_LUMINA(18), ITEM_MEDIKIT(19), 
		ITEM_NUTS(20), ITEM_SNIPES(21), ITEM_TEQUILA(22), ITEM_BARREL(23);
		
	    private final int value;
	    Subtype(int value) { this.value = value; }
	    public int getValue() { return value; }
	}
	private Subtype subtype;

	private String mountedTo = "";
	private short amount;

	private AccessoryExtra extraInfo;

	public Accessory() { }

	@Override
	public String toString() {
		String extra = "";
		if (extraInfo != null) extra = extraInfo.toString();
		return String.format("ACCESSORY,%s,%d%s,%d", subtype.toString(), getHorseId(), extra, amount);
	}

	public Accessory(int subtype) {
		switch (subtype) {
		case 1:
			this.subtype = Subtype.DYNAMITE;
			break;
		case 2:
			this.subtype = Subtype.SCARECROW;
			break;
		case 3:
			this.subtype = Subtype.TEQUILA;
			break;
		case 4:
			this.subtype = Subtype.SNAKE;
			break;
		case 5:
			this.subtype = Subtype.WATCH;
			break;
		case 6:
			this.subtype = Subtype.CARD;
			break;
		case 7:
			this.subtype = Subtype.KNIFE;
			break;
		case 8:
			this.subtype = Subtype.TNT;
			break;
		case 9:
			this.subtype = Subtype.FLASKS;
			break;
		case 10:
			this.subtype = Subtype.PEANUTS;
			break;
		case 11:
			this.subtype = Subtype.SADDLE;
			break;
		case 12:
			this.subtype = Subtype.GATTLING;
			break;
		case 13:
			this.subtype = Subtype.STONE;
			break;
		case 14:
			this.subtype = Subtype.FLASH;
			break;
		case 16:
			this.subtype = Subtype.ITEM_DYNAMITE;
			break;
		case 17:
			this.subtype = Subtype.ITEM_GAS;
			break;
		case 18:
			this.subtype = Subtype.ITEM_LUMINA;
			break;
		case 19:
			this.subtype = Subtype.ITEM_MEDIKIT;
			break;
		case 20:
			this.subtype = Subtype.ITEM_NUTS;
			break;
		case 21:
			this.subtype = Subtype.ITEM_SNIPES;
			break;
		case 22:
			this.subtype = Subtype.ITEM_TEQUILA;
			break;
		case 23:
			this.subtype = Subtype.ITEM_BARREL;
			break;
		default:
			throw new AssertionError(subtype);
		}
	}

	public Subtype getSubtype() {
		return subtype;
	}

	public void setSubtype(Subtype subtype) {
		this.subtype = subtype;
	}

	public void setMountedTo(String mountedTo) {
		this.mountedTo = mountedTo;
	}

	public void setMountedTo(Element horse) {
		this.mountedTo = horse.getIdentifier();
	}

	public String getMountedTo() {
		return mountedTo;
	}

	@JsonIgnore
	public short getHorseId() {
		try {
			return FileService.lookupElementByIdentifier(mountedTo);
		} catch (IdentifierNotFoundException e) {
			System.out.println("SADDLE.mountedTo: " + e.getMessage());
		}
		return -1;
	}

	@JsonIgnore
	public Element getHorse() {
		int horseId = getHorseId();
		if (horseId == -1) return null;
		
		try {
			return FileService.lookupElementByIndex(horseId, false);
		} catch (IndexNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@JsonIgnore
	public boolean isGattling() {
		return (subtype == Subtype.GATTLING);
	}

	public short getAmount() {
		return amount;
	}

	public void setAmount(short amount) {
		this.amount = amount;
	}

	public AccessoryExtra getExtraInfo() {
		return extraInfo;
	}

	public AccessoryExtra createExtraInfo() {
		extraInfo = new AccessoryExtra();
		return extraInfo;
	}

	@JsonIgnore
	public byte getDirection() {
		if (hasExtraInfo()) {
			return (byte)extraInfo.getS5();
		}
		return 0;
	}

	@JsonIgnore
	public byte getAnimID() {
		if (hasExtraInfo()) {
			return (byte)extraInfo.getS6();
		}
		return 0;
	}

	@Override
	public void checkIntegrity() throws JsonParseException {
		super.checkIntegrity();
		if (subtype == null) {
			throw new JsonParseException("Error: Accessory " + super.toString() + "has no subtype!");
		}
		try {
			FileService.lookupElementByIdentifier(mountedTo);
		} catch (IdentifierNotFoundException e) {
			throw new JsonParseException("HORSE.mountedTo: " + e.getMessage());
		}
	}

	@Override
	public void writeToStream(LittleEndianOuputStream stream, AnimationService animService) throws IOException {
		stream.writeByte(subtype.getValue());
		stream.writeByte(TYPE_ID);
		stream.writeString(dvf);
		stream.writeString(sprite);
		stream.writeByte(B1);
		
		if (extraInfo == null) {
			stream.writeByte(1);
		} else {
			stream.writeByte(0);
		}
		
		stream.writeShort(getHorseId());
		
		if (extraInfo != null) {
			Point origin = animService.getOrigin(getDvf(), getSprite());
			stream.writeShort(extraInfo.getX() - origin.x);
			stream.writeShort(extraInfo.getY() - origin.y);
			stream.writeShort(extraInfo.getS2());
			stream.writeShort(extraInfo.getS3());
			stream.writeShort(extraInfo.getS4());
			stream.writeShort(extraInfo.getS5());
			stream.writeShort(extraInfo.getS6());
		}
		
		stream.writeShort(amount);
		
		if (extraInfo != null && extraInfo.getGattlingInfo() != null) {
			stream.writeShort(extraInfo.getGattlingInfo().getGattlingInfo1());
			stream.writeShort(extraInfo.getGattlingInfo().getGattlingInfo2());
		}
	}

	@JsonIgnore
	public boolean hasExtraInfo() {
		return (extraInfo != null);
	}

	@JsonIgnore
	public short getX() {
		if (hasExtraInfo()) {
			return extraInfo.getX();
		}
		return -1;
	}

	@JsonIgnore
	public short getY() {
		if (hasExtraInfo()) {
			return extraInfo.getY();
		}
		return -1;
	}

	@JsonIgnore
	public void setX(short x) {
		if (hasExtraInfo()) {
			extraInfo.setX(x);
		}
	}

	@JsonIgnore
	public void setY(short y) {
		if (hasExtraInfo()) {
			extraInfo.setY(y);
		}
	}

	@JsonIgnore
	public void setOrigin(short x, short y) {
		if (hasExtraInfo()) {
			extraInfo.setOrigin(x, y);
		}
	}
}
