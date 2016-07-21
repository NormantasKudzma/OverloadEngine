package engine;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import graphics.Color;
import graphics.FontBuilder;
import graphics.Texture;
import graphics.TextureLoader;
import utils.Vector2;

public abstract class Renderer {
	public static final int BYTES_PER_FLOAT = 4;
	public static final int DATA_PER_VERTEX = 8;
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int DATA_PER_SPRITE = VERTICES_PER_SPRITE * DATA_PER_VERTEX;
	
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
			FloatBuffer newBuffer = FloatBuffer.allocate(bufferSize);
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
	
	public abstract void postRender();
	
	public abstract void preRender();
	
	public void releaseId(int id){
		releasedIds.add(id);
	}
	
	public abstract void render(int id, Texture tex, Vector2 size, Vector2 position, Vector2 scale, float rotation);
	
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
	
	public void setVertexData(int id, Vector2 pos){
		if (pos == null || id < 0 || id >= nextSpriteId){
			return;
		}
		
		isModified = true;
		
		int offset = id * DATA_PER_SPRITE;
		vbo.put(8 + offset, pos.x)
			.put(16 + offset, pos.x)
			.put(17 + offset, pos.y)
			.put(25 + offset, pos.y);
	}
	
	public void setTextureData(int id, Vector2 tl, Vector2 br){
		if (tl == null || br == null || id < 0 || id >= nextSpriteId){
			return;
		}
	
		isModified = true;
		
		int offset = id * DATA_PER_SPRITE;
		vbo.put(2 + offset, tl.x).put(3 + offset, tl.y)
			.put(10 + offset, br.x).put(11 + offset, tl.y)
			.put(18 + offset, br.x).put(19 + offset, br.y)
			.put(26 + offset, tl.x).put(27 + offset, br.y);
	}
}
