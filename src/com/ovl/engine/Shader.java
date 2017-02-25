package com.ovl.engine;

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
	
	public static final String HANDLE_NAMES[] = {
		"a_Position",
		"a_TexCoord",
		"a_Color",
		"u_Texture",
		"u_MVPMatrix",
		"u_Color"
	};
	
	public static final int HANDLE_SIZES[] = {
		2, 
		2, 
		4, 
		0, 
		0,
		0
	};
	
	public static final int HANDLE_A_POSITION 	= 0;
	public static final int HANDLE_A_TEXCOORD 	= 1;
	public static final int HANDLE_A_COLOR 		= 2;
	public static final int HANDLE_U_TEX 			= 3;
	public static final int HANDLE_U_MVPMATRIX 	= 4;
	public static final int HANDLE_U_COLOR		= 5;
	public static final int HANDLE_COUNT 			= 6;
	
	private String resourceName;
	private int programId = -1;
	private int vsId = -1;
	private int fsId = -1;
	private Handle handles[];
	
	public Shader(String name){
		resourceName = name;
		handles = new Handle[HANDLE_COUNT];
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
	
	public static int getHandleIndexByName(String name){
		for (int i = 0; i < HANDLE_COUNT; ++i){
			if (HANDLE_NAMES[i].equals(name)){
				return i;
			}
		}
		return -1;
	}
	
	public void calculateOffsets(int typeSize){
		int handleOffset = 0;
		for (Handle handle : handles){
			if (handle != null){
				handle.offset = handleOffset;
				handleOffset += handle.size * typeSize;
			}
		}
	}
	
	public void createHandle(int handle, int id){
		handles[handle] = new Handle(HANDLE_NAMES[handle], id, HANDLE_SIZES[handle], 0);
	}
	
	public Handle getHandle(int handle){
		return handles[handle];
	}
}
