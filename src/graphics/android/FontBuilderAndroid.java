package graphics.android;

import graphics.CustomFont;
import graphics.FontBuilder;
import graphics.SimpleFont;

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
