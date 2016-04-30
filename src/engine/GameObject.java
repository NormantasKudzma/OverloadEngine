package engine;

import graphics.Color;
import graphics.Renderable;

import org.jbox2d.dynamics.Fixture;

import physics.Collidable;
import physics.PhysicsBody;
import physics.PhysicsWorld;
import utils.ICloneable;
import utils.Vector2;

public abstract class GameObject<S extends Renderable & Updatable> implements Collidable, Renderable, Updatable, Cloneable {
	protected boolean isDestructible = true;
	protected boolean isLifetimeFinite = false;
	protected boolean isToBeDestroyed = false;
	protected boolean isVisible = true;
	protected float lifetime = 0.0f;
	protected PhysicsBody body;
	protected BaseGame game = null;
	protected S sprite;

	public GameObject(BaseGame game) {
		this.game = game;
	}

	public void applyForce(Vector2 dir) {
		body.applyForce(dir);
	}

	public void applyImpulse(Vector2 dir) {
		body.applyImpulse(dir);
	}

	public GameObject<S> clone(){
		GameObject<S> clone = null;
		try {
			Object obj = super.clone();
			if (obj instanceof GameObject){
				clone = (GameObject<S>)obj;
				clone.body = body.clone(clone);
				clone.initEntity(body.getType());
				clone.isDestructible = isDestructible;
				clone.isLifetimeFinite = isLifetimeFinite;
				clone.isToBeDestroyed = isToBeDestroyed;
				clone.isVisible = isVisible;
				clone.lifetime = lifetime;
				if (sprite instanceof ICloneable){
					clone.sprite = (S)((ICloneable)sprite).clone();
				}
				else {
					clone.sprite = sprite;
				}
			}
		}
		catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		return clone;
	}
	
	@Override
	public void collisionEnd(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable) {
		//
	}

	@Override
	public void collisionStart(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable) {
		//
	}
	
	public void destroy() {
		body.destroyBody();
		body = null;
		if (sprite != null){
			sprite.destroy();
			sprite = null;
		}
		game = null;
	}
	
	public float getHorizontalVelocity(){
		return body.getBody().m_linearVelocity.x;
	}
	
	public float getLifetime(){
		return lifetime;
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

	public void initEntity(PhysicsBody.EBodyType type) {
		if (body == null){
			body = PhysicsWorld.getInstance().getNewBody(type, this);
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
		render(body.getPosition(), body.getScale(), body.getRotation());
	}
	
	public void render(Vector2 position, Vector2 scale, float rotation) {
		if (!isVisible || sprite == null) {
			return;
		}
		
		sprite.render(position, scale, rotation);
	}

	public void setCollisionFlags(int category, int mask){
		if (body != null){
			body.setCollisionCategory(category, PhysicsBody.EMaskType.SET);
			body.setCollisionFlags(mask, PhysicsBody.EMaskType.SET);
		}
	}
	
	public void setColor(Color c){
		sprite.setColor(c);
	}
	
	public void setHorizontalVelocity(float v){
		body.getBody().m_linearVelocity.x = v;
	}
	
	public void setLinearVelocity(Vector2 velocity){
		body.getBody().setLinearVelocity(velocity.toVec2());
	}
	
	public void setPosition(Vector2 pos) {
		setPosition(pos.x, pos.y);
	}

	public void setPosition(float x, float y) {
		body.setPosition(x, y);
	}

	public void setRotation(float angle) {
		body.setRotation(angle);
	}

	public void setScale(Vector2 scale) {
		body.setScale(scale);
	}

	public void setScale(float x, float y){
		body.setScale(x, y);
	}
	
	public void setSprite(S spr) {
		sprite = spr;
	}
	
	public void setVerticalVelocity(float v){
		body.getBody().m_linearVelocity.y = v;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
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
