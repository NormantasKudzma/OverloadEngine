package com.ovl.engine;

public interface ParamSetterBuilder<T> {
	public ParamSetter build(int paramId, T param);
}
