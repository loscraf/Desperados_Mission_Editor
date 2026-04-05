package desperados.dvd.elements;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

import desperados.dvd.chunk.Identifiable;
import desperados.exception.IdentifierNotFoundException;
import desperados.exception.JsonParseException;
import desperados.service.AnimationService;
import desperados.service.FileService;
import desperados.util.LittleEndianOuputStream;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
	use = JsonTypeInfo.Id.NAME, 
	include = JsonTypeInfo.As.PROPERTY, 
	property = "type")
@JsonSubTypes({ 
	@Type(value = Accessory.class, name = "ACCESSORY"),
	@Type(value = Animation.class, name = "ANIMATION"),
	@Type(value = Animal.class, name = "ANIMAL"),
	@Type(value = Item.class, name = "ITEM"),
	@Type(value = NPC.class, name = "NPC"),
	@Type(value = Player.class, name = "PLAYER")
})
public abstract class Element extends Identifiable {

	protected String dvf;
	protected String sprite;

	@Override
	public String toString() {
		return String.format("\"%s\",\"%s\",\"%s\"", identifier, dvf, sprite);
	}

	@JsonIgnore
	public short getId() throws IdentifierNotFoundException {
		return FileService.lookupElementByIdentifier(identifier);
	}

	public String getDvf() {
		return dvf;
	}

	public void setDvf(String dvf) {
		this.dvf = dvf;
	}

	public String getSprite() {
		return sprite;
	}

	public void setSprite(String sprite) {
		this.sprite = sprite;
	}

	public void checkIntegrity() throws JsonParseException {
		if (dvf == null || dvf.length() == 0) {
			throw new JsonParseException("Error: Element \"" + identifier + "\" has no dvf!");
		}
		if (sprite == null || sprite.length() == 0) {
			throw new JsonParseException("Error: Element \"" + identifier + "\" has no sprite!");
		}
		if (identifier == null || identifier.length() == 0) {
			throw new JsonParseException("Error: Element " + toString() + " has no identifier!");
		}
	}

	public void writeToStream(LittleEndianOuputStream stream, AnimationService animService) throws IOException {
		stream.writeString(dvf);
		stream.writeString(sprite);
	}

	public abstract short getX();
	public abstract short getY();
	public abstract void setX(short x);
	public abstract void setY(short y);

	@JsonIgnore
	public abstract void setOrigin(short x, short y);
}
