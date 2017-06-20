package com.ovl.engine.pc;

import org.lwjgl.opengl.GL20;

import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterBuilder;

public class TextureParamSetter extends ParamSetter {
	public static class Builder implements ParamSetterBuilder<Integer>{
		@Override
		public ParamSetter build(int paramId, Integer param) {
			return new TextureParamSetter(paramId, param);
		}
	}
	
	Integer textureId;
	
	public TextureParamSetter(int paramId, Integer texId){
		super(paramId);
		textureId = texId;
	}

	@Override
	public void setParam() {
		GL20.glUniform1i(shaderParamId, textureId);
	}
}
