package com.ovl.graphics;

import java.nio.ByteBuffer;
import java.util.HashMap;

public abstract class TextureLoader {
	public static enum TexSize {
		POT,
		NON_POT;
	}

	public static enum ColorFormat {
		RGB,
		RGBA
	}
	
	public static class ImageData {
		public ByteBuffer buffer;
		public int width;
		public int height;
		public ColorFormat format;
	}

	protected HashMap<String, Texture> textureTable = new HashMap<>();

	public abstract Texture getTexture(String resourceName);
	
	protected abstract void createTexture(Texture tex, ImageData data, TexSize type);

	public abstract ByteBuffer getTextureData(Texture tex);

	public void unloadTextures(){}

	public void reloadTextures(){}

	/**
	 * Get the closest greater power of 2 to the fold number
	 */
	protected int get2Fold(int fold) {
		int ret = 2;
		while (ret < fold) {
			ret *= 2;
		}
		return ret;
	}
}
