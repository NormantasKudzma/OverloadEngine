package com.ovl.engine.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
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

		shaders = new Shader[SHADER_COUNT];
		shaders[SHADER_TEXTURE] = new Shader("Texture");
		shaders[SHADER_PRIMITIVE] = new Shader("Primitive");
		
		for (int i = 0; i < SHADER_COUNT; ++i){
			if (shaders[i] == null){
				continue;
			}
			
			shaders[i].setVSId(compileShader(GLES20.GL_VERTEX_SHADER, shaders[i].getVSCode()));
			shaders[i].setFSId(compileShader(GLES20.GL_FRAGMENT_SHADER, shaders[i].getPSCode()));
			shaders[i].setProgramId(GLES20.glCreateProgram());
			
			GLES20.glAttachShader(shaders[i].getProgramId(), shaders[i].getVSId());
			GLES20.glAttachShader(shaders[i].getProgramId(), shaders[i].getFSId());
			GLES20.glLinkProgram(shaders[i].getProgramId());
			//Util.checkGLError();
			
			HashMap<String, Integer> handles = loadProgramInfo(shaders[i].getProgramId());
			
			Shader.Handle handle = null;
			for (int j = 0; j < Shader.HANDLE_COUNT; ++j){
				handle = Shader.HANDLES[j];
				if (handles.containsKey(handle.name)){
					shaders[i].setHandleId(j, handles.get(handle.name));
				}
			}
		}

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

	protected HashMap<String, Integer> loadProgramInfo(int programId){
		HashMap<String, Integer> handles = new HashMap<String, Integer>();
		GLES20.glUseProgram(programId);
		Log.w("------------\nProgram info\n------------");
		
		String infoLog = GLES20.glGetProgramInfoLog(programId);
		Log.w(infoLog);
		
		int ib[] = new int[1];
		GLES20.glGetProgramiv(programId, GLES20.GL_ACTIVE_ATTRIBUTES, ib, 0);
		int count = ib[0];
		
		int length[] = new int[1];
		int size[] = new int[1];
		int type[] = new int[1];
		byte name[] = new byte[256];
		
		// Attributes
		Log.w(String.format("%-20s%-20s%-20s\n", "Attribute name", "Index", "Location"));
		for (int i = 0; i < count; ++i){
			GLES20.glGetActiveAttrib(programId, i, 256, length, 0, size, 0, type, 0, name, 0);
			name[length[0]] = '\0';
			String attrib = new String(name, 0, length[0]);
			int loc = GLES20.glGetAttribLocation(programId, attrib);
			handles.put(attrib, loc);
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
			handles.put(uniform, loc);
			Log.w(String.format("%-20s%-20d%-20d\n", uniform, i, loc));
		}
		Log.w("------------");
		
		return handles;
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
		if (activeShader != null){
			cleanupShader(activeShader);
		}
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
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		
		if (activeShader != null){
			prepareShader(activeShader);
		}		
	}
	
	protected void prepareShader(Shader shader){
		Shader.Handle handle = null;
		GLES20.glUseProgram(shader.getProgramId());
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);
		for (int i = 0; i < Shader.HANDLE_COUNT; ++i){
			handle = Shader.HANDLES[i];
			if (shader.getHandleId(i) != -1 && handle.size != -1){
				GLES20.glVertexAttribPointer(activeShader.getHandleId(i), handle.size, GLES20.GL_FLOAT, false, DATA_PER_SPRITE, handle.offset);
				GLES20.glEnableVertexAttribArray(activeShader.getHandleId(i));
			}
		}
	}
	
	protected void cleanupShader(Shader shader){
		for (int i = 0; i < Shader.HANDLE_COUNT; ++i){
			if (shader.getHandleId(i) != -1){
				GLES20.glDisableVertexAttribArray(shader.getHandleId(i));
			}
		}
	}

	@Override
	public void renderTextured(int id, Vector2 size, Vector2 position, Vector2 scale, float rotation) {		
		if (activeShader != shaders[SHADER_TEXTURE]){
			if (activeShader != null){
				cleanupShader(activeShader);
			}			
			activeShader = shaders[SHADER_TEXTURE];
			prepareShader(activeShader);
		}
		
		Matrix.setIdentityM(renderMatrix, 0);

		Matrix.translateM(renderMatrix, 0, -size.x, -size.y, 0.0f);
		Matrix.translateM(renderMatrix, 0, position.x, position.y, 0.0f);
		Matrix.scaleM(renderMatrix, 0, scale.x, scale.y, 1.0f);
		
		// android gets no rotation either, kek kek
		Matrix.multiplyMM(renderMatrix, 0, mvpMatrix, 0, renderMatrix, 0);
		
		GLES20.glUniformMatrix4fv(activeShader.getHandleId(Shader.HANDLE_MVPMATRIX), 1, false, renderMatrix, 0);		
		GLES20.glUniform1i(activeShader.getHandleId(Shader.HANDLE_TEX), 0);
		
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, id * VERTICES_PER_SPRITE, VERTICES_PER_SPRITE);
	}
	
	@Override
	public void renderPrimitive(PrimitiveRenderMode mode, Vector2[] vertices, Vector2 size, Vector2 position, Vector2 scale, float rotation) {
		// not yet
	}

	@Override
	protected int compileShader(int type, String shaderCode) {
		int shader = GLES20.glCreateShader(type);

		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);
		
		int result[] = new int[1];
		GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, result, 0);
		boolean isCompiled = result[0] != GLES20.GL_FALSE;

		if (!isCompiled){
			String shaderInfoLog = GLES20.glGetShaderInfoLog(shader);
			Log.w("Shader compilation error. More info:\n" + shaderInfoLog);	
		}
		
		return shader;
	}
}
