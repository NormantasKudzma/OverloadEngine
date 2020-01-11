package com.ovl.engine.android;

import android.opengl.GLES20;

import com.ovl.engine.ParamSetter;

public class MatrixParamSetter extends ParamSetter<MatrixAndroid> {
	public static class Builder implements ParamSetter.Builder<MatrixAndroid>{
		@Override
		public ParamSetter build(int paramId, Producer<MatrixAndroid> p) {
			return new MatrixParamSetter(paramId, p);
		}
	}
	
	public MatrixParamSetter(int paramId, Producer<MatrixAndroid> p){
		super(paramId, p);
	}
	
	@Override
	public void setParam() {
		GLES20.glUniformMatrix4fv(shaderParamId, 1, false, producer.produce().matrixImpl, 0);
	}
}

