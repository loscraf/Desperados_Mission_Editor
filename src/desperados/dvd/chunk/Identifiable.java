package desperados.dvd.chunk;

public abstract class Identifiable {
	
	protected String identifier;

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}
}
