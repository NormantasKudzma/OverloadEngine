package com.ovl.engine.android;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.ShaderParams;
import com.ovl.engine.Vbo;
import com.ovl.graphics.Color;
import com.ovl.graphics.Texture;
import com.ovl.graphics.android.FontBuilderAndroid;
import com.ovl.graphics.android.TextureAndroid;
import com.ovl.graphics.android.TextureLoaderAndroid;
import com.ovl.utils.MutableFloat;
import com.ovl.utils.Pair;
import com.ovl.utils.Vector2;
import com.ovl.utils.android.Log;

public final class RendererAndroid extends Renderer {
	private MatrixAndroid mvpMatrix = new MatrixAndroid();
	
	public RendererAndroid() {
		textureLoader = new TextureLoaderAndroid();
		fontBuilder = new FontBuilderAndroid();
		
		primitiveModes[PrimitiveType.Lines.getIndex()] = GLES20.GL_LINES;
		primitiveModes[PrimitiveType.LineLoop.getIndex()] = GLES20.GL_LINE_LOOP;
		primitiveModes[PrimitiveType.LineStrip.getIndex()] = GLES20.GL_LINE_STRIP;
		primitiveModes[PrimitiveType.Polygon.getIndex()] = GLES20.GL_TRIANGLE_FAN; // WORKAROUND
		primitiveModes[PrimitiveType.Points.getIndex()] = GLES20.GL_POINTS;
		primitiveModes[PrimitiveType.QuadStrip.getIndex()] = GLES20.GL_TRIANGLE_FAN; // WORKAROUND
		primitiveModes[PrimitiveType.Quads.getIndex()] = GLES20.GL_TRIANGLE_FAN; // WORKAROUND
		primitiveModes[PrimitiveType.TriangleFan.getIndex()] = GLES20.GL_TRIANGLE_FAN;
		primitiveModes[PrimitiveType.TriangleStrip.getIndex()] = GLES20.GL_TRIANGLE_STRIP;
		primitiveModes[PrimitiveType.Triangles.getIndex()] = GLES20.GL_TRIANGLES;
		
		paramSetterBuilders.put(Color.class, new Pair<ParamSetter.Builder<?>, Object>(new ColorParamSetter.Builder(), Color.WHITE));
		paramSetterBuilders.put(Vector2.class, new Pair<ParamSetter.Builder<?>, Object>(new Vector2ParamSetter.Builder(), new Vector2()));
		paramSetterBuilders.put(mvpMatrix.getClass(), new Pair<ParamSetter.Builder<?>, Object>(new MatrixParamSetter.Builder(), mvpMatrix));
		paramSetterBuilders.put(Texture.class, new Pair<ParamSetter.Builder<?>, Object>(new TextureParamSetter.Builder(), ((Texture)new TextureAndroid())));
		paramSetterBuilders.put(TextureAndroid.class, new Pair<ParamSetter.Builder<?>, Object>(new TextureParamSetter.Builder(), new TextureAndroid()));
		paramSetterBuilders.put(MutableFloat.class, new Pair<ParamSetter.Builder<?>, Object>(new FloatParamSetter.BuilderMutable(), new MutableFloat(0.0f)));
		paramSetterBuilders.put(Float.class, new Pair<ParamSetter.Builder<?>, Object>(new FloatParamSetter.BuilderImmutable(), 0.0f));
	}

	@Override
	public void init() {
		GLES20.glDisable(GLES20.GL_DEPTH_TEST);
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glFrontFace(GLES20.GL_CCW);

		OverloadEngine i = OverloadEngine.getInstance();

		Matrix.setIdentityM(mvpMatrix.matrixImpl, 0);
		mvpMatrix.matrixImpl[0] = 2.0f / OverloadEngine.getInstance().aspectRatio;
	}

	public void onSurfaceChanged(int w, int h, float aspect){
		mvpMatrix.matrixImpl[0] = 2.0f / aspect;
	}

	protected void loadProgramInfo(Shader shader){
		int programId = shader.getProgramId();
		GLES20.glUseProgram(programId);
		Log.w("------------\nProgram info\n------------");
		
		String infoLog = GLES20.glGetProgramInfoLog(programId);
		Log.w(infoLog);
		
		int ib[] = new int[1];
		GLES20.glGetProgramiv(programId, GLES20.GL_ACTIVE_ATTRIBUTES, ib, 0);
		int count = ib[0];
		
		int length[] = new int[1];
		int glSize[] = new int[1];
		int glType[] = new int[1];
		byte name[] = new byte[256];
		
		// Attributes
		Log.w(String.format("%-20s%-20s%-20s\n", "Attribute name", "Index", "Location"));
		shader.startAttributeDeclaration(count);
		for (int i = 0; i < count; ++i){
			GLES20.glGetActiveAttrib(programId, i, 256, length, 0, glSize, 0, glType, 0, name, 0);
			name[length[0]] = '\0';
			String attrib = new String(name, 0, length[0]);
			int loc = GLES20.glGetAttribLocation(programId, attrib);
			int size = glSize[0] * getSizeInFloats(glType[0]);
			shader.addAttribute(attrib, loc, size);
			Log.w(String.format("%-20s%-20d%-20d", attrib, i, loc));
		}
		shader.finishAttributeDeclaration();
		
		GLES20.glGetProgramiv(programId, GLES20.GL_ACTIVE_UNIFORMS, ib, 0);
		count = ib[0];
		
		// Uniforms
		Log.w("------------");
		Log.w(String.format("%-20s%-20s%-20s\n", "Uniform name", "Index", "Location"));
		shader.startUniformDeclaration(count);
		for (int i = 0; i < count; ++i){
			GLES20.glGetActiveUniform(programId, i, 256, length, 0, glSize, 0, glType, 0, name, 0);
			name[length[0]] = '\0';
			String uniform = new String(name, 0, length[0]);
			int loc = GLES20.glGetUniformLocation(programId, uniform);
			shader.addUniform(uniform, loc, glTypeToOvl(glType[0]));
			Log.w(String.format("%-20s%-20d%-20d", uniform, i, loc));
		}
		shader.finishUniformDeclaration();
		Log.w("------------");
	}

	// Converts opengl type to ovl type
	private Class<?> glTypeToOvl(int glType){
		switch (glType){
			case GLES20.GL_FLOAT:{
				return Float.class;
			}
			case GLES20.GL_INT:{
				return Integer.class;
			}
			case GLES20.GL_SAMPLER_2D:{
				return Texture.class;
			}
			case GLES20.GL_FLOAT_VEC2:{
				return Vector2.class;
			}
			case GLES20.GL_FLOAT_VEC4:{
				return Color.class;	// TODO: fixplz
			}
			case GLES20.GL_FLOAT_MAT4:{
				return mvpMatrix.getClass();
			}
		}
		
		return null;
	}
	
	// Returns opengl type size in floats
	private int getSizeInFloats(int glType){
		switch (glType){
			case GLES20.GL_FLOAT_VEC2:{
				return 2;
			}
			case GLES20.GL_FLOAT_VEC3:{
				return 3;
			}
			case GLES20.GL_FLOAT_VEC4:{
				return 4;
			}
			case GLES20.GL_FLOAT_MAT2:{
				return 4;
			}
			case GLES20.GL_FLOAT_MAT3:{
				return 9;
			}
			case GLES20.GL_FLOAT_MAT4:{
				return 16;
			}
		}
		
		System.err.println("Unknown gl type " + glType);
		return 0;
	}
	
	public Shader createShader(String name){
		try {
			Shader shader = new Shader(name);
			shader.setVSId(compileShader(GLES20.GL_VERTEX_SHADER, shader.getVSCode()));
			shader.setFSId(compileShader(GLES20.GL_FRAGMENT_SHADER, shader.getPSCode()));
			shader.setProgramId(GLES20.glCreateProgram());
			
			GLES20.glAttachShader(shader.getProgramId(), shader.getVSId());
			GLES20.glAttachShader(shader.getProgramId(), shader.getFSId());
			GLES20.glLinkProgram(shader.getProgramId());
			
			loadProgramInfo(shader);
			shaders.put(name, shader);
			
			return shader;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void postRender() {
		// Disable vertex array
		/*if (activeShader != null){
			cleanupShader(activeShader);
		}
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);*/
	}

	@Override
	public void preRender() {
		for (Vbo vbo : vbos){
			if (vbo.isModified()){
				vbo.setModified(false);
				vbo.getBuffer().rewind();
				GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo.getId());
				GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vbo.getCapacity() * BYTES_PER_FLOAT, vbo.getBuffer(), GLES20.GL_STATIC_DRAW);
			}
		}
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);	
	}
	
	protected void useShader(Shader shader){
		activeShader = shader;
		GLES20.glUseProgram(shader.getProgramId());
	}
	
	protected void bindVbo(Vbo vbo){
		boundVbo = vbo;
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, boundVbo.getId());
		
		for (Shader.Attribute a : activeShader.getAttributes()){
			GLES20.glVertexAttribPointer(a.id, a.size, GLES20.GL_FLOAT, false, boundVbo.getStride(), a.offset);
			GLES20.glEnableVertexAttribArray(a.id);
		}
	}
	
	protected void cleanupShader(Shader shader){
		for (Shader.Handle handle : shader.getAttributes()){
			GLES20.glDisableVertexAttribArray(handle.id);
		}
	}

	private void setParams(ShaderParams vboId){
		if (activeShader != vboId.getVbo().getShader()){
			useShader(vboId.getVbo().getShader());
		}
		
		if (boundVbo != vboId.getVbo()){
			bindVbo(vboId.getVbo());
		}
		
		for (ParamSetter paramSetter : vboId.getParams().values()){
			paramSetter.setParam();
		}
	}
	
	@Override
	public void render(ShaderParams vboId, PrimitiveType mode){
		render(vboId, mode, vboId.getIndex() * vboId.getVbo().getVertexCount(), vboId.getVbo().getVertexCount());
	}
	
	public void render(ShaderParams vboId, PrimitiveType mode, int offset, int count){
		setParams(vboId);
		GLES20.glDrawArrays(primitiveModes[mode.getIndex()], offset, count);
	}
	
	@Override
	public void renderIndexed(ShaderParams vboId, PrimitiveType mode, ByteBuffer indices, int count) {
		setParams(vboId);
		GLES20.glDrawElements(primitiveModes[mode.getIndex()], count, GLES20.GL_UNSIGNED_SHORT, indices);
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
	
	public void deleteVbo(ShaderParams vboId){
		Vbo vbo = vboId.getVbo();
		IntBuffer buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		buffer.put(vbo.getId());
		buffer.rewind();
		GLES20.glDeleteBuffers(1, buffer);
		vbos.remove(vbo);
	}
	
	protected void initVbo(Vbo vbo){
		IntBuffer buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		GLES20.glGenBuffers(1, buffer);
		vbo.setId(buffer.get(0));
		vbos.add(vbo);
	}
}
