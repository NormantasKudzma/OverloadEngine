package physics;

import org.jbox2d.dynamics.Fixture;

public interface ICollidable {
	public void collisionEnd(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable);
	public void collisionStart(Fixture myFixture, Fixture otherFixture, ICollidable otherCollidable);
}
