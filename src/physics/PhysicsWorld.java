package physics;

import engine.Entity;

import java.util.ArrayList;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

public class PhysicsWorld implements ContactListener {
	private static final PhysicsWorld INSTANCE = new PhysicsWorld();
	private static ArrayList<PhysicsBody> bodyList = new ArrayList<PhysicsBody>();
	private World world;
	
	private PhysicsWorld(){
		world = new World(new Vec2());
		world.setContactListener(this);
		world.setAllowSleep(false);
	}
	
	public static PhysicsWorld getInstance(){
		return INSTANCE;
	}
	
	public World getWorld(){
		return world;
	}
	
	public PhysicsBody getBodyFromDef(BodyDef def){
		PhysicsBody b = new PhysicsBody(def);
		bodyList.add(b);
		return b;
	}
	
	public final ArrayList<PhysicsBody> getBodyList(){
		return bodyList;
	}
	
	public PhysicsBody getNewBody(Entity e){
		PhysicsBody b = new PhysicsBody(e);
		bodyList.add(b);
		return b;
	}
	
	public BodyDef getRawBodyDef(){
		return new BodyDef();
	}

	@Override
	public void beginContact(Contact c) {
		Object userDataA = c.getFixtureA().getBody().getUserData();
		Object userDataB = c.getFixtureB().getBody().getUserData();
		ICollidable collidableA = null;
		ICollidable collidableB = null;
		
		if (userDataA instanceof ICollidable){
			collidableA = (ICollidable)userDataA;
		}
		if (userDataB instanceof ICollidable){
			collidableB = (ICollidable)userDataB;
		}
		
		if (collidableA != null)
		{
			collidableA.collisionStart(c.getFixtureA(), collidableB);
		}
		if (collidableB != null)
		{
			collidableB.collisionStart(c.getFixtureB(), collidableA);
		}
	}

	@Override
	public void endContact(Contact c) {
		Object userDataA = c.getFixtureA().getBody().getUserData();
		Object userDataB = c.getFixtureB().getBody().getUserData();
		ICollidable collidableA = null;
		ICollidable collidableB = null;
		
		if (userDataA instanceof ICollidable){
			collidableA = (ICollidable)userDataA;
		}
		if (userDataB instanceof ICollidable){
			collidableB = (ICollidable)userDataB;
		}
		
		if (collidableA != null)
		{
			collidableA.collisionEnd(c.getFixtureA(), collidableB);
		}
		if (collidableB != null)
		{
			collidableB.collisionEnd(c.getFixtureB(), collidableA);
		}
	}

	@Override
	public void postSolve(Contact c, ContactImpulse arg1i) {
		//stub
	}

	@Override
	public void preSolve(Contact c, Manifold m) {
		//stub
	}
}
