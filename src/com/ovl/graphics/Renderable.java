package com.ovl.graphics;

import com.ovl.utils.Vector2;

public interface Renderable {
	public void destroy();
	
	public Vector2 getSize();
	
	public void render();
	
	public Color getColor();
	public void setColor(Color c);
	
	public void updateVertices(Vector2 pos, Vector2 scale, float rotation);
}
