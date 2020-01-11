package com.ovl.engine;

public abstract class ParamSetter<T>  {
	public interface Builder<T> {
		public ParamSetter build(int paramId, Producer<T> producer);
	}

	public interface Producer<T> {
		public T produce();
	}
	
	protected int shaderParamId;
	protected Producer<T> producer;

	public ParamSetter(){

	}

	public ParamSetter(int paramId, Producer<T> producer){
		shaderParamId = paramId;
		this.producer = producer;
	}
	
	public abstract void setParam();
}
