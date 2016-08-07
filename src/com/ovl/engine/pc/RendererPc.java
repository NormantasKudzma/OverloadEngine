package com.ovl.engine.pc;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.Util;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import com.ovl.engine.Renderer;
import com.ovl.graphics.pc.FontBuilderPc;
import com.ovl.graphics.pc.TextureLoaderPc;
import com.ovl.utils.Vector2;

public final class RendererPc extends Renderer {
	private Matrix4f mvpMatrix = new Matrix4f();
	private Matrix4f renderMatrix = new Matrix4f();
	private FloatBuffer renderBuffer;
	
	private HashMap<String, Integer> attributeMap = new HashMap<String, Integer>();
	private HashMap<String, Integer> uniformMap = new HashMap<String, Integer>();
	
	public RendererPc(){
		vbo = ByteBuffer.allocateDirect(bufferSize * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
		
		IntBuffer buffer = BufferUtils.createIntBuffer(1);
		GL15.glGenBuffers(buffer);
		vboId = buffer.get(0);
		
		renderBuffer = ByteBuffer.allocateDirect(16 * BYTES_PER_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();

		vertShaderId = loadShader(GL20.GL_VERTEX_SHADER, vertexShaderCode);
		fragShaderId = loadShader(GL20.GL_FRAGMENT_SHADER, fragmentShaderCode);
		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertShaderId);
		GL20.glAttachShader(programId, fragShaderId);
		GL20.glLinkProgram(programId);
		Util.checkGLError();

		GL20.glUseProgram(programId);
		loadProgramInfo();
		
		positionHandle = attributeMap.get("a_Position");
		colorHandle = attributeMap.get("a_Color");
		texCoordHandle = attributeMap.get("a_TexCoordinate");
		
		mvpMatrixHandle = uniformMap.get("u_MVPMatrix");
		texHandle = uniformMap.get("u_Texture");
		
		textureLoader = new TextureLoaderPc();
		fontBuilder = new FontBuilderPc();
	}
	
	public void init(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//GLES11.glDisable(GLES11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glClearColor(0.4f, 0.4f, 0.4f, 1.0f);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
		
		lookAt(mvpMatrix, new Vector3f(0.0f, 0.0f, 1.0f), new Vector3f(0.0f, 0.0f, -5.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		mvpMatrix.translate(new Vector3f(-1.0f, -1.0f, 0.0f));
	}

	protected int loadShader(int type, String shaderCode) {
		int shader = GL20.glCreateShader(type);

		GL20.glShaderSource(shader, shaderCode);
		GL20.glCompileShader(shader);

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
	
	public void postRender(){
		// Disable vertex array
		GL20.glDisableVertexAttribArray(positionHandle);
		GL20.glDisableVertexAttribArray(colorHandle);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	private void loadProgramInfo(){
		//GL20.glUseProgram(programId);
		System.out.println("------------\nProgram info\n------------");
		
		String infoLog = GL20.glGetProgramInfoLog(programId, 2048);
		System.out.println(infoLog);
		
		int count = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_ATTRIBUTES);
		
		// Attributes
		System.out.printf("%-20s%-20s%-20s\n", "Attribute name", "Index", "Location");
		for (int i = 0; i < count; ++i){
			String attrib = GL20.glGetActiveAttrib(programId, i, 256);
			int loc = GL20.glGetAttribLocation(programId, attrib);
			attributeMap.put(attrib, loc);
			System.out.printf("%-20s%-20d%-20d\n", attrib, i, loc);
		}
		
		count = GL20.glGetProgrami(programId, GL20.GL_ACTIVE_UNIFORMS);
		
		// Uniforms
		System.out.println("------------");
		System.out.printf("%-20s%-20s%-20s\n", "Uniform name", "Index", "Location");
		for (int i = 0; i < count; ++i){
			String uniform = GL20.glGetActiveUniform(programId, i, 256);
			int loc = GL20.glGetUniformLocation(programId, uniform);
			uniformMap.put(uniform, loc);
			System.out.printf("%-20s%-20d%-20d\n", uniform, i, loc);
		}
		System.out.println("------------");
	}
	
	public void preRender(){
		if (isModified) {
			vbo.rewind();
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo, GL15.GL_STATIC_DRAW);
		}
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		GL20.glUseProgram(programId);
		
		// Position
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL20.glVertexAttribPointer(positionHandle, 2, GL11.GL_FLOAT, false, DATA_PER_SPRITE, 0);
		GL20.glEnableVertexAttribArray(positionHandle);
		
		// Texture
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL20.glVertexAttribPointer(texCoordHandle, 2, GL11.GL_FLOAT, false, DATA_PER_SPRITE, 8);
		GL20.glEnableVertexAttribArray(texCoordHandle);

		// Color
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL20.glVertexAttribPointer(colorHandle, 4, GL11.GL_FLOAT, false, DATA_PER_SPRITE, 16);
		GL20.glEnableVertexAttribArray(colorHandle);
		
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
	}
	
	public void render(int id, Vector2 size, Vector2 position, Vector2 scale, float rotation){
		renderMatrix.setIdentity();
		
		Vector3f v = new Vector3f(-size.x, -size.y, 0.0f);
		renderMatrix.translate(v);

		v.set(position.x, position.y, 0.0f);
		renderMatrix.translate(v);
		
		v.set(scale.x, scale.y, 1.0f);
		renderMatrix.scale(v);
		
		
		// No rotation, kek
		
		Matrix4f.mul(mvpMatrix, renderMatrix, renderMatrix);
		
		renderBuffer.clear();
		renderMatrix.store(renderBuffer);
		renderBuffer.rewind();
		GL20.glUniformMatrix4(mvpMatrixHandle, false, renderBuffer);
		GL20.glUniform1i(texHandle, 0);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, id * VERTICES_PER_SPRITE, VERTICES_PER_SPRITE);
	}
}
