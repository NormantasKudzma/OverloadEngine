package com.ovl.engine;

import java.util.Arrays;

import com.ovl.utils.ConfigManager;
import com.ovl.utils.Paths;

public class Shader {
	public static class Handle {
		public final String name;
		public final int size;
		public final int offset;
		
		private Handle(String name, int size, int offset){
			this.name = name;
			this.size = size;
			this.offset = offset;
		}
	}
	
	public static final Handle HANDLES[] = {
		new Handle("a_Position", 2, 0),
		new Handle("a_Color", 4, 16),
		new Handle("a_TexCoord", 2, 8),
		new Handle("u_Texture", -1, -1),
		new Handle("u_MVPMatrix", -1, -1),
	};
	
	public static final int HANDLE_POSITION 	= 0;
	public static final int HANDLE_COLOR 		= 1;
	public static final int HANDLE_TEXCOORD 	= 2;
	public static final int HANDLE_TEX 		= 3;
	public static final int HANDLE_MVPMATRIX 	= 4;
	public static final int HANDLE_COUNT 		= 5;
	
	private String resourceName;
	private int programId = -1;
	private int vsId = -1;
	private int fsId = -1;
	private int handles[];
	
	public Shader(String name){
		resourceName = name;
		handles = new int[HANDLE_COUNT];
		Arrays.fill(handles, -1);
	}
	
	public String getVSCode(){
		StringBuilder resource = new StringBuilder();
		resource.append(Paths.SHADERS).append(resourceName).append("_VS");
		return ConfigManager.loadFile(resource.toString());
	}
	
	public String getPSCode(){
		StringBuilder resource = new StringBuilder();
		resource.append(Paths.SHADERS).append(resourceName).append("_FS");
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
	
	public void setHandleId(int index, int value){
		handles[index] = value;
	}
	
	public int getHandleId(int index){
		return handles[index];
	}
}
