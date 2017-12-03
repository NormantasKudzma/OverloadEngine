package com.ovl.graphics.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.ovl.graphics.Texture;
import com.ovl.graphics.TextureLoader;

public class TextureLoaderAndroid extends TextureLoader {
	private int[] textureIDBuffer = new int[1];

	public TextureLoaderAndroid() {

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
		ByteBuffer buf = getRGBABytes(bmp);
		Texture tex = createTexture(buf, bmp.getWidth(), bmp.getHeight(), TexSize.NON_POT);
		bmp.recycle();
		
		return tex;
	}

	@Override
	public Texture createTexture(ByteBuffer buf, int width, int height, TexSize type) {
		Texture texture = new TextureAndroid(createTextureID());

		texture.bind();

		texture.setWidth(width);
		texture.setHeight(height);

		int texWidth = type == TexSize.POT ? get2Fold(width) : width;
		int texHeight = type == TexSize.POT ? get2Fold(height) : height;
		texture.setTextureWidth(texWidth);
		texture.setTextureHeight(texHeight);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth, texHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, buf);

		return texture;
	}
	
	public ByteBuffer getTextureData(Texture tex){
		// workaround, just reload tex from file..
		Bitmap bmp = null;
		for (Map.Entry<String, Texture> i : table.entrySet()){
			if (i.getValue() == tex){
				bmp = loadImage(i.getKey());
				break;
			}
		}
		
		if (bmp == null){
			return null;
		}

		ByteBuffer buf = getRGBABytes(bmp);
		bmp.recycle();
		
		return buf;
	}
	
	protected ByteBuffer getRGBABytes(Bitmap bmp){
		int numBytes = bmp.getRowBytes() * bmp.getHeight();
		ByteBuffer buf = ByteBuffer.allocateDirect(numBytes).order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = buf.asIntBuffer();
		
		int pixels[] = new int[bmp.getWidth() * bmp.getHeight()];
		bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
		
		for(int i = 0; i < pixels.length; ++i) {
			ib.put(pixels[i] << 8 | pixels[i] >>> 24);
		}
		
		buf.rewind();
		return buf;
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
