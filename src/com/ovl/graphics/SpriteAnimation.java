package com.ovl.graphics;

import java.util.HashMap;

import com.ovl.engine.ParamSetter;
import com.ovl.engine.ShaderParams;
import com.ovl.engine.Vbo;
import com.ovl.game.Updatable;
import com.ovl.utils.ICloneable;
import com.ovl.utils.Vector2;

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

	@Override
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
	
	@Override
	public void useShader(Vbo vbo, HashMap<String, ParamSetter> params){
		//TODO:implement me!
	}
	
	public ShaderParams getShaderParams(){
		// TODO: implement me!?
		return null;
	}

	public void setShaderParams(ShaderParams params){
		// TODO: implement me?
	}
	
	@Override
	public void destroy(){
		for (int i = 0; i < spriteArray.length; ++i){
			for (int j = 0; j < spriteArray[i].length; ++j){
				spriteArray[i][j].destroy();
			}
		}
		spriteArray = null;
	}
	
	@Override
	public Color getColor(){
		return spriteArray[currentState][currentFrame].getColor();
	}
	
	public float getDuration(){
		return spriteArray[currentState].length * frameDelay;
	}
	
	@Override
	public Vector2 getSize(){
		return size;
	}
	
	public void onAnimationEnd(){
		//
	}
	
	@Override
	public void render() {
		spriteArray[currentState][currentFrame].render();
		size = spriteArray[currentState][currentFrame].getSize();
	}
	
	@Override
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
	
	@Override
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
	
	@Override
	public void updateVertices(Vector2 pos, Vector2 scale, float rotation){
		for (Sprite[] i : spriteArray){
			for (Sprite j : i){
				j.updateVertices(pos, scale, rotation);
			}
		}
	}
}
