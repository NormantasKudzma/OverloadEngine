package com.ovl.engine.android;

import android.opengl.GLES20;

import com.ovl.engine.ParamSetter;

public class MatrixParamSetter extends ParamSetter {
	public static class Builder implements ParamSetter.Builder<MatrixAndroid>{
		@Override
		public ParamSetter build(int paramId, MatrixAndroid param) {
			return new MatrixParamSetter(paramId, param);
		}
	}
	
	MatrixAndroid matrix;
	
	public MatrixParamSetter(int paramId, MatrixAndroid matrix){
		super(paramId);
		this.matrix = matrix;
	}
	
	@Override
	public void setParam() {
		GLES20.glUniformMatrix4fv(shaderParamId, 1, false, matrix.matrixImpl, 0);		
	}
}

