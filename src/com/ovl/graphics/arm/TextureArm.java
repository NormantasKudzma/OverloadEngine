package com.ovl.graphics.arm;

import com.jogamp.opengl.GLES2;
import com.ovl.engine.arm.OverloadEngineArm;
import com.ovl.graphics.Texture;

public class TextureArm extends Texture {
	static final int boundTextures[] = new int[64 /*GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS*/];
	
	public TextureArm(){
		super();
	}

	@Override
	public void bind() {
		if (boundTextures[target] != id){
			boundTextures[target] = id;
			OverloadEngineArm.gl.glActiveTexture(GLES2.GL_TEXTURE0 + target);
			OverloadEngineArm.gl.glBindTexture(GLES2.GL_TEXTURE_2D, id);
		}
	}

	@Override
	protected int generateId() {
		int ids[] = new int[1];
		OverloadEngineArm.gl.glGenTextures(1, ids, 0);
		return ids[0];
	}

	@Override
	protected void destroy() {
		int ids[] = new int[]{ id };
		OverloadEngineArm.gl.glDeleteTextures(1, ids, 0);
	}
}
