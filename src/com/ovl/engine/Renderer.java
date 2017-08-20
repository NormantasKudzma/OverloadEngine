package com.ovl.engine;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.ovl.graphics.Color;
import com.ovl.graphics.FontBuilder;
import com.ovl.graphics.TextureLoader;
import com.ovl.utils.Pair;
import com.ovl.utils.Vector2;

public abstract class Renderer {
	public enum PrimitiveType {
		Lines(0),
		LineStrip(1),
		LineLoop(2),
		Polygon(3),
		Points(4),
		Quads(5),
		QuadStrip(6),
		Triangles(7),
		TriangleStrip(8),
		TriangleFan(9);
		
		private PrimitiveType(int i){
			index = i;
		}
		
		public final int getIndex(){
			return index;
		}
		
		private int index;
	}
	
	public enum VboType {
		Textured,
		Primitive
	}
	
	public static final int BYTES_PER_FLOAT = 4;
	public static final int DATA_PER_VERTEX = 4;	//xy, uv - not necessarilly in that order
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int DATA_PER_SPRITE = VERTICES_PER_SPRITE * DATA_PER_VERTEX;
	public static final int DATA_PER_PRIMITIVE = 2; //xy - not necessarilly in that order
	
	public static final int SHADER_TEXTURE = 0;
	public static final int SHADER_PRIMITIVE = 1;
	public static final int SHADER_COUNT = 2;
	
	protected final int primitiveModes[] = new int[PrimitiveType.values().length];
	
	protected ArrayList<Vbo> vbos = new ArrayList<Vbo>();
	protected Vbo textureVbo;
	protected Vbo boundVbo;
	
	protected HashMap<Class<?>, Pair<ParamSetter.Builder<?>, Object>> paramSetterBuilders = new HashMap<Class<?>, Pair<ParamSetter.Builder<?>, Object>>();
	protected HashMap<String, Shader> shaders = new HashMap<String, Shader>();
	protected Shader activeShader = null;
	
	protected FontBuilder fontBuilder;
	protected TextureLoader textureLoader;
	
	public abstract void init();
	
	public FontBuilder getFontBuilder(){
		return fontBuilder;
	}
	
	public TextureLoader getTextureLoader(){
		return textureLoader;
	}
	
	public abstract Shader createShader(String name);
	
	protected abstract int compileShader(int type, String shaderCode);
	
	public abstract void postRender();
	
	public abstract void preRender();
	
	public abstract void render(ShaderParams vboId, PrimitiveType mode);
	
	public abstract void render(ShaderParams vboId, PrimitiveType mode, int offset, int count);
	
	public abstract void renderIndexed(ShaderParams vboId, PrimitiveType mode, ByteBuffer indices, int count);
	
	public void setTextureData(ShaderParams vboId, Vector2 tl, Vector2 br){
		Vbo vbo = vboId.vbo;
		
		if (tl == null || br == null || vboId.index < 0 || vboId.index >= vbo.getSize()){
			return;
		}
		
		vbo.setModified(true);
		
		int offset = vboId.index * vbo.getObjectSize();
		vbo.getVbo().put(2 + offset, tl.x).put(3 + offset, br.y)
			.put(6 + offset, tl.x).put(7 + offset, tl.y)
			.put(10 + offset, br.x).put(11 + offset, br.y)
			.put(14 + offset, br.x).put(15 + offset, tl.y);
	}
	
	// TODO: implement rotation
	public void setVertexData(ShaderParams vboId, Vector2[] v){
		Vbo vbo = vboId.vbo;
		
		if (v == null || vboId.index < 0 || vboId.index >= vbo.getSize()){
			return;
		}
		
		vbo.setModified(true);
		
		int offset = vboId.index * vbo.getObjectSize();
		vbo.getVbo().put(offset + 0, v[0].x).put(offset + 1, v[0].y)
					.put(offset + 4, v[1].x).put(offset + 5, v[1].y)
					.put(offset + 8, v[2].x).put(offset + 9, v[2].y)
					.put(offset + 12, v[3].x).put(offset + 13, v[3].y);
	}
	
	// TODO: refactor, so all kind of vbos use same methods
	// will probably need some kind of vertex configuration
	public void setPrimitiveData(ShaderParams vboId, Vector2[] vertices, Color c){
		Vbo vbo = vboId.vbo;
		
		if (vertices == null || vertices.length != vbo.getVertexCount() 
			|| vboId.index < 0 || vboId.index >= vbo.getSize()){
			return;
		}
		
		vbo.setModified(true);
		
		int offset = vboId.index * vbo.getObjectSize();
		for (int i = 0; i < vertices.length; ++i){
			vbo.getVbo()
				.put(offset + 0, vertices[i].x)
				.put(offset + 1, vertices[i].y);
			
			// Move offset by attribute count to put i-th vertex data
			offset += DATA_PER_PRIMITIVE;
		}
	}
	
	public ShaderParams generateShaderParams(Vbo vbo){
		return new ShaderParams(vbo, vbo.generateId());
	}
	
	public Vbo createVbo(String shaderName, int vertsPerObject){
		return createVbo(shaderName, -1, vertsPerObject);
	}
	
	public Vbo createVbo(String shaderName, int initialSize, int vertsPerObject){
		Shader shader = shaders.get(shaderName);
		
		if (shader != null){
			Vbo vbo = new Vbo(shader, initialSize, vertsPerObject, BYTES_PER_FLOAT);
			initVbo(vbo);
			return vbo;
		}
		
		return null;
	}

	public void releaseId(ShaderParams vboId){
		vboId.getVbo().releaseId(vboId.index);
	}
	
	public HashMap<Class<?>, Pair<ParamSetter.Builder<?>, Object>> getParamSetterBuilders(){
		return paramSetterBuilders;
	}
	
	protected abstract void initVbo(Vbo vbo);
	
	public abstract void deleteVbo(ShaderParams vboId);
}
