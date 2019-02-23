package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;

public class MatrixParamSetter extends ParamSetter {
	public static class Builder implements ParamSetter.Builder<MatrixArm>{
		@Override
		public ParamSetter build(int paramId, MatrixArm param) {
			return new MatrixParamSetter(paramId, param);
		}
	}
	
	MatrixArm matrix;
	
	public MatrixParamSetter(int paramId, MatrixArm matrix){
		super(paramId);
		this.matrix = matrix;
	}
	
	@Override
	public void setParam() {
		OverloadEngineArm.gl.glUniformMatrix4fv(shaderParamId, 1, false, matrix.matrixImpl, 0);
	}
}

