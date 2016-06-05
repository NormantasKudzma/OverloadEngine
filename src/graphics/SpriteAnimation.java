package graphics;

import utils.ICloneable;
import utils.Vector2;
import engine.Updatable;

public class SpriteAnimation implements Renderable, Updatable, ICloneable{
	protected Sprite spriteArray[][];
	protected int currentFrame = 0;
	protected int currentState = 0;
	protected int numStates = 0;
	protected float frameDelay = 0.15f;
	protected float timePassed = 0.0f;
	protected boolean isPaused = true;
	protected Vector2 size = new Vector2();
	
	public SpriteAnimation(){

	}

	public SpriteAnimation clone(){
		SpriteAnimation clone = new SpriteAnimation();
		Sprite sprites[][] = new Sprite[spriteArray.length][];
		for (int i = 0; i < spriteArray.length; ++i){
			Sprite state[] = new Sprite[spriteArray[i].length];
			for (int j = 0; j < spriteArray[i].length; ++j){
				state[j] = spriteArray[i][j].clone();
			}
			sprites[i] = state;
		}
		clone.setSpriteArray(sprites);
		clone.currentFrame = currentFrame;
		clone.currentState = currentState;
		clone.numStates = numStates;
		clone.frameDelay = frameDelay;
		clone.timePassed = timePassed;
		clone.isPaused = isPaused;
		return clone;
	}
	
	public void destroy(){
		for (int i = 0; i < spriteArray.length; ++i){
			for (int j = 0; j < spriteArray[i].length; ++j){
				spriteArray[i][j].destroy();
			}
		}
		spriteArray = null;
	}
	
	public float getDuration(){
		return spriteArray[currentState].length * frameDelay;
	}
	
	public Vector2 getSize(){
		return size;
	}
	
	public void onAnimationEnd(){
		//
	}
	
	@Override
	public void render() {
		render(Vector2.one, Vector2.one, 0.0f);
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		spriteArray[currentState][currentFrame].render(position, scale, rotation);
		size = spriteArray[currentState][currentFrame].getSize();
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
	
	public void setSpriteArray(Sprite[][] sprites){
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
			if (currentFrame == 0){
				onAnimationEnd();
			}
		}
	}
}
