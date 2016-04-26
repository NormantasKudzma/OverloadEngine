package physics;

import java.util.ArrayList;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import utils.Vector2;
import engine.GameObject;

public class PhysicsWorld implements ContactListener {
	private static final PhysicsWorld INSTANCE = new PhysicsWorld();
	private ArrayList<PhysicsBody> bodyList = new ArrayList<PhysicsBody>();
	private World world;
	
	private PhysicsWorld(){
		world = new World(new Vec2());
		world.setContactListener(this);
		world.setAllowSleep(true);
	}
	
	public void addBody(PhysicsBody body){
		bodyList.add(body);
	}
	
	@Override
	public void beginContact(Contact c) {
		Object userDataA = c.getFixtureA().getBody().getUserData();
		Object userDataB = c.getFixtureB().getBody().getUserData();
		Collidable collidableA = null;
		Collidable collidableB = null;
		
		if (userDataA instanceof Collidable){
			collidableA = (Collidable)userDataA;
		}
		if (userDataB instanceof Collidable){
			collidableB = (Collidable)userDataB;
		}
		
		if (collidableA != null)
		{
			collidableA.collisionStart(c.getFixtureA(), c.getFixtureB(), collidableB);
		}
		if (collidableB != null)
		{
			collidableB.collisionStart(c.getFixtureB(), c.getFixtureA(), collidableA);
		}
	}

	@Override
	public void endContact(Contact c) {
		Object userDataA = c.getFixtureA().getBody().getUserData();
		Object userDataB = c.getFixtureB().getBody().getUserData();
		Collidable collidableA = null;
		Collidable collidableB = null;
		
		if (userDataA instanceof Collidable){
			collidableA = (Collidable)userDataA;
		}
		if (userDataB instanceof Collidable){
			collidableB = (Collidable)userDataB;
		}
		
		if (collidableA != null)
		{
			collidableA.collisionEnd(c.getFixtureA(), c.getFixtureB(), collidableB);
		}
		if (collidableB != null)
		{
			collidableB.collisionEnd(c.getFixtureB(), c.getFixtureA(), collidableA);
		}
	}
	
	public static PhysicsWorld getInstance(){
		return INSTANCE;
	}
	
	public World getWorld(){
		return world;
	}
	
	public PhysicsBody getBodyFromDef(PhysicsBody.EBodyType type, BodyDef def){
		PhysicsBody b = new PhysicsBody(type, def);
		bodyList.add(b);
		return b;
	}
	
	public final ArrayList<PhysicsBody> getBodyList(){
		return bodyList;
	}
	
	public PhysicsBody getNewBody(PhysicsBody.EBodyType type, GameObject e){
		PhysicsBody b = new PhysicsBody(type, e);
		bodyList.add(b);
		return b;
	}
	
	public BodyDef getRawBodyDef(){
		return new BodyDef();
	}

	@Override
	public void postSolve(Contact c, ContactImpulse i) {
		//stub
	}

	@Override
	public void preSolve(Contact c, Manifold m) {
		//stub
	}
	
	public void setGravity(Vector2 gravity){
		world.setGravity(gravity.toVec2());
	}
}
