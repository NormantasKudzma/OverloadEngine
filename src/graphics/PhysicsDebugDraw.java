package graphics;

import org.jbox2d.collision.AABB;
import org.jbox2d.dynamics.Fixture;
import org.lwjgl.opengl.GL11;

import physics.PhysicsBody;
import physics.PhysicsWorld;
import utils.Vector2;

public class PhysicsDebugDraw {
	 public static void render(){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1.0f, 0.25f, 0.1f);

		AABB aabb;
		Vector2 ul, br;
		Fixture fixture;
		PhysicsBody b;
		for (int i = 0; i < PhysicsWorld.getInstance().getBodyList().size(); ++i){
			b = PhysicsWorld.getInstance().getBodyList().get(i);
			if (b == null || b.getBody() == null){
				continue;
			}
			
			fixture = b.getBody().getFixtureList();
			
			while (fixture != null){
				GL11.glPushMatrix();
				GL11.glLineWidth(2.0f);
				GL11.glBegin(GL11.GL_LINE_LOOP);
				
				switch (fixture.getType()){
					/*case POLYGON:{
						PolygonShape shape = (PolygonShape)fixture.getShape();
						Vector2 pos = null;
						for (int j = 0; j < shape.m_count; ++j){
							pos = Vector2.fromVec2(shape.m_vertices[j]);
							GL11.glVertex2f(pos.x, pos.y);
						}
					}*/
					default:{
						aabb = fixture.getAABB(0);
						ul = Vector2.fromVec2(aabb.upperBound);
						br = Vector2.fromVec2(aabb.lowerBound);
						
						GL11.glVertex2f(br.x, br.y);
						GL11.glVertex2f(br.x, ul.y);
						GL11.glVertex2f(ul.x, ul.y);
						GL11.glVertex2f(ul.x, br.y);
					}
				}
				
			    GL11.glEnd();
				GL11.glPopMatrix();
				fixture = fixture.m_next;
			}
		}
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	 }
}
