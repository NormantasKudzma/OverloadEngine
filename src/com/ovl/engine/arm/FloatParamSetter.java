package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;
import com.ovl.utils.MutableFloat;

public class FloatParamSetter extends ParamSetter {
	public static class BuilderImmutable implements ParamSetter.Builder<Float>{
		@Override
		public ParamSetter build(int paramId, Float f) {
			return new FloatParamSetter(paramId, f);
		}
	}
	
	public static class BuilderMutable implements ParamSetter.Builder<MutableFloat>{
		@Override
		public ParamSetter build(int paramId, MutableFloat f) {
			return new FloatParamSetter(paramId, f);
		}
	}
	
	MutableFloat f;
	
	public FloatParamSetter(int paramId, Float f){
		super(paramId);
		this.f = new MutableFloat(f);
	}
	
	public FloatParamSetter(int paramId, MutableFloat f){
		super(paramId);
		this.f = f;
	}
	
	@Override
	public void setParam(){
		OverloadEngineArm.gl.glUniform1f(shaderParamId, f.value);
	}
}
