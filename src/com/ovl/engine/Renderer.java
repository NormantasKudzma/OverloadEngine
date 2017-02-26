package com.ovl.engine;

import java.util.ArrayList;
import java.util.HashMap;

import com.ovl.graphics.Color;
import com.ovl.graphics.FontBuilder;
import com.ovl.graphics.TextureLoader;
import com.ovl.utils.Vector2;

public abstract class Renderer {
	public enum PrimitiveType {
		Lines,
		LineStrip,
		LineLoop,
		Polygon,
		Points,
		Quads,
		QuadStrip,
		Triangles,
		TriangleStrip,
		TriangleFan
	}
	
	public enum VboType {
		Textured,
		Primitive
	}
	
	public class VboId {
		protected Vbo vbo;
		protected int index;			// renderable index within vbo
		protected boolean isDeletable; // can be deleted with deleteVbo call? if false - it is owned by renderer
		
		protected VboId(Vbo vbo, int index, boolean isDeletable){
			this.vbo = vbo;
			this.index = index;
			this.isDeletable = isDeletable;
		}
		
		public int getIndex(){
			return index;
		}
		
		public Vbo getVbo(){
			return vbo;
		}
		
		public boolean isDeletable(){
			return isDeletable;
		}
	}
	
	public static final int BYTES_PER_FLOAT = 4;
	public static final int DATA_PER_VERTEX = 4;	//xy, uv - not necessarilly in that order
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int DATA_PER_SPRITE = VERTICES_PER_SPRITE * DATA_PER_VERTEX;
	public static final int DATA_PER_PRIMITIVE = 2; //xy - not necessarilly in that order
	
	public static final int SHADER_TEXTURE = 0;
	public static final int SHADER_PRIMITIVE = 1;
	public static final int SHADER_COUNT = 2;
	
	protected ArrayList<Vbo> vbos = new ArrayList<Vbo>();
	protected Vbo textureVbo;
	protected Vbo boundVbo;
	
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
	
	public abstract void renderPrimitive(VboId vboId, PrimitiveType mode, Vector2 vertices[], Color c, Vector2 position, Vector2 scale, float rotation);
	
	public abstract void renderTextured(VboId vboId, Color c, Vector2 size, Vector2 position, Vector2 scale, float rotation);
	
	public void setTextureData(VboId vboId, Vector2 tl, Vector2 br){
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
	
	public void setVertexData(VboId vboId, Vector2 pos){
		Vbo vbo = vboId.vbo;
		
		if (pos == null || vboId.index < 0 || vboId.index >= vbo.getSize()){
			return;
		}
		
		vbo.setModified(true);
		
		int offset = vboId.index * vbo.getObjectSize();
		vbo.getVbo().put(offset + 5, pos.y)
			.put(offset + 8, pos.x)
			.put(offset + 12, pos.x)
			.put(offset + 13, pos.y);
	}
	
	// TODO: refactor, so all kind of vbos use same methods
	// will probably need some kind of vertex configuration
	public void setPrimitiveData(VboId vboId, Vector2[] vertices, Color c){
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
	
	public VboId generateId(String shaderName, int vertsPerObject){
		Shader shader = shaders.get(shaderName);
		if (shader == null){
			return null;
		}
		
		for (Vbo vbo : vbos){
			if (vbo.getShader() == shader && vertsPerObject == vbo.getVertexCount()){
				return new VboId(vbo, vbo.generateId(), false);
			}
		}
		
		return null;
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

	public void releaseId(VboId vboId){
		vboId.getVbo().releaseId(vboId.index);
	}
	
	protected abstract void initVbo(Vbo vbo);
	
	public abstract void deleteVbo(VboId vboId);
}
