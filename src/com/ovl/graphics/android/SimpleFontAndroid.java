package com.ovl.graphics.android;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;

import com.ovl.graphics.Color;
import com.ovl.graphics.CustomFont;
import com.ovl.graphics.SimpleFont;
import com.ovl.graphics.Sprite;
import com.ovl.utils.FastMath;
import com.ovl.utils.Vector2;

public class SimpleFontAndroid extends SimpleFont {
	private Canvas canvas;
	private Bitmap bmp;
	private Paint paint;
	private Paint clearPaint;
	private CustomFontAndroid font;	
	private Rect textBounds = new Rect();
	
	public SimpleFontAndroid(){
		canvas = new Canvas();
		paint = new Paint();
	
		paint.setColor(android.graphics.Color.WHITE);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paint.setTextAlign(Paint.Align.LEFT);
		
		clearPaint = new Paint();
	}
	
	public CustomFont getFont(){
		return font;
	}
	
	@Override
	protected void initBufferedImage(int w, int h) {
		bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		//bmp.setHasAlpha(true);
		canvas.setBitmap(bmp);
	}

	@Override
	protected void prerenderText() {
		if (text == null || text.isEmpty()){
			return;
		}
		
		Color oldColor = null;
		if (sprite != null){
			oldColor = sprite.getColor();
			sprite.destroy();
		}
		
		// Prerender text to a texture using default java tools, 
		// because dealing with fonts is a nightmare
		Rect rect = new Rect();
		paint.getTextBounds(text, 0, text.length(), rect);
		Vector2.pixelCoordsToNormal(textSize.set(rect.width(), rect.height()));
		Vector2.pixelCoordsToNormal(textOffset.set(rect.left, rect.bottom));
		
		int newWidth = FastMath.nextPowerOfTwo(rect.width());
		int newHeight = FastMath.nextPowerOfTwo(rect.height());
		int newX = (int)((newWidth - rect.width()) * 0.5f);
		int newY = (int)((newHeight + rect.height()) * 0.5f);
		
		if (newWidth > bmp.getWidth() || newHeight > bmp.getHeight()){
			initBufferedImage(newWidth, newHeight);
		}
		else
		{
			canvas.drawColor(android.graphics.Color.TRANSPARENT, Mode.CLEAR);
		}
		
		textBounds.set(0, newHeight, newWidth, 0);
		
		canvas.drawText(text, newX, newY, paint);
		Bitmap textSubImage = Bitmap.createBitmap(bmp, 0, 0, newWidth, newHeight);
		
		TextureLoaderAndroid textureLoader = ((TextureLoaderAndroid)RENDERER.getTextureLoader());
		setSprite(new Sprite(textureLoader.getTexture(textSubImage)));
		
		if (oldColor != null){
			sprite.setColor(oldColor);
		}
	}

	@Override
	public void setFont(CustomFont f) {
		if (f != null && font != null && font.equals(f)){
			return;
		}
		
		font = (CustomFontAndroid)f;
		paint.setTextSize(font.getSize());
		paint.setTypeface(font.getTypeface());
		prerenderText();
	}
}
