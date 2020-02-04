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
		
		public Attribute(String name, int id, int size){
			this.name = name;
			this.id = id;
			this.size = size;
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
	private int totalSize = 0;
	private ArrayList<Uniform> uniforms;
	private Attribute attributes[];
	
	public Shader(String name){
		resourceName = name;
	}
	
	public String getVSCode(){
		String resource = Paths.SHADERS + resourceName + "_VS";
		return ConfigManager.loadFile(resource);
	}
	
	public String getFSCode(){
		String resource = Paths.SHADERS + resourceName + "_FS";
		return ConfigManager.loadFile(resource);
	}

	public int getProgramId() {
		return programId;
	}

	public void setProgramId(int programId) {
		this.programId = programId;
	}
	
	public void startAttributeDeclaration(int count){
		if (attributes == null) {
			attributes = new Attribute[count];
		}
	}
	
	public void startUniformDeclaration(int count){
		if (uniforms == null) {
			uniforms = new ArrayList<>();
		}
	}
	
	public void addAttribute(String name, int id, int size){
		attributes[id] = new Attribute(name, id, size);
		totalSize += size;
	}
	
	public void addUniform(String name, int id, Class<?> type){
		uniforms.add(new Uniform(name, id, type));
	}
	
	public void finishAttributeDeclaration(){
		int byteOffset = 0;
		for (Attribute attr : attributes){
			attr.offset = byteOffset;
			byteOffset += attr.size * Renderer.BYTES_PER_FLOAT;
		}
	}
	
	public void finishUniformDeclaration(){
		
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
	
	public Attribute[] getAttributes(){
		return attributes;
	}
	
	public int getTotalAttributesSize(){
		return totalSize;
	}

	public void reset(){
		programId = -1;
		totalSize = 0;
		uniforms.clear();
		attributes = null;
	}
}
