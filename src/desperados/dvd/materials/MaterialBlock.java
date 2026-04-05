package desperados.dvd.materials;

import java.util.List;

public class MaterialBlock {
	
	//private short unknown;
	private List<Material> materials;

	public MaterialBlock(short unknown, List<Material> materials) {
		//this.unknown = unknown;
		this.materials = materials;
	}

	public List<Material> getMaterials() {
		return materials;
	}
}
