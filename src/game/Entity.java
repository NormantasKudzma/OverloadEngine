package game;

import graphics.IRenderable;

import org.jbox2d.dynamics.Fixture;

import physics.ICollidable;
import physics.PhysicsBody;
import physics.PhysicsWorld;
import utils.Vector2;

public abstract class Entity<S extends IRenderable & IUpdatable> implements ICollidable, IRenderable, IUpdatable {
	protected PhysicsBody body;
	protected boolean isDestructible = true;
	protected boolean isLifetimeFinite = false;
	protected boolean isToBeDestroyed = false;
	protected boolean isVisible = true;
	protected float lifetime = 0.0f;
	protected S sprite;
	
	public Entity(){
		
	}
	
	public void applyForce(Vector2 dir){
		body.applyForce(dir);
	}
	
	public void applyImpulse(Vector2 dir){
		body.applyImpulse(dir);
	}
	
	public void destroy(){
		body.destroyBody();
	}
	
	public PhysicsBody getBody(){
		return body;
	}
	
	public Vector2 getPosition(){
		return body.getPosition();
	}
	
	public float getRotation(){
		return body.getRotation();
	}
	
	public Vector2 getScale(){
		return body.getScale();
	}
	
	public S getSprite(){
		return sprite;
	}
	
	@Override
	public void collisionEnd(Fixture me, ICollidable other) {
		//
	}
	
	@Override
	public void collisionStart(Fixture me, ICollidable other) {
		//
	}
	
	public void initEntity(){
		if (body == null){
			body = PhysicsWorld.getInstance().getNewBody(this);
		}
	}
	
	public boolean isDestroyed(){
		return isToBeDestroyed;
	}
	
	public boolean isDestructible(){
		return isDestructible;
	}
	
	public void markForDestruction(){
		onDestroy();
		isToBeDestroyed = true;
	}
	
	public void onDestroy(){
		//
	}
	
	public void setLifetime(float time){
		lifetime = time;
		isLifetimeFinite = true;
	}
	
	public boolean isVisible(){
		return isVisible;
	}
	
	public void render(){
		render(body.getPosition(), body.getRotation(), body.getScale());
	}
	
	public void render(Vector2 position, float rotation, Vector2 scale){
		if (!isVisible){
			return;
		}
		sprite.render(position, rotation, scale);
	}
	
	public void setPosition(Vector2 pos){
		body.setPosition(pos);
	}
	
	public void setPosition(float x, float y){
		body.setPosition(x, y);
	}
	
	public void setRotation(float angle){
		body.setRotation(angle);
	}
	
	/**
	 * Set scale for sprite, 
	 * WARNING: DOES NOT RESIZE COLLIDERS
	 * @param scale - desired sprite scale
	 */
	public void setScale(Vector2 scale){
		body.setScale(scale);
	}
	
	
	public void setSprite(S spr){
		sprite = spr;
	}
	
	public void setVisible(boolean isVisible){
		this.isVisible = isVisible;
	}
	
	public void update(float deltaTime){
		if (sprite != null){
			sprite.update(deltaTime);
		}
		
		if (isLifetimeFinite){
			lifetime -= deltaTime;
			if (lifetime <= 0.0f){
				markForDestruction();
			}
		}
	}
}
