package com.ovl.engine.android;

import android.opengl.GLES20;

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
		Texture t = producer.produce();
		t.bind();
		GLES20.glUniform1i(shaderParamId, t.getTarget());
	}
}
