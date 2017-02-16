package com.ovl.graphics.pc;

import java.awt.Font;

import com.ovl.graphics.CustomFont;

public class CustomFontPc implements CustomFont{
	private Font f;
	
	public CustomFontPc(Font f){
		this.f = f;
	}
	
	public CustomFontPc(String name, int style, int size) {
		f = new Font(name, style, size);
	}

	public Font get(){
		return f;
	}
	
	@Override
	public CustomFont deriveFont(float size) {
		return new CustomFontPc(f.getName(), f.getStyle(), (int)size);
	}
}
