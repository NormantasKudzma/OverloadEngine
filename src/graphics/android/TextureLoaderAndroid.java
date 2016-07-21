package graphics.android;

import graphics.Texture;
import graphics.TextureLoader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;

public class TextureLoaderAndroid extends TextureLoader {
	//private ColorModel glAlphaColorModel;
	//private ColorModel glColorModel;
	private IntBuffer textureIDBuffer = IntBuffer.allocate(1);

	public TextureLoaderAndroid() {
		/*glAlphaColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 8 }, true, false, ComponentColorModel.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		glColorModel = new ComponentColorModel(
				ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8,
						8, 0 }, false, false, ComponentColorModel.OPAQUE,
				DataBuffer.TYPE_BYTE);*/
	}

	protected int createTextureID() {
		GLES20.glGenTextures(1, textureIDBuffer);
		return textureIDBuffer.get(0);
	}

	public Texture getTexture(String resourceName){
		Texture tex = table.get(resourceName);

		if (tex != null) {
			return tex;
		}

		Bitmap bmp = loadImage(resourceName);
		tex = getTexture(bmp);

		table.put(resourceName, tex);
		
		return tex;
	}
	
	public Texture getTexture(Bitmap bmp){
		int srcPixelFormat = -1;
		int textureID = createTextureID();
		Texture texture = new TextureAndroid(GLES20.GL_TEXTURE_2D, textureID);

		// bind this texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

		texture.setWidth(bmp.getWidth());
		texture.setHeight(bmp.getHeight());

		if (bmp.hasAlpha()){
			srcPixelFormat = GLES20.GL_RGBA;
		} 
		else {
			srcPixelFormat = GLES20.GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		ByteBuffer textureBuffer = convertImageData(bmp, texture);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

		// produce a texture from the byte buffer
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
				get2Fold(bmp.getWidth()),
				get2Fold(bmp.getHeight()), 0, srcPixelFormat,
				GLES20.GL_UNSIGNED_BYTE, textureBuffer);

		return texture;
	}

	private ByteBuffer convertImageData(Bitmap bmp, Texture texture) {
		ByteBuffer imageBuffer = ByteBuffer.allocateDirect(bmp.getRowBytes() * bmp.getHeight());
		
		imageBuffer.order(ByteOrder.nativeOrder());
		bmp.copyPixelsToBuffer(imageBuffer);
		imageBuffer.flip();
		
		return imageBuffer;
	}

	protected Bitmap loadImage(String ref){
		Bitmap bmp = BitmapFactory.decodeFile(ref);
		return bmp;
	}
}
