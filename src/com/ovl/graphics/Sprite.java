package com.ovl.graphics;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.Renderer;
import com.ovl.utils.ICloneable;
import com.ovl.utils.Vector2;

public class Sprite implements Renderable, ICloneable {
	protected static final Renderer renderer;
	private Texture texture;						// Sprite's texture
	private Vector2 internalScale = new Vector2();	// Vertex positioning in normalized coordinates (real object size)
	private Vector2 topLeft;
	private Vector2 botRight;
	private Color color;
	private Renderer.VboId id;
	
	static {
		renderer = OverloadEngine.getInstance().renderer;
	}
	
	// Internal vector for render calculations
	private Vector2 size = new Vector2();
	
	private Sprite(){
		
	}
	
	public Sprite(String path){
		this(path, new Vector2(), null);
	}
	
	public Sprite(Texture tex){
		this(tex, new Vector2(), null);
	}
	
	public Sprite(String path, Vector2 tl, Vector2 br){
		init();
		loadTexture(path);
		setClippingBounds(tl, br);
	}
	
	public Sprite(Texture tex, Vector2 tl, Vector2 br){
		init();
		texture = tex;
		setClippingBounds(tl, br);
	}
	
	public Sprite clone(){
		Sprite clone = new Sprite();
		clone.init();
		clone.texture = texture;
		clone.setClippingBounds(topLeft.copy(), botRight.copy());
		clone.setInternalScale(internalScale.copy());
		clone.size = size.copy();
		return clone;
	}
	
	public void destroy(){
		renderer.releaseId(id);
		id = null;
		texture = null;
		topLeft = null;
		botRight = null;
		internalScale = null;
		size = null;
	}
	
	public Color getColor(){
		return color;
	}
	
	public Vector2 getInternalScale(){
		return internalScale;
	}
	
	public Vector2 getSize(){
		return internalScale.copy();
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public static Sprite getSpriteFromSheet(int x, int y, int w, int h, Sprite sheet){
		Vector2 sheetSizeCoef = new Vector2(sheet.getTexture().getWidth(), sheet.getTexture().getHeight());
		sheetSizeCoef.div(sheet.getTexture().getImageWidth(), sheet.getTexture().getImageHeight());
		
		Vector2 topLeft = new Vector2(x, y);
		Vector2 botRight = topLeft.copy().add(new Vector2(w, h));
		
		topLeft.mul(sheetSizeCoef);
		botRight.mul(sheetSizeCoef);
		
		Sprite sprite = new Sprite(sheet.getTexture(), topLeft, botRight);
		sprite.setInternalScale(w, h);
		return sprite;
	}	
	
	private void init(){
		id = renderer.generateId(Renderer.VboType.Textured, 4);
		setColor(Color.WHITE);
	}
	
	public void loadTexture(String path){
		texture = renderer.getTextureLoader().getTexture(path);
	}
	
	public void render(){
		render(Vector2.one, Vector2.one, 0.0f);
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		float scaleY = rotation != 0.0f ? scale.y / OverloadEngine.getInstance().aspectRatio : scale.y;
		size.set(internalScale).mul(scale.x, scaleY).mul(0.5f);
		
		texture.bind();
		renderer.renderTextured(id, color, size, position, scale, rotation);
	}
	
	public void setColor(Color c){
		if (c != null){
			color = c;
		}
	}
	
	private void setClippingBounds(Vector2 topLeftCorner, Vector2 bottomRightCorner){
		if (topLeftCorner == null){
			topLeft = new Vector2();
		}
		else {
			topLeft = topLeftCorner;
		}

		if (bottomRightCorner == null) {
			botRight = new Vector2(texture.getWidth(), texture.getHeight());
		}
		else {
			botRight = bottomRightCorner;
		}
		
		renderer.setTextureData(id, topLeft, botRight);
		setInternalScale(texture.getImageWidth(), texture.getImageHeight());
	}
	
	public void setInternalScale(int w, int h){
		internalScale.set(w, h);
		Vector2.pixelCoordsToNormal(internalScale);
		setInternalScale(internalScale);
	}
	
	public void setInternalScale(Vector2 v){
		internalScale = v;
		renderer.setVertexData(id, internalScale);
	}
}
