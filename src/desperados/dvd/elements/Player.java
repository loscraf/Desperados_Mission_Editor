package desperados.dvd.elements;

import java.io.IOException;

import desperados.exception.JsonParseException;
import desperados.service.AnimationService;
import desperados.util.LittleEndianOuputStream;

public class Player extends Alive {

	private final static int TYPE_ID = 0;

	private enum Subtype {
		COOPER(1), DOC(2), SAM(3), KATE(4), SANCHEZ(5), MIA(6), LEONE(7);
		
		private final int value;
	    Subtype(int value) { this.value = value; }
	    public int getValue() { return value; }
	}
	private Subtype subtype;

	public Player() { }

	@Override
	public String toString() {
		return String.format("PLAYER,%s,%s", subtype.toString(), super.toString());
	}

	public Player(int subtype) {
		switch (subtype) {
		case 1:
			this.subtype = Subtype.COOPER;
			break;
		case 2:
			this.subtype = Subtype.DOC;
			break;
		case 3:
			this.subtype = Subtype.SAM;
			break;
		case 4:
			this.subtype = Subtype.KATE;
			break;
		case 5:
			this.subtype = Subtype.SANCHEZ;
			break;
		case 6:
			this.subtype = Subtype.MIA;
			break;
		case 7:
			this.subtype = Subtype.LEONE;
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
			throw new JsonParseException("Error: Player " + super.toString() + " has no subtype!");
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
