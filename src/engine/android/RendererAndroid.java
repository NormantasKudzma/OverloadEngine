package engine.android;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import utils.Vector2;
import android.opengl.GLES11;
import android.opengl.GLES20;
import engine.OverloadEngine;
import engine.Renderer;
import graphics.Texture;
import graphics.android.FontBuilderAndroid;
import graphics.android.TextureLoaderAndroid;

public final class RendererAndroid extends Renderer {
	public RendererAndroid(){
		vbo = FloatBuffer.allocate(bufferSize);
		
		IntBuffer buffer = IntBuffer.allocate(1);
		GLES20.glGenBuffers(1, buffer);
		vboId = buffer.get(0);
		
		textureLoader = new TextureLoaderAndroid();
		fontBuilder = new FontBuilderAndroid();
	}
	
	@Override
	public void init() {
		OverloadEngine engine = OverloadEngine.getInstance();
		
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		GLES11.glMatrixMode(GLES11.GL_MODELVIEW);
		GLES11.glTranslatef(-1.0f, -1.0f, 0.0f);
		GLES20.glViewport(0, 0, engine.frameWidth, engine.frameHeight);
		GLES11.glTexParameteri(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_S, GLES11.GL_CLAMP_TO_EDGE);
		GLES11.glTexParameteri(GLES11.GL_TEXTURE_2D, GLES11.GL_TEXTURE_WRAP_T, GLES11.GL_CLAMP_TO_EDGE);
	}

	@Override
	public void postRender() {
		GLES11.glDisableClientState(GLES11.GL_COLOR_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glDisableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void preRender() {
		if (isModified){
			vbo.rewind();
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bufferSize * BYTES_PER_FLOAT, vbo , GLES20.GL_STATIC_DRAW);
		}
		
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
		GLES11.glVertexPointer(2, GLES11.GL_FLOAT, 32, 0);
		GLES11.glTexCoordPointer(2, GLES11.GL_FLOAT, 32, 8);
		GLES11.glColorPointer(4, GLES11.GL_FLOAT, 32, 16);

		GLES11.glEnableClientState(GLES11.GL_COLOR_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_VERTEX_ARRAY);
		GLES11.glEnableClientState(GLES11.GL_TEXTURE_COORD_ARRAY);	

		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void render(int id, Texture tex, Vector2 size, Vector2 position, Vector2 scale, float rotation) {
		// store the current model matrix
		/*GLES20.glPushMatrix();
	
		tex.bind();

		GLES20.glTranslatef(position.x, position.y, 0);
		GLES20.glRotatef(rotation, 0, 0, 1.0f);
		GLES20.glTranslatef(-size.x, size.y, 0);
		GLES20.glScalef(scale.x, -scale.y, 1.0f);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, id * VERTICES_PER_SPRITE, 4);

        // Restore the model view matrix to prevent contamination
		GLES20.glPopMatrix();*/
	}
}
