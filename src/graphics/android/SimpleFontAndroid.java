package graphics.android;

import graphics.Color;
import graphics.CustomFont;
import graphics.SimpleFont;
import graphics.Sprite;
import utils.FastMath;
import utils.Vector2;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

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
		
		clearPaint = new Paint();
		clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR)); 
	}
	
	public CustomFont getFont(){
		return font;
	}
	
	@Override
	protected void initBufferedImage(int w, int h) {
		bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		//bmp.setHasAlpha(true);	
		canvas.setBitmap(bmp);
		
		paint.setColor(android.graphics.Color.WHITE);
		paint.setFlags(Paint.ANTI_ALIAS_FLAG);
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
		Vector2.pixelCoordsToNormal(textSize.set(rect.right, rect.top));
		Vector2.pixelCoordsToNormal(textOffset.set(rect.left, rect.bottom));
		
		int newWidth = FastMath.nextPowerOfTwo(rect.right + rect.left);
		int newHeight = FastMath.nextPowerOfTwo(rect.top);
		int newX = (int)((newWidth - rect.right - rect.left) * 0.5f);
		int newY = (int)((newHeight - rect.top) * 0.5f);
		
		if (newWidth > bmp.getWidth() || newHeight > bmp.getHeight()){
			initBufferedImage(newWidth, newHeight);
		}
		else
		{
			canvas.drawRect(textBounds, clearPaint);
		}
		
		textBounds.set(0, newHeight, newWidth, 0);
		
		canvas.drawText(text, newX, newY, paint);
		Bitmap textSubImage = Bitmap.createBitmap(bmp, 0, 0, newWidth, newHeight);
		
		TextureLoaderAndroid textureLoader = ((TextureLoaderAndroid)RENDERER.getTextureLoader());
		sprite = new Sprite(textureLoader.getTexture(textSubImage));
		sprite.setColor(oldColor);
	}

	@Override
	public void setFont(CustomFont f) {
		if (font != null && f.equals(font)){
			return;
		}
		
		font = (CustomFontAndroid)f;
		paint.setTextSize(font.getSize());
		paint.setTypeface(font.getTypeface());
		prerenderText();
	}
}
