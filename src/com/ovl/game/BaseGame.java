package com.ovl.game;

import java.util.ArrayList;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.Renderer;
import com.ovl.graphics.Layer;
import com.ovl.graphics.UnsortedLayer;
import com.ovl.physics.PhysicsWorld;
import com.ovl.ui.BaseDialog;
import com.ovl.utils.Vector2;

public class BaseGame implements Updatable {
	protected static final int NUM_VELOCITY_ITERATIONS = 2;
	protected static final int NUM_POSITION_ITERATIONS = 4;
	protected static final float PHYSICS_STEP = 0.02f;

	/*protected MusicManager musicManager;
	protected SoundManager soundManager;*/
	
	protected ArrayList<BaseDialog> dialogList;
	protected ArrayList<Layer> layers;
	protected PhysicsWorld physicsWorld;
	protected float accumulatedTime = 0.0f;

	public BaseGame() {
		dialogList = new ArrayList<BaseDialog>();
		layers = new ArrayList<Layer>();
		physicsWorld = PhysicsWorld.getInstance();
		
		layers.add(new UnsortedLayer(Layer.DEFAULT_NAME, Layer.DEFAULT_INDEX));
	}

	public void addDialog(BaseDialog d){
		dialogList.add(d);
	}
	
	public void addObject(GameObject gameObject){
		addObject(gameObject, Layer.DEFAULT_NAME);
	}
	
	public void addObject(GameObject gameObject, String layerName){
		Layer layer = getLayer(layerName);
		if (layer == null) { return; }

		layer.addObject(gameObject);
	}

	public void removeObject(GameObject gameObject, String layerName){
		Layer layer = getLayer(layerName);
		if (layer == null) { return; }

		layer.removeObject(gameObject);
	}
	
	public void addLayer(Layer layer){
		int index = -1;
		
		for (int i = 0; i < layers.size(); ++i){
			Layer l = layers.get(i);
			if (layer.getIndex() > l.getIndex()){
				index = i;
				break;
			}
		}
		
		if (index == -1){
			index = layers.size() - 1;
		}
		
		layers.add(index, layer);
	}
	
	public void addLayer(String layerName, int index){
		addLayer(new UnsortedLayer(layerName, index));
	}
	
	/**
	 * Game destruction method. This method will be called last. Any acquired resources
	 * should be released here.
	 */
	public void destroy() {
		for (int i = 0; i < layers.size(); ++i){
			layers.get(i).destroy();
		}
		layers.clear();
		
		for (int i = 0; i < dialogList.size(); ++i){
			dialogList.get(i).destroy();
		}
		dialogList.clear();
		
		/*if (musicManager != null){
			musicManager.destroy();
		}
		
		if (soundManager != null){
			soundManager.destroy();
		}*/
	}

	public BaseDialog getDialog(String name){
		BaseDialog d;
		for (int i = 0; i < dialogList.size(); i++){
			if ((d = dialogList.get(i)).getName().equals(name)){
				return d;
			}
		}
		return null;
	}
	
	public Layer getLayer(int index){
		for (Layer l : layers){
			if (l.getIndex() == index){
				return l;
			}
		}
		
		return null;
	}
	
	public Layer getLayer(String layerName){
		for (Layer l : layers){
			if (l.getName().equalsIgnoreCase(layerName)){
				return l;
			}
		}
		
		return null;
	}
	
	/*public MusicManager getMusicManager(){
		return musicManager;
	}
	
	public SoundManager getSoundManager(){
		return soundManager;
	}*/
	
	/**
	 * Game initialization (creating entities, loading map etc.) goes here
	 */
	public void init() {
		/*MouseController c = (MouseController) ControllerManager.getInstance().getController(EController.MOUSE_CONTROLLER);
		c.addKeybind(0, new ControllerEventListener() {

			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				if (params[0] == 1) {
					onClick(pos);
				}
			}
			
		});

		c.setMouseMoveListener(new ControllerEventListener() {
			
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				onHover(pos);
			}
			
		});
		c.startController();
	
		KeyboardController k = (KeyboardController) ControllerManager.getInstance().getController(EController.KEYBOARD_CONTROLLER);
		k.addKeybind(Keyboard.KEY_ESCAPE, new ControllerEventListener(){
			
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				OverloadEngine.requestClose();
			}
			
		});
		k.startController();*/
	}

	/** 
	 * Configuration is loaded once at the creation of engine, then this method is called.
	 * User should parse and use whatever values he needs from the configuration file.
	 * User can use this method whenever the game configuration values must be refreshed.
	 * 
	 * @param config - Configuration file as lines
	 */
	public void onGameConfigurationLoaded(ArrayList<String> config){
		//
	}
	
	public void removeDialog(String name){
		for (int i = 0; i < dialogList.size(); i++){
			if (dialogList.get(i).getName().equals(name)){
				dialogList.remove(i);
				return;
			}
		}
	}

	public void render() {
		// If there are visible dialogs, don't render the game
		BaseDialog d;
		for (int i = 0; i < dialogList.size(); i++){
			if ((d = dialogList.get(i)).isVisible()){
				d.render();
				return;
			}
		}
		
		renderGame();
	}

	protected void renderGame(){
		for (int i = 0 ; i < layers.size(); ++i){
			layers.get(i).render();
		}
	}
	
	public Layer removeLayer(int index){
		Layer l = null;
		for (int i = 0; i < layers.size(); ++i){
			l = layers.get(i);
			if (l.getIndex() == index){
				layers.remove(l);
				return l;
			}
		}
		
		return null;
	}
	
	public Layer removeLayer(String layerName){
		Layer l = null;
		for (int i = 0; i < layers.size(); ++i){
			l = layers.get(i);
			if (l.getName().equalsIgnoreCase(layerName)){
				layers.remove(l);
				return l;
			}
		}
		
		return null;
	}
	
	public Layer removeLayer(Layer layer){
		if (layers.remove(layer)){
			return layer;
		}
		
		return null;
	}
	
	public void setDialogVisible(String name, boolean isVisible){
		BaseDialog d;
		for (int i = 0; i < dialogList.size(); i++){
			if ((d = dialogList.get(i)).getName().equals(name)){
				d.setVisible(isVisible);
				return;
			}
		}
	}
	
	/**
	 * Main game update method. Physics and entities should be moved during
	 * update.
	 * 
	 * @param deltaTime - time that has passed since last frame (in seconds)
	 */
	@Override
	public void update(float deltaTime) {
		/*if (soundManager != null){
			soundManager.update(deltaTime);
		}
		
		if (musicManager != null){
			musicManager.update(deltaTime);
		}*/
		
		// If there are visible dialogs, don't update the game
		BaseDialog d;
		for (int i = 0; i < dialogList.size(); i++){
			if ((d = dialogList.get(i)).isVisible()){
				d.update(deltaTime);
				return;
			}
		}
		
		// Update physics
		accumulatedTime += deltaTime;
		while (accumulatedTime >= PHYSICS_STEP){
			physicsWorld.getWorld().step(PHYSICS_STEP, NUM_VELOCITY_ITERATIONS, NUM_POSITION_ITERATIONS);
			accumulatedTime -= PHYSICS_STEP;
		}

		// Update all entities
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).update(deltaTime);
		}
	}

	public boolean onHover(Vector2 pos) {
		for (int i = 0; i < dialogList.size(); i++){
			if (dialogList.get(i).onHover(pos)){
				return true;
			}
		}
		return false;
	}

	public boolean onClick(Vector2 pos) {
		for (int i = 0; i < dialogList.size(); i++){
			if (dialogList.get(i).onClick(pos)){
				return true;
			}
		}
		return false;
	}

	protected Renderer getRenderer(){
		return OverloadEngine.getInstance().renderer;
	}

	public void unloadResources(){
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).unloadResources();
		}
	}

	public void reloadResources(){
		for (int i = 0; i < layers.size(); i++) {
			layers.get(i).reloadResources();
		}
	}
}
