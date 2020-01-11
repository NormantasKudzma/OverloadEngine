package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;
import com.ovl.utils.MutableFloat;

public class FloatParamSetter extends ParamSetter<MutableFloat> {
	public static class Builder implements ParamSetter.Builder<MutableFloat>{
		@Override
		public ParamSetter build(int paramId, Producer<MutableFloat> p) {
			return new FloatParamSetter(paramId, p);
		}
	}

	public FloatParamSetter(int paramId, Producer<MutableFloat> p){
		super(paramId, p);
	}
	
	@Override
	public void setParam(){
		OverloadEngineArm.gl.glUniform1f(shaderParamId, producer.produce().value);
	}
}
