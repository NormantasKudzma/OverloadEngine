package com.ovl.graphics.arm;

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
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import javax.swing.ImageIcon;

import com.jogamp.opengl.GL2ES2;
import com.ovl.engine.arm.OverloadEngineArm;
import com.ovl.graphics.Texture;
import com.ovl.graphics.TextureLoader;

public class TextureLoaderArm extends TextureLoader {
	private ColorModel glAlphaColorModel;
	private ColorModel glColorModel;

	public TextureLoaderArm() {
		glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 8 }, true, false, Transparency.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 0 }, false, false, Transparency.OPAQUE,
				DataBuffer.TYPE_BYTE);
	}

	@Override
	public Texture getTexture(String resourceName){
		Texture tex = textureTable.get(resourceName);

		if (tex != null) {
			return tex;
		}

		tex = createTexture(loadImage(resourceName));
		
		textureTable.put(resourceName, tex);
		
		return tex;
	}

	public Texture createTexture(BufferedImage image) {
		Texture texture = new TextureArm();
		createTexture(texture, image);
		return texture;
	}
	
	private void createTexture(Texture texture, BufferedImage bufferedImage){
		ImageData data = new ImageData();
		data.buffer = convertImageData(bufferedImage);
		data.width = bufferedImage.getWidth();
		data.height = bufferedImage.getHeight();
		createTexture(texture, data, TexSize.POT);
	}
	
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
	
	protected void createTexture(Texture texture, ImageData data, TexSize type) {
		texture.bind();

		texture.setWidth(data.width);
		texture.setHeight(data.height);

		int texWidth = type == TexSize.POT ? get2Fold(data.width) : data.width;
		int texHeight = type == TexSize.POT ? get2Fold(data.height) : data.height;
		texture.setTextureWidth(texWidth);
		texture.setTextureHeight(texHeight);

		OverloadEngineArm.gl.glTexParameteri(GL2ES2.GL_TEXTURE_2D, GL2ES2.GL_TEXTURE_MIN_FILTER, GL2ES2.GL_NEAREST);
		OverloadEngineArm.gl.glTexParameteri(GL2ES2.GL_TEXTURE_2D, GL2ES2.GL_TEXTURE_MAG_FILTER, GL2ES2.GL_NEAREST);

		// produce a texture from the byte buffer
		OverloadEngineArm.gl.glTexImage2D(GL2ES2.GL_TEXTURE_2D, 0, GL2ES2.GL_RGBA, texWidth, texHeight, 0, GL2ES2.GL_RGBA, GL2ES2.GL_UNSIGNED_BYTE, data.buffer);
	}

	public ByteBuffer getTextureData(Texture tex){
		/*tex.bind();
		
		int tw = (int)(tex.getImageWidth() / tex.getWidth());
		int th = (int)(tex.getImageHeight() / tex.getHeight());
		ByteBuffer buf = ByteBuffer.allocateDirect(tw * th * 4).order(ByteOrder.nativeOrder());
		
		OverloadEngineArm.gl.glPixelStorei(GL2ES2.GL_UNPACK_ALIGNMENT, 1);
		OverloadEngineArm.gl.glGetTexImage(GL2ES2.GL_TEXTURE_2D, 0, GL2ES2.GL_RGBA, GL2ES2.GL_UNSIGNED_BYTE, buf);
		buf.rewind();
		
		int glerr = OverloadEngineArm.gl.glGetError();
		if (glerr != GL2ES2.GL_NO_ERROR){
			System.err.println("GL err! " + glerr);
			return null;
		}
		
		return buf;*/
		return null;
	}
	
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
