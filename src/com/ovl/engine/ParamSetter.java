package com.ovl.engine;

public abstract class ParamSetter {
	protected int shaderParamId;
	
	public ParamSetter(int paramId){
		shaderParamId = paramId;
	}
	
	public abstract void setParam();
}
