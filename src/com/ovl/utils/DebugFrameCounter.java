package com.ovl.utils;

import com.ovl.physics.PhysicsBody.EBodyType;
import com.ovl.ui.Label;

public class DebugFrameCounter extends Label{
	private static final Vector2 fpsTextPosition = new Vector2(0.2f, 0.15f);
	
	private boolean isFirstFrame = true;
	private float debugTime = 0;
	private float fpsTextUpdate = 0.0f;
	private float numFrames = 0;

	public DebugFrameCounter(){
		super(null, "00");
	}
	
	@Override
	public void initEntity(EBodyType type) {
		super.initEntity(type);
		setPosition(fpsTextPosition);
		setScale(Vector2.one);
	}
	
	@Override
	public void update(float deltaTime) {
		if (!isFirstFrame){
			numFrames++;
			debugTime += deltaTime;
			fpsTextUpdate += deltaTime;
			
			// Only set text every once in a while (text is expensive)
			if (fpsTextUpdate > 0.5f){
				setText((int)(numFrames / debugTime) + " fps");
				fpsTextUpdate = 0.0f;
				numFrames = 0.0f;
				debugTime = 0.0f;
			}
		}
		
		if (isFirstFrame){
			isFirstFrame = false;
		}
	}
}
