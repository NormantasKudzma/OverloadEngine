package graphics.android;

import android.graphics.Typeface;
import graphics.CustomFont;

public class CustomFontAndroid implements CustomFont {
	Typeface f;
	int size;
	
	public CustomFontAndroid(String name, int style, int size){
		f = Typeface.create(name, style);
		this.size = size;
	}
	
	@Override
	public CustomFont deriveFont(float size) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Typeface getTypeface(){
		return f;
	}
	
	public int getSize(){
		return size;
	}
}
