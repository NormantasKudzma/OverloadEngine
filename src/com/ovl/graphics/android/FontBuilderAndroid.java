package com.ovl.graphics.android;

import java.util.Locale;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.android.OverloadEngineAndroid;
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
	
	@Override
	public CustomFont buildFont(String path) {
		AssetManager am = ((OverloadEngineAndroid)OverloadEngine.getInstance()).getContext().getApplicationContext().getAssets();
		Typeface f = Typeface.createFromAsset(am, path);
		return new CustomFontAndroid(f);
	}
}
