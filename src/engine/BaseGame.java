package engine;

import graphics.Layer;

import java.util.ArrayList;

import physics.PhysicsWorld;
import utils.Vector2;
import audio.MusicManager;
import audio.SoundManager;
import controls.IClickable;
import dialogs.BaseDialog;

public class BaseGame implements IUpdatable, IClickable {
	private static final int NUM_VELOCITY_ITERATIONS = 2;
	private static final int NUM_POSITION_ITERATIONS = 4;

	protected SoundManager<?> soundManager = new SoundManager<String>();
	protected MusicManager<?> musicManager = new MusicManager<String>();
	protected ArrayList<BaseDialog> dialogList = new ArrayList<BaseDialog>();
	protected ArrayList<Layer> layers = new ArrayList<Layer>();
	protected PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
	private boolean isGameOver = false;

	public BaseGame() {
		layers.add(new Layer(Layer.DEFAULT_NAME, Layer.DEFAULT_INDEX));
	}

	public void addDialog(BaseDialog d){
		dialogList.add(d);
	}
	
	public void addEntity(Entity<?> e){
		addEntity(e, Layer.DEFAULT_NAME);
	}
	
	public void addEntity(Entity<?> e, String layerName){
		for (Layer l : layers){
			if (l.getName().equalsIgnoreCase(layerName)){
				l.addEntity(e);
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
	
	public MusicManager<?> getMusicManager(){
		return musicManager;
	}
	
	public SoundManager<?> getSoundManager(){
		return soundManager;
	}
	
	/**
	 * Game initialization (creating entities, loading map etc.) goes here
	 */
	public void init() {

	}

	public void removeDialog(String name){
		BaseDialog d;
		for (int i = 0; i < dialogList.size(); i++){
			if ((d = dialogList.get(i)).getName().equals(name)){
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
		physicsWorld.getWorld().step(deltaTime, NUM_VELOCITY_ITERATIONS, NUM_POSITION_ITERATIONS);

		// Update all entities
		Layer layer = null;
		for (int i = 0; i < layers.size(); i++) {
			layer = layers.get(i);
			layer.update(deltaTime);
			layer.destroyMarkedEntities();
		}
	}
	
	public boolean isGameOver(){
		return isGameOver;
	}
	
	@Override
	public boolean isMouseOver(Vector2 pos) {
		return true;
	}

	@Override
	public boolean onHover(Vector2 pos) {
		for (int i = 0; i < dialogList.size(); i++){
			if (dialogList.get(i).onHover(pos)){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onClick(Vector2 pos) {
		for (int i = 0; i < dialogList.size(); i++){
			if (dialogList.get(i).onClick(pos)){
				return true;
			}
		}
		return false;
	}
}
