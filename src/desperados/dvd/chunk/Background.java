package desperados.dvd.chunk;

/*
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.tools.bzip2.CBZip2InputStream;

import desperados.util.Bitmap;
*/

/**
 * Clase Background para lectura de chunks BGND del archivo DVD.
 * 
 * Los métodos fueron comentados porque:
 * - No se encontraron referencias directas (la clase se carga dinámicamente por reflexión en DvdReader)
 * - El código parece ser experimental/incompleto para procesar fondos de mapas
 * - Se mantienen comentados por si se necesitan en el futuro para completar esta funcionalidad
 */
public class Background extends Chunk {

	/*
	private void readChunk2() throws IOException {
		
		// check if file already exists (similar to dvm)
		
	    int nameLength = stream.readShort();
		String name = stream.readString(nameLength);
		
		// TODO 
		String bitmapFilename = "C:/Files/Desperados/Game/Data/Levels/dvm/" + name + ".bmp";
		
		int width  = stream.readShort();
		int height = stream.readShort();
		stream.skip(4); // unknown, maybe color depth
		int length = stream.readInt();
		
		stream.readShort(); // magic word BZ
		
		byte[] buffer = stream.readBytes(length - 2);
		
		byte[] bitmapRGB565 = new byte[width * height * 2];
		CBZip2InputStream in = new CBZip2InputStream(new ByteArrayInputStream(buffer));
		in.read(bitmapRGB565);
		in.close();
		
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(new File(bitmapFilename)));
		
		byte bmp_header[] = Bitmap.getHeader(width, height);
		out.write(bmp_header);
		
		byte[][] bitmapRGB888 = convertBitmap(bitmapRGB565, width, height);
		
		for (int h = height - 1; h >= 0; h--) {
			byte[] row = bitmapRGB888[h];
			out.write(row);
		}
        out.close();
	}

	private byte[][] convertBitmap(byte[] bitmapRGB565, int width, int height) {
		int rowSize = ((8 * 3 * width + 31) / 32) * 4;
		
		byte[][] bitmapRGB888 = new byte[height][rowSize];
		
		for (int h = 0; h < height; h++) {
			int offset = h * width * 2;
			for (int w = 0; w < width; w++) {
				byte byte1 = bitmapRGB565[offset + (w * 2)];
				byte byte2 = bitmapRGB565[offset + (w * 2) + 1];
				
				byte B5 = (byte) (byte1 & 0x1F);
				byte G6 = (byte) (((byte1 & 0xE0) >> 5) | ((byte2 & 0x07) << 3));
				byte R5 = (byte) (byte2 >> 3);
				
                byte R8 = (byte) (( R5 * 527 + 23 ) >> 6);
                byte G8 = (byte) (( G6 * 259 + 33 ) >> 6);
                byte B8 = (byte) (( B5 * 527 + 23 ) >> 6);
                
                bitmapRGB888[h][(w * 3)] = B8;
                bitmapRGB888[h][(w * 3) + 1] = G8;
                bitmapRGB888[h][(w * 3) + 2] = R8;
			}
		}
		return bitmapRGB888;
	}
	*/
}
