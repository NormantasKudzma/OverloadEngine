package com.ovl.engine;

import java.util.HashMap;

public class ShaderParams {
	protected Vbo vbo;
	protected int index;			// renderable index within vbo
	protected HashMap<String, ParamSetter> params;
	
	protected ShaderParams(Vbo vbo, int index){
		this.vbo = vbo;
		this.index = index;
		params = new HashMap<String, ParamSetter>();
	}
	
	public void addParam(String param, ParamSetter paramSetter){
		if (param == null || paramSetter == null){
			return;
		}
		
		params.put(param, paramSetter);
	}
	
	public int getIndex(){
		return index;
	}
	
	public Vbo getVbo(){
		return vbo;
	}
	
	public HashMap<String, ParamSetter> getParams(){
		return params;
	}
}
