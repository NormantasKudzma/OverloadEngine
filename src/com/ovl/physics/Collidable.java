package com.ovl.physics;

import org.jbox2d.dynamics.Fixture;

public interface Collidable {
	public void collisionEnd(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable);
	public void collisionStart(Fixture myFixture, Fixture otherFixture, Collidable otherCollidable);
}
