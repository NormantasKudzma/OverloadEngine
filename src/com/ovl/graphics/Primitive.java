package com.ovl.graphics;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.Renderer;
import com.ovl.utils.Vector2;

public class Primitive implements Renderable {
	protected static final Renderer renderer;
	
	private Vector2 vertices[];
	protected Renderer.PrimitiveType renderMode;
	private Renderer.VboId id;

	static {
		renderer = OverloadEngine.getInstance().renderer;
	}
	
	public Primitive(int numVerts, Renderer.PrimitiveType renderMode){
		this(new Vector2[numVerts], renderMode);
	}
	
	public Primitive(Vector2 verts[], Renderer.PrimitiveType renderMode){
		vertices = verts;
		this.renderMode = renderMode;
		init();
	}

	private void init(){
		id = renderer.generateId(Renderer.VboType.Primitive, vertices.length);
	}
	
	@Override
	public void destroy() {
		renderer.deleteVbo(id);
		id = null;
	}

	@Override
	public Vector2 getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	public Vector2 getVertex(int index){
		return vertices[index];
	}
	
	@Override
	public void render() {
		render(Vector2.one, Vector2.one, 0.0f);
	}

	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		renderer.renderPrimitive(id, renderMode, vertices, position, scale, rotation);
	}

	@Override
	public Color getColor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setColor(Color c) {
		// TODO Auto-generated method stub
		
	}
	
	public void setVertex(int index, Vector2 pos){
		vertices[index] = pos.copy();
	}
	
	public void setVertex(int index, float x, float y){
		vertices[index].set(x, y);
	}
}
