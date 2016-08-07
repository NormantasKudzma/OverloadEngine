package com.ovl.engine;

import org.jbox2d.dynamics.Fixture;

import com.ovl.graphics.Color;
import com.ovl.graphics.Renderable;
import com.ovl.physics.Collidable;
import com.ovl.physics.PhysicsBody;
import com.ovl.physics.PhysicsWorld;
import com.ovl.utils.ICloneable;
import com.ovl.utils.Vector2;

public class GameObject implements Collidable, Renderable, Updatable, Cloneable {
	protected boolean isDestructible = true;
	protected boolean isLifetimeFinite = false;
	protected boolean isToBeDestroyed = false;
	protected boolean isVisible = true;
	protected float lifetime = 0.0f;
	protected PhysicsBody body;
	protected BaseGame game = null;
	protected Renderable sprite;
	protected Updatable spriteUpdatable;

	public GameObject(){
		this(null);
	}
	
	public GameObject(BaseGame game) {
		this.game = game;
	}

	public void applyForce(Vector2 dir) {
		body.applyForce(dir);
	}

	public void applyImpulse(Vector2 dir) {
		body.applyImpulse(dir);
	}

	public GameObject clone(){
		GameObject clone = null;
		try {
			Object obj = super.clone();
			if (obj instanceof GameObject){
				clone = (GameObject)obj;
				clone.body = body.clone(clone);
				clone.initEntity(body.getType());
				clone.isDestructible = isDestructible;
				clone.isLifetimeFinite = isLifetimeFinite;
				clone.isToBeDestroyed = isToBeDestroyed;
				clone.isVisible = isVisible;
				clone.lifetime = lifetime;
				
				if (sprite instanceof ICloneable){
					clone.sprite = (Renderable)((ICloneable)sprite).clone();
				}
				else {
					// If current renderable does not support cloning, leave it null
					clone.sprite = null;
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
	
	public Color getColor(){
		return sprite == null ? null : sprite.getColor();
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

	public Vector2 getSize(){
		if (sprite != null){
			return sprite.getSize();
		}
		return null;
	}
	
	public Renderable getSprite() {
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
		setScale(scale.x, scale.y);
	}

	public void setScale(float x, float y){
		body.setScale(x, y);
	}
	
	public void setSprite(Renderable spr) {
		sprite = spr;
		if (spr instanceof Updatable){
			spriteUpdatable = (Updatable)spr;
		}
		else {
			spriteUpdatable = null;
		}
	}
	
	public void setVerticalVelocity(float v){
		body.getBody().m_linearVelocity.y = v;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public void update(float deltaTime) {
		if (spriteUpdatable != null) {
			spriteUpdatable.update(deltaTime);
		}

		if (isLifetimeFinite) {
			lifetime -= deltaTime;
			if (lifetime <= 0.0f) {
				markForDestruction();
			}
		}
	}
}
