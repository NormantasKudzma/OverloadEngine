package com.ovl.graphics;

import java.util.ArrayList;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.ShaderParams;
import com.ovl.utils.Vector2;

public class Primitive implements Renderable {
	private static final Renderer renderer;
	private static final String defaultShaderName;
	
	private Vector2 vertices[];
	private Renderer.PrimitiveType renderMode;
	private ShaderParams id;
	private Color color = new Color();

	static {
		defaultShaderName = "Primitive";
		renderer = OverloadEngine.getInstance().renderer;
		renderer.createShader(defaultShaderName);
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
		ArrayList<ParamSetter> shaderParams = new ArrayList<ParamSetter>();
		shaderParams.add(ParamSetterFactory.build(id, Shader.U_COLOR, color));
		shaderParams.add(ParamSetterFactory.buildDefault(id, Shader.U_MVPMATRIX));
		
		useShader(defaultShaderName, shaderParams);	
	}
	
	public void useShader(String shaderName, ArrayList<ParamSetter> params){
		if (id != null){
			renderer.releaseId(id);
		}

		if ((id = renderer.generateId(shaderName, vertices.length)) == null){
			renderer.createVbo(shaderName, vertices.length);
			id = renderer.generateId(shaderName, vertices.length);
		}
		
		for (ParamSetter p : params){
			id.addParam(p);
		}
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
		renderer.renderPrimitive(id, renderMode, color);
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color c) {
		color.set(c);
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
	
	// TODO: implement
	public void updateVertices(Vector2 pos, Vector2 scale, float rotation){
		//
	}
}
