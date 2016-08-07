package com.ovl.graphics;

import com.ovl.engine.Updatable;

public abstract class AnimationBase implements Updatable{
	protected float duration;
	protected float position = 0.0f;
	protected boolean isFinished = false;
	
	public AnimationBase(float duration){
		this.duration = duration;
	}
	
	protected abstract void animationStep();
	
	protected abstract void onAnimationEnd();
	
	@Override
	public void update(float deltaTime) {
		if (isFinished){
			return;
		}
		
		animationStep();
		position += deltaTime;
		if (position > duration){
			isFinished = true;
			onAnimationEnd();
		}
	}
}
