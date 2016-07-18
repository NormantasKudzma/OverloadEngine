package graphics.pc;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;
import graphics.Texture;
import graphics.TextureLoader;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

/**
 * A utility class to load textures for OpenGL. This source is based on a
 * texture that can be found in the Java Gaming (www.javagaming.org) Wiki. It
 * has been simplified slightly for explicit 2D graphics use.
 * 
 * OpenGL uses a particular image format. Since the images that are loaded from
 * disk may not match this format this loader introduces a intermediate image
 * which the source image is copied into. In turn, this image is used as source
 * for the OpenGL texture.
 * 
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class TextureLoaderPc extends TextureLoader {
	/** The colour model including alpha for the GL image */
	private ColorModel glAlphaColorModel;

	/** The colour model for the GL image */
	private ColorModel glColorModel;

	/** Scratch buffer for texture ID's */
	private IntBuffer textureIDBuffer = BufferUtils.createIntBuffer(1);

	/**
	 * Create a new texture loader based on the game panel
	 */
	public TextureLoaderPc() {
		glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 8 }, true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 0 }, false, false, ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE);
	}

	/**
	 * Create a new texture ID
	 * 
	 * @return A new texture ID
	 */
	protected int createTextureID() {
		glGenTextures(textureIDBuffer);
		return textureIDBuffer.get(0);
	}

	public Texture getTexture(BufferedImage bufferedImage){
		int srcPixelFormat = -1;
		int textureID = createTextureID();
		Texture texture = new TexturePc(GL_TEXTURE_2D, textureID);

		// bind this texture
		glBindTexture(GL_TEXTURE_2D, textureID);

		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL_RGBA;
		} else {
			srcPixelFormat = GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		// produce a texture from the byte buffer
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA,
				get2Fold(bufferedImage.getWidth()),
				get2Fold(bufferedImage.getHeight()), 0, srcPixelFormat,
				GL_UNSIGNED_BYTE, textureBuffer);

		return texture;
	}

	/**
	 * Convert the buffered image to a texture
	 * 
	 * @param bufferedImage
	 *            The image to convert to a texture
	 * @param texture
	 *            The texture to store the data into
	 * @return A buffer containing the data
	 */
	private ByteBuffer convertImageData(BufferedImage bufferedImage, Texture texture) {
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = get2Fold(bufferedImage.getWidth());
		int texHeight = get2Fold(bufferedImage.getHeight());

		texture.setTextureHeight(texHeight);
		texture.setTextureWidth(texWidth);

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false,
					new Hashtable());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false,
					new Hashtable());
		}

		// copy the source image into the produced image
		Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(bufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}
}
