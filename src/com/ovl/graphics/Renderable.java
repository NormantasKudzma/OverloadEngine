package com.ovl.graphics;

import java.util.HashMap;

import com.ovl.engine.ParamSetter;
import com.ovl.engine.Vbo;
import com.ovl.utils.Vector2;

public interface Renderable {
	public void destroy();
	
	public Vector2 getSize();
	
	public void render();
	
	public Color getColor();
	public void setColor(Color c);
	
	public void useShader(Vbo vbo, HashMap<String, ParamSetter> params);
	
	public void updateVertices(Vector2 pos, Vector2 scale, float rotation);
}
