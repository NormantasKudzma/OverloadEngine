package com.ovl.graphics.pc;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;

import com.ovl.graphics.Texture;

public class TexturePc extends Texture {
	static final int boundTextures[] = new int[GL20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS];
	
	public TexturePc(){
		super();
	}
	
	public TexturePc(int id){
		super(id);
	}
	
	public TexturePc(int id, int target) {
		super(id, target);
	}

	@Override
	public void bind() {
		if (boundTextures[target] != id){
			boundTextures[target] = id;
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + target);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
		}
	}
}
