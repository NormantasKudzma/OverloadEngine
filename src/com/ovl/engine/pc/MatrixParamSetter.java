package com.ovl.engine.pc;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL20;

import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterBuilder;

public class MatrixParamSetter extends ParamSetter {
	public static class Builder implements ParamSetterBuilder<FloatBuffer>{
		@Override
		public ParamSetter build(int paramId, FloatBuffer param) {
			return new MatrixParamSetter(paramId, param);
		}
	}
	
	FloatBuffer matrix;
	
	public MatrixParamSetter(int paramId, FloatBuffer matrix){
		super(paramId);
		this.matrix = matrix;
	}
	
	@Override
	public void setParam() {
		GL20.glUniformMatrix4(shaderParamId, false, matrix);
	}
}

