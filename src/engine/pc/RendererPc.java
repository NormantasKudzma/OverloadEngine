package engine.pc;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import utils.Vector2;
import engine.OverloadEngine;
import engine.Renderer;
import graphics.Color;
import graphics.Texture;
import graphics.TextureLoader;
import graphics.pc.TextureLoaderPc;

public final class RendererPc implements Renderer {	
	public static final int DATA_PER_VERTEX = 8;
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int DATA_PER_SPRITE = VERTICES_PER_SPRITE * DATA_PER_VERTEX;
	
	private int bufferSize = 4096;	
	private int nextSpriteId = 0;
	private int vboId = GL15.glGenBuffers();
	private boolean isModified = false;
	private FloatBuffer vbo = BufferUtils.createFloatBuffer(bufferSize);
	private ArrayList<Integer> releasedIds = new ArrayList<Integer>();
	private TextureLoaderPc textureLoader;
	
	public RendererPc(){
		textureLoader = new TextureLoaderPc();
	}
	
	public void init(){
		OverloadEngine engine = OverloadEngine.getInstance();
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glTranslatef(-1.0f, -1.0f, 0.0f);
		GL11.glViewport(0, 0, engine.frameWidth, engine.frameHeight);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
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
	
	public TextureLoader getTextureLoader(){
		return textureLoader;
	}
	
	public void postRender(){
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
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
		GL11.glColorPointer(4, GL11.GL_FLOAT, 32, 16);

		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);	

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	public void releaseId(int id){
		releasedIds.add(id);
	}
	
	public final void render(int id, Texture tex, Vector2 size, Vector2 position, Vector2 scale, float rotation){
		// store the current model matrix
		GL11.glPushMatrix();
	
		tex.bind();

		GL11.glTranslatef(position.x, position.y, 0);
		GL11.glRotatef(rotation, 0, 0, 1.0f);
		GL11.glTranslatef(-size.x, size.y, 0);
		GL11.glScalef(scale.x, -scale.y, 1.0f);

		GL11.glDrawArrays(GL11.GL_QUADS, id * VERTICES_PER_SPRITE, 4);

        // Restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
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
