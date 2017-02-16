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
	protected int stride = 0;		// bytes per renderable (vertex count * bytes per element)
	protected int typeSize = 0;	// bytes per attribute
	protected int vertexCount = 0;	// number of vertices per renderable
	
	public Vbo(int initialSize, int stride, int vertexCount, int dataSize){
		bufferSize = initialSize;
		this.vertexCount = vertexCount;
		
		if (stride > 0){
			this.stride = stride;
		}
		
		if (dataSize > 0){
			this.typeSize = dataSize;
		}
		
		vbo = ByteBuffer.allocateDirect(bufferSize * typeSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}
	
	public void setModified(boolean isModified){
		this.isModified = isModified;
	}
	
	public boolean isModified(){
		return isModified;
	}
	
	public int generateId(){
		if (nextId >= bufferSize / stride){
			bufferSize *= 2;
			vbo.rewind();
			FloatBuffer newBuffer = ByteBuffer.allocateDirect(bufferSize * typeSize).order(ByteOrder.nativeOrder()).asFloatBuffer();
			newBuffer.put(vbo);
			vbo = newBuffer;
			isModified = true;
		}
		
		// If there are any released ids, then return them instead of a new id
		if (releasedIds.size() > 0){
			return releasedIds.remove(releasedIds.size() - 1);
		}

		return nextId++;
	}
	
	public void releaseId(int id){
		releasedIds.add(id);
	}
	
	public FloatBuffer getVbo(){
		return vbo;
	}
	
	public int getSize(){
		return nextId;
	}
	
	public int getCapacity(){
		return bufferSize;
	}
	
	public int getStride(){
		return stride;
	}
	
	public int getDataSize(){
		return typeSize;
	}
	
	public int getVertexCount(){
		return vertexCount;
	}
	
	public int getId(){
		return vboId;
	}
	
	public void setId(int id){
		vboId = id;
	}
}
