package com.ovl.engine.pc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.apache.commons.lang3.mutable.MutableFloat;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.vector.Matrix4f;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.ShaderParams;
import com.ovl.engine.Vbo;
import com.ovl.graphics.Color;
import com.ovl.graphics.Texture;
import com.ovl.graphics.pc.FontBuilderPc;
import com.ovl.graphics.pc.TextureLoaderPc;
import com.ovl.graphics.pc.TexturePc;
import com.ovl.utils.Pair;
import com.ovl.utils.Vector2;

public final class RendererPc extends Renderer {
	private FloatBuffer mvpMatrix;
	
	public RendererPc(){
		mvpMatrix = ByteBuffer.allocateDirect(16 * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();		
		textureLoader = new TextureLoaderPc();
		fontBuilder = new FontBuilderPc();
		
		prepareMvpMatrix();
		
		primitiveModes[PrimitiveType.Lines.getIndex()] = GL11.GL_LINES;
		primitiveModes[PrimitiveType.LineLoop.getIndex()] = GL11.GL_LINE_LOOP;
		primitiveModes[PrimitiveType.LineStrip.getIndex()] = GL11.GL_LINE_STRIP;
		primitiveModes[PrimitiveType.Polygon.getIndex()] = GL11.GL_POLYGON;
		primitiveModes[PrimitiveType.Points.getIndex()] = GL11.GL_POINT;
		primitiveModes[PrimitiveType.QuadStrip.getIndex()] = GL11.GL_QUAD_STRIP;
		primitiveModes[PrimitiveType.Quads.getIndex()] = GL11.GL_QUADS;
		primitiveModes[PrimitiveType.TriangleFan.getIndex()] = GL11.GL_TRIANGLE_FAN;
		primitiveModes[PrimitiveType.TriangleStrip.getIndex()] = GL11.GL_TRIANGLE_STRIP;
		primitiveModes[PrimitiveType.Triangles.getIndex()] = GL11.GL_TRIANGLES;
		
		paramSetterBuilders.put(Color.class, new Pair<ParamSetter.Builder<?>, Object>(new ColorParamSetter.Builder(), Color.WHITE));
		paramSetterBuilders.put(mvpMatrix.getClass(), new Pair<ParamSetter.Builder<?>, Object>(new MatrixParamSetter.Builder(), mvpMatrix));
		paramSetterBuilders.put(Texture.class, new Pair<ParamSetter.Builder<?>, Object>(new TextureParamSetter.Builder(), ((Texture)new TexturePc())));
		paramSetterBuilders.put(TexturePc.class, new Pair<ParamSetter.Builder<?>, Object>(new TextureParamSetter.Builder(), ((Texture)new TexturePc())));
		paramSetterBuilders.put(MutableFloat.class, new Pair<ParamSetter.Builder<?>, Object>(new MutableFloatParamSetter.Builder(), new MutableFloat(0.0f)));
		paramSetterBuilders.put(Float.class, new Pair<ParamSetter.Builder<?>, Object>(new FloatParamSetter.Builder(), new Float(0.0f)));
	}
	
	public Shader createShader(String name){
		try {
			Shader shader = new Shader(name);
			shader.setVSId(compileShader(GL20.GL_VERTEX_SHADER, shader.getVSCode()));
			shader.setFSId(compileShader(GL20.GL_FRAGMENT_SHADER, shader.getPSCode()));
			shader.setProgramId(GL20.glCreateProgram());
			
			GL20.glAttachShader(shader.getProgramId(), shader.getVSId());
			GL20.glAttachShader(shader.getProgramId(), shader.getFSId());
			GL20.glLinkProgram(shader.getProgramId());
			Util.checkGLError();
			
			loadProgramInfo(shader);
			shaders.put(name, shader);
			
			return shader;
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public void init(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(0.75f, 0.25f, 0.4f, 1.0f);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
	}

	protected int compileShader(int type, String shaderCode) {
		int shader = GL20.glCreateShader(type);

		GL20.glShaderSource(shader, shaderCode);
		GL20.glCompileShader(shader);
		boolean isCompiled = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) != GL11.GL_FALSE;

		if (!isCompiled){
			String shaderInfoLog = GL20.glGetShaderInfoLog(shader, 2048);
			System.out.println("Shader compilation error. More info:\n" + shaderInfoLog);	
		}
		
		return shader;
	}
	
	protected void cleanupShader(Shader shader){
		for (Shader.Handle handle : shader.getAttributes()){
			GL20.glDisableVertexAttribArray(handle.id);
		}
	}
	
	protected void prepareShader(Shader shader){
		GL20.glUseProgram(shader.getProgramId());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, boundVbo.getId());
		
		for (Shader.Attribute a : shader.getAttributes()){
			GL20.glVertexAttribPointer(a.id, a.size, GL11.GL_FLOAT, false, boundVbo.getStride(), a.offset);
			GL20.glEnableVertexAttribArray(a.id);
		}
	}
	
	public void postRender(){
		// Disable vertex array
		/*if (activeShader != null){
			cleanupShader(activeShader);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);*/
	}
	
	protected void loadProgramInfo(Shader shader){
		int programId = shader.getProgramId();
		GL20.glUseProgram(programId);
		System.out.println("------------\nProgram info\n------------");
		
		String infoLog = GL20.glGetProgramInfoLog(programId, 2048);
		System.out.println(infoLog);
		
		int glErr = GL11.glGetError();
		if (glErr != GL11.GL_NO_ERROR){
			System.err.println("LoadProgramInfo error " + glErr);
			return;
		}

		IntBuffer lengthBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		IntBuffer typeBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		IntBuffer sizeBuf = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		
		final int maxNameLength = 256;
		ByteBuffer nameBuf = ByteBuffer.allocateDirect(256).order(ByteOrder.nativeOrder());
		byte nameArr[] = new byte[maxNameLength];
		
		int count = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_ATTRIBUTES);
		int byteOffset = 0;
		
		// Attributes
		System.out.printf("%-20s%-20s%-20s\n", "Attribute name", "Index", "Location");
		for (int i = 0; i < count; ++i){
			lengthBuf.clear();
			sizeBuf.clear();
			typeBuf.clear();
			nameBuf.clear();
			GL20.glGetActiveAttrib(programId, i, lengthBuf, sizeBuf, typeBuf, nameBuf);
			lengthBuf.rewind();
			sizeBuf.rewind();
			typeBuf.rewind();
			nameBuf.rewind();
			nameBuf.get(nameArr);
			String attrib = new String(nameArr, 0, lengthBuf.get());
			int loc = GL20.glGetAttribLocation(programId, attrib);
			int type = typeBuf.get();
			int size = sizeBuf.get() * getSizeInFloats(type);
			shader.addAttribute(attrib, loc, size, byteOffset);
			byteOffset += size * BYTES_PER_FLOAT;
			System.out.printf("%-20s%-20d%-20d\n", attrib, i, loc);
		}
		
		count = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_UNIFORMS);
		
		// Uniforms
		System.out.println("------------");
		System.out.printf("%-20s%-20s%-20s\n", "Uniform name", "Index", "Location");
		for (int i = 0; i < count; ++i){
			lengthBuf.clear();
			sizeBuf.clear();
			typeBuf.clear();
			nameBuf.clear();
			GL20.glGetActiveUniform(programId, i, lengthBuf, sizeBuf, typeBuf, nameBuf);
			lengthBuf.rewind();
			sizeBuf.rewind();
			typeBuf.rewind();
			nameBuf.rewind();
			nameBuf.get(nameArr);
			String uniform = new String(nameArr, 0, lengthBuf.get());
			int loc = GL20.glGetUniformLocation(programId, uniform);
			shader.addUniform(uniform, loc, glTypeToOvl(typeBuf.get()));
			System.out.printf("%-20s%-20d%-20d\n", uniform, i, loc);
		}
		System.out.println("------------");
	}
	
	// Converts opengl type to ovl type
	private Class<?> glTypeToOvl(int glType){
		switch (glType){
			case GL11.GL_FLOAT:{
				return Float.class;
			}
			case GL11.GL_INT:{
				return Integer.class;
			}
			case GL20.GL_SAMPLER_2D:{
				return Texture.class;
			}
			case GL20.GL_FLOAT_VEC2:{
				return Vector2.class;
			}
			case GL20.GL_FLOAT_VEC4:{
				return Color.class;	// TODO: fixplz
			}
			case GL20.GL_FLOAT_MAT4:{
				return mvpMatrix.getClass();
			}
		}
		
		return null;
	}
	
	// Returns opengl type size in floats
	private int getSizeInFloats(int glType){
		switch (glType){
			case GL20.GL_FLOAT_VEC2:{
				return 2;
			}
			case GL20.GL_FLOAT_VEC3:{
				return 3;
			}
			case GL20.GL_FLOAT_VEC4:{
				return 4;
			}
			case GL20.GL_FLOAT_MAT2:{
				return 4;
			}
			case GL20.GL_FLOAT_MAT3:{
				return 9;
			}
			case GL20.GL_FLOAT_MAT4:{
				return 16;
			}
		}
		
		System.err.println("Unknown gl type " + glType);
		return 0;
	}
	
	public void preRender(){
		for (Vbo vbo : vbos){
			if (vbo.isModified()){
				vbo.setModified(false);
				vbo.getVbo().rewind();
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo.getId());
				GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo.getVbo(), GL15.GL_STATIC_DRAW);
			}
		}
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}
	
	protected void prepareMvpMatrix(){
		Matrix4f mvp = new Matrix4f();
		mvp.m00 = 2.0f / OverloadEngine.getInstance().aspectRatio;
				
		mvpMatrix.clear();
		mvp.store(mvpMatrix);
		mvpMatrix.rewind();
	}
	
	public void render(ShaderParams vboId, PrimitiveType mode){
		if (boundVbo != vboId.getVbo())
		{
			boundVbo = vboId.getVbo();
			activeShader = boundVbo.getShader();
			prepareShader(activeShader);
		}
		
		for (ParamSetter paramSetter : vboId.getParams().values()){
			paramSetter.setParam();
		}
		
		GL11.glDrawArrays(primitiveModes[mode.getIndex()], vboId.getIndex() * boundVbo.getVertexCount(), boundVbo.getVertexCount());
	}

	public void deleteVbo(ShaderParams vboId){
		Vbo vbo = vboId.getVbo();
		GL15.glDeleteBuffers(vbo.getId());
		vbos.remove(vbo);
	}
	
	protected void initVbo(Vbo vbo){
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers(buffer);
		vbo.setId(buffer.get(0));
		vbos.add(vbo);
	}
}
