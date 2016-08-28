package com.ovl.engine.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ovl.engine.Renderer;
import com.ovl.graphics.android.FontBuilderAndroid;
import com.ovl.graphics.android.TextureLoaderAndroid;
import com.ovl.utils.Vector2;
import com.ovl.utils.android.Log;

public final class RendererAndroid extends Renderer {
	private float mvpMatrix[] = new float[16];
	private float renderMatrix[] = new float[16];
	
	public RendererAndroid() {
		vbo = ByteBuffer.allocateDirect(bufferSize * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

		IntBuffer buffer = IntBuffer.allocate(1);
		GLES20.glGenBuffers(1, buffer);
		vboId = buffer.get(0);

		vertShaderId = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		fragShaderId = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		programId = GLES20.glCreateProgram();
		GLES20.glAttachShader(programId, vertShaderId);
		GLES20.glAttachShader(programId, fragShaderId);
		GLES20.glLinkProgram(programId);

		GLES20.glUseProgram(programId);
		loadProgramInfo();

		positionHandle = attributeMap.get("a_Position");
		colorHandle = attributeMap.get("a_Color");
		texCoordHandle = attributeMap.get("a_TexCoordinate");
		
		mvpMatrixHandle = uniformMap.get("u_MVPMatrix");
		texHandle = uniformMap.get("u_Texture");

		textureLoader = new TextureLoaderAndroid();
		fontBuilder = new FontBuilderAndroid();
	}

	@Override
	public void init() {
		GLES20.glEnable(GLES20.GL_TEXTURE_2D);
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		//GLES11.glDisable(GLES11.GL_LIGHTING);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
	
		Matrix.setLookAtM(mvpMatrix, 0, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, -5.0f, 0.0f, 1.0f, 0.0f);
		Matrix.translateM(mvpMatrix, 0, -1.0f, -1.0f, 0.0f);
	}

	protected void loadProgramInfo(){
		Log.w("------------\nProgram info\n------------");
		
		String infoLog = GLES20.glGetProgramInfoLog(programId);
		Log.w(infoLog);
		
		int ib[] = new int[1];
		GLES20.glGetProgramiv(programId, GLES20.GL_ACTIVE_ATTRIBUTES, ib, 0);
		int count = ib[0];
		
		// Attributes
		Log.w(String.format("%-20s%-20s%-20s\n", "Attribute name", "Index", "Location"));
		int length[] = new int[1];
		int size[] = new int[1];
		int type[] = new int[1];
		byte name[] = new byte[256];
		for (int i = 0; i < count; ++i){
			GLES20.glGetActiveAttrib(programId, i, 256, length, 0, size, 0, type, 0, name, 0);
			String attrib = new String(name, 0, length[0]);
			int loc = GLES20.glGetAttribLocation(programId, attrib);
			attributeMap.put(attrib, loc);
			Log.w(String.format("%-20s%-20d%-20d\n", attrib, i, loc));
		}
		
		GLES20.glGetProgramiv(programId, GLES20.GL_ACTIVE_UNIFORMS, ib, 0);
		count = ib[0];
		
		// Uniforms
		Log.w("------------");
		Log.w(String.format("%-20s%-20s%-20s\n", "Uniform name", "Index", "Location"));
		for (int i = 0; i < count; ++i){
			GLES20.glGetActiveUniform(programId, i, 256, length, 0, size, 0, type, 0, name, 0);
			name[length[0]] = '\0';
			String uniform = new String(name, 0, length[0]);
			int loc = GLES20.glGetUniformLocation(programId, uniform);
			uniformMap.put(uniform, loc);
			Log.w(String.format("%-20s%-20d%-20d\n", uniform, i, loc));
		}
		Log.w("------------");
	}
	
	protected int loadShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);

		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}
	
	@Override
	public void postRender() {
		// Disable vertex array
		GLES20.glDisableVertexAttribArray(positionHandle);
		GLES20.glDisableVertexAttribArray(colorHandle);
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void preRender() {
		if (isModified) {
			vbo.rewind();
			GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
			GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, bufferSize * BYTES_PER_FLOAT, vbo, GLES20.GL_STATIC_DRAW);
		}
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		GLES20.glUseProgram(programId);
		
		// Position
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
		GLES20.glVertexAttribPointer(positionHandle, 2, GLES20.GL_FLOAT, false, DATA_PER_SPRITE, 0);
		GLES20.glEnableVertexAttribArray(positionHandle);
		
		// Texture
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
		GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, DATA_PER_SPRITE, 8);
		GLES20.glEnableVertexAttribArray(texCoordHandle);

		// Color
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
		GLES20.glVertexAttribPointer(colorHandle, 4, GLES20.GL_FLOAT, false, DATA_PER_SPRITE, 16);
		GLES20.glEnableVertexAttribArray(colorHandle);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	}

	@Override
	public void render(int id, Vector2 size, Vector2 position, Vector2 scale, float rotation) {		
		Matrix.setIdentityM(renderMatrix, 0);

		Matrix.translateM(renderMatrix, 0, -size.x, -size.y, 0.0f);
		Matrix.translateM(renderMatrix, 0, position.x, position.y, 0.0f);
		Matrix.scaleM(renderMatrix, 0, scale.x, scale.y, 1.0f);
		
		// android gets no rotation either, kek kek
		Matrix.multiplyMM(renderMatrix, 0, mvpMatrix, 0, renderMatrix, 0);
		
		GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false, renderMatrix, 0);		
		GLES20.glUniform1i(texHandle, 0);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, id * VERTICES_PER_SPRITE, VERTICES_PER_SPRITE);
	}
}
