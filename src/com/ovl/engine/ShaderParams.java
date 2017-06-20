package com.ovl.engine;

import java.util.ArrayList;

public class ShaderParams {
	protected Vbo vbo;
	protected int index;			// renderable index within vbo
	protected boolean isDeletable; // can be deleted with deleteVbo call? if false - it is owned by renderer
	protected ArrayList<ParamSetter> params;
	
	protected ShaderParams(Vbo vbo, int index, boolean isDeletable){
		this.vbo = vbo;
		this.index = index;
		this.isDeletable = isDeletable;
		params = new ArrayList<ParamSetter>();
	}
	
	public void addParam(ParamSetter paramSetter){
		params.add(paramSetter);
	}
	
	public int getIndex(){
		return index;
	}
	
	public Vbo getVbo(){
		return vbo;
	}
	
	public boolean isDeletable(){
		return isDeletable;
	}
	
	public ArrayList<ParamSetter> getParams(){
		return params;
	}
}
