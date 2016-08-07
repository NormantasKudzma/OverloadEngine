package com.ovl.graphics;

import com.ovl.engine.GameObject;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.Renderer;
import com.ovl.physics.PhysicsBody;
import com.ovl.utils.Vector2;

public abstract class SimpleFont extends GameObject {
	public static final CustomFont DEFAULT_FONT;
	public static final Renderer RENDERER;
	
	protected String text;
	
	protected Vector2 textSize = new Vector2();
	protected Vector2 textOffset = new Vector2();
	
	static {
		RENDERER = OverloadEngine.getInstance().renderer;
		DEFAULT_FONT = RENDERER.getFontBuilder().buildFont("Consolas", 0, 32);
	}
	
	public static SimpleFont create(String text){
		return create(text, DEFAULT_FONT);
	}
	
	public static SimpleFont create(String text, CustomFont f){
		SimpleFont cf = RENDERER.getFontBuilder().createFontObject();		
		cf.initBufferedImage(1024, 128);
		cf.initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
		cf.text = text;
		cf.setFont(f);
		return cf;
	}
	
	public abstract CustomFont getFont();
	
	public static CustomFont getDefaultFont(){
		return DEFAULT_FONT;
	}
	
	public String getText() {
		return text;
	}
	
	public Vector2 getTextOffset(){
		return textOffset;
	}
	
	public Vector2 getTextSize(){
		return textSize;
	}
	
	protected abstract void initBufferedImage(int w, int h);
	
	protected abstract void prerenderText();
	
	public abstract void setFont(CustomFont f);
	
	public void setText(String text) {
		if (this.text != null && text.equals(this.text)){
			return;
		}
		
		this.text = text;
		prerenderText();
	}
}
