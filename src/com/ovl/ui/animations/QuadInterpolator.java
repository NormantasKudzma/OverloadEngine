package com.ovl.ui.animations;

import com.ovl.utils.Vector2;

public class QuadInterpolator implements Interpolator {
	private float a, a1, a2;
	private Vector2 vertex;
	private float value;
	
	public QuadInterpolator(){
		this(new Vector2(0.75f, 1.25f));
	}
	
	public QuadInterpolator(Vector2 vertex){
		setVertex(vertex);
	}
	
	public void setVertex(Vector2 vertex){
		this.vertex = vertex;
		
		a1 = -vertex.y / (vertex.x * vertex.x);
		a2 = (1.0f - vertex.y) / ((1.0f - vertex.x) * (1.0f - vertex.x));
		
		a = a1;
	}
	
	@Override
	public void interpolate(Vector2 startVal, Vector2 endVal, Vector2 out, float t) {
		a = t <= 0.5f ? a1 : a2;
		
		value = a * (t - vertex.x) * (t - vertex.x) + vertex.y;
		out.set(startVal.x + (endVal.x - startVal.x) * value, startVal.y + (endVal.y - startVal.y) * value);
	}
}
