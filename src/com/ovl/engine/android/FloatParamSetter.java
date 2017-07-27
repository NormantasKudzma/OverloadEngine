package com.ovl.engine.android;

import android.opengl.GLES20;

import com.ovl.engine.ParamSetter;

public class FloatParamSetter extends ParamSetter {
	public static class Builder implements ParamSetter.Builder<MutableFloat>{
		@Override
		public ParamSetter build(int paramId, MutableFloat f) {
			return new FloatParamSetter(paramId, f);
		}
	}
	
	MutableFloat f;
	
	public FloatParamSetter(int paramId, MutableFloat f){
		super(paramId);
		this.f = f;
	}
	
	@Override
	public void setParam(){
		GLES20.glUniform1f(shaderParamId, f.f);
	}
}
