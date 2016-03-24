package graphics;

public class Color {
	public float r;
	public float g;
	public float b;
	public float a = 1.0f;
	
	public Color(float r, float g, float b){
		this(r, g, b, 1.0f);
	}
	
	public Color(float r, float g, float b, float a){
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
}
