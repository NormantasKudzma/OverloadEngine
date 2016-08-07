package com.ovl.graphics.android;

import com.ovl.graphics.CustomFont;
import com.ovl.graphics.FontBuilder;
import com.ovl.graphics.SimpleFont;

public class FontBuilderAndroid implements FontBuilder {
	@Override
	public CustomFont buildFont(String name, int style, int size) {
		return new CustomFontAndroid(name, style, size);
	}

	@Override
	public SimpleFont createFontObject() {
		return new SimpleFontAndroid();
	}
}
