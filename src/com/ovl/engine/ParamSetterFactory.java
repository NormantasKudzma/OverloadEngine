package com.ovl.engine;

import java.util.HashMap;

public class ParamSetterFactory {
	public static ParamSetter buildDefault(ShaderParams params, String paramName){
		HashMap<String, Object> defaults = OverloadEngine.getInstance().renderer.getParamSetterDefaults();
		return ParamSetterFactory.build(params, paramName, defaults.get(paramName));
	}
	
	public static <T> ParamSetter build(ShaderParams params, String paramName, T param){
		HashMap<Class<?>, ParamSetterBuilder<?>> builders = OverloadEngine.getInstance().renderer.getParamSetterBuilders();
		ParamSetterBuilder<T> builder = (ParamSetterBuilder<T>)builders.get(param.getClass());
		if (builder == null || params == null || params.getVbo().getShader() == null){
			return null;
		}
		
		Shader.Handle handle = params.getVbo().getShader().getHandle(paramName);
		if (handle == null){
			return null;
		}
		
		return builder.build(handle.id, param);
	}
}
