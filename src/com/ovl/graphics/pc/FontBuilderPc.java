package com.ovl.graphics.pc;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.net.URL;

import com.ovl.graphics.CustomFont;
import com.ovl.graphics.FontBuilder;
import com.ovl.graphics.SimpleFont;

public class FontBuilderPc implements FontBuilder {
	@Override
	public CustomFont buildFont(String name, int style, int size) {
		return new CustomFontPc(name, style, size);
	}
	
	@Override
	public CustomFont buildFont(String path) {
		return new CustomFontPc(loadFont(path));
	}
	
	@Override
	public SimpleFont createFontObject() {
		return new SimpleFontPc();
	}
	
	private Font loadFont(String path) {
		try {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			URL url = Thread.currentThread().getContextClassLoader().getResource(path);
			Font font = Font.createFont(Font.TRUETYPE_FONT, url.openStream());
			ge.registerFont(font);
			return font;
		}
		catch (Exception e) {
			return null;
		}
	}
}
