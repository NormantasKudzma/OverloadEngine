package graphics;

import java.util.ArrayList;

import utils.Vector2;
import engine.GameObject;
import engine.Updatable;

public class Layer implements Renderable, Updatable {
	public static final String DEFAULT_NAME = "default";
	public static final int DEFAULT_INDEX = 0;
	
	private ArrayList<GameObject> entityList = new ArrayList<GameObject>();
	private ArrayList<GameObject> destroyList = new ArrayList<GameObject>();
	private String layerName;
	private int index;
	
	public Layer(String name, int index){
		this.layerName = name;
		this.index = index;
	}
	
	public void addEntity(GameObject e){
		entityList.add(e);
	}
	
	public void clear(){
		entityList.clear();
		destroyMarkedEntities();
	}
	
	@Override
	public void destroy() {
		for (GameObject<?> i : entityList) {
			i.destroy();
		}
		clear();
	}
	
	public void destroyMarkedEntities(){
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
	
	@Override
	public void render() {
		for (int i = 0; i < entityList.size(); ++i){
			entityList.get(i).render();
		}
	}

	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		//
	}

	@Override
	public void setColor(Color c) {
		//
	}

	@Override
	public void update(float deltaTime) {
		GameObject e;
		for (int i = 0; i < entityList.size(); ++i){
			e = entityList.get(i);
			e.update(deltaTime);
			if (e.isDestroyed()){
				destroyList.add(e);
				entityList.remove(e);
			}
		}
	}
}
