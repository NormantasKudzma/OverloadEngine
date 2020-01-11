package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;
import com.ovl.utils.Vector2;

public class Vector2ParamSetter extends ParamSetter<Vector2> {
	public static class Builder implements ParamSetter.Builder<Vector2>{
		@Override
		public ParamSetter build(int paramId, Producer<Vector2> param) {
			return new Vector2ParamSetter(paramId, param);
		}
	}

	public Vector2ParamSetter(int paramId, Producer<Vector2> p){
		super(paramId, p);
	}
	
	@Override
	public void setParam(){
		Vector2 v = producer.produce();
		OverloadEngineArm.gl.glUniform2f(shaderParamId, v.x, v.y);
	}
}