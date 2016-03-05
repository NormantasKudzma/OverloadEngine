package engine;

import graphics.PhysicsDebugDraw;
import graphics.Sprite2D;
import graphics.SpriteAnimation;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import controls.IClickable;

import dialogs.BaseDialog;

import physics.PhysicsWorld;
import utils.Vector2;

public class BaseGame implements IUpdatable, IClickable {
	private static final int NUM_VELOCITY_ITERATIONS = 2;
	private static final int NUM_POSITION_ITERATIONS = 4;

	protected ArrayList<Integer> destroyList = new ArrayList<Integer>();
	protected ArrayList<Entity> entityList = new ArrayList<Entity>();
	protected ArrayList<BaseDialog> dialogList = new ArrayList<BaseDialog>();
	protected PhysicsWorld physicsWorld = PhysicsWorld.getInstance();
	private boolean isGameOver = false;

	public BaseGame() {

	}

	public void addDialog(BaseDialog d){
		dialogList.add(d);
		d.setGame(this);
	}
	
	public void addEntity(Entity e){
		entityList.add(e);
	}
	
	/**
	 * Game destruction method. This method will be called last. Any resources
	 * that must be released, should be released here.
	 */
	public void destroy() {
		for (Entity i : entityList) {
			i.destroy();
		}
		entityList.clear();
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
	
	public ArrayList<Entity> getEntityList(){
		return entityList;
	}
	
	/**
	 * Game initialization (creating entities, loading map etc.) goes here
	 * 
	 */
	public void init() {

	}

	/**
	 * Render method - call render for each and every entity
	 * 
	 */
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
		for (int i = 0 ; i < entityList.size(); ++i){
			entityList.get(i).render();
		}
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
	 * @param deltaTime - time that has passed since last frame (in ms)
	 */
	public void update(float deltaTime) {
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
		Entity e;
		for (int i = 0; i < entityList.size(); i++) {
			e = entityList.get(i);
			e.update(deltaTime);
			if (e.isDestroyed()) {
				destroyList.add(i);
			}
		}

		// Delete entities which are marked for destruction
		for (Integer i : destroyList) {
			entityList.get(i).destroy();
			entityList.remove((int)i);
		}
		destroyList.clear();
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
