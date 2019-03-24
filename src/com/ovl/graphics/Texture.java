package com.ovl.graphics;

public abstract class Texture {
	private int height;
	private int width;
	private int texWidth;
	private int texHeight;
	private float widthRatio;
	private float heightRatio;
	private boolean hasData = false;

	protected int id = -1;	// for glBindTexture
	protected int target;	// for glActiveTexture

	public Texture(){
		this(0);
	}
	
	public Texture(int target) {
		this.target = target;
		create();
	}
	
	public abstract void bind();

	protected abstract int generateId();

	protected abstract void destroy();
	
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

	public void destroyTexture(){
		if (hasData) {
			destroy();
			hasData = false;
		}
	}

	public void create(){
		if (!hasData){
			this.id = generateId();
			hasData = true;
		}
	}

	public boolean hasData(){
		return hasData;
	}
}
