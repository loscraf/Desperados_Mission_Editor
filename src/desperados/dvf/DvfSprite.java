package desperados.dvf;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;

public class DvfSprite {

	private short width;
	private short height;
	//private short unknown; // usually 01 00

	private byte[] data;
	private int dataPos;

	private int linePosV;
	private int linePosH;
	private short[][] bitmap;
	private byte[] mask;
	
	private ImageData imageData;

	public DvfSprite(short width, short height, short unknown, byte[] data) {
		this.width = width;
		this.height = height;
		//this.unknown = unknown;
		this.data = data;
	}

	private void decode(byte[] data) {
		bitmap = new short[height][width];
		mask = new byte[height * width];
		
		for (linePosV = 0; linePosV < height; linePosV++) {
			linePosH = 0;
			
			int numTransparentPixels = readShort();
			int numTotalPixels = readShort() + 1;
			
			if (numTransparentPixels == 0 && numTotalPixels == 0x10000) {
				addTransparentPixels(width);
			} else {
				addTransparentPixels(numTransparentPixels);
				readPixels(numTotalPixels - numTransparentPixels);
				addTransparentPixels(width - numTotalPixels);
			}
		}
	}

	private short readShort() {
		return (short) ((data[dataPos++] & 0xFF) | (data[dataPos++] & 0xFF) << 8);
	}

	private void addTransparentPixels(int numPixels) {
		/* do nothing
		for (int i = 0; i < numPixels; i++) {
			bitmap[linePosV][linePosH + i] = (short) 0x07C0; // C0 07
			// mask[linePosV][linePosH + i] = 0;
		}
		*/
		linePosH += numPixels;
	}

	private void readPixels(int numPixels) {
		for (int i = 0; i < numPixels; i++) {
			short s = readShort();
			
			if (s == 0x1F) { // shadow
				s = 0; // change color of the pixel from blue to black
				mask[linePosV * width +  linePosH + i] = (byte) 200; // = set opacity of shadow to 78%
			} else if (s == 0x07C0) { // transparent
				// do nothing
				// mask[linePosV][linePosH + i] = 0;
			} else {
				mask[linePosV * width +  linePosH + i] = (byte) 255;
			}
			bitmap[linePosV][linePosH + i] = s;
		}
		linePosH += numPixels;
	}

	public void createImageData() {
		if (bitmap == null) {
			decode(data);
		}
		
		int padding = (4 - ((width * 3) % 4)) % 4;
		int newWidth = width * 3 + padding;
		
		byte[] bmp = new byte[newWidth * height];
		
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				if (bitmap[h][w] == 0) { // nothing to do here
					continue;
				}
				
				byte b1 = (byte)(bitmap[h][w] & 0xFF);
				byte b2 = (byte)((bitmap[h][w] >> 8) & 0xFF);
				
				byte B5 = (byte) (b1 & 0x1F);
				byte G6 = (byte) (((b1 & 0xE0) >> 5) | ((b2 & 0x07) << 3));
				byte R5 = (byte) ((b2 & 0xF8) >> 3);
				byte R8 = (byte) (( R5 * 527 + 23 ) >> 6);
				byte G8 = (byte) (( G6 * 259 + 33 ) >> 6);
				byte B8 = (byte) (( B5 * 527 + 23 ) >> 6);
				
				bmp[h * newWidth + w * 3]     = B8;
				bmp[h * newWidth + w * 3 + 1] = G8;
				bmp[h * newWidth + w * 3 + 2] = R8;
			}
		}
		
		imageData = new ImageData(width, height, 24, new PaletteData(0xFF, 0xFF00, 0xFF0000), 4, bmp);
		imageData.setAlphas(0, 0, width * height, mask, 0);
	}

	public void createPNG(int index, String outputPath, String dvfName) {
		getImageData();
		
		File folder = new File(outputPath);
		if (!folder.isDirectory()) {
			folder.mkdir();
		}
		folder = new File(outputPath + "\\" + dvfName);
		if (!folder.isDirectory()) {
			folder.mkdir();
		}
		
		String filename = outputPath + "\\" + dvfName + "\\sprite_" + index + ".png";
		
		ImageLoader saver = new ImageLoader();
		saver.data = new ImageData[] { imageData };
		saver.save(filename, SWT.IMAGE_PNG);
	}

	/*
	private void createBMP(int width, int height, byte[] data, String filename) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "bmp", baos);
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] header = baos.toByteArray();
		header = Arrays.copyOfRange(header, 0, 0x36);
		
		BufferedOutputStream bos;
		
		try {
			bos = new BufferedOutputStream(new FileOutputStream(filename), 8 * 1024);
			bos.write(header);
			bos.write(data);
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	*/

	public ImageData getImageData() {
		if (imageData == null) {
			createImageData();
		}
		return imageData;
	}

	public String toString() {
		return "Sprite: " + width + " x " + height;
	}
}
