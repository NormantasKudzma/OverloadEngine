package engine;

import graphics.Layer;

import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import controls.ControllerEventListener;
import controls.ControllerManager;
import controls.EController;
import controls.KeyboardController;
import controls.MouseController;

import physics.PhysicsWorld;
import ui.BaseDialog;
import ui.OnClickListener;
import utils.Vector2;
import audio.MusicManager;
import audio.SoundManager;

public class BaseGame implements Updatable {
	protected static final int NUM_VELOCITY_ITERATIONS = 2;
	protected static final int NUM_POSITION_ITERATIONS = 4;
	protected static final float PHYSICS_STEP = 0.02f;

	protected MusicManager musicManager;
	protected SoundManager soundManager;
	
	protected ArrayList<BaseDialog> dialogList = new ArrayList<BaseDialog>();
	protected ArrayList<Layer> layers = new ArrayList<Layer>();
	protected PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
	protected float accumulatedTime = 0.0f;

	public BaseGame() {
		layers.add(new Layer(Layer.DEFAULT_NAME, Layer.DEFAULT_INDEX));
	}

	public void addDialog(BaseDialog d){
		dialogList.add(d);
	}
	
	public void addObject(GameObject gameObject){
		addObject(gameObject, Layer.DEFAULT_NAME);
	}
	
	public void addObject(GameObject gameObject, String layerName){
		for (Layer l : layers){
			if (l.getName().equalsIgnoreCase(layerName)){
				l.addObject(gameObject);
				break;
			}
		}
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
		addLayer(new Layer(layerName, index));
	}
	
	public void clearLayer(Layer l){
		clearLayer(l.getName());
	}
	
	public void clearLayer(String layerName){
		for (Layer l : layers){
			if (l.getName().equalsIgnoreCase(layerName)){
				l.clear();
				break;
			}
		}
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
		
		if (musicManager != null){
			musicManager.destroy();
		}
		
		if (soundManager != null){
			soundManager.destroy();
		}
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
	
	public MusicManager getMusicManager(){
		return musicManager;
	}
	
	public SoundManager getSoundManager(){
		return soundManager;
	}
	
	/**
	 * Game initialization (creating entities, loading map etc.) goes here
	 */
	public void init() {
		MouseController c = (MouseController) ControllerManager.getInstance().getController(EController.MOUSE_CONTROLLER);
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
		k.startController();
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
	public void update(float deltaTime) {
		if (soundManager != null){
			soundManager.update(deltaTime);
		}
		
		if (musicManager != null){
			musicManager.update(deltaTime);
		}
		
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
		Layer layer = null;
		for (int i = 0; i < layers.size(); i++) {
			layer = layers.get(i);
			layer.update(deltaTime);
			layer.destroyMarkedObjects();
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
}
