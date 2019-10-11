package com.ovl.graphics;

import com.ovl.game.GameObject;
import com.ovl.game.Updatable;

public abstract class Layer implements Updatable {
	public static final String DEFAULT_NAME = "default";
	public static final int DEFAULT_INDEX = 0;
	
	protected String layerName;
	protected int index;
	
	public Layer(String name, int index){
		this.layerName = name;
		this.index = index;
	}
	
	public int getIndex(){
		return index;
	}
	
	public String getName(){
		return layerName;
	}
	
	public abstract void addObject(GameObject obj);
	public abstract void removeObject(GameObject obj);
	public abstract void destroy();
	public abstract void render();

	public abstract void unloadResources();
	public abstract void reloadResources();
}
