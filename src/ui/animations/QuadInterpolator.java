package ui.animations;

import utils.Vector2;

public class QuadInterpolator implements Interpolator {
	private float a;
	private float aisq;
	private Vector2 vertex;
	private float d;
	
	public QuadInterpolator(){
		this(new Vector2(0.75f, 1.25f));
	}
	
	public QuadInterpolator(Vector2 vertex){
		a = -(vertex.y) / (vertex.x * vertex.x);
		this.vertex = vertex;
		
		aisq = a * vertex.x * vertex.x;
	}
	
	@Override
	public void interpolate(Vector2 startVal, Vector2 endVal, Vector2 out, float t) {
		d = startVal.x + (endVal.x - startVal.x) * t;		
		out.x = a * d * d - aisq + vertex.y;
		
		d = startVal.y + (endVal.y - startVal.y) * t;		
		out.y = a * d * d - aisq + vertex.y;
	}
}
