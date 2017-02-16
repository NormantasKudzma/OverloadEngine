package com.ovl.physics;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.ovl.engine.GameObject;
import com.ovl.utils.Vector2;

/**
 * Wrapper for box2d Body class.
 * 
 */
public class PhysicsBody {
	/**
	 * 	Internal type of PhysicsBody. Not to be confused with org.jbox2d.dynamics.BodyType,
	 * 	which marks type for box2d body.
	 */
	public enum BodyType {
		/** 
		* 	This type marks a physics body, which will not interact with the world. 
		* 	Created physics body will not have a box2d body attached to it.
		**/
		NON_INTERACTIVE,
		/** 
		*	This type indicates that physics body can interact with the physics world
		* 	via collisions/forces/etc.
		* 	Created physics body will have a box2d body and can have colliders/sensors.
		**/
		INTERACTIVE
	}
	
	public enum MaskType {
		EXCLUDE,
		INCLUDE,
		SET,
		SET_NOT
	}
	
	private Body body;
	private ArrayList<Fixture> fixtureList = new ArrayList<Fixture>(1);
	private Transform transform = new Transform();
	private BodyType bodyType = BodyType.INTERACTIVE;

	public PhysicsBody(BodyType type, GameObject e){
		bodyType = type;
		if (type == BodyType.INTERACTIVE){
			createBody(null, e);
		}
	}

	public PhysicsBody(BodyType type, BodyDef def) {
		bodyType = type;
		if (type == BodyType.INTERACTIVE){
			createBody(def, null);
		}
	}

	public void applyForce(Vector2 dir) {
		body.applyForceToCenter(dir.toVec2());
		//body.setLinearVelocity(dir.toVec2());
	}
	
	public void applyImpulse(Vector2 dir){
		body.applyLinearImpulse(dir.toVec2(), body.getPosition());
	}

	public Fixture attachBoxCollider(Vector2 size){
		return attachBoxCollider(size, new Vector2(), 0.0f);
	}
	
	public Fixture attachBoxCollider(Vector2 size, Vector2 position, float rotation) {
		return attachBoxCollider(size, position, rotation, false);
	}
	
	public Fixture attachBoxCollider(Vector2 size, Vector2 position, float rotation, boolean isSensor) {
		if (size == null || position == null){
			return null;
		}
		
		Vec2 size2 = size.toVec2().mul(0.5f);
		Vec2 pos2 = position.toVec2();

		PolygonShape polygon = new PolygonShape();
		polygon.setAsBox(size2.x, size2.y, pos2, rotation);

		return attachCollider(polygon, isSensor);
	}

	public Fixture attachCircleCollider(Vector2 position, float radius) {
		return attachCircleCollider(position, radius, false);
	}
	
	public Fixture attachCircleCollider(Vector2 position, float radius, boolean isSensor) {
		if (position == null || radius <= 0.0f){
			return null;
		}
		
		CircleShape circle = new CircleShape();
		circle.setRadius(radius * Vector2.VECTOR2_TO_PHYSICS);
		circle.m_p.set(position.toVec2());

		return attachCollider(circle, isSensor);
	}

	public Fixture attachPolygonCollider(Vector2[] vertices){
		return attachPolygonCollider(vertices, false);
	}
	
	public Fixture attachPolygonCollider(Vector2[] vertices, boolean isSensor) {
		if (vertices == null || vertices.length < 3 || vertices.length > 8){
			return null;
		}
		
		Vec2[] vec2Verts = new Vec2[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			vec2Verts[i] = vertices[i].toVec2();
		}

		PolygonShape polygon = new PolygonShape();
		polygon.set(vec2Verts, vec2Verts.length);
	
		return attachCollider(polygon, isSensor);
	}

	private Fixture attachCollider(Shape shape, boolean isSensor) {
		if (body == null || bodyType == BodyType.NON_INTERACTIVE){
			System.err.println("Cannot attach fixture to a physics body.");
			return null;
		}
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.userData = body.m_userData;
		fixtureDef.isSensor = isSensor;

		Fixture fixture = body.createFixture(fixtureDef);
		fixtureList.add(fixture);
		return fixture;
	}

	public PhysicsBody clone(GameObject userData){
		try {
			PhysicsBody clone = new PhysicsBody(bodyType, userData);
			clone.transform = transform.clone();
			
			if (body != null){
				Body b = clone.body;
				b.m_angularDamping = body.m_angularDamping;
				b.m_angularVelocity = body.m_angularVelocity;
				b.m_flags = body.m_flags;
				b.m_gravityScale = body.m_gravityScale;
				b.m_linearDamping = body.m_linearDamping;
				b.m_torque = body.m_torque;
				b.m_type = body.m_type;
				b.m_userData = userData;
				b.setBullet(body.isBullet());
				b.setAwake(body.isAwake());			
				clone.getBody().setActive(true);	
			
				Fixture fixture = body.m_fixtureList;
				while (fixture != null){
					clone.attachCollider(fixture.getShape().clone(), fixture.isSensor());				
					fixture = fixture.m_next;
				}
				b.setTransform(body.getPosition(), transform.getRotation());
				
				if (body.m_fixtureList != null){
					Filter filter = body.m_fixtureList.getFilterData();
					clone.setCollisionCategory(filter.categoryBits, MaskType.SET);
					clone.setCollisionFlags(filter.maskBits, MaskType.SET);
				}
			}
			
			PhysicsWorld.getInstance().addBody(clone);
			return clone;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private void createBody(BodyDef def, GameObject gameObject) {
		if (def == null) {
			def = new BodyDef();
			/*if (def.type == BodyType.STATIC){
				def.allowSleep = true;
			}
			else {
				def.allowSleep = false;
			}*/
			def.linearDamping = 0.1f;
			def.fixedRotation = true;
			def.type = org.jbox2d.dynamics.BodyType.DYNAMIC;
			def.userData = gameObject;
		}
		body = PhysicsWorld.getInstance().getWorld().createBody(def);
	}

	public void destroyBody() {
		destroyFixtures();
		PhysicsWorld.getInstance().getBodyList().remove(this);
		if (body != null){
			PhysicsWorld.getInstance().getWorld().destroyBody(body);
			body = null;
		}
	}

	public void destroyFixtures(){
		for (Fixture i : fixtureList) {
			destroyFixture(i);
		}
		fixtureList.clear();
	}
	
	public void destroyFixture(Fixture f){
		if (f != null){
			body.destroyFixture(f);
		}
	}
	
	public Body getBody() {
		return body;
	}

	public Vector2 getPosition() {
		switch (bodyType){
			case INTERACTIVE:{
				return Vector2.fromVec2(body.getPosition());
			}
			case NON_INTERACTIVE:{
				return transform.getPosition();
			}
		}
		return null;
	}

	public float getRotation() {
		return transform.getRotation();
	}

	public Vector2 getScale() {
		return transform.getScale();
	}

	public BodyType getType(){
		return bodyType;
	}
	
	public Transform getTransform(){
		return transform;
	}
	
	public void resizeColliders(Vector2 scale){
		if (body == null){
			return;
		}
		
		Fixture f = body.m_fixtureList;
		while (f != null){
			Shape s = f.getShape();
			if (s instanceof PolygonShape){
				PolygonShape poly = (PolygonShape)s;
				for (int j = 0; j < poly.m_count; ++j){
					Vec2 vert = poly.m_vertices[j];
					poly.m_vertices[j].set(vert.x * scale.x, vert.y * scale.y);
				}
			}
			if (s instanceof CircleShape){
				CircleShape circ = (CircleShape)s;
				circ.m_radius *= scale.x;
				Vec2 pos = circ.m_p;
				circ.m_p.set(pos.x * scale.x, pos.y * scale.y);
			}
			
			f = f.m_next;
		}
	}
	
	public void setBodyType(BodyType type, GameObject userData){
		this.bodyType = type;
		switch (type){
			case INTERACTIVE:{
				if (body == null){
					createBody(null, userData);
				}
				break;
			}
			case NON_INTERACTIVE:{
				destroyBody();
				break;
			}
		}
	}
	
	public void setCollisionCategory(int flags, MaskType type){
		if (body != null){
			Fixture f = body.m_fixtureList;
			while (f != null){
				Filter filterData = f.getFilterData();
				switch (type){
					case EXCLUDE:{
						filterData.categoryBits &= ~flags;
						break;
					}
					case INCLUDE:{
						filterData.categoryBits |= flags;
						break;
					}
					case SET:{
						filterData.categoryBits = flags;
						break;
					}
					case SET_NOT:{
						filterData.categoryBits = ~flags;
						break;
					}
					default:{
						break;
					}
				}
				f = f.m_next;
			}
		}
	}
	
	public void setCollisionFlags(int flags, MaskType type){
		if (body != null){
			Fixture f = body.m_fixtureList;
			while (f != null){
				Filter filterData = f.getFilterData();
				switch (type){
					case EXCLUDE:{
						filterData.maskBits &= ~flags;
						break;
					}
					case INCLUDE:{
						filterData.maskBits |= flags;
						break;
					}
					case SET:{
						filterData.maskBits = flags;
						break;
					}
					case SET_NOT:{
						filterData.maskBits = ~flags;
						break;
					}
					default:{
						break;
					}
				}
				f = f.m_next;
			}
		}
	}
	
	public void setPosition(Vector2 pos) {
		setPosition(pos.x, pos.y);
	}

	public void setPosition(float x, float y) {
		transform.setPosition(x, y);
		if (bodyType == BodyType.INTERACTIVE){
			body.setTransform(Vector2.toVec2(x, y), transform.getRotation());
		}
	}

	public void setRotation(float angle) {
		transform.setRotation(angle);
		if (bodyType != BodyType.NON_INTERACTIVE){
			body.setTransform(body.getPosition(), angle);
		}
	}
	
	public void setScale(Vector2 scale) {
		setScale(scale.x, scale.y);
	}
	
	public void setScale(float x, float y){
		transform.setScale(x, y);
	}
}
