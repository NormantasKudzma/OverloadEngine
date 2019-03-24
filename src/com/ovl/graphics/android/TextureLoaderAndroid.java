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

import com.ovl.graphics.Texture;
import com.ovl.graphics.TextureLoader;
import com.ovl.utils.android.Log;

public class TextureLoaderAndroid extends TextureLoader {
	public Texture getTexture(String resourceName){
		Texture tex = textureTable.get(resourceName);

		if (tex == null){
			tex = new TextureAndroid();
		}
		else if (tex.hasData()) {
			return tex;
		}

		createTexture(tex, loadImage(resourceName));

		textureTable.put(resourceName, tex);
		
		return tex;
	}

	public TextureAndroid createTexture(Bitmap bmp){
		TextureAndroid tex = new TextureAndroid();
		createTexture(tex, bmp);
		return tex;
	}

	private void createTexture(Texture tex, Bitmap bmp){
		ByteBuffer buf = getRGBABytes(bmp);
		ImageData data = new ImageData();
		data.buffer = buf;
		data.width = bmp.getWidth();
		data.height = bmp.getHeight();
		createTexture(tex, data, TexSize.NON_POT);
		
		bmp.recycle();
	}
	
	protected void createTexture(Texture tex, ImageData data, TexSize type){
		tex.bind();

		tex.setWidth(data.width);
		tex.setHeight(data.height);
		
		int texWidth = type == TexSize.POT ? get2Fold(data.width) : data.width;
		int texHeight = type == TexSize.POT ? get2Fold(data.height) : data.height;
		tex.setTextureWidth(texWidth);
		tex.setTextureHeight(texHeight);
		
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, texWidth, texHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data.buffer);
		
		int glErr = GLES20.glGetError();
		if (glErr != GLES20.GL_NO_ERROR){
			Log.w("GL error when creating texture " + glErr);
		}
	}
	
	public ByteBuffer getTextureData(Texture tex){
		// workaround, just reload tex from file..
		Bitmap bmp = null;
		for (Map.Entry<String, Texture> i : textureTable.entrySet()){
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
	
	private ByteBuffer getRGBABytes(Bitmap bmp){
		int numBytes = bmp.getRowBytes() * bmp.getHeight();
		ByteBuffer buf = ByteBuffer.allocateDirect(numBytes).order(ByteOrder.BIG_ENDIAN);
		IntBuffer ib = buf.asIntBuffer();
		
		int pixels[] = new int[bmp.getWidth() * bmp.getHeight()];
		bmp.getPixels(pixels, 0, bmp.getWidth(), 0, 0, bmp.getWidth(), bmp.getHeight());
		
		for (int i = 0; i < pixels.length; ++i) {
			ib.put(pixels[i] << 8 | pixels[i] >>> 24);
		}
		
		buf.rewind();
		return buf;
	}

	private Bitmap loadImage(String ref){
		InputStream stream = null;
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(ref);
			stream = url.openStream();
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false;
			Bitmap bmp = BitmapFactory.decodeStream(stream, null, options);
			stream.close();
			return bmp;
		}
		catch (IOException e){
			Log.w(e.toString());
			try {if (stream != null) stream.close();}
			catch (Exception ex){ Log.w(ex.toString()); }
			return null;
		}
	}

	@Override
	public void unloadTextures() {
		for (Map.Entry<String, Texture> i : textureTable.entrySet()){
			i.getValue().destroyTexture();
		}
	}

	@Override
	public void reloadTextures() {
		for (Map.Entry<String, Texture> i : textureTable.entrySet()){
			i.getValue().create();
			createTexture(i.getValue(), loadImage(i.getKey()));
		}
	}
}
