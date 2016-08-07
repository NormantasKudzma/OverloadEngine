package com.ovl.ui.animations;

import com.ovl.engine.Updatable;
import com.ovl.ui.Component;
import com.ovl.utils.Vector2;

public class TransformAnimation implements Updatable {
	private static final Interpolator DEFAULT_FUNCTION = new LerpInterpolator();
	
	protected Component component;
	protected Interpolator function;
	protected Vector2 from;
	protected Vector2 to;
	protected Vector2 out = new Vector2();
	protected boolean isLooping = false;
	protected boolean isReversed = false;
	protected float duration = 1.0f;
	protected float progress = 1.0f;
	protected float speed = 1.0f;
	
	public TransformAnimation(Component c){
		this(c, null, null);
	}
	
	public TransformAnimation(Component c, Vector2 from, Vector2 to){
		this(c, from, to, DEFAULT_FUNCTION);
	}
	
	public TransformAnimation(Component c, Vector2 from, Vector2 to, Interpolator fn){
		component = c;
		function = fn;
		setBounds(from, to);
	}

	protected void animationStep(){
		//
	}
	
	public Vector2 getFrom(){
		return from;
	}
	
	public Vector2 getTo(){
		return to;
	}
	
	public boolean isPlaying(){
		return (progress < duration);
	}
	
	public boolean isReversed(){
		return isReversed;
	}
	
	protected void onAnimationEnd(){
		//
	}
	
	public void reverse(){
		Vector2 temp = from;
		from = to;
		to = temp;
		
		isReversed = !isReversed;
	}
	
	public void setBounds(Vector2 from, Vector2 to){
		if (from != null){
			this.from = from.copy();
		}
		
		if (to != null){
			this.to = to.copy();
		}
	}
	
	public void setDuration(float duration){
		this.duration = duration;
	}
	
	public void setFunction(Interpolator function){
		this.function = function;
	}
	
	public void setLooping(boolean isLooping){
		this.isLooping = isLooping;
	}
	
	public void setSpeed(float speed){
		this.speed = speed;
	}
	
	public void start(){
		progress = 0.0f;
	}
	
	@Override
	public void update(float deltaTime) {
		if (progress < duration && from != null && to != null){
			float dt = speed * deltaTime;
			progress += dt;
			function.interpolate(from, to, out, progress / duration);
			animationStep();
			
			if (progress >= duration){
				onAnimationEnd();
				if (isLooping){
					progress = 0.0f;
				}
			}
		}
	}
}
