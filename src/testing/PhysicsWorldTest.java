package testing;

import junit.framework.TestCase;
import physics.PhysicsBody;
import physics.PhysicsBody.EBodyType;
import physics.PhysicsWorld;
import utils.Vector2;

public class PhysicsWorldTest extends TestCase {
	public void testGravity(){
		PhysicsWorld physics = PhysicsWorld.getInstance();
		
		Vector2 testGravity = new Vector2(99.0f, 0.0f);
		physics.setGravity(testGravity);
		assertEquals(testGravity, PhysicsWorld.getInstance().getGravity());
		
		physics.setGravity(null);
		assertEquals(testGravity, PhysicsWorld.getInstance().getGravity());
	}
	
	public void testBodyCreation(){
		PhysicsWorld physics = PhysicsWorld.getInstance();
		PhysicsBody body1 = physics.getBodyFromDef(EBodyType.INTERACTIVE, null);
		assertNotNull(body1);
		
		PhysicsBody body2 = physics.getBodyFromDef(EBodyType.INTERACTIVE, physics.getRawBodyDef());
		assertNotNull(body2);
	}
}
