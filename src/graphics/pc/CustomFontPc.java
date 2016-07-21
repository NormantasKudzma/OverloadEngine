package graphics.pc;

import graphics.CustomFont;

import java.awt.Font;

public class CustomFontPc implements CustomFont{
	private Font f;
	
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
