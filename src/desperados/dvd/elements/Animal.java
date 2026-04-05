package desperados.dvd.elements;

import java.io.IOException;

import desperados.exception.JsonParseException;
import desperados.service.AnimationService;
import desperados.util.LittleEndianOuputStream;

public class Animal extends Alive {
	
	private final static int TYPE_ID = 2;

	private enum Subtype {
		HORSE(1), DOG(16), COW(17), HEN(18), PIG(19), CROW(20), CROCODILE(21);
		
		private final int value;
	    Subtype(int value) { this.value = value; }
	    public int getValue() { return value; }
	}
	private Subtype subtype;

	public Animal() { }

	@Override
	public String toString() {
		return String.format("ANIMAL,%s,%s", subtype.toString(), super.toString());
	}

	public Animal(int subtype) {
		switch (subtype) {
		case 1:
			this.subtype = Subtype.HORSE;
			break;
		case 16:
			this.subtype = Subtype.DOG;
			break;
		case 17:
			this.subtype = Subtype.COW;
			break;
		case 18:
			this.subtype = Subtype.HEN;
			break;
		case 19:
			this.subtype = Subtype.PIG;
			break;
		case 20:
			this.subtype = Subtype.CROW;
			break;
		case 21:
			this.subtype = Subtype.CROCODILE;
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

	@Override
	public void checkIntegrity() throws JsonParseException {
		super.checkIntegrity();
		if (subtype == null) {
			throw new JsonParseException("Error: Animal " + super.toString() + " has no subtype!");
		}
	}

	@Override
	public void writeToStream(LittleEndianOuputStream stream, AnimationService animService) throws IOException {
		stream.writeByte(subtype.getValue());
		stream.writeByte(TYPE_ID);
		super.writeToStream(stream, animService);
		stream.writeShort(stance.getValue());
	}
}
