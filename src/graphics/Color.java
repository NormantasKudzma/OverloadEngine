package graphics;

public class Color {
	private float[] rgba = new float[4];
	
	public Color(float r, float g, float b){
		this(r, g, b, 1.0f);
	}
	
	public Color(float r, float g, float b, float a){
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = a;
	}
	
	public float[] getRgba(){
		return rgba;
	}
}