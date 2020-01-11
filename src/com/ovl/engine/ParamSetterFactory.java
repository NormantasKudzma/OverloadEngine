package com.ovl.engine;

import java.util.HashMap;

import com.ovl.utils.Pair;

public class ParamSetterFactory {
	private static class DefaultProducer<T> implements ParamSetter.Producer<T> {
		private T instance;

		public DefaultProducer(T instance) {
			this.instance = instance;
		}

		public T produce() {
			return instance;
		}
	}

	public static ParamSetter buildDefault(Shader shader, String paramName) {
		HashMap<Class<?>, Pair<ParamSetter.Builder<?>, Object>> builders = OverloadEngine.getInstance().renderer.getParamSetterBuilders();
		Shader.Uniform uniform = shader.getUniform(paramName);
		Pair<ParamSetter.Builder<?>, Object> param = builders.get(uniform.type);
		ParamSetter.Builder<Object> builder = (ParamSetter.Builder<Object>) param.key;
		return builder.build(uniform.id, new DefaultProducer<Object>(param.value));
	}

	public static <T> ParamSetter build(Shader shader, String paramName, T param) {
		HashMap<Class<?>, Pair<ParamSetter.Builder<?>, Object>> builders = OverloadEngine.getInstance().renderer.getParamSetterBuilders();
		Pair<ParamSetter.Builder<?>, Object> pair = builders.get(param.getClass());
		if (pair == null) {
			System.err.println(String.format("Could not build shader param for %s=%s", paramName, param.getClass().getName()));
			System.err.println("Available builders:");
			for (Class<?> b : builders.keySet()) {
				System.err.println(String.format(" %s", b.getName()));
			}
			return null;
		}
		
		ParamSetter.Builder<T> builder = (ParamSetter.Builder<T>)pair.key;
		if (builder == null || param == null || shader == null) {
			return null;
		}

		Shader.Handle handle = shader.getUniform(paramName);
		if (handle == null) {
			return null;
		}

		return builder.build(handle.id, new DefaultProducer<T>(param));
	}

	public static <T> ParamSetter build(Shader shader, String paramName, ParamSetter.Producer<T> producer) {
		Shader.Uniform handle = shader.getUniform(paramName);
		if (handle == null) {
			return null;
		}

		HashMap<Class<?>, Pair<ParamSetter.Builder<?>, Object>> builders = OverloadEngine.getInstance().renderer.getParamSetterBuilders();
		ParamSetter.Builder<T> builder = (ParamSetter.Builder<T>) builders.get(handle.type).key;
		if (builder == null || shader == null) {
			return null;
		}

		return builder.build(handle.id, producer);
	}
}
