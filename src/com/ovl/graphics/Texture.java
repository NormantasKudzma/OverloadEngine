package com.ovl.graphics;

public abstract class Texture {
	private int height;
	private int width;
	private int texWidth;
	private int texHeight;
	private float widthRatio;
	private float heightRatio;
	
	protected int id;		// for glBindTexture
	protected int target;	// for glActiveTexture

	public Texture(){
		this(0, 0);
	}
	
	public Texture(int id){
		this(id, 0);
	}
	
	public Texture(int id, int target) {
		this.id = id;
		this.target = target;
	}
	
	public abstract void bind();
	
	public int getId(){
		return id;
	}
	
	public int getTarget(){
		return target;
	}
	
	public void setHeight(int height) {
		this.height = height;
		setHeight();
	}

	public void setWidth(int width) {
		this.width = width;
		setWidth();
	}

	public int getImageHeight() {
		return height;
	}

	public int getImageWidth() {
		return width;
	}

	public float getHeight() {
		return heightRatio;
	}

	public float getWidth() {
		return widthRatio;
	}

	public void setTextureHeight(int texHeight) {
		this.texHeight = texHeight;
		setHeight();
	}

	public void setTextureWidth(int texWidth) {
		this.texWidth = texWidth;
		setWidth();
	}

	private void setHeight() {
		if (texHeight != 0) {
			heightRatio = ((float) height) / texHeight;
		}
	}

	private void setWidth() {
		if (texWidth != 0) {
			widthRatio = ((float) width) / texWidth;
		}
	}
}
