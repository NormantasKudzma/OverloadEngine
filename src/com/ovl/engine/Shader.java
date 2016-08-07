package com.ovl.engine;

public class Shader {
	public static final String vertShader = ""
	+ "uniform mat4 u_MVPMatrix;"
	+ "attribute vec4 a_Position;"
	+ "attribute vec4 a_Color;"
	+ "attribute vec2 a_TexCoordinate;"
	+ "varying vec4 v_Color;"
	+ "varying vec2 v_TexCoordinate;"
	+ "void main() {" 
	+ "  gl_Position = u_MVPMatrix * a_Position;"
	+ "  v_Color = a_Color;"
	+ "  v_TexCoordinate = a_TexCoordinate;"
	+ "}";

	public static final String fragShader = ""
	+ "precision mediump float;" 
	+ "uniform sampler2D u_Texture;"
	+ "varying vec4 v_Color;"
	+ "varying vec2 v_TexCoordinate;"
	+ "void main() {" 
	+ "  gl_FragColor = v_Color * texture2D(u_Texture, v_TexCoordinate);"
	+ "}";
}
