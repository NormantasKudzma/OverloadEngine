package physics;

import game.Entity;

import java.util.ArrayList;

import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;

public class PhysicsWorld {
	private static final PhysicsWorld INSTANCE = new PhysicsWorld();
	private static ArrayList<PhysicsBody> bodyList = new ArrayList<PhysicsBody>();
	private World world;
	
	private PhysicsWorld(){
		world = new World(new Vec2());
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

	public void setDebugDraw(DebugDraw debugDraw){
		int debugFlags = 0x0;
		debugFlags |= DebugDraw.e_shapeBit;
		debugDraw.setFlags(debugFlags);
		
		world.setDebugDraw(debugDraw);
	}
}
