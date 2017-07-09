package com.ovl.engine;

import java.util.HashMap;

import com.ovl.utils.Pair;

public class ParamSetterFactory {
	public static ParamSetter buildDefault(Shader shader, String paramName){
		HashMap<Class<?>, Pair<ParamSetter.Builder<?>, Object>> builders = OverloadEngine.getInstance().renderer.getParamSetterBuilders();	
		Shader.Uniform uniform = shader.getUniform(paramName);
		Pair<ParamSetter.Builder<?>, Object> param = builders.get(uniform.type);
		ParamSetter.Builder<Object> builder = (ParamSetter.Builder<Object>)param.key;
		return builder.build(uniform.id, param.value);
	}
	
	public static <T> ParamSetter build(Shader shader, String paramName, T param){
		HashMap<Class<?>, Pair<ParamSetter.Builder<?>, Object>> builders = OverloadEngine.getInstance().renderer.getParamSetterBuilders();
		ParamSetter.Builder<T> builder = (ParamSetter.Builder<T>)builders.get(param.getClass()).key;
		if (builder == null || param == null || shader == null){
			return null;
		}
		
		Shader.Handle handle = shader.getUniform(paramName);
		if (handle == null){
			return null;
		}
		
		return builder.build(handle.id, param);
	}
}
