package desperados.dvd.chunk;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import desperados.dvd.obstacles.Obstacle;

public class Obstacles extends Chunk  {

	@Override
	public void readChunk() throws IOException {
		
		int numObstacles = stream.readShort();
		
		List<Obstacle> obstacles = new ArrayList<Obstacle>(numObstacles);
		
		for (int i = 0; i < numObstacles; i++) {
			
			Obstacle o = new Obstacle();
			obstacles.add(o);
			
			int numPoints = stream.readShort();
			for (int j = 0; j < numPoints; j++) {
				o.addPoint(stream.readFloat(), stream.readFloat(), stream.readFloat(), stream.readFloat());
			}
			
			// min, max floats
			float[] minMax = new float[6];
			for (int j = 0; j < minMax.length; j++) {
				minMax[j] = stream.readFloat();
			}
			o.setMinMax(minMax);
			
			if (stream.readByte() == 1) {
				o.setExtra(stream.readShort(), stream.readShort());
			}
			
			o.setBytes(stream.readByte(), stream.readByte(), stream.readByte(), stream.readByte());
			o.setFloats(stream.readFloat(), stream.readFloat());
			o.setUnknownInt(stream.readInt());
			o.setUnknownByte(stream.readByte());
		}
		
		dvdContainer.setObstacles(obstacles);
	}
}
