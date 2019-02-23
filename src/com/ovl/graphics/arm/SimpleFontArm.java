package com.ovl.graphics.arm;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferedImage;

import com.ovl.graphics.Color;
import com.ovl.graphics.CustomFont;
import com.ovl.graphics.SimpleFont;
import com.ovl.graphics.Sprite;
import com.ovl.utils.FastMath;
import com.ovl.utils.Vector2;

public class SimpleFontArm extends SimpleFont {
	private CustomFontArm font;
	
	private BufferedImage bufferedImage;
	private Graphics2D measureGraphics;
	private FontRenderContext frc;
	private Rectangle textBounds = new Rectangle();
	
	private Vector2 textSize = new Vector2();
	private Vector2 textOffset = new Vector2();
	
	public SimpleFontArm(){
		
	}
	
	@Override
	public void destroy() {
		super.destroy();
		measureGraphics.dispose();
	}
	
	@Override
	public CustomFont getFont(){
		return font;
	}
	
	@Override
	protected void initBufferedImage(int w, int h){
		bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		measureGraphics = (Graphics2D)bufferedImage.getGraphics();
		measureGraphics.setColor(java.awt.Color.WHITE);
		measureGraphics.setBackground(new java.awt.Color(0, true));
		
		measureGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		measureGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}
	
	@Override
	protected void prerenderText(){
		if (text == null || text.isEmpty()){
			setVisible(false);
			return;
		}
		
		setVisible(true);
		
		Color oldColor = null;
		if (sprite != null){
			oldColor = sprite.getColor();
			sprite.destroy();
		}

		String lines[] = text.split("\n");
		String longestLine = lines[0];
		for (String line : lines){
			if (line.length() > longestLine.length()){
				longestLine = line;
			}
		}
		
		Font f = font.get();
		GlyphVector gv = f.createGlyphVector(frc, longestLine);
		Rectangle rect = gv.getVisualBounds().getBounds();
		
		Vector2.pixelCoordsToNormal(textSize.set(rect.width, rect.height * lines.length));
		Vector2.pixelCoordsToNormal(textOffset.set(rect.x, rect.y));
		
		int newWidth = FastMath.nextPowerOfTwo(rect.width + rect.x);
		int newHeight = FastMath.nextPowerOfTwo(Math.abs(rect.y) + rect.height * lines.length);
		int newX = (int)((newWidth - rect.width - rect.x) * 0.5f);
		int newY = (int)((newHeight - rect.height * lines.length) * 0.5f);
		
		if (newWidth > bufferedImage.getWidth() || newHeight > bufferedImage.getHeight()){
			measureGraphics.dispose();
			initBufferedImage(newWidth, newHeight);
			measureGraphics.setFont(f);
		}
		else
		{
			measureGraphics.clearRect(0, 0, textBounds.width, textBounds.height);
		}
		
		textBounds.setBounds(0, 0, newWidth, newHeight);
		
		for (String line : lines){
			measureGraphics.drawString(line, newX, -rect.y + newY);
			newY += rect.height;
		}
		
		BufferedImage textSubImage = bufferedImage.getSubimage(0, 0, newWidth, newHeight);
		TextureLoaderArm textureLoader = ((TextureLoaderArm)RENDERER.getTextureLoader());
		setSprite(new Sprite(textureLoader.getTexture(textSubImage)));
		
		if (oldColor != null){
			sprite.setColor(oldColor);
		}
	}
	
	@Override
	public void setFont(CustomFont f){
		if (font != null && f.equals(font)){
			return;
		}
		
		font = (CustomFontArm)f;
		measureGraphics.setFont(font.get());
		frc = measureGraphics.getFontMetrics().getFontRenderContext();
		prerenderText();
	}
}
