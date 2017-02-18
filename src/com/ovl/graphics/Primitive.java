package com.ovl.graphics;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.Renderer;
import com.ovl.utils.Vector2;

public class Primitive implements Renderable {
	private static final Renderer renderer;
	
	private Vector2 vertices[];
	private Renderer.PrimitiveType renderMode;
	private Renderer.VboId id;
	private Color color;

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
		refreshVertexData();
	}

	private void init(){
		id = renderer.generateId(Renderer.VboType.Primitive, vertices.length);
		setColor(Color.WHITE);
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
		renderer.renderPrimitive(id, renderMode, vertices, color, position, scale, rotation);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color c) {
		if (c != null){
			color = c;
		}
	}
	
	public void setVertex(int index, Vector2 pos){
		vertices[index] = pos.copy();
		refreshVertexData();
	}
	
	public void setVertex(int index, float x, float y){
		vertices[index].set(x, y);
		refreshVertexData();
	}
	
	public void setVertices(Vector2[] verts){
		for (int i = 0; i < verts.length && i < vertices.length; ++i){
			vertices[i].set(verts[i]);
		}
		refreshVertexData();
	}
	
	private void refreshVertexData(){
		renderer.setPrimitiveData(id, vertices, color);
	}
}
