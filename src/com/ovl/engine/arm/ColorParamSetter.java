package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;
import com.ovl.graphics.Color;

public class ColorParamSetter extends ParamSetter {
	public static class Builder implements ParamSetter.Builder<Color>{
		@Override
		public ParamSetter build(int paramId, Color param) {
			return new ColorParamSetter(paramId, param);
		}
	}
	
	Color color;
	
	public ColorParamSetter(int paramId, Color c){
		super(paramId);
		color = c;
	}
	
	@Override
	public void setParam(){
		float[] rgba = color.rgba;
		OverloadEngineArm.gl.glUniform4f(shaderParamId, rgba[0], rgba[1], rgba[2], rgba[3]);
	}
}
