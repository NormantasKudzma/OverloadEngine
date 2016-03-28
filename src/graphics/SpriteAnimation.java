package graphics;

import physics.Transform;
import utils.Vector2;
import engine.IUpdatable;

public class SpriteAnimation implements IRenderable, IUpdatable{
	protected Sprite2D spriteArray[][];
	protected int currentFrame = 0;
	protected int currentState = 0;
	protected int numStates = 0;
	protected float frameDelay = 0.15f;
	protected float timePassed = 0.0f;
	protected boolean isPaused = true;
	
	public SpriteAnimation(){

	}

	public void destroy(){
		for (int i = 0; i < spriteArray.length; ++i){
			for (int j = 0; j < spriteArray[i].length; ++j){
				spriteArray[i][j].destroy();
			}
		}
	}
	
	@Override
	public void render() {
		render(Vector2.one, Vector2.one, 0.0f);
	}
	
	@Override
	public void render(Transform t) {
		render(t.getPosition(), t.getScale(), t.getRotation());
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		spriteArray[currentState][currentFrame].render(position, scale, rotation);
	}
	
	public void setColor(Color c){
		spriteArray[currentState][currentFrame].setColor(c);
	}
	
	public void setFrameDelay(float delay){
		if (delay > 0.0f){
			frameDelay = delay;
		}
	}
	
	public void setPaused(boolean isPaused){
		this.isPaused = isPaused;
		
		if (isPaused){
			currentFrame = 0;
		}
	}
	
	public void setSpriteArray(Sprite2D[][] sprites){
		spriteArray = sprites;
		numStates = sprites.length;
	}
	
	public void setState(int state){
		currentState = state;
		currentFrame = 0;
	}
	
	public void update(float deltaTime){
		if (isPaused){
			return;
		}
		
		timePassed += deltaTime;
		if (timePassed >= frameDelay){
			timePassed = 0.0f;
			currentFrame = (currentFrame + 1) % spriteArray[currentState].length;
		}
	}

}
