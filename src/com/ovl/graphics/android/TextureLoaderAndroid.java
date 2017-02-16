package com.ovl.graphics.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.ovl.graphics.Texture;
import com.ovl.graphics.TextureLoader;

public class TextureLoaderAndroid extends TextureLoader {
	//private ColorModel glAlphaColorModel;
	//private ColorModel glColorModel;
	private int[] textureIDBuffer = new int[1];

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
		GLES20.glGenTextures(1, textureIDBuffer, 0);
		return textureIDBuffer[0];
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
		int textureID = createTextureID();
		Texture texture = new TextureAndroid(GLES20.GL_TEXTURE_2D, textureID);

		// bind this texture
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

		int texW = bmp.getWidth();
		int texH = bmp.getHeight();
		
		texture.setWidth(texW);
		texture.setHeight(texH);

		texture.setTextureWidth(texW);
		texture.setTextureHeight(texH);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, bmp, 0);
		bmp.recycle();
		
		return texture;
	}

	protected Bitmap loadImage(String ref){
		InputStream stream = null;
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(ref);
			stream = url.openStream();
			Bitmap bmp = BitmapFactory.decodeStream(stream);
			stream.close();
			return bmp;
		}
		catch (IOException e){
			Log.w("ovl", e.toString());
			try {if (stream != null) stream.close();} catch (Exception ex){}
			return null;
		}
	}
}
