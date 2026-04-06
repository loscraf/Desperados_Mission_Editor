package desperados.dvd.chunk;

/*
import java.io.IOException;
import java.util.Locale;
*/

/**
 * Clase Move para lectura de chunks MOVE del archivo DVD.
 * 
 * El método readChunk2() fue comentado porque:
 * - No se encontraron referencias directas al método
 * - Contiene código experimental/de debug con múltiples System.println y asserts
 * - La mayoría es código exploratorio para entender la estructura del chunk
 * - Se mantiene comentado por si se necesita en el futuro para completar esta funcionalidad
 */
public class Move extends Chunk  {

	/*
	private void readChunk2() throws IOException {
		
		short numUnknown1 = stream.readShort();
		System.out.println(numUnknown1 + "\t# numUnknown1\n");
		
		short numBlocksTotal = stream.readShort();
		System.out.println(numBlocksTotal + "\t# numBlocksTotal\n");
		
		short numHeaderBlocks = stream.readShort();
		System.out.println(numHeaderBlocks + "\t# numHeaderBlocks");
		
		for (int i = 0; i < numHeaderBlocks; i++) {
			
			if (i != 0) {
				int seperator = stream.readInt();
				assert (seperator == 0);
			}
			
			short numCoords = stream.readShort();
			System.out.println("\n" + numCoords + "\t# map borders");
			
			for (int j = 0; j < numCoords; j++) {
				short x = stream.readShort();
				short y = stream.readShort();
				System.out.println(x + ", " + y);
			}
		}
		
		short numUnknownCoords = stream.readShort();
		System.out.println("\n" + numUnknownCoords + "\t# some lines"); // maybe pathfinding hints?
		
		for (int i = 0; i < numUnknownCoords; i++) {
			short a = stream.readShort();
			short b = stream.readShort();
			short c = stream.readShort();
			short d = stream.readShort();
			System.out.println("line " + a + ", " + b + ", " + c + ", " + d);
		}
		
		short numDataBlocks = stream.readShort();
		System.out.println("\n" + numDataBlocks + "\t# blocked areas");
		
		assert (numBlocksTotal == numHeaderBlocks + numDataBlocks);
		
		for (int i = 0; i < numDataBlocks; i++) {
			short numCoords = stream.readShort();
			System.out.println("\n" + numCoords + "\t# numCoords");
			
			for (int j = 0; j < numCoords; j++) {
				short x = stream.readShort();
				short y = stream.readShort();
				System.out.println(x + ", " + y);
			}
		}
		
		for (int h = 0; h < numUnknown1 - 1; h++) {
			
			short numAreasTotal = stream.readShort();
			System.out.println("\n" + numAreasTotal + "\t# numAreasTotal");
			
			short numAreasGrouped = stream.readShort();
			System.out.println("\n" + numAreasGrouped + "\t# numAreasGrouped");
			
			if (numAreasTotal == 0) {
				break;
			}
			
			for (int i = 0; i < numAreasGrouped; i++) {
				short numCoords = stream.readShort();
				System.out.println("\n" + numCoords + "\t# numCoords (walkable area above ground level)");
				
				for (int j = 0; j < numCoords; j++) {
					short x = stream.readShort();
					short y = stream.readShort();
					System.out.println(x + ", " + y);
				}
				
				short extra1 = stream.readShort();
				short extra2 = stream.readShort();
				System.out.println(extra1 + ", " + extra2 + " # extra");
				
				for (int j = 0; j < extra2; j++) {
					short numCoords2 = stream.readShort();
					System.out.println(numCoords2 + "\t# numCoords (subtracted area)");
					
					for (int k = 0; k < numCoords2; k++) {
						short x = stream.readShort();
						short y = stream.readShort();
						System.out.println(x + ", " + y);
					}
				}
			}
		
		}
		
		short numUnknown2b = stream.readShort();
		System.out.println("\n" + numUnknown2b + "\t# numUnknown2b");
		
		for (int i = 0; i < numUnknown2b; i++) {
			float x = stream.readFloat();
			float y = stream.readFloat();
			System.out.println(String.format(Locale.US, "%f, %f", x, y));
		}
		
		
		short s1 = stream.readShort();
		short s2 = stream.readShort();
		System.out.println(s1 + ", " + s2 + " # s1, s2");
		
		int[] counter = new int[]{23,3,1,0,0,0,0,0,0,0};
		
		for (int h = 0; h < 3; h++) {
			
			System.out.println("\n# next block");
			System.out.println("pos: " + Integer.toHexString(stream.getPosition()));
			
			short s3 = stream.readShort();
			short s4 = stream.readShort();
			System.out.println(s3 + ", " + s4 + " # s1, s2");
			
			for (int i = 0; i < counter[h]; i++) {
				short numCoords = stream.readShort();
				for (int j = 0; j < numCoords; j++) {
					stream.skip(6);
					short x = stream.readShort();
					short y = stream.readShort();
					System.out.println(x + ", " + y);
					stream.skip(8);
					short numIndex = stream.readShort();
					stream.skip(numIndex * 2);
				}
				System.out.println();
			}
		}
		
		for (int k = 0; k < 100; k++) {
			short x = stream.readShort();
			short y = stream.readShort();
			System.out.println(x + ", " + y);
		}
	}
	*/
}
