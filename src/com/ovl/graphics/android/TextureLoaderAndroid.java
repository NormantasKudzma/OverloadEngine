package com.ovl.graphics.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import com.ovl.graphics.Texture;
import com.ovl.graphics.TextureLoader;

public class TextureLoaderAndroid extends TextureLoader {
	private int[] textureIDBuffer = new int[1];
	private int[] framebufferId = new int[1];

	public TextureLoaderAndroid() {
		GLES20.glGenFramebuffers(1, framebufferId, 0);
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
		ByteBuffer buf = ByteBuffer.allocate(bmp.getRowBytes() * bmp.getHeight()).order(ByteOrder.nativeOrder());
		bmp.copyPixelsToBuffer(buf);
		buf.rewind();
		Texture tex = createTexture(buf, bmp.getWidth(), bmp.getHeight(), TexSize.POT);
		bmp.recycle();
		
		return tex;
	}

	@Override
	public Texture createTexture(ByteBuffer buf, int width, int height, TexSize type) {
		int textureID = createTextureID();
		Texture texture = new TextureAndroid(textureID);

		texture.bind();

		texture.setWidth(width);
		texture.setHeight(height);

		int texWidth = type == TexSize.POT ? get2Fold(width) : width;
		int texHeight = type == TexSize.POT ? get2Fold(height) : height;
		texture.setTextureWidth(texWidth);
		texture.setTextureHeight(texHeight);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

		// produce a texture from the byte buffer
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

		int tw = (int)(tex.getImageWidth() / tex.getWidth());
		int th = (int)(tex.getImageHeight() / tex.getHeight());
		ByteBuffer buf = ByteBuffer.allocateDirect(tw * th * 4).order(ByteOrder.nativeOrder());
		
		bmp.copyPixelsToBuffer(buf);
		buf.rewind();		
		bmp.recycle();
		
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
