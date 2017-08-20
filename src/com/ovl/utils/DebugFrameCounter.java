package com.ovl.utils;

import com.ovl.engine.Renderer;
import com.ovl.graphics.Color;
import com.ovl.graphics.Primitive;
import com.ovl.physics.PhysicsBody.BodyType;
import com.ovl.ui.Label;

public class DebugFrameCounter extends Label{
	private float fpsTextUpdate = 0.0f;
	private int frame = 0;
	private final float textUpdate = 0.5f;
	
	private final int max = 120;
	private float frameTimeSum = 0.0f;
	private float frameTimes[] = new float[max];
	private Vector2 verts[] = new Vector2[max * 6];
	private Primitive bar;

	public DebugFrameCounter(){
		super(null, "-");
		getSimpleFont().setPosition(-0.72f, -0.9f);
		for (int i = 0; i < verts.length; ++i){
			verts[i] = new Vector2();
		}
		bar = new Primitive(verts, Renderer.PrimitiveType.Triangles);
		bar.setColor(new Color(0.8f, 0.8f, 0.0f));
	}
	
	@Override
	public void initEntity(BodyType type) {
		super.initEntity(type);
	}
	
	@Override
	public void update(float deltaTime) {
		fpsTextUpdate += deltaTime;
		
		// Only set text every once in a while (text is expensive)
		if (fpsTextUpdate > textUpdate){
			setText(String.format("%2.2f ms", 1000.0f * frameTimeSum / max));
			fpsTextUpdate -= textUpdate;
		}
		
		frameTimeSum -= frameTimes[frame];
		frameTimeSum += deltaTime;
		frameTimes[frame] = deltaTime;
		/*System.arraycopy(verts, 6, verts, 0, verts.length - 6);*/
		if (++frame >= max){
			frame = 0;
		}
	}
	
	@Override
	public void render() {
		super.render();
		/*bar.render();*/
	}
}
