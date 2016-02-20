package physics;

import org.jbox2d.dynamics.Fixture;

public interface ICollidable {
	public void collisionEnd(Fixture me, ICollidable other);
	public void collisionStart(Fixture me, ICollidable other);
}
