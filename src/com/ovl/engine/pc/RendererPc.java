package com.ovl.engine.pc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.Vbo;
import com.ovl.graphics.Color;
import com.ovl.graphics.pc.FontBuilderPc;
import com.ovl.graphics.pc.TextureLoaderPc;
import com.ovl.utils.Pair;
import com.ovl.utils.Vector2;

public final class RendererPc extends Renderer {
	private Matrix4f mvpMatrix = new Matrix4f();
	private Matrix4f renderMatrix = new Matrix4f();
	private FloatBuffer renderBuffer;
	
	public RendererPc(){
		renderBuffer = ByteBuffer.allocateDirect(16 * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();		
		textureLoader = new TextureLoaderPc();
		fontBuilder = new FontBuilderPc();
		
		renderMatrix.setIdentity();
		renderBuffer.clear();
		renderMatrix.store(renderBuffer);
		renderBuffer.rewind();
		prepareRenderMatrix(new Vector2(-1.0f, -1.0f), new Vector2(1.0f, 1.0f), 0.0f);
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
			
			ArrayList<Pair<String, Integer>> handles = loadProgramInfo(shader.getProgramId());
			
			for (Pair<String, Integer> pair : handles){
				int handleIndex = Shader.getHandleIndexByName(pair.key);
				if (handleIndex != -1){
					shader.createHandle(handleIndex, pair.value);
				}
			}
			shader.calculateOffsets(BYTES_PER_FLOAT);
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
		//GLES11.glDisable(GLES11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		
		lookAt(mvpMatrix, new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		mvpMatrix.translate(new Vector3f(-1.0f, -1.0f, 0.0f));
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
	
	private void lookAt(Matrix4f mat, Vector3f eye, Vector3f center, Vector3f up) {
		Vector3f f = new Vector3f();
		Vector3f.sub(center, eye, f);
		f = (Vector3f)f.normalise();
		Vector3f u = (Vector3f)up.normalise();
		Vector3f s = new Vector3f();
		Vector3f.cross(f, u, s);
		s = (Vector3f)s.normalise();
		Vector3f.cross(s, f, u);
		
		mat.setIdentity();
		
		mat.m00 = s.x;
		mat.m10 = s.y;
		mat.m20 = s.z;
		
		mat.m01 = u.x;
		mat.m11 = u.y;
		mat.m21 = u.z;
		
		mat.m02 = -f.x;
		mat.m12 = -f.y;
		mat.m22 = -f.z;
		
		mat.translate(new Vector3f(-eye.x,-eye.y,-eye.z));
	}
	
	protected void cleanupShader(Shader shader){
		Shader.Handle handle = null;
		for (int i = 0; i < Shader.HANDLE_COUNT; ++i){
			if ((handle = shader.getHandle(i)) != null){
				GL20.glDisableVertexAttribArray(handle.id);
			}
		}
	}
	
	protected void prepareShader(Shader shader){
		Shader.Handle handle = null;
		GL20.glUseProgram(shader.getProgramId());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, boundVbo.getId());
		
		for (int i = 0; i < Shader.HANDLE_COUNT; ++i){
			if ((handle = shader.getHandle(i)) != null && handle.size > 0){
				GL20.glVertexAttribPointer(handle.id, handle.size, GL11.GL_FLOAT, false, boundVbo.getStride(), handle.offset);
				GL20.glEnableVertexAttribArray(handle.id);
			}
		}
	}
	
	public void postRender(){
		// Disable vertex array
		/*if (activeShader != null){
			cleanupShader(activeShader);
		}
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);*/
	}
	
	protected ArrayList<Pair<String, Integer>> loadProgramInfo(int programId){
		ArrayList<Pair<String, Integer>> handles = new ArrayList<Pair<String, Integer>>();
		GL20.glUseProgram(programId);
		System.out.println("------------\nProgram info\n------------");
		
		String infoLog = GL20.glGetProgramInfoLog(programId, 2048);
		System.out.println(infoLog);
		
		int count = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_ATTRIBUTES);
		
		// Attributes
		System.out.printf("%-20s%-20s%-20s\n", "Attribute name", "Index", "Location");
		for (int i = 0; i < count; ++i){
			String attrib = GL20.glGetActiveAttrib(programId, i, 256);
			int loc = GL20.glGetAttribLocation(programId, attrib);
			handles.add(new Pair<String, Integer>(attrib, loc));
			System.out.printf("%-20s%-20d%-20d\n", attrib, i, loc);
		}
		
		count = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_UNIFORMS);
		
		// Uniforms
		System.out.println("------------");
		System.out.printf("%-20s%-20s%-20s\n", "Uniform name", "Index", "Location");
		for (int i = 0; i < count; ++i){
			String uniform = GL20.glGetActiveUniform(programId, i, 256);
			int loc = GL20.glGetUniformLocation(programId, uniform);
			handles.add(new Pair<String, Integer>(uniform, loc));
			System.out.printf("%-20s%-20d%-20d\n", uniform, i, loc);
		}
		System.out.println("------------");
		
		return handles;
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
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
	}
	
	protected void prepareRenderMatrix(Vector2 position, Vector2 scale, float rotation){
		renderMatrix.setIdentity();
		
		Vector3f v = new Vector3f();

		v.set(position.x, position.y, 0.0f);
		renderMatrix.translate(v);
		
		v.set(scale.x, scale.y, 1.0f);
		renderMatrix.scale(v);
		
		
		// No rotation, kek
		
		Matrix4f.mul(mvpMatrix, renderMatrix, renderMatrix);
		
		renderBuffer.clear();
		renderMatrix.store(renderBuffer);
		renderBuffer.rewind();
	}
	
	public void renderTextured(VboId vboId, Color c){
		if (boundVbo != vboId.getVbo())
		{
			boundVbo = vboId.getVbo();
			activeShader = boundVbo.getShader();
			prepareShader(activeShader);
		}
		
		//prepareRenderMatrix(size, position, scale, rotation);
		GL20.glUniform4f(activeShader.getHandle(Shader.HANDLE_U_COLOR).id, c.rgba[0], c.rgba[1], c.rgba[2], c.rgba[3]);
		GL20.glUniformMatrix4(activeShader.getHandle(Shader.HANDLE_U_MVPMATRIX).id, false, renderBuffer);
		GL20.glUniform1i(activeShader.getHandle(Shader.HANDLE_U_TEX).id, 0);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, vboId.getIndex() * boundVbo.getVertexCount(), boundVbo.getVertexCount());
	}
	
	public void renderPrimitive(VboId vboId, PrimitiveType mode, Color c){		
		if (boundVbo != vboId.getVbo())
		{
			boundVbo = vboId.getVbo();
			activeShader = boundVbo.getShader();
			prepareShader(activeShader);
		}
		
		//prepareRenderMatrix(Vector2.zero, position, scale, rotation);
		GL20.glUniform4f(activeShader.getHandle(Shader.HANDLE_U_COLOR).id, c.rgba[0], c.rgba[1], c.rgba[2], c.rgba[3]);
		GL20.glUniformMatrix4(activeShader.getHandle(Shader.HANDLE_U_MVPMATRIX).id, false, renderBuffer);
		
		// plzfix
		int openGlMode = -1;
		switch (mode){
			case LineLoop:{
				openGlMode = GL11.GL_LINE_LOOP;
				break;
			}
			case LineStrip:{
				openGlMode = GL11.GL_LINE_STRIP;
				break;
			}
			case Lines:{
				openGlMode = GL11.GL_LINES;
				break;
			}
			case Polygon:{
				openGlMode = GL11.GL_POLYGON;
				break;
			}
			case Points:{
				openGlMode = GL11.GL_POINT;
			}
			case QuadStrip:{
				openGlMode = GL11.GL_QUAD_STRIP;
				break;
			}
			case Quads:{
				openGlMode = GL11.GL_QUADS;
				break;
			}
			case TriangleFan:{
				openGlMode = GL11.GL_TRIANGLE_FAN;
				break;
			}
			case TriangleStrip:{
				openGlMode = GL11.GL_TRIANGLE_STRIP;
				break;
			}
			case Triangles:{
				openGlMode = GL11.GL_TRIANGLES;
				break;
			}
			default:{
				openGlMode = GL11.GL_LINE_LOOP;
				break;
			}
		}

		GL11.glDrawArrays(openGlMode, vboId.getIndex() * boundVbo.getVertexCount(), boundVbo.getVertexCount());
	}

	public void deleteVbo(VboId vboId){
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
