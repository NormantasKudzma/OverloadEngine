package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;

public class FloatParamSetter extends ParamSetter {
	public static class Builder implements ParamSetter.Builder<Float>{
		@Override
		public ParamSetter build(int paramId, Float f) {
			return new FloatParamSetter(paramId, f);
		}
	}
	
	Float f;
	
	public FloatParamSetter(int paramId, Float f){
		super(paramId);
		this.f = f;
	}
	
	@Override
	public void setParam(){
		OverloadEngineArm.gl.glUniform1f(shaderParamId, f);
	}
}
