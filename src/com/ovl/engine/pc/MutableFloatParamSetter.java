package com.ovl.engine.pc;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.lwjgl.opengl.GL20;

import com.ovl.engine.ParamSetter;

public class MutableFloatParamSetter extends ParamSetter {
	public static class Builder implements ParamSetter.Builder<MutableFloat>{
		@Override
		public ParamSetter build(int paramId, MutableFloat f) {
			return new MutableFloatParamSetter(paramId, f);
		}
	}
	
	MutableFloat f;
	
	public MutableFloatParamSetter(int paramId, MutableFloat f){
		super(paramId);
		this.f = f;
	}
	
	@Override
	public void setParam(){
		GL20.glUniform1f(shaderParamId, f.getValue());
	}
}
