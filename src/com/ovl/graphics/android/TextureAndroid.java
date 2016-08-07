package com.ovl.graphics.android;

import android.opengl.GLES20;

import com.ovl.graphics.Texture;

public class TextureAndroid extends Texture {
	private static int lastTexId = 0;
	
	private int target;
	private int textureID;

	public TextureAndroid(int target, int textureID) {
		this.target = target;
		this.textureID = textureID;
	}

	public void bind() {
		if (textureID != lastTexId){
			GLES20.glBindTexture(target, textureID);
			lastTexId = textureID;
		}
	}
}
