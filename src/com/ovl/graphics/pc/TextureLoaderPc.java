package com.ovl.graphics.pc;

import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.ovl.graphics.Texture;
import com.ovl.graphics.TextureLoader;

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
						8, 8 }, true, false, Transparency.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 0 }, false, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
	}

	/**
	 * Create a new texture ID
	 * 
	 * @return A new texture ID
	 */
	@Override
	protected int createTextureID() {
		glGenTextures(textureIDBuffer);
		return textureIDBuffer.get(0);
	}

	@Override
	public Texture getTexture(String resourceName){
		Texture tex = textureTable.get(resourceName);

		if (tex != null) {
			return tex;
		}

		BufferedImage bufferedImage = loadImage(resourceName);
		tex = getTexture(bufferedImage);

		textureTable.put(resourceName, tex);
		
		return tex;
	}
	
	public Texture getTexture(BufferedImage bufferedImage){
		// convert that image into a byte buffer of texture data
		ByteBuffer buf = convertImageData(bufferedImage);
		return createTexture(buf, bufferedImage.getWidth(), bufferedImage.getHeight(), TexSize.POT);
	}
	
	@Override
	public Texture createTexture(ByteBuffer buf, int width, int height, TexSize type) {
		int textureID = createTextureID();
		Texture texture = new TexturePc(textureID);

		texture.bind();

		texture.setWidth(width);
		texture.setHeight(height);

		int texWidth = type == TexSize.POT ? get2Fold(width) : width;
		int texHeight = type == TexSize.POT ? get2Fold(height) : height;
		texture.setTextureWidth(texWidth);
		texture.setTextureHeight(texHeight);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

		// produce a texture from the byte buffer
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, texWidth, texHeight, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);

		return texture;
	}

	public ByteBuffer getTextureData(Texture tex){
		tex.bind();
		
		int tw = (int)(tex.getImageWidth() / tex.getWidth());
		int th = (int)(tex.getImageHeight() / tex.getHeight());
		ByteBuffer buf = ByteBuffer.allocateDirect(tw * th * 4).order(ByteOrder.nativeOrder());
		
		GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
		GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf);
		buf.rewind();
		
		int glerr = GL11.glGetError();
		if (glerr != GL11.GL_NO_ERROR){
			System.err.println("GL err! " + glerr);
			return null;
		}
		
		return buf;
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
	private ByteBuffer convertImageData(BufferedImage bufferedImage) {
		ByteBuffer imageBuffer;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = get2Fold(bufferedImage.getWidth());
		int texHeight = get2Fold(bufferedImage.getHeight());

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (bufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 4, null);
			texImage = new BufferedImage(glAlphaColorModel, raster, false,
					new Hashtable<String, Object>());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 3, null);
			texImage = new BufferedImage(glColorModel, raster, false,
					new Hashtable<String, Object>());
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

	/**
	 * Load a given resource as a buffered image
	 * 
	 * @param ref The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException
	 *             Indicates a failure to find a resource
	 */
	protected BufferedImage loadImage(String ref) {
		System.out.println("Tryload " + ref);
		URL url = TextureLoader.class.getClassLoader().getResource(ref);
		Image img = new ImageIcon(url).getImage();
		BufferedImage bufferedImage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImage.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();

		return bufferedImage;
	}


}
