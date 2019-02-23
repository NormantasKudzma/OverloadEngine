package com.ovl.engine;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Vbo {
	protected int bufferSize;	
	protected int nextId = 0;
	protected int vboId = -1;
	protected boolean isModified = false;
	protected FloatBuffer vbo = null;
	protected ArrayList<Integer> releasedIds = new ArrayList<Integer>();
	protected int stride = 0;		// bytes per vertex
	protected int typeSize = 0;	// bytes per attribute
	protected int vertexCount = 0;	// number of vertices per renderable
	protected int objectSize = 0;	// number of typeSizes (ie floats) per object
	protected Shader shader;
	
	public Vbo(Shader shader, int initialSize, int vertexCount, int dataSize){
		this.vertexCount = vertexCount;
		this.shader = shader;
		stride = dataSize * shader.getTotalAttributesSize();
		objectSize = vertexCount * shader.getTotalAttributesSize();
		typeSize = dataSize;

		if (initialSize <= 0){
			initialSize = objectSize;
		}
		bufferSize = initialSize;
		vbo = ByteBuffer.allocateDirect(bufferSize * typeSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	public void setModified(boolean isModified){
		this.isModified = isModified;
	}
	
	public boolean isModified(){
		return isModified;
	}
	
	public int generateId(){
		// If there are any released ids, then return them instead of a new id
		if (releasedIds.size() > 0){
			return releasedIds.remove(releasedIds.size() - 1);
		}
		
		if (nextId >= bufferSize / objectSize){
			bufferSize *= 2;
			vbo.rewind();
			FloatBuffer newBuffer = ByteBuffer.allocateDirect(bufferSize * typeSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
			newBuffer.put(vbo);
			vbo = newBuffer;
			isModified = true;
		}

		return nextId++;
	}
	
	public void releaseId(int id){
		releasedIds.add(id);
	}
	
	public FloatBuffer getBuffer(){
		return vbo;
	}
	
	public int getSize(){
		return bufferSize / objectSize;
	}
	
	public int getCapacity(){
		return bufferSize;
	}
	
	public int getStride(){
		return stride;
	}
	
	public int getObjectSize(){
		return objectSize;
	}
	
	public int getTypeSize(){
		return typeSize;
	}
	
	public int getVertexCount(){
		return vertexCount;
	}
	
	public Shader getShader(){
		return shader;
	}
	
	public int getId(){
		return vboId;
	}
	
	public void setId(int id){
		vboId = id;
	}
}
