package com.ovl.engine;

import java.util.ArrayList;

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
		protected Vbo vbo;	// vbo id
		protected int id;	// renderable id
		protected boolean isDeletable;
		
		protected VboId(Vbo vbo, int id, boolean isDeletable){
			this.vbo = vbo;
			this.id = id;
			this.isDeletable = isDeletable;
		}
		
		public int getId(){
			return id;
		}
		
		public Vbo getVbo(){
			return vbo;
		}
		
		public boolean isDeletable(){
			return isDeletable;
		}
	}
	
	public static final int BYTES_PER_FLOAT = 4;
	public static final int DATA_PER_VERTEX = 8;	//xy, uv, rgba - not necessarilly in that order
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int DATA_PER_SPRITE = VERTICES_PER_SPRITE * DATA_PER_VERTEX;
	public static final int DATA_PER_PRIMITIVE = 6; //xy, rgba - not necessarilly in that order
	
	public static final int SHADER_TEXTURE = 0;
	public static final int SHADER_PRIMITIVE = 1;
	public static final int SHADER_COUNT = 2;
	
	protected ArrayList<Vbo> vbos = new ArrayList<Vbo>();
	protected Vbo textureVbo;
	protected Vbo boundVbo;
	
	protected Shader shaders[];
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
	
	protected abstract int compileShader(int type, String shaderCode);

	protected void loadProgramInfo(){
		
	}
	
	public abstract void postRender();
	
	public abstract void preRender();
	
	public abstract void renderPrimitive(VboId vboId, PrimitiveType mode, Vector2 vertices[], Vector2 position, Vector2 scale, float rotation);
	
	public abstract void renderTextured(VboId vboId, Vector2 size, Vector2 position, Vector2 scale, float rotation);
	
	public void setColorData(VboId vboId, Color c){
		Vbo vbo = vboId.vbo;
		
		if (c == null || vboId.id < 0 || vboId.id >= vbo.getSize()){
			return;
		}
		
		vbo.setModified(true);
		
		float[] rgba = c.getRgba();
		int offset = vboId.id * vbo.getStride();
		for (int i = 4 + offset; i < 32 + offset; i += 8){
			vbo.getVbo().put(i, rgba[0]).put(i + 1, rgba[1]).put(i + 2, rgba[2]).put(i + 3, rgba[3]);
		}
	}
	
	public void setTextureData(VboId vboId, Vector2 tl, Vector2 br){
		Vbo vbo = vboId.vbo;
		
		if (tl == null || br == null || vboId.id < 0 || vboId.id >= vbo.getSize()){
			return;
		}
		
		vbo.setModified(true);
		
		int offset = vboId.id * vbo.getStride();
		vbo.getVbo().put(2 + offset, tl.x).put(3 + offset, br.y)
			.put(10 + offset, tl.x).put(11 + offset, tl.y)
			.put(18 + offset, br.x).put(19 + offset, br.y)
			.put(26 + offset, br.x).put(27 + offset, tl.y);
	}
	
	public void setVertexData(VboId vboId, Vector2 pos){
		Vbo vbo = vboId.vbo;
		
		if (pos == null || vboId.id < 0 || vboId.id >= vbo.getSize()){
			return;
		}
		
		vbo.setModified(true);
		
		int offset = vboId.id * vbo.getStride();
		vbo.getVbo().put(offset + 9, pos.y)
			.put(offset + 16, pos.x)
			.put(offset + 24, pos.x)
			.put(offset + 25, pos.y);
	}
	
	public VboId generateId(VboType vboType, int vertexCount){
		switch (vboType)
		{
			case Textured:{
				return new VboId(textureVbo, textureVbo.generateId(), false);	
			}
			case Primitive:{
				int size = DATA_PER_PRIMITIVE * vertexCount;
				Vbo vbo = new Vbo(size, DATA_PER_PRIMITIVE, vertexCount, BYTES_PER_FLOAT);
				initVbo(vbo);
				return new VboId(vbo, vbo.generateId(), true);
			}
			default:{
				return null;
			}
		}	
	}

	public void releaseId(VboId vboId){
		vboId.getVbo().releaseId(vboId.id);
	}
	
	protected abstract void initVbo(Vbo vbo);
	
	public abstract void deleteVbo(VboId vboId);
}
