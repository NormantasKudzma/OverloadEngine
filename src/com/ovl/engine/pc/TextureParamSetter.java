package com.ovl.engine.pc;

import org.lwjgl.opengl.GL20;

import com.ovl.engine.ParamSetter;
import com.ovl.graphics.Texture;

public class TextureParamSetter extends ParamSetter {
	public static class Builder implements ParamSetter.Builder<Texture>{
		@Override
		public ParamSetter build(int paramId, Texture param) {
			return new TextureParamSetter(paramId, param);
		}
	}
	
	Texture texture;
	
	public TextureParamSetter(int paramId, Texture tex){
		super(paramId);
		texture = tex;
	}

	@Override
	public void setParam() {
		texture.bind();
		GL20.glUniform1i(shaderParamId, texture.getTarget());
	}
}
