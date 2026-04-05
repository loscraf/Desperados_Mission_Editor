package desperados.dvd.elements;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;

import desperados.exception.IdentifierNotFoundException;
import desperados.exception.JsonParseException;
import desperados.service.AnimationService;
import desperados.service.FileService;
import desperados.util.LittleEndianOuputStream;

public class NPC extends Alive {

	private final static int TYPE_ID = 1;
	private final static int N1 = 0;

	private enum Subtype {
		ENEMY(1), CIVILIAN(2);
		
		private final int value;
	    Subtype(int value) { this.value = value; }
	    public int getValue() { return value; }
	};
	private Subtype subtype;

	public NPC() { }

	private enum Character {
		DEFAULT_VILLAIN(0),
		DEFAULT_CHILD(1),
		LOPEZ(2),
		COWBOY1(3),
		COWBOY2(4),
		SHERIF(5),
		COWBOY3(6),
		PEDRO1(7),
		GARCIA1(8),
		PEDRO2(9),
		PEDRO3(10),
		PEDRO4(11),
		SOLDAT1(12),
		SOLDAT2(13),
		GARDE1(14),
		GARCIA2(15),
		GARDE2(16),
		DESPERADO1(17),
		COWBOY4(18),
		LINO(19),
		DESPERADO2(20),
		DESPERADO3(21),
		DESPERADO4(22),
		DEMONIO(23),
		COURAGE_5(24),
		IQ_100(25),
		IQ_10(26),
		JUST_A_TEST(27),
		CARLOS(28),
		LOPEZ2(29),
		CARLOS2(30),
		GROSDEBILE(32),
		SCHWARZENEGGER(33),
		SANCHEZ_NPC(34),
		BIGBILL_PEUREUX(35),
		TUNIQUE_BLEUE(36),
		TUNIQUE_BLEUE_OF(37),
		EL_DIABLO(38),
		COWBOY3_SNIPER(39),
		DILLON(40);
		
		private final int value;
		Character(int value) { this.value = value; }
	    public int getValue() { return value; }
	}
	private Character character;
	
	private short tiredness; // 0 - 100
	private enum Attitude {
		SUSPICIOUS(0), NEUTRAL(1), NERVOUS(2), HOSTILE(3);
		
		private final int value;
		Attitude(int value) { this.value = value; }
	    public int getValue() { return value; }
	};
	private Attitude attitude;
	private byte drunkLevel; // 0 - 100
	private String route = "";

	public NPC(int subtype) {
		switch (subtype) {
		case 1:
			this.subtype = Subtype.ENEMY;
			break;
		case 2:
			this.subtype = Subtype.CIVILIAN;
			break;
		default:
			throw new AssertionError(subtype);
		}
	}

	@Override
	public String toString() {
		return String.format("NPC,%s,%s,%d,%d,%s,%d,%d", subtype.toString(), super.toString(), character.toString(), tiredness, attitude.toString(), drunkLevel, getRouteId());
	}

	public Subtype getSubtype() {
		return subtype;
	}

	public void setSubtype(Subtype subtype) {
		this.subtype = subtype;
	}

	public Character getCharacter() {
		return character;
	}

	public void setCharacter(Character character) {
		this.character = character;
	}

	public void initCharacter(short characterId) {
		switch (characterId) {
		case 0:
			character = Character.DEFAULT_VILLAIN;
			break;
		case 1:
			character = Character.DEFAULT_CHILD;
			break;
		case 2:
			character = Character.LOPEZ;
			break;
		case 3:
			character = Character.COWBOY1;
			break;
		case 4:
			character = Character.COWBOY2;
			break;
		case 5:
			character = Character.SHERIF;
			break;
		case 6:
			character = Character.COWBOY3;
			break;
		case 7:
			character = Character.PEDRO1;
			break;
		case 8:
			character = Character.GARCIA1;
			break;
		case 9:
			character = Character.PEDRO2;
			break;
		case 10:
			character = Character.PEDRO3;
			break;
		case 11:
			character = Character.PEDRO4;
			break;
		case 12:
			character = Character.SOLDAT1;
			break;
		case 13:
			character = Character.SOLDAT2;
			break;
		case 14:
			character = Character.GARDE1;
			break;
		case 15:
			character = Character.GARCIA2;
			break;
		case 16:
			character = Character.GARDE2;
			break;
		case 17:
			character = Character.DESPERADO1;
			break;
		case 18:
			character = Character.COWBOY4;
			break;
		case 19:
			character = Character.LINO;
			break;
		case 20:
			character = Character.DESPERADO2;
			break;
		case 21:
			character = Character.DESPERADO3;
			break;
		case 22:
			character = Character.DESPERADO4;
			break;
		case 23:
			character = Character.DEMONIO;
			break;
		case 24:
			character = Character.COURAGE_5;
			break;
		case 25:
			character = Character.IQ_100;
			break;
		case 26:
			character = Character.IQ_10;
			break;
		case 27:
			character = Character.JUST_A_TEST;
			break;
		case 28:
			character = Character.CARLOS;
			break;
		case 29:
			character = Character.LOPEZ2;
			break;
		case 30:
			character = Character.CARLOS2;
			break;
		case 32:
			character = Character.GROSDEBILE;
			break;
		case 33:
			character = Character.SCHWARZENEGGER;
			break;
		case 34:
			character = Character.SANCHEZ_NPC;
			break;
		case 35:
			character = Character.BIGBILL_PEUREUX;
			break;
		case 36:
			character = Character.TUNIQUE_BLEUE;
			break;
		case 37:
			character = Character.TUNIQUE_BLEUE_OF;
			break;
		case 38:
			character = Character.EL_DIABLO;
			break;
		case 39:
			character = Character.COWBOY3_SNIPER;
			break;
		case 40:
			character = Character.DILLON;
			break;
		default:
			throw new AssertionError(characterId);
		}
	}

	public short getTiredness() {
		return tiredness;
	}

	public void setTiredness(short tiredness) {
		this.tiredness = tiredness;
	}

	public Attitude getAttitude() {
		return attitude;
	}

	public void setAttitude(Attitude attitude) {
		this.attitude = attitude;
	}

	public void initAttitude(byte attitude) {
		switch (attitude) {
		case 0:
			this.attitude = Attitude.SUSPICIOUS;
			break;
		case 1:
			this.attitude = Attitude.NEUTRAL;
			break;
		case 2:
			this.attitude = Attitude.NERVOUS;
			break;
		case 3:
			this.attitude = Attitude.HOSTILE;
			break;
		default:
			throw new AssertionError(attitude);
		}
	}

	public byte getDrunkLevel() {
		return drunkLevel;
	}

	public void setDrunkLevel(byte drunkLevel) {
		this.drunkLevel = drunkLevel;
	}

	public String getRoute() {
		return route;
	}

	@JsonIgnore
	public short getRouteId() {
		try {
			return FileService.lookupRouteByIdentifier(route);
		} catch (IdentifierNotFoundException e) {
			System.out.println("NPC.route: " + e.getMessage());
		}
		return -1;
	}

	@JsonIgnore
	public boolean isEnemy() {
		return (subtype == Subtype.ENEMY);
	}

	@Override
	public void checkIntegrity() throws JsonParseException {
		super.checkIntegrity();
		if (subtype == null) {
			throw new JsonParseException("Error: NPC " + super.toString() + " has no subtype!");
		}
		if (attitude == null) {
			throw new JsonParseException("Error: NPC " + super.toString() + " has no attitude!");
		}
		try {
			FileService.lookupRouteByIdentifier(route);
		} catch (IdentifierNotFoundException e) {
			throw new JsonParseException("NPC.route: " + e.getMessage());
		}
	}

	@Override
	public void writeToStream(LittleEndianOuputStream stream, AnimationService animService) throws IOException {
		stream.writeByte(subtype.getValue());
		stream.writeByte(TYPE_ID);
		
		super.writeToStream(stream, animService);
		
		stream.writeShort(character.getValue());
		stream.writeShort(N1);
		if (isEnemy()) {
			stream.writeShort(tiredness);
		}
		stream.writeByte(attitude.getValue());
		stream.writeByte(drunkLevel);
		stream.writeShort(getRouteId());
		
		stream.writeShort(stance.getValue());
	}

	public void setRouteName(String routeName) {
		this.route = routeName;
	}
}
