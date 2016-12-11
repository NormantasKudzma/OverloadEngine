package com.ovl.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import com.ovl.graphics.Color;
import com.ovl.graphics.FontBuilder;
import com.ovl.graphics.TextureLoader;
import com.ovl.utils.Vector2;

public abstract class Renderer {
	public static final int BYTES_PER_FLOAT = 4;
	public static final int DATA_PER_VERTEX = 8;
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int DATA_PER_SPRITE = VERTICES_PER_SPRITE * DATA_PER_VERTEX;
	
	public static final int SHADER_TEXTURE = 0;
	public static final int SHADER_PRIMITIVE = 1;
	public static final int SHADER_COUNT = 2;
	
	protected Shader shaders[];
	protected Shader activeShader = null;
	
	protected int bufferSize = 4096;	
	protected int nextSpriteId = 0;
	protected int vboId;
	protected boolean isModified = false;
	protected FloatBuffer vbo;
	protected ArrayList<Integer> releasedIds = new ArrayList<Integer>();
	
	protected FontBuilder fontBuilder;
	protected TextureLoader textureLoader;
	
	public abstract void init();
	
	public int genSpriteId(){
		if (nextSpriteId >= bufferSize / DATA_PER_SPRITE){
			bufferSize *= 2;
			vbo.rewind();
			FloatBuffer newBuffer = ByteBuffer.allocateDirect(bufferSize * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
			newBuffer.put(vbo);
			vbo = newBuffer;
			isModified = true;
		}
		
		// If there are any released ids, then return them instead of a new id
		if (releasedIds.size() > 0){
			return releasedIds.remove(releasedIds.size() - 1);
		}

		return nextSpriteId++;
	}
	
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
	
	public void releaseId(int id){
		releasedIds.add(id);
	}
	
	public abstract void renderPrimitive(int id, Vector2 size, Vector2 position, Vector2 scale, float rotation);
	
	public abstract void renderTextured(int id, Vector2 size, Vector2 position, Vector2 scale, float rotation);
	
	public void setColorData(int id, Color c){
		if (c == null || id < 0 || id >= nextSpriteId){
			return;
		}
		
		isModified = true;
		
		float[] rgba = c.getRgba();
		int offset = id * DATA_PER_SPRITE;
		for (int i = 4 + offset; i < 32 + offset; i += 8){
			vbo.put(i, rgba[0]).put(i + 1, rgba[1]).put(i + 2, rgba[2]).put(i + 3, rgba[3]);
		}
	}
	
	public void setTextureData(int id, Vector2 tl, Vector2 br){
		if (tl == null || br == null || id < 0 || id >= nextSpriteId){
			return;
		}
	
		isModified = true;
		
		int offset = id * DATA_PER_SPRITE;
		vbo.put(2 + offset, tl.x).put(3 + offset, br.y)
			.put(10 + offset, tl.x).put(11 + offset, tl.y)
			.put(18 + offset, br.x).put(19 + offset, br.y)
			.put(26 + offset, br.x).put(27 + offset, tl.y);
	}
	
	public void setVertexData(int id, Vector2 pos){
		if (pos == null || id < 0 || id >= nextSpriteId){
			return;
		}
		
		isModified = true;
		
		int offset = id * DATA_PER_SPRITE;
		vbo.put(offset + 9, pos.y)
			.put(offset + 16, pos.x)
			.put(offset + 24, pos.x)
			.put(offset + 25, pos.y);
	}
}
