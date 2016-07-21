package graphics.pc;

import graphics.CustomFont;
import graphics.FontBuilder;
import graphics.SimpleFont;

public class FontBuilderPc implements FontBuilder {
	@Override
	public CustomFont buildFont(String name, int style, int size) {
		return new CustomFontPc(name, style, size);
	}

	
	@Override
	public SimpleFont createFontObject() {
		return new SimpleFontPc();
	}	
}
