package com.ovl.graphics.android;

import android.opengl.GLES20;

import com.ovl.graphics.Texture;

public class TextureAndroid extends Texture {
	static final int boundTextures[] = new int[4];
	
	public TextureAndroid(){
		super();
	}

	public void bind() {
		if (boundTextures[target] != id){
			boundTextures[target] = id;
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + target);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
		}
	}

	@Override
	protected int generateId() {
		int ids[] = new int[1];
		GLES20.glGenTextures(1, ids, 0);
		return ids[0];
	}

	@Override
	protected void destroy() {
		int ids[] = new int[]{ id };
		GLES20.glDeleteTextures(1, ids, 0);
		boundTextures[target] = -1;
		id = -1;
	}
}
