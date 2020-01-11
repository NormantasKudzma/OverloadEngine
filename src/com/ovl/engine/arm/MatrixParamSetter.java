package com.ovl.engine.arm;

import com.ovl.engine.ParamSetter;

public class MatrixParamSetter extends ParamSetter<MatrixArm> {
	public static class Builder implements ParamSetter.Builder<MatrixArm>{
		@Override
		public ParamSetter build(int paramId, Producer<MatrixArm> p) {
			return new MatrixParamSetter(paramId, p);
		}
	}
	
	public MatrixParamSetter(int paramId, Producer<MatrixArm> p){
		super(paramId, p);
	}
	
	@Override
	public void setParam() {
		OverloadEngineArm.gl.glUniformMatrix4fv(shaderParamId, 1, false, producer.produce().matrixImpl, 0);
	}
}

