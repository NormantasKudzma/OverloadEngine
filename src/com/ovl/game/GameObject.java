package com.ovl.game;

import org.jbox2d.dynamics.Fixture;

import com.ovl.graphics.Color;
import com.ovl.graphics.Renderable;
import com.ovl.physics.Collidable;
import com.ovl.physics.PhysicsBody;
import com.ovl.physics.PhysicsBody.BodyType;
import com.ovl.physics.PhysicsWorld;
import com.ovl.utils.ICloneable;
import com.ovl.utils.Vector2;

public class GameObject implements Collidable, Updatable, ICloneable {
	protected boolean isVisible = true;
	protected PhysicsBody body;
	protected BaseGame game = null;
	protected Renderable sprite;
	protected Updatable spriteUpdatable;

	public GameObject(){
		this(null);
	}
	
	public GameObject(BaseGame game) {
		this.game = game;
		initEntity(BodyType.NON_INTERACTIVE);
	}

	public void applyForce(Vector2 dir) {
		body.applyForce(dir);
	}

	public void applyImpulse(Vector2 dir) {
		body.applyImpulse(dir);
	}

	@Override
	public GameObject clone(){
		GameObject clone = null;
		try {
			Object obj = super.clone();
			if (obj instanceof GameObject){
				clone = (GameObject)obj;
				clone.body = body.clone(clone);
				clone.isVisible = isVisible;
				
				if (sprite instanceof ICloneable && sprite instanceof Renderable){
					clone.setSprite((Renderable)((ICloneable)sprite).clone());
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

	public void initEntity(PhysicsBody.BodyType type) {
		if (body != null){
			body.destroyBody();
		}
		body = PhysicsWorld.getInstance().getNewBody(type, this);
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void onDestroy() {
		//
	}

	public void render() {
		if (!isVisible || sprite == null) {
			return;
		}
		
		sprite.render();
	}

	public void setCollisionFlags(int category, int mask){
		if (body != null){
			body.setCollisionCategory(category, PhysicsBody.MaskType.SET);
			body.setCollisionFlags(mask, PhysicsBody.MaskType.SET);
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
		updateVertices(body.getPosition(), body.getScale(), body.getRotation());
	}

	public void setRotation(float angle) {
		body.setRotation(angle);
		updateVertices(body.getPosition(), body.getScale(), body.getRotation());
	}

	public void setScale(Vector2 scale) {
		setScale(scale.x, scale.y);
	}

	public void setScale(float x, float y){
		body.setScale(x, y);
		updateVertices(body.getPosition(), body.getScale(), body.getRotation());
	}
	
	public void setSprite(Renderable spr) {
		sprite = spr;
		if (spr instanceof Updatable){
			spriteUpdatable = (Updatable)spr;
		}
		else {
			spriteUpdatable = null;
		}
		updateVertices(body.getPosition(), body.getScale(), body.getRotation());
	}
	
	public void setVerticalVelocity(float v){
		body.getBody().m_linearVelocity.y = v;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	@Override
	public void update(float deltaTime) {
		if (spriteUpdatable != null) {
			spriteUpdatable.update(deltaTime);
		}
	}
	
	public void updateVertices(Vector2 pos, Vector2 scale, float rotation){
		if (sprite != null){
			sprite.updateVertices(pos, scale, rotation);
		}
	}

	public void unloadResources(){
		if (sprite != null) sprite.unloadResources();
	}

	public void reloadResources(){
		if (sprite != null) sprite.reloadResources();
	}
}
