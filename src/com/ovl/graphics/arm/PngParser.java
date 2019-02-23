package com.ovl.graphics.arm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import com.ovl.graphics.TextureLoader.ColorFormat;
import com.ovl.graphics.TextureLoader.ImageData;

public class PngParser {
	// Png signature, 8 bytes (decimal) : 137 80 78 71 13 10 26 10
	private static final long PNG_SIG = -8552249625308161526L;

	private static final int CHUNK_IHDR = (('I' << 24) | ('H' << 16) | ('D' << 8) | ('R' << 0));
	private static final int CHUNK_IEND = (('I' << 24) | ('E' << 16) | ('N' << 8) | ('D' << 0));
	private static final int CHUNK_IDAT = (('I' << 24) | ('D' << 16) | ('A' << 8) | ('T' << 0));

	private static final int IHDR_LENGTH = 13;
	private static final int CRC_BYTES = 4;

	private static final byte COLOR_RGB = 2;
	private static final byte COLOR_RGBA = 6;
	private static final int RGB_BYTES = 3;
	private static final int RGBA_BYTES = 4;
	
	private static final byte FILTER_NONE = 0;
	private static final byte FILTER_SUB = 1;
	private static final byte FILTER_UP = 2;
	private static final byte FILTER_AVG = 3;
	private static final byte FILTER_PAETH = 4;  

	private static final Inflater inflater;

	static {
		inflater = new Inflater();
	}

	public static ImageData parse(final byte bytes[]) {
		final ByteBuffer wrappedBytes = ByteBuffer.wrap(bytes);

		long sig = wrappedBytes.getLong();
		if (sig != PNG_SIG) {
			return null;
		}

		int chunkLength = -1;
		int chunkType = -1;

		chunkLength = wrappedBytes.getInt();
		chunkType = wrappedBytes.getInt();

		if (chunkType != CHUNK_IHDR) {
			return null;
		}

		if (chunkLength != IHDR_LENGTH) {
			return null;
		}

		ImageData imageData = new ImageData();
		imageData.width = wrappedBytes.getInt();
		imageData.height = wrappedBytes.getInt();

		byte bitDepth = wrappedBytes.get();
		if (bitDepth != 8) {
			System.err.println("This image has wrong color bit depth");
			return null;
		}

		byte colorType = wrappedBytes.get();
		if (colorType != COLOR_RGBA && colorType != COLOR_RGB) {
			System.err.println("This image has wrong color coding");
			return null;
		}

		imageData.format = colorType == COLOR_RGBA ? ColorFormat.RGBA : ColorFormat.RGB;

		int colorTypeSize = getColorTypeSize(colorType);
		imageData.buffer = ByteBuffer.allocateDirect(imageData.width * imageData.height * RGBA_BYTES);
		imageData.buffer.order(ByteOrder.nativeOrder());
		imageData.buffer.rewind();

		byte compressionMethod = wrappedBytes.get();
		byte filterMethod = wrappedBytes.get();
		if (compressionMethod != 0 || filterMethod != 0) {
			System.err.println("This image has wrong compression or filtering method");
			return null;
		}

		byte interlaceMethod = wrappedBytes.get();
		if (interlaceMethod != 0) {
			System.err.println("This image has unsupported interlacing method");
			return null;
		}

		wrappedBytes.position(wrappedBytes.position() + CRC_BYTES);

		byte[] chunkBytes = new byte[imageData.width * imageData.height * colorTypeSize + imageData.width];
		ByteBuffer wrappedChunk = ByteBuffer.wrap(chunkBytes);
		int inflated = 0;

		do {
			chunkLength = wrappedBytes.getInt();
			chunkType = wrappedBytes.getInt();

			System.out.println("Type " + (char) ((chunkType >> 24) & 0xff) + (char) ((chunkType >> 16) & 0xff) + (char) ((chunkType >> 8) & 0xff) + (char) ((chunkType >> 0) & 0xff));
			System.out.println("Size " + chunkLength);

			if (chunkLength == 0 || chunkType != CHUNK_IDAT) {
				wrappedBytes.position(wrappedBytes.position() + chunkLength + CRC_BYTES);
				continue;
			}

			//inflater.reset();
			inflater.setInput(bytes, wrappedBytes.position(), chunkLength);
			try {
				inflated += inflater.inflate(chunkBytes, inflated, chunkBytes.length - inflated);
			}
			catch (DataFormatException e) {
				e.printStackTrace();
			}

			wrappedBytes.position(wrappedBytes.position() + chunkLength + CRC_BYTES);
		} while (chunkType != CHUNK_IEND);

		inflater.reset();

		byte[] rowBytes = new byte[imageData.width * RGBA_BYTES];
		byte[] rowBytesOld = new byte[rowBytes.length];
		for (int y = 0; y < imageData.height; ++y) {
			byte filterType = wrappedChunk.get();

			switch (colorType) {
				case COLOR_RGB: {
					int rowOffset = 0;
					for (int x = 0; x < imageData.width; ++x) {
						wrappedChunk.get(rowBytes, rowOffset, colorTypeSize);
						rowBytes[rowOffset + RGBA_BYTES - 1] = (byte) 0xff;
						rowOffset += RGBA_BYTES;
					}
					break;
				}
				case COLOR_RGBA: {
					wrappedChunk.get(rowBytes);
					break;
				}
			}

			//filter(filterType, rowBytes, rowBytesOld);
			imageData.buffer.put(rowBytes);
			
			byte[] temp = rowBytes;
			rowBytes = rowBytesOld;
			rowBytesOld = temp;
		}

		imageData.buffer.rewind();
		return imageData;
	}

	private static int getColorTypeSize(final int colorType) {
		switch (colorType) {
			case COLOR_RGBA: {
				return RGBA_BYTES;
			}
			case COLOR_RGB: {
				return RGB_BYTES;
			}
			default: {
				return -1;
			}
		}
	}

	private static void filter(final byte filterType, byte bytes[], byte rowAbove[]) {
		switch (filterType) {
			case FILTER_NONE:{
				break;
			}
			case FILTER_SUB: {
				byte AA = bytes[0];
				byte RR = bytes[1];
				byte GG = bytes[2];
				byte BB = bytes[3];
				for (int n = RGBA_BYTES; n < bytes.length; n += RGBA_BYTES) {
					RR = (bytes[n + 0] += RR);
					GG = (bytes[n + 1] += GG);
					BB = (bytes[n + 2] += BB);
					AA = (bytes[n + 3] += AA);
				}
				break;
			}
			case FILTER_UP: {
				for (int n = RGBA_BYTES; n < bytes.length; n += RGBA_BYTES) {
					bytes[n + 0] += rowAbove[n + 0];
					bytes[n + 1] += rowAbove[n + 1];
					bytes[n + 2] += rowAbove[n + 2];
					bytes[n + 3] += rowAbove[n + 3];
				}
				break;
			}
			case FILTER_AVG: {
				for (int n = RGBA_BYTES; n < bytes.length; n += RGBA_BYTES) {
					bytes[n + 0] = (byte)((rowAbove[n + 0] >> 1) + (bytes[n - 4] >> 1));
					bytes[n + 1] = (byte)((rowAbove[n + 1] >> 1) + (bytes[n - 3] >> 1));
					bytes[n + 2] = (byte)((rowAbove[n + 2] >> 1) + (bytes[n - 2] >> 1));
					bytes[n + 3] = (byte)((rowAbove[n + 3] >> 1) + (bytes[n - 1] >> 1));
				}
				break;
			}
			case FILTER_PAETH: {
				/*int AA = 0;
				int RA = 0;
				int GA = 0;
				int BA = 0;

				int AC = 0;
				int RC = 0;
				int GC = 0;
				int BC = 0;

				int ofsRowB = ofsRow - width;
				for (int n = 0; n < width; n++) {
					int pixelB = 0;
					if (y > 0)
						pixelB = pixels[ofsRowB + n];
					int AB = (pixelB >> 24) & 0xFF;
					int RB = (pixelB >> 16) & 0xFF;
					int GB = (pixelB >> 8) & 0xFF;
					int BB = pixelB & 0xFF;

					int pixel = pixels[ofsRow + n];
					int A = (pixel >> 24) & 0xFF;
					int R = (pixel >> 16) & 0xFF;
					int G = (pixel >> 8) & 0xFF;
					int B = pixel & 0xFF;

					int PA = AA + AB - AC;
					int PAA = Math.abs(PA - AA);
					int PAB = Math.abs(PA - AB);
					int PAC = Math.abs(PA - AC);
					if (PAA <= PAB && PAA <= PAC)
						A += AA;
					else {
						if (PAB <= PAC)
							A += AB;
						else
							A += AC;
					}

					int PR = RA + RB - RC;
					int PRA = Math.abs(PR - RA);
					int PRB = Math.abs(PR - RB);
					int PRC = Math.abs(PR - RC);
					if (PRA <= PRB && PRA <= PRC)
						R += RA;
					else {
						if (PRB <= PRC)
							R += RB;
						else
							R += RC;
					}

					int PG = GA + GB - GC;
					int PGA = Math.abs(PG - GA);
					int PGB = Math.abs(PG - GB);
					int PGC = Math.abs(PG - GC);
					if (PGA <= PGB && PGA <= PGC)
						G += GA;
					else {
						if (PGB <= PGC)
							G += GB;
						else
							G += GC;
					}

					int PB = BA + BB - BC;
					int PBA = Math.abs(PB - BA);
					int PBB = Math.abs(PB - BB);
					int PBC = Math.abs(PB - BC);
					if (PBA <= PBB && PBA <= PBC)
						B += BA;
					else {
						if (PBB <= PBC)
							B += BB;
						else
							B += BC;
					}

					A &= 0xFF;
					R &= 0xFF;
					G &= 0xFF;
					B &= 0xFF;

					pixels[ofsRow + n] = (A << 24) | (R << 16) | (G << 8) | B;

					AA = A;
					RA = R;
					GA = G;
					BA = B;

					AC = AB;
					RC = RB;
					GC = GB;
					BC = BB;

				}*/
				break;
			}
			default:{
				break;
			}
		}
	}
}
