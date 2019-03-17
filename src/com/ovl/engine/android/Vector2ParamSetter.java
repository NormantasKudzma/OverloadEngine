package com.ovl.engine.android;

import android.opengl.GLES20;

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
		GLES20.glUniform2f(shaderParamId, vec.x, vec.y);
	}
}