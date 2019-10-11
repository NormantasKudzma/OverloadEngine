package com.ovl.graphics;

import java.util.ArrayList;

import com.ovl.game.GameObject;

public class UnsortedLayer extends Layer {

	protected ArrayList<GameObject> gameObjectList = new ArrayList<GameObject>();
	protected ArrayList<GameObject> destroyList = new ArrayList<GameObject>();

	public UnsortedLayer(String name, int index) {
		super(name, index);
	}
	
	@Override
	public void addObject(GameObject gameObject){
		gameObjectList.add(gameObject);
	}

	public void removeObject(GameObject gameObject){
		gameObjectList.remove(gameObject);
	}

	public void destroy() {
		for (GameObject i : gameObjectList) {
			i.destroy();
		}
		gameObjectList.clear();
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
	
	@Override
	public void render() {
		for (int i = 0; i < gameObjectList.size(); ++i){
			gameObjectList.get(i).render();
		}
	}

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < gameObjectList.size(); ++i){
			gameObjectList.get(i).update(deltaTime);
		}
	}

	@Override
	public void unloadResources() {
		for (int i = 0; i < gameObjectList.size(); ++i){
			gameObjectList.get(i).unloadResources();
		}
	}

	@Override
	public void reloadResources() {
		for (int i = 0; i < gameObjectList.size(); ++i){
			gameObjectList.get(i).reloadResources();
		}
	}
}
