package graphics;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import utils.Vector2;

public class Renderer {
	private static Renderer INSTANCE = new Renderer();
	
	public static final int DATA_PER_VERTEX = 8;
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int DATA_PER_SPRITE = VERTICES_PER_SPRITE * DATA_PER_VERTEX;
	
	private int bufferSize = 4096;	
	private int nextSpriteId = 0;
	private int vboId = GL15.glGenBuffers();
	private boolean isModified = false;
	private FloatBuffer vbo = BufferUtils.createFloatBuffer(bufferSize);
	private ArrayList<Integer> releasedIds = new ArrayList<Integer>();
	
	private Renderer(){
		
	}
	
	public static Renderer getInstance(){
		return INSTANCE;
	}
	
	public int genSpriteId(){
		if (nextSpriteId >= bufferSize / DATA_PER_SPRITE){
			bufferSize *= 2;
			vbo.rewind();
			FloatBuffer newBuffer = BufferUtils.createFloatBuffer(bufferSize);
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
	
	public void postRender(){
		//GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public final void preRender(){
		if (isModified){
			vbo.rewind();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo, GL15.GL_STATIC_DRAW);
		}
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 32, 0);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 8);
		//GL11.glColorPointer(4, GL11.GL_FLOAT, 32, 16);

		//GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
	}
	
	public void releaseId(int id){
		releasedIds.add(id);
	}
	
	public final void render(int id){
		GL11.glDrawArrays(GL11.GL_QUADS, id * VERTICES_PER_SPRITE, 4);
	}
	
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
	
	public void setVertexPosition(int id, Vector2 pos){
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
	
	public void setTexturePosition(int id, Vector2 tl, Vector2 br){
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
