package desperados.dvd.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.materials.Material;
import desperados.dvd.materials.MaterialBlock;
import desperados.util.Point;

public class Materials extends Chunk {

	@Override
	public void readChunk() throws IOException {
		
		short numMaterialBlocks = stream.readShort();
		List<MaterialBlock> materialBlocks = new ArrayList<MaterialBlock>(numMaterialBlocks);
		
		for (int i = 0; i < numMaterialBlocks; i++) {
			
			short unknown = stream.readShort();
			short numMaterials = stream.readShort();
			
			List<Material> materials = new ArrayList<Material>(numMaterials);
			
			for (int j = 0; j < numMaterials; j++) {
			
				byte type = stream.readByte();
				
				float f1 = 0, f2 = 0;
				int unk1 = 0;
				byte unk2 = 0;
				
				if (type == 8) {
					unk1 = stream.readShort();
				} else {
					f1 = stream.readFloat();
					f2 = stream.readFloat();
					unk1 = stream.readInt();
					unk2 = stream.readByte();
				}
				
				short numPoints = stream.readShort();
				List<Point> points = new ArrayList<Point>(numPoints);
				
				for (int k = 0; k < numPoints; k++) {
					points.add(new Point(stream.readShort(), stream.readShort()));
				}
				
				materials.add(new Material(type, f1, f2, unk1, unk2, points));
			}
			
			materialBlocks.add(new MaterialBlock(unknown, materials));
		}
		dvdContainer.setMaterials(materialBlocks);
	}
}
