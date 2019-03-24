package com.ovl.engine.arm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import com.jogamp.opengl.GL2ES2;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.ShaderParams;
import com.ovl.engine.Vbo;
import com.ovl.graphics.Color;
import com.ovl.graphics.Texture;
import com.ovl.graphics.arm.FontBuilderArm;
import com.ovl.graphics.arm.TextureArm;
import com.ovl.graphics.arm.TextureLoaderArm;
import com.ovl.utils.MutableFloat;
import com.ovl.utils.Pair;
import com.ovl.utils.Vector2;

public final class RendererArm extends Renderer {
	private MatrixArm mvpMatrix = new MatrixArm();
	
	public RendererArm() {
		textureLoader = new TextureLoaderArm();
		fontBuilder = new FontBuilderArm();
		
		primitiveModes[PrimitiveType.Lines.getIndex()] = GL2ES2.GL_LINES;
		primitiveModes[PrimitiveType.LineLoop.getIndex()] = GL2ES2.GL_LINE_LOOP;
		primitiveModes[PrimitiveType.LineStrip.getIndex()] = GL2ES2.GL_LINE_STRIP;
		primitiveModes[PrimitiveType.Polygon.getIndex()] = GL2ES2.GL_TRIANGLE_FAN;
		primitiveModes[PrimitiveType.Points.getIndex()] = GL2ES2.GL_POINTS;
		primitiveModes[PrimitiveType.QuadStrip.getIndex()] = GL2ES2.GL_TRIANGLE_FAN;
		primitiveModes[PrimitiveType.Quads.getIndex()] = GL2ES2.GL_TRIANGLE_FAN;
		primitiveModes[PrimitiveType.TriangleFan.getIndex()] = GL2ES2.GL_TRIANGLE_FAN;
		primitiveModes[PrimitiveType.TriangleStrip.getIndex()] = GL2ES2.GL_TRIANGLE_STRIP;
		primitiveModes[PrimitiveType.Triangles.getIndex()] = GL2ES2.GL_TRIANGLES;
		
		paramSetterBuilders.put(Color.class, new Pair<ParamSetter.Builder<?>, Object>(new ColorParamSetter.Builder(), Color.WHITE));
		paramSetterBuilders.put(Vector2.class, new Pair<ParamSetter.Builder<?>, Object>(new Vector2ParamSetter.Builder(), new Vector2()));
		paramSetterBuilders.put(mvpMatrix.getClass(), new Pair<ParamSetter.Builder<?>, Object>(new MatrixParamSetter.Builder(), mvpMatrix));
		paramSetterBuilders.put(Texture.class, new Pair<ParamSetter.Builder<?>, Object>(new TextureParamSetter.Builder(), ((Texture)new TextureArm())));
		paramSetterBuilders.put(TextureArm.class, new Pair<ParamSetter.Builder<?>, Object>(new TextureParamSetter.Builder(), new TextureArm()));
		paramSetterBuilders.put(MutableFloat.class, new Pair<ParamSetter.Builder<?>, Object>(new FloatParamSetter.BuilderMutable(), new MutableFloat(0.0f)));
		paramSetterBuilders.put(Float.class, new Pair<ParamSetter.Builder<?>, Object>(new FloatParamSetter.BuilderImmutable(), 0.0f));
	}

	@Override
	public void init() {
		OverloadEngineArm.gl.glDisable(GL2ES2.GL_DEPTH_TEST);
		OverloadEngineArm.gl.glEnable(GL2ES2.GL_BLEND);
		OverloadEngineArm.gl.glBlendFunc(GL2ES2.GL_SRC_ALPHA, GL2ES2.GL_ONE_MINUS_SRC_ALPHA);
		OverloadEngineArm.gl.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		OverloadEngineArm.gl.glTexParameteri(GL2ES2.GL_TEXTURE_2D, GL2ES2.GL_TEXTURE_WRAP_S, GL2ES2.GL_CLAMP_TO_EDGE);
		OverloadEngineArm.gl.glTexParameteri(GL2ES2.GL_TEXTURE_2D, GL2ES2.GL_TEXTURE_WRAP_T, GL2ES2.GL_CLAMP_TO_EDGE);
		
		mvpMatrix.matrixImpl[0] = 2.0f / OverloadEngine.getInstance().aspectRatio;
		mvpMatrix.matrixImpl[5] = 1.0f;
		mvpMatrix.matrixImpl[10] = 1.0f;
		mvpMatrix.matrixImpl[15] = 1.0f;
	}

	protected void loadProgramInfo(Shader shader){
		int programId = shader.getProgramId();
		OverloadEngineArm.gl.glUseProgram(programId);
		System.out.println("------------\nProgram info\n------------");

		int lengthBuf[] = new int[1];
		byte logBuf[] = new byte[1024];
		
		OverloadEngineArm.gl.glGetProgramInfoLog(programId, 1024, lengthBuf, 0, logBuf, 0);
		System.out.println(new String(logBuf, 0, lengthBuf[0]));

		int ib[] = new int[1];
		OverloadEngineArm.gl.glGetProgramiv(programId, GL2ES2.GL_ACTIVE_ATTRIBUTES, ib, 0);
		int count = ib[0];
		
		int length[] = new int[1];
		int glSize[] = new int[1];
		int glType[] = new int[1];
		byte name[] = new byte[256];
		
		// Attributes
		System.out.println(String.format("%-20s%-20s%-20s\n", "Attribute name", "Index", "Location"));
		shader.startAttributeDeclaration(count);
		for (int i = 0; i < count; ++i){
			OverloadEngineArm.gl.glGetActiveAttrib(programId, i, 256, length, 0, glSize, 0, glType, 0, name, 0);
			name[length[0]] = '\0';
			String attrib = new String(name, 0, length[0]);
			int loc = OverloadEngineArm.gl.glGetAttribLocation(programId, attrib);
			int size = glSize[0] * getSizeInFloats(glType[0]);
			shader.addAttribute(attrib, loc, size);
			System.out.println(String.format("%-20s%-20d%-20d", attrib, i, loc));
		}
		shader.finishAttributeDeclaration();
		
		OverloadEngineArm.gl.glGetProgramiv(programId, GL2ES2.GL_ACTIVE_UNIFORMS, ib, 0);
		count = ib[0];
		
		// Uniforms
		System.out.println("------------");
		System.out.println(String.format("%-20s%-20s%-20s\n", "Uniform name", "Index", "Location"));
		shader.startUniformDeclaration(count);
		for (int i = 0; i < count; ++i){
			OverloadEngineArm.gl.glGetActiveUniform(programId, i, 256, length, 0, glSize, 0, glType, 0, name, 0);
			name[length[0]] = '\0';
			String uniform = new String(name, 0, length[0]);
			int loc = OverloadEngineArm.gl.glGetUniformLocation(programId, uniform);
			shader.addUniform(uniform, loc, glTypeToOvl(glType[0]));
			System.out.println(String.format("%-20s%-20d%-20d", uniform, i, loc));
		}
		shader.finishUniformDeclaration();
		System.out.println("------------");
	}

	// Converts opengl type to ovl type
	private Class<?> glTypeToOvl(int glType){
		switch (glType){
			case GL2ES2.GL_FLOAT:{
				return Float.class;
			}
			case GL2ES2.GL_INT:{
				return Integer.class;
			}
			case GL2ES2.GL_SAMPLER_2D:{
				return Texture.class;
			}
			case GL2ES2.GL_FLOAT_VEC2:{
				return Vector2.class;
			}
			case GL2ES2.GL_FLOAT_VEC4:{
				return Color.class;	// TODO: fixplz
			}
			case GL2ES2.GL_FLOAT_MAT4:{
				return mvpMatrix.getClass();
			}
		}
		
		return null;
	}
	
	// Returns opengl type size in floats
	private int getSizeInFloats(int glType){
		switch (glType){
			case GL2ES2.GL_FLOAT_VEC2:{
				return 2;
			}
			case GL2ES2.GL_FLOAT_VEC3:{
				return 3;
			}
			case GL2ES2.GL_FLOAT_VEC4:{
				return 4;
			}
			case GL2ES2.GL_FLOAT_MAT2:{
				return 4;
			}
			case GL2ES2.GL_FLOAT_MAT3:{
				return 9;
			}
			case GL2ES2.GL_FLOAT_MAT4:{
				return 16;
			}
		}
		
		System.err.println("Unknown gl type " + glType);
		return 0;
	}
	
	public Shader createShader(String name) {
		Shader shader = new Shader(name);
		int vsId = compileShader(GL2ES2.GL_VERTEX_SHADER, shader.getVSCode());
		int fsId = compileShader(GL2ES2.GL_FRAGMENT_SHADER, shader.getFSCode());
		shader.setProgramId(OverloadEngineArm.gl.glCreateProgram());
		
		OverloadEngineArm.gl.glAttachShader(shader.getProgramId(), vsId);
		OverloadEngineArm.gl.glAttachShader(shader.getProgramId(), fsId);
		OverloadEngineArm.gl.glLinkProgram(shader.getProgramId());
		
		int glErr = OverloadEngineArm.gl.glGetError();
		if (glErr != GL2ES2.GL_NO_ERROR){
			System.err.println("GL error " + glErr);
			return null;
		}

		OverloadEngineArm.gl.glDetachShader(shader.getProgramId(), vsId);
		OverloadEngineArm.gl.glDetachShader(shader.getProgramId(), fsId);
		OverloadEngineArm.gl.glDeleteShader(vsId);
		OverloadEngineArm.gl.glDeleteShader(fsId);
		
		loadProgramInfo(shader);
		shaders.put(name, shader);
		
		return shader;
	}
	
	@Override
	public void postRender() {
		// Disable vertex array
		/*if (activeShader != null){
			cleanupShader(activeShader);
		}
		OverloadEngineArm.gl.glBindBuffer(OverloadEngineArm.gl.GL_ARRAY_BUFFER, 0);*/
	}

	@Override
	public void preRender() {
		for (Vbo vbo : vbos){
			if (vbo.isModified()){
				vbo.setModified(false);
				vbo.getBuffer().rewind();
				OverloadEngineArm.gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, vbo.getId());
				OverloadEngineArm.gl.glBufferData(GL2ES2.GL_ARRAY_BUFFER, vbo.getCapacity() * BYTES_PER_FLOAT, vbo.getBuffer(), GL2ES2.GL_STATIC_DRAW);
			}
		}
		
		OverloadEngineArm.gl.glClear(GL2ES2.GL_COLOR_BUFFER_BIT);
		
		OverloadEngineArm.gl.glActiveTexture(GL2ES2.GL_TEXTURE0);	
	}
	
	protected void useShader(Shader shader){
		activeShader = shader;
		OverloadEngineArm.gl.glUseProgram(shader.getProgramId());
	}
	
	protected void bindVbo(Vbo vbo){
		boundVbo = vbo;
		OverloadEngineArm.gl.glBindBuffer(GL2ES2.GL_ARRAY_BUFFER, boundVbo.getId());
		
		for (Shader.Attribute a : activeShader.getAttributes()){
			OverloadEngineArm.gl.glVertexAttribPointer(a.id, a.size, GL2ES2.GL_FLOAT, false, boundVbo.getStride(), a.offset);
			OverloadEngineArm.gl.glEnableVertexAttribArray(a.id);
		}
	}
	
	protected void cleanupShader(Shader shader){
		for (Shader.Handle handle : shader.getAttributes()){
			OverloadEngineArm.gl.glDisableVertexAttribArray(handle.id);
		}
	}

	private void setParams(ShaderParams vboId){
		if (activeShader != vboId.getVbo().getShader()){
			useShader(vboId.getVbo().getShader());
		}
		
		if (boundVbo != vboId.getVbo())
		{
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
		OverloadEngineArm.gl.glDrawArrays(primitiveModes[mode.getIndex()], offset, count);
	}
	
	@Override
	public void renderIndexed(ShaderParams vboId, PrimitiveType mode, ByteBuffer indices, int count) {
		//setParams(vboId);
		//OverloadEngineArm.gl.glDrawElements(primitiveModes[mode.getIndex()], count, GL2ES2.GL_UNSIGNED_SHORT, indices);
	}
	
	@Override
	protected int compileShader(int type, String shaderCode) {
		int shader = OverloadEngineArm.gl.glCreateShader(type);

		IntBuffer size = IntBuffer.allocate(1);
		size.put(shaderCode.length());
		size.rewind();
		
		OverloadEngineArm.gl.glShaderSource(shader, 1, new String[]{ shaderCode }, size);
		OverloadEngineArm.gl.glCompileShader(shader);
		
		int result[] = new int[1];
		OverloadEngineArm.gl.glGetShaderiv(shader, GL2ES2.GL_COMPILE_STATUS, result, 0);
		boolean isCompiled = result[0] != GL2ES2.GL_FALSE;

		if (!isCompiled){
			int lengthBuf[] = new int[1];
			byte logBuf[] = new byte[1024];
			
			OverloadEngineArm.gl.glGetShaderInfoLog(shader, 1024, lengthBuf, 0, logBuf, 0);
			System.err.println("Shader compilation error. More info:\n" + new String(logBuf, 0, logBuf[lengthBuf[0]]));
		}
		
		return shader;
	}
	
	public void deleteVbo(ShaderParams vboId){
		if (vboId == null){
			return;
		}
		
		Vbo vbo = vboId.getVbo();
		if (vbo == null){
			return;
		}
		
		IntBuffer buffer = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer();
		buffer.put(vbo.getId());
		buffer.rewind();
		OverloadEngineArm.gl.glDeleteBuffers(1, buffer);
		vbos.remove(vbo);
	}
	
	protected void initVbo(Vbo vbo){
		IntBuffer buffer = IntBuffer.allocate(1);
		OverloadEngineArm.gl.glGenBuffers(1, buffer);
		vbo.setId(buffer.get(0));
		vbos.add(vbo);
	}

	@Override
	public void unloadResources() {

	}

	@Override
	public void reloadResources() {

	}
}
