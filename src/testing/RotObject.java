package testing;

import engine.BaseGame;
import engine.GameObject;
import graphics.Sprite;

public class RotObject extends GameObject<Sprite>{
	private float kampas = 0.0f;
	
	public RotObject(BaseGame game) {
		super(game);
	}

	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		kampas += 180.0f * deltaTime;
		setRotation(kampas);
	}
}
