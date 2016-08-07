package com.ovl.graphics;

public interface FontBuilder {
	public CustomFont buildFont(String name, int style, int size);
	public CustomFont buildFont(String path);
	public SimpleFont createFontObject();
}
