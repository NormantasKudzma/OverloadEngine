package com.ovl.graphics.android;

import android.opengl.GLES20;

import com.ovl.graphics.Texture;

public class TextureAndroid extends Texture {
	/*GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS
	 * Way way too many (36k)
	 * */
	static final int boundTextures[] = new int[4];
	
	public TextureAndroid(){
		super();
	}
	
	public TextureAndroid(int id){
		super(id);
	}
	
	public TextureAndroid(int id, int target) {
		super(id, target);
	}

	public void bind() {
		if (boundTextures[target] != id){
			boundTextures[target] = id;
			GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + target);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);
		}
	}
}
