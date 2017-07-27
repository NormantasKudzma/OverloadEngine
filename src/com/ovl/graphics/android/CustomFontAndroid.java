package com.ovl.graphics.android;

import android.graphics.Typeface;

import com.ovl.graphics.CustomFont;

public class CustomFontAndroid implements CustomFont {
	private Typeface f;
	private String name;
	private int size;
	
	public CustomFontAndroid(Typeface typeface){
		f = typeface;
		size = 20;
	}
	
	public CustomFontAndroid(String name, int style, int size){
		f = Typeface.create(name, style);
		this.name = name;
		this.size = size;
	}
	
	@Override
	public CustomFont deriveFont(float size) {
		CustomFontAndroid clone = new CustomFontAndroid(f);
		clone.name = name;
		clone.size = (int)size;
		return clone;
	}
	
	public Typeface getTypeface(){
		return f;
	}
	
	public int getSize(){
		return size;
	}
}
