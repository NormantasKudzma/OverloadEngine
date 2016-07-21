package engine.pc;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import utils.Vector2;
import engine.OverloadEngine;
import engine.Renderer;
import graphics.Color;
import graphics.Texture;
import graphics.pc.FontBuilderPc;
import graphics.pc.TextureLoaderPc;

public final class RendererPc extends Renderer {	
	public RendererPc(){	
		vbo = BufferUtils.createFloatBuffer(bufferSize);
		vboId = GL15.glGenBuffers();
		
		textureLoader = new TextureLoaderPc();
		fontBuilder = new FontBuilderPc();
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
	
	public void postRender(){
		GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void preRender(){
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
	
	public void render(int id, Texture tex, Vector2 size, Vector2 position, Vector2 scale, float rotation){
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
}
