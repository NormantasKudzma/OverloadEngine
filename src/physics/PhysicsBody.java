package physics;

import java.util.ArrayList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Rot;
import org.jbox2d.common.Transform;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import utils.Vector2;
import engine.Entity;

/**
 * Wrapper for box2d Body class.
 * 
 * @author Nor-Vartotojas
 * 
 */
public class PhysicsBody {
	public enum MaskType {
		EXCLUDE,
		INCLUDE,
		SET,
		SET_NOT
	}
	
	private Body body;
	private ArrayList<Fixture> fixtureList = new ArrayList<Fixture>(1);
	private float bodyRotation;
	private Vector2 bodyScale = new Vector2(1.0f, 1.0f);

	/**
	 * Creates a body with a defined user data.
	 * 
	 * @param e - Entity that will be set to UserData.
	 */
	public PhysicsBody(Entity e) {
		createBody(null, e);
	}

	/**
	 * Creates a body from body definition.
	 * 
	 * @param def- Body Definition.
	 */
	public PhysicsBody(BodyDef def) {
		createBody(def, null);
	}

	public void applyForce(Vector2 dir) {
		body.applyForceToCenter(dir.toVec2());
		//body.setLinearVelocity(dir.toVec2());
	}
	
	public void applyImpulse(Vector2 dir){
		body.applyLinearImpulse(dir.toVec2(), body.getPosition());
	}

	public void attachBoxCollider(Vector2 size, Vector2 position, float rotation) {
		attachBoxCollider(size, position, rotation, false);
	}
	
	public void attachBoxCollider(Vector2 size, Vector2 position, float rotation, boolean isSensor) {
		Vec2 size2 = size.toVec2().mul(0.5f);
		Vec2 pos2 = position.toVec2();

		PolygonShape polygon = new PolygonShape();
		polygon.setAsBox(size2.x, size2.y);
		if (position != null) {
			polygon.centroid(new Transform(pos2, new Rot(rotation)));
		}

		attachCollider(polygon, isSensor);
	}

	public void attachCircleCollider(float radius, Vector2 position) {
		attachCircleCollider(radius, position, false);
	}
	
	public void attachCircleCollider(float radius, Vector2 position, boolean isSensor) {
		CircleShape circle = new CircleShape();
		circle.setRadius(radius);
		if (position != null) {
			circle.m_p.set(position.toVec2());
		}

		attachCollider(circle, isSensor);
	}

	public void attachPolygonCollider(Vector2[] vertices){
		attachPolygonCollider(vertices, false);
	}
	
	public Fixture attachPolygonCollider(Vector2[] vertices, boolean isSensor) {
		Vec2[] vec2Verts = new Vec2[vertices.length];
		for (int i = 0; i < vertices.length; i++) {
			vec2Verts[i] = vertices[i].toVec2();
		}

		PolygonShape polygon = new PolygonShape();
		polygon.set(vec2Verts, vec2Verts.length);
	
		return attachCollider(polygon, isSensor);
	}

	private Fixture attachCollider(Shape shape, boolean isSensor) {
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = shape;
		fixtureDef.userData = body.m_userData;
		fixtureDef.isSensor = isSensor;

		Fixture fixture = body.createFixture(fixtureDef);
		fixtureList.add(fixture);
		return fixture;
	}

	public PhysicsBody clone(Entity userData){
		try {
			PhysicsBody clone = new PhysicsBody((Entity)body.m_userData);
			clone.bodyRotation = bodyRotation;
			clone.bodyScale = bodyScale;
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
			b.setTransform(body.getPosition(), bodyRotation);
			
			if (body.m_fixtureList != null){
				Filter filter = body.m_fixtureList.getFilterData();
				clone.setCollisionCategory(filter.categoryBits, MaskType.SET);
				clone.setCollisionFlags(filter.maskBits, MaskType.SET);
			}
			return clone;
		}
		catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	private void createBody(BodyDef def, Entity e) {
		if (def == null) {
			def = new BodyDef();
			if (def.type == BodyType.STATIC){
				def.allowSleep = true;
			}
			else {
				def.allowSleep = false;
			}
			def.linearDamping = 0.1f;
			def.fixedRotation = true;
			def.type = BodyType.DYNAMIC;
			def.userData = e;
		}
		body = PhysicsWorld.getInstance().getWorld().createBody(def);
	}

	public void destroyBody() {
		destroyFixtures();
		PhysicsWorld.getInstance().getBodyList().remove(this);
		PhysicsWorld.getInstance().getWorld().destroyBody(body);
		body = null;
	}

	public void destroyFixtures(){
		for (Fixture i : fixtureList) {
			destroyFixture(i);
		}
		fixtureList.clear();
	}
	
	public void destroyFixture(Fixture f){
		body.destroyFixture(f);
	}
	
	public Body getBody() {
		return body;
	}

	public Vector2 getPosition() {
		return Vector2.fromVec2(body.getPosition());
	}

	public float getRotation() {
		return bodyRotation;
	}

	public Vector2 getScale() {
		return bodyScale;
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
		body.setTransform(Vector2.toVec2(pos), bodyRotation);
	}

	public void setPosition(float x, float y) {
		setPosition(new Vector2(x, y));
	}

	public void setRotation(float angle) {
		bodyRotation = angle;
		body.setTransform(body.getPosition(), angle);
	}
	
	/**
	 * Set scale for sprite, WARNING: DOES NOT RESIZE COLLIDERS
	 * 
	 * @param scale - desired sprite scale
	 */
	public void setScale(Vector2 scale) {
		bodyScale = scale;
	}
	
	public void setScale(float scaleX, float scaleY){
		bodyScale.set(scaleX, scaleY);
	}
}
