package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;
import com.ovl.graphics.Texture;

public class TextureParamSetter extends ParamSetter<Texture> {
	public static class Builder implements ParamSetter.Builder<Texture>{
		@Override
		public ParamSetter build(int paramId, Producer<Texture> param) {
			return new TextureParamSetter(paramId, param);
		}
	}
	
	public TextureParamSetter(int paramId, Producer<Texture> p){
		super(paramId, p);
	}

	@Override
	public void setParam() {
		Texture texture = producer.produce();
		texture.bind();
		OverloadEngineArm.gl.glUniform1i(shaderParamId, texture.getTarget());
	}
}
