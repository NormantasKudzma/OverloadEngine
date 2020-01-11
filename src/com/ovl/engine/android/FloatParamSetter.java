package com.ovl.engine.android;

import android.opengl.GLES20;

import com.ovl.engine.ParamSetter;

public class FloatParamSetter extends ParamSetter<Float> {
	public static class Builder implements ParamSetter.Builder<Float>{
		@Override
		public ParamSetter build(int paramId, Producer<Float> p) {
			return new FloatParamSetter(paramId, p);
		}
	}

	public FloatParamSetter(int paramId, Producer<Float> p){
		super(paramId, p);
	}
	
	@Override
	public void setParam(){
		GLES20.glUniform1f(shaderParamId, producer.produce());
	}
}
