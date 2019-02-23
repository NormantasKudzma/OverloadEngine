package com.ovl.graphics.arm;

import java.awt.Font;

import com.ovl.graphics.CustomFont;

public class CustomFontArm implements CustomFont{
	private Font f;
	
	public CustomFontArm(Font f){
		this.f = f;
	}
	
	public CustomFontArm(String name, int style, int size) {
		f = new Font(name, style, size);
	}

	public Font get(){
		return f;
	}
	
	@Override
	public CustomFont deriveFont(float size) {
		return new CustomFontArm(f.getName(), f.getStyle(), (int)size);
	}
}
