package com.ovl.engine;

import java.util.ArrayList;

import com.ovl.utils.ConfigManager;
import com.ovl.utils.Paths;

public class Shader {
	public static class Handle {
		public String name;
		public int id;
	}
	
	public static class Attribute extends Handle {
		public int size;
		public int offset;
		
		public Attribute(String name, int id, int size, int offset){
			this.name = name;
			this.id = id;
			this.size = size;
			this.offset = offset;
		}
	}
	
	public static class Uniform extends Handle {
		public Class<?> type;
		
		public Uniform(String name, int id, Class<?> type){
			this.name = name;
			this.id = id;
			this.type = type;
		}
	}

	public static final String A_POSITION = "a_Position";
	public static final String A_TEXCOORD = "a_TexCoord";
	public static final String A_COLOR = "a_Color";
	public static final String U_TEXTURE = "u_Texture";
	public static final String U_MVPMATRIX = "u_MVPMatrix";
	public static final String U_COLOR = "u_Color";
	public static final String U_FLOAT1 = "u_Float1";
	public static final String U_FLOAT2 = "u_Float2";
	public static final String U_FLOAT3 = "u_Float3";
	public static final String U_FLOAT4 = "u_Float4";
	
	private String resourceName;
	private int programId = -1;
	private int vsId = -1;
	private int fsId = -1;
	private int totalSize = 0;
	private ArrayList<Uniform> uniforms;
	private ArrayList<Attribute> attributes;
	
	public Shader(String name){
		resourceName = name;
		uniforms = new ArrayList<>();
		attributes = new ArrayList<>();
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
	
	public void addAttribute(String name, int id, int size, int offset){
		attributes.add(new Attribute(name, id, size, offset));
		totalSize += size;
	}
	
	public void addUniform(String name, int id, Class<?> type){
		uniforms.add(new Uniform(name, id, type));
	}
	
	public ArrayList<Uniform> getUniforms(){
		return uniforms;
	}
	
	public Uniform getUniform(String name){
		for (Uniform u : uniforms){
			if (u.name.equals(name)){
				return u;
			}
		}
		return null;
	}
	
	public ArrayList<Attribute> getAttributes(){
		return attributes;
	}
	
	public int getTotalAttributesSize(){
		return totalSize;
	}
}
