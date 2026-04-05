package desperados.dvm;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.tools.bzip2.CBZip2InputStream;
import org.eclipse.swt.graphics.Image;

import desperados.util.LittleEndianInputStream;

public class DvmReader {

	public static Image getDvmImage(String filename) {
		
		String bmpFilename = filename.substring(0, filename.length() - 4) + ".bmp";
		
		File file = new File(bmpFilename);
		if (file.exists()) {
			return new Image(null, bmpFilename);
		}
		
		try {
			return extractDvm(bmpFilename);
		} catch (IOException e) {
			System.err.println("Error: Could not load background image!");
			return null;
		}
	}

	public static Image extractDvm(String filename) throws IOException {
		
		String dvmFilename = filename.substring(0, filename.length() - 4) + ".dvm";
		String bmpFilename = filename.substring(0, filename.length() - 4) + ".bmp";
		
		// read dvm file
		LittleEndianInputStream stream = new LittleEndianInputStream(dvmFilename);
		
		int width = stream.readShort();
		int height = stream.readShort();
		stream.skip(4); // unknown, 02 00 00 00
		int length = stream.readInt();
		
		stream.skip(2); // magic word BZ
		byte[] bzBuffer = stream.readBytes(length - 2);
		stream.close();
		
		// decompress bzip2 compressed data
		byte[] bmpBuffer = new byte[width * height * 2];
		CBZip2InputStream in = new CBZip2InputStream(new ByteArrayInputStream(bzBuffer));
		in.read(bmpBuffer);
		in.close();
		
		// create bmp
		byte bmpHeader[];
		byte bmpData[];
		
		int spaces = (4 - ((3 * width) % 4)) % 4;
		int bmpSize = height *(3 * width + spaces);
		
		// create bmp header
		bmpHeader = new byte[]{ 66,77, 0, 0, 0, 0, 0, 0, 0, 0,
                54, 0, 0, 0,40, 0, 0, 0, 0, 0,
                 0, 0, 0, 0, 0, 0, 1, 0,24, 0,
                 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                 0, 0, 0, 0};
		
		bmpHeader[2]  = (byte)((bmpSize + 54) % 256);
		bmpHeader[3]  = (byte)((java.lang.Math.floor((bmpSize + 54) / 256.0)) % 256);
		bmpHeader[4]  = (byte)(java.lang.Math.floor((bmpSize + 54) / 65536.0));
		bmpHeader[18] = (byte)(width % 256);
		bmpHeader[19] = (byte)(java.lang.Math.floor(width / 256.0));
		bmpHeader[22] = (byte)(height % 256);
		bmpHeader[23] = (byte)(java.lang.Math.floor(height / 256.0));
		bmpHeader[34] = (byte)(bmpSize % 256);
		bmpHeader[35] = (byte)((java.lang.Math.floor(bmpSize / 256.0)) % 256);
		bmpHeader[36] = (byte)(java.lang.Math.floor(bmpSize / 65536.0));
		
		bmpData = new byte[bmpSize];
		byte byte1, byte2, R5, G6, B5, R8, G8, B8;
		
		// transform colors (16-bit RGB565 -> 24-bit RGB888)
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				byte1 = bmpBuffer[h * 2 * width+ w * 2];
				byte2 = bmpBuffer[h * 2 * width+ w * 2 + 1];
				
				B5 = (byte) (byte1 & 0x1F);
				G6 = (byte) (((byte1 & 0xE0) >> 5) | ((byte2 & 0x07) << 3));
				R5 = (byte) ((byte2 & 0xF8) >> 3);
				R8 = (byte) (( R5 * 527 + 23 ) >> 6);
				G8 = (byte) (( G6 * 259 + 33 ) >> 6);
				B8 = (byte) (( B5 * 527 + 23 ) >> 6);

				int offset = (height - h - 1) * ((3 * width) + spaces) + w * 3;
				bmpData[offset++] = B8;
				bmpData[offset++] = G8;
				bmpData[offset]   = R8;
			}
		}
		
		// write bitmap to file
		OutputStream output = new BufferedOutputStream(new FileOutputStream(bmpFilename));
		output.write(bmpHeader);
		output.write(bmpData);
		output.close();
		
		return getDvmImage(filename);
	}
}
