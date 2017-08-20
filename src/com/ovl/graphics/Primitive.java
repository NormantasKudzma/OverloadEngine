package com.ovl.graphics;

import java.util.HashMap;
import java.util.Map;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.ShaderParams;
import com.ovl.engine.Vbo;
import com.ovl.utils.Vector2;

public class Primitive implements Renderable {
	private static final Renderer renderer;
	private static final String defaultShaderName;
	private static final Shader defaultShader;
	
	private Vector2 vertices[];
	private Renderer.PrimitiveType renderMode;
	private ShaderParams id;
	private Color color = new Color();
	private Vbo vbo;

	static {
		defaultShaderName = "Primitive";
		renderer = OverloadEngine.getInstance().renderer;
		defaultShader = renderer.createShader(defaultShaderName);
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
		HashMap<String, ParamSetter> shaderParams = new HashMap<String, ParamSetter>();
		shaderParams.put(Shader.U_COLOR, ParamSetterFactory.build(defaultShader, Shader.U_COLOR, color));
		shaderParams.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(defaultShader, Shader.U_MVPMATRIX));

		vbo = renderer.createVbo(defaultShaderName, vertices.length);
		useShader(vbo, shaderParams);	
	}
	
	@Override
	public void useShader(Vbo vbo, HashMap<String, ParamSetter> params){
		if (id != null){
			renderer.releaseId(id);
		}

		id = renderer.generateShaderParams(vbo);
		
		for (Map.Entry<String, ParamSetter> kv : params.entrySet()){
			id.addParam(kv.getKey(), kv.getValue());
		}
		refreshVertexData();
	}
	
	public ShaderParams getShaderParams(){
		return id;
	}
	
	public void setShaderParams(ShaderParams params){
		if (id != null){
			renderer.releaseId(id);
		}
		id = params;
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
		renderer.render(id, renderMode);
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
	
	public void refreshVertexData(){
		renderer.setPrimitiveData(id, vertices, color);
	}
	
	// TODO: implement
	@Override
	public void updateVertices(Vector2 pos, Vector2 scale, float rotation){
		refreshVertexData();
	}
}
