package engine;

import graphics.IRenderable;

import org.jbox2d.dynamics.Fixture;

import physics.ICollidable;
import physics.PhysicsBody;
import physics.PhysicsWorld;
import utils.Vector2;

public abstract class Entity<S extends IRenderable & IUpdatable> implements ICollidable, IRenderable, IUpdatable, Cloneable {
	protected PhysicsBody body;
	protected boolean isDestructible = true;
	protected boolean isLifetimeFinite = false;
	protected boolean isToBeDestroyed = false;
	protected boolean isVisible = true;
	protected float lifetime = 0.0f;
	protected S sprite;

	public Entity() {

	}

	public void applyForce(Vector2 dir) {
		body.applyForce(dir);
	}

	public void applyImpulse(Vector2 dir) {
		body.applyImpulse(dir);
	}

	public Entity<S> clone(){
		Entity<S> clone = null;
		try {
			Object obj = super.clone();
			if (obj instanceof Entity){
				clone = (Entity<S>)obj;
				clone.body = body.clone();
				clone.isDestructible = isDestructible;
				clone.isLifetimeFinite = isLifetimeFinite;
				clone.isToBeDestroyed = isToBeDestroyed;
				clone.isVisible = isVisible;
				clone.lifetime = lifetime;
				clone.sprite = sprite;
			}
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		return clone;
	}
	
	@Override
	public void collisionEnd(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable) {
		//
	}

	@Override
	public void collisionStart(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable) {
		//
	}
	
	public void destroy() {
		body.destroyBody();
	}

	public PhysicsBody getPhysicsBody() {
		return body;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public float getRotation() {
		return body.getRotation();
	}

	public Vector2 getScale() {
		return body.getScale();
	}

	public S getSprite() {
		return sprite;
	}

	public void initEntity() {
		if (body == null) {
			body = PhysicsWorld.getInstance().getNewBody(this);
		}
	}

	public boolean isDestroyed() {
		return isToBeDestroyed;
	}

	public boolean isDestructible() {
		return isDestructible;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void markForDestruction() {
		onDestroy();
		isToBeDestroyed = true;
	}

	public void onDestroy() {
		//
	}

	public void setLifetime(float time) {
		lifetime = time;
		isLifetimeFinite = true;
	}

	public void render() {
		render(body.getPosition(), body.getRotation(), body.getScale());
	}

	public void render(Vector2 position, float rotation, Vector2 scale) {
		if (!isVisible) {
			return;
		}
		sprite.render(position, rotation, scale);
	}

	public void setPosition(Vector2 pos) {
		body.setPosition(pos);
	}

	public void setPosition(float x, float y) {
		body.setPosition(x, y);
	}

	public void setRotation(float angle) {
		body.setRotation(angle);
	}

	/**
	 * Set scale for sprite, WARNING: CURRENTLY DOES NOT RESIZE COLLIDERS
	 * 
	 * @param scale - desired sprite scale
	 */
	public void setScale(Vector2 scale) {
		body.setScale(scale);
	}

	public void setScale(float scaleX, float scaleY){
		body.setScale(scaleX, scaleY);
	}
	
	public void setSprite(S spr) {
		sprite = spr;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}
	
	public void setHorizontalVelocity(float v){
		body.getBody().m_linearVelocity.x = v;
	}
	
	public void setLinearVelocity(Vector2 velocity){
		body.getBody().setLinearVelocity(velocity.toVec2());
	}
	
	public void setVerticalVelocity(float v){
		body.getBody().m_linearVelocity.y = v;
	}

	public void update(float deltaTime) {
		if (sprite != null) {
			sprite.update(deltaTime);
		}

		if (isLifetimeFinite) {
			lifetime -= deltaTime;
			if (lifetime <= 0.0f) {
				markForDestruction();
			}
		}
	}
}
