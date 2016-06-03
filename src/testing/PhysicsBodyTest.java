package testing;

import junit.framework.TestCase;

import org.jbox2d.dynamics.Fixture;

import physics.PhysicsBody;
import physics.PhysicsBody.EBodyType;
import physics.PhysicsBody.EMaskType;
import physics.PhysicsWorld;
import utils.Vector2;


public class PhysicsBodyTest extends TestCase {
	public void testAttachCollider(){
		PhysicsBody body = PhysicsWorld.getInstance().getNewBody(EBodyType.INTERACTIVE, null);
		
		// Box colliders
		Fixture boxCollider1 = body.attachBoxCollider(null, Vector2.one, 0.0f, false);
		assertNull("Box collider with null size should not be attached!", boxCollider1);
		
		Fixture boxCollider2 = body.attachBoxCollider(Vector2.one, null, 0.0f, false);
		assertNull("Box collider with null position should not be attached!", boxCollider2);
		
		Fixture boxCollider3 = body.attachBoxCollider(Vector2.one, Vector2.one, 0.0f, false);
		assertNotNull("Box collider with valid position and size should be attached!", boxCollider3);
		
		// Circle colliders
		Fixture circleCollider1 = body.attachCircleCollider(null, 1.0f, false);
		assertNull("Circle collider with null position should not be attached!", circleCollider1);
		
		Fixture circleCollider2 = body.attachCircleCollider(Vector2.one, -0.1f, false);
		assertNull("Circle collider with size <= 0.0f should not be attached!", circleCollider2);
		
		Fixture circleCollider3 = body.attachCircleCollider(Vector2.one, 1.0f, false);
		assertNotNull("Circle collider with valid size and position should be attached!", circleCollider3);
		
		// Polygon colliders
		Fixture polyCollider1 = body.attachPolygonCollider(null, false);
		assertNull("Polygon collider with null vertices should not be attached!", polyCollider1);
		
		Vector2 verts2[] = new Vector2[]{Vector2.zero, Vector2.right};
		Fixture polyCollider2 = body.attachPolygonCollider(verts2, false);
		assertNull("Polygon collider with less than 3 vertices should not be attached!", polyCollider2);
		
		Vector2 verts3[] = new Vector2[]{Vector2.zero, Vector2.right, Vector2.one};
		Fixture polyCollider3 = body.attachPolygonCollider(verts3, false);
		assertNotNull("Polygon collider with more than 3 vertices should be attached!", polyCollider3);
		
		// Cloning
		PhysicsBody clone = body.clone(null);
		assertNotNull(clone);
		assertNotNull(clone.getTransform());
		
		// Destroy single fixture
		body.destroyFixture(polyCollider3);
		Fixture f = body.getBody().getFixtureList();
		while (f != null){
			assertNotSame(polyCollider3, f);
			f = f.m_next;
		}
		
		// Flags and categories
		int category = 0x1000;
		body.setCollisionCategory(category, EMaskType.SET);
		assertTrue(body.getBody().getFixtureList().m_filter.categoryBits == category);
		body.setCollisionCategory(0x1000, EMaskType.SET_NOT);
		assertTrue(body.getBody().getFixtureList().m_filter.categoryBits == ~category);
		
		// Destroy all fixtures
		body.destroyFixtures();
		assertNull(body.getBody().m_fixtureList);
		
		// Transform functions
		Vector2 pos = new Vector2(0.66f, 0.66f);
		body.setPosition(pos);
		assertEquals(pos, body.getPosition());
		
		Vector2 scale = new Vector2(0.77f, 0.77f);
		body.setScale(scale);
		assertEquals(scale, body.getScale());
		
		float rotation = 88.0f;
		body.setRotation(rotation);
		assertEquals(rotation, body.getRotation());
		
		// Destroy inner body
		body.destroyBody();
		assertNull(body.getBody());
	}
	
	public void testInnerBody(){
		PhysicsBody body1 = PhysicsWorld.getInstance().getNewBody(EBodyType.INTERACTIVE, null);
		assertNotNull("Interactive physics body should have a box2d body!", body1.getBody());
		
		PhysicsBody body2 = PhysicsWorld.getInstance().getNewBody(EBodyType.NON_INTERACTIVE, null);
		assertNull("Non-interactive physics body should not have a box2d body!", body2.getBody());
	}
}
