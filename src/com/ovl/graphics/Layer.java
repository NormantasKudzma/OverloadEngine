package com.ovl.graphics;

import java.util.ArrayList;

import com.ovl.game.GameObject;
import com.ovl.game.Updatable;

public class Layer implements Updatable {
	public static final String DEFAULT_NAME = "default";
	public static final int DEFAULT_INDEX = 0;
	
	private ArrayList<GameObject> gameObjectList = new ArrayList<GameObject>();
	private ArrayList<GameObject> destroyList = new ArrayList<GameObject>();
	private String layerName;
	private int index;
	
	public Layer(String name, int index){
		this.layerName = name;
		this.index = index;
	}
	
	public void addObject(GameObject gameObject){
		gameObjectList.add(gameObject);
	}
	
	public void clear(){
		gameObjectList.clear();
		destroyMarkedObjects();
	}
	
	public void destroy() {
		for (GameObject i : gameObjectList) {
			i.destroy();
		}
		clear();
	}
	
	public void destroyMarkedObjects(){
		if (destroyList.isEmpty()){
			return;
		}
		
		for (int i = 0; i < destroyList.size(); ++i){
			destroyList.get(i).onDestroy();
			destroyList.get(i).destroy();
		}
		destroyList.clear();
	}
	
	public ArrayList<GameObject> getDestroyList(){
		return destroyList;
	}
	
	public int getIndex(){
		return index;
	}
	
	public String getName(){
		return layerName;
	}
	
	public void render() {
		for (int i = 0; i < gameObjectList.size(); ++i){
			gameObjectList.get(i).render();
		}
	}

	@Override
	public void update(float deltaTime) {
		GameObject e;
		for (int i = 0; i < gameObjectList.size(); ++i){
			e = gameObjectList.get(i);
			e.update(deltaTime);
			if (e.isDestroyed()){
				destroyList.add(e);
				gameObjectList.remove(e);
			}
		}
	}
}
