package com.ovl.engine;

public abstract class ParamSetter {
	public interface Builder<T>{
		public ParamSetter build(int paramId, T param);
	}
	
	protected int shaderParamId;
	
	public ParamSetter(int paramId){
		shaderParamId = paramId;
	}
	
	public abstract void setParam();
}
