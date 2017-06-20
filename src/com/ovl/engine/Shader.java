package com.ovl.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.ovl.utils.ConfigManager;
import com.ovl.utils.Paths;

public class Shader {
	public static class Handle {
		public String name;
		public int id;
		public int size;
		public int offset;
		
		public Handle(String name, int id, int size, int offset){
			this.id = id;
			this.name = name;
			this.size = size;
			this.offset = offset;
		}
	}

	public static final String A_POSITION = "a_Position";
	public static final String A_TEXCOORD = "a_TexCoord";
	public static final String A_COLOR = "a_Color";
	public static final String U_TEXTURE = "u_Texture";
	public static final String U_MVPMATRIX = "u_MVPMatrix";
	public static final String U_COLOR = "u_Color";
	
	static {
		HashMap<String, Integer> sizes = new HashMap<String, Integer>();
		sizes.put(A_POSITION, 2);
		sizes.put(A_TEXCOORD, 2);
		sizes.put(A_COLOR, 4);
		sizes.put(U_TEXTURE, 0);
		sizes.put(U_MVPMATRIX, 0);
		sizes.put(U_COLOR, 0);
		handleSizes = Collections.unmodifiableMap(sizes);
	}
	
	public static final Map<String, Integer> handleSizes;
	
	private String resourceName;
	private int programId = -1;
	private int vsId = -1;
	private int fsId = -1;
	private ArrayList<Handle> handles;
	private int totalHandlesSize = 0;
	
	public Shader(String name){
		resourceName = name;
		handles = new ArrayList<Handle>();
	}
	
	public String getVSCode(){
		StringBuilder resource = new StringBuilder();
		resource.append(Paths.getShaders()).append(resourceName).append("_VS");
		return ConfigManager.loadFile(resource.toString());
	}
	
	public String getPSCode(){
		StringBuilder resource = new StringBuilder();
		resource.append(Paths.getShaders()).append(resourceName).append("_FS");
		return ConfigManager.loadFile(resource.toString());
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}

	public int getVSId() {
		return vsId;
	}

	public void setVSId(int vsId) {
		this.vsId = vsId;
	}

	public int getFSId() {
		return fsId;
	}

	public void setFSId(int fsId) {
		this.fsId = fsId;
	}
	
	public int getTotalHandlesSize(){
		return totalHandlesSize;
	}
	
	public void calculateOffsets(int typeSize){
		int handleOffset = 0;
		totalHandlesSize = 0;
		for (Handle handle : handles){
			handle.offset = handleOffset;
			handleOffset += handle.size * typeSize;
			totalHandlesSize += handle.size;
		}
	}
	
	public void createHandle(String handle, int id){
		handles.add(new Handle(handle, id, handleSizes.get(handle), 0));
	}
	
	public ArrayList<Handle> getHandles(){
		return handles;
	}
	
	public Handle getHandle(String name){
		for (Handle handle : handles){
			if (handle.name.equals(name)){
				return handle;
			}
		}
		return null;
	}
}
