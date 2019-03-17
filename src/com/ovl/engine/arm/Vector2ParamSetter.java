package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;
import com.ovl.utils.Vector2;

public class Vector2ParamSetter extends ParamSetter {
	public static class Builder implements ParamSetter.Builder<Vector2>{
		@Override
		public ParamSetter build(int paramId, Vector2 param) {
			return new Vector2ParamSetter(paramId, param);
		}
	}
	
	Vector2 vec;
	
	public Vector2ParamSetter(int paramId, Vector2 v){
		super(paramId);
		vec = v;
	}
	
	@Override
	public void setParam(){
		OverloadEngineArm.gl.glUniform2f(shaderParamId, vec.x, vec.y);
	}
}