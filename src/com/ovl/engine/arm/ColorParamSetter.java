package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;
import com.ovl.graphics.Color;

public class ColorParamSetter extends ParamSetter<Color> {
	public static class Builder implements ParamSetter.Builder<Color>{
		@Override
		public ParamSetter build(int paramId, Producer<Color> p) {
			return new ColorParamSetter(paramId, p);
		}
	}
	
	public ColorParamSetter(int paramId, Producer<Color> p){
		super(paramId, p);
	}
	
	@Override
	public void setParam(){
		float[] rgba = producer.produce().rgba;
		OverloadEngineArm.gl.glUniform4f(shaderParamId, rgba[0], rgba[1], rgba[2], rgba[3]);
	}
}
