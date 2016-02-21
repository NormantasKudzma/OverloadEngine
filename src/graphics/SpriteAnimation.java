package graphics;

import game.IUpdatable;

import org.json.JSONObject;

import utils.ConfigManager;
import utils.Paths;
import utils.Vector2;

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
	
	public Vector2 getHalfSize() {
		if (spriteArray != null && spriteArray[0][0] != null){
			return spriteArray[0][0].getHalfSize();
		}
		return null;
	}

	@Override
	public void render() {
		render(Vector2.one, 0.0f, Vector2.one);
	}
	
	@Override
	public void render(Vector2 position, float rotation, Vector2 scale) {
		spriteArray[currentState][currentFrame].render(position, rotation, scale);
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
