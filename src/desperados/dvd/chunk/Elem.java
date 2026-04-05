package desperados.dvd.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.elements.Accessory;
import desperados.dvd.elements.AccessoryExtra;
import desperados.dvd.elements.AccessoryExtraGattling;
import desperados.dvd.elements.Alive;
import desperados.dvd.elements.Animal;
import desperados.dvd.elements.Animation;
import desperados.dvd.elements.Element;
import desperados.dvd.elements.Item;
import desperados.dvd.elements.Player;
import desperados.service.FileService;
import desperados.dvd.elements.NPC;

public class Elem extends Chunk  {
	
	private List<Element> elements;
	
	// MAYOR TYPES
	private final static int PLAYER    =  0;
	private final static int NPC       =  1;
	private final static int ANIMAL    =  2;
	private final static int ITEM      =  8;
	private final static int ANIMATION = 16;
	private final static int ACCESSORY = 17;
	
	/*
	// MINOR TYPES
	private final static int COOPER  = 1;
	private final static int DOC     = 2;
	private final static int SAM     = 3;
	private final static int KATE    = 4;
	private final static int SANCHEZ = 5;
	private final static int MIA     = 6;
	private final static int LEIONE  = 7;

	private final static int ENEMY    = 1;
	private final static int CIVILIAN = 2;
	
	private final static int HORSE =  1;
	private final static int DOG   = 16;
	private final static int COW   = 17;
	private final static int HEN   = 18;
	private final static int PIG   = 19;
	private final static int CROW  = 20;
	private final static int CROCO = 21;
	
	private final static int DYNAMITE  =  1;
	private final static int SCARECROW =  2;
	private final static int TEQUILA   =  3;
	private final static int SNAKE     =  4;
	private final static int WATCH     =  5;
	private final static int CARD      =  6;
	private final static int KNIFE     =  7;
	private final static int TNT       =  8;
	private final static int FLASKS    =  9;
	private final static int PEANUTS   = 10;
	private final static int SADDLE    = 11;
	private final static int GATTLING  = 12;
	private final static int STONE     = 13;
	private final static int FLASH     = 14;
	private final static int ITEM_DYNAMITE = 16;
	private final static int ITEM_GAS      = 17;
	private final static int ITEM_LUMINA   = 18;
	private final static int ITEM_MEDIKIT  = 19;
	private final static int ITEM_NUTS     = 20;
	private final static int ITEM_SNIPES   = 21;
	private final static int ITEM_TEQUILA  = 22;
	private final static int ITEM_BARREL   = 23;
	
	// STANCE
	private final static int STANDING  =  0;
	private final static int SITTING   =  1;
	private final static int CHATTING  =  2;
	private final static int CROUCHING =  6;
	private final static int PRONE     =  7;
	private final static int MOUNTED   = 16;
	private final static int DEAD      = 33;
	private final static int SLEEPING  = 84;
	*/

	@Override
	public void readChunk() throws IOException {
		
		int numElements = stream.readShort();
		
		elements = new ArrayList<>(numElements);
		
		for (int i = 0; i < numElements; i++) {
			
			Element element = null;
			
			int type = stream.readShort();
			int majorType = type >> 8;
			int minorType = type & 0xFF;
			
			String dvfName    = stream.readString();
			String objectName = stream.readString();
			
			switch (majorType) {
			case PLAYER:
				element = new Player(minorType);
				readCharacterData((Alive) element);
				break;
			case NPC:
				element = new NPC(minorType);
				readNpcData((NPC) element);
				break;
			case ANIMAL:
				element = new Animal(minorType);
				readCharacterData((Alive) element);
				break;
			case ITEM: // minor type is always 0
				element = new Item();
				readItem((Item) element);
				break;
			case ANIMATION: // minor type is always 1
				element = new Animation();
				readAnimation((Animation) element);
				break;
			case ACCESSORY:
				element = new Accessory(minorType);
				readAccessory((Accessory) element);
				break;
			default:
				// impossible
			}
			
			if (majorType <= ANIMAL) {
				((Alive)element).initStance(stream.readShort());
			}
			
			element.setIdentifier("Element_" + i);
			element.setDvf(dvfName);
			element.setSprite(objectName);
			
			elements.add(element);
		}
		
		dvdContainer.setElements(elements);
	}

	private void readAccessory(Accessory element) throws IOException {
		
		stream.readByte(); // b1 is always 0
		byte b2 = stream.readByte();
		
		short horseId = stream.readShort();
		if (horseId != -1) {
			element.setMountedTo(elements.get(horseId));
		}
		
		if (b2 == 0) {
			AccessoryExtra extraInfo = element.createExtraInfo();
			extraInfo.setX((short) (stream.readShort()));
			extraInfo.setY((short) (stream.readShort()));
			extraInfo.setS2(stream.readShort());
			extraInfo.setS3(stream.readShort());
			extraInfo.setS4(stream.readShort());
			extraInfo.setS5(stream.readShort());
			extraInfo.setS6(stream.readShort());
		}
		
		element.setAmount(stream.readShort());
		
		if (element.isGattling()) {
			AccessoryExtraGattling gattlingInfo = element.getExtraInfo().createGattlingInfo();
			gattlingInfo.setGattlingInfo1(stream.readShort());
			gattlingInfo.setGattlingInfo2(stream.readShort());
		}
	}

	private void readNpcData(NPC element) throws IOException {
		readCharacterData(element);
		
		element.initCharacter(stream.readShort());
		stream.readShort(); // n1 is always 0
		
		if (element.isEnemy()) {
			element.setTiredness(stream.readShort());
		}
		
		element.initAttitude(stream.readByte());
		
		element.setDrunkLevel(stream.readByte());
		
		short routeId = stream.readShort();
		element.setRouteName(FileService.lookupRouteById(routeId));
	}

	private void readCharacterData(Alive element) throws IOException {
		
		byte hasAltProfile = stream.readByte();
		if (hasAltProfile == 1) {
			element.setDvfAlternative(stream.readString());
			element.setSpriteAlternative(stream.readString());
		}
		
		element.setP00(stream.readByte());
		element.setP01(stream.readShort());
		element.setP02(stream.readShort());
		element.setP03(stream.readShort());
		element.setP04(stream.readShort());
		
		element.setP10(stream.readByte());
		element.setP11(stream.readShort());
		element.setP12(stream.readShort());
		element.setP13(stream.readShort());
		element.setP14(stream.readShort());
		
		element.setX((short) (stream.readShort()));
		element.setY((short) (stream.readShort()));
		
		element.setU1(stream.readShort());
		element.setU2(stream.readShort());
		element.setU3(stream.readShort());
		
		element.setU4(stream.readByte());
		element.setDirection(stream.readByte());
		
		element.setClassName(stream.readString());
	}

	private void readAnimation(Animation element) throws IOException {
		element.setX(stream.readShort());
		element.setY(stream.readShort());
		element.setU1(stream.readShort());
		element.setU2(stream.readByte());
		element.setU3(stream.readByte());
		element.setU4(stream.readByte());
	}

	private void readItem(Item element) throws IOException {
		readAnimation(element);
		
		element.setX2(stream.readShort());
		element.setY2(stream.readShort());
		element.setU5(stream.readShort());
		element.setU6(stream.readShort());
		element.setU7(stream.readShort());
		element.setU8(stream.readInt());
		element.setClassName(stream.readString());
	}
}
