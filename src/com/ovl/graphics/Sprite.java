package com.ovl.graphics;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.Renderer;
import com.ovl.utils.ICloneable;
import com.ovl.utils.Vector2;

public class Sprite implements Renderable, ICloneable {
	protected static final Renderer renderer;
	protected static final String shaderName;
	
	private Texture texture;						// Sprite's texture
	private Vector2 textureSize = new Vector2();
	private Vector2 texTopLeft;		// Texture coordinates
	private Vector2 texBotRight;
	private Vector2 vertTopLeft;	// Vertices, scaled, rotated, positioned
	private Vector2 vertBotRight;
	
	private Color color;
	private Renderer.VboId id;
	
	static {
		shaderName = "Texture";
		renderer = OverloadEngine.getInstance().renderer;
		renderer.createShader(shaderName);
	}
	
	// Internal vector for render calculations
	//private Vector2 size = new Vector2();
	
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
		setTextureCoordinates(tl, br);
	}
	
	public Sprite(Texture tex, Vector2 tl, Vector2 br){
		init();
		texture = tex;
		setTextureCoordinates(tl, br);
	}
	
	public Sprite clone(){
		Sprite clone = new Sprite();
		clone.init();
		clone.texture = texture;
		clone.setTextureCoordinates(texTopLeft.copy(), texBotRight.copy());
		clone.textureSize = textureSize.copy();
		return clone;
	}
	
	public void destroy(){
		renderer.releaseId(id);
		id = null;
		texture = null;
		texTopLeft = null;
		texBotRight = null;
		textureSize = null;
	}
	
	public Color getColor(){
		return color;
	}
	
	public Vector2 getTextureSize(){
		return textureSize.copy();
	}
	
	public Vector2 getSize(){
		return textureSize.copy();
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
		sprite.setTextureSize(w, h);
		return sprite;
	}	
	
	private void init(){
		if ((id = renderer.generateId(shaderName, 4)) == null){
			renderer.createVbo(shaderName, 1024, 4);
			id = renderer.generateId(shaderName, 4);
		}
		
		setColor(Color.WHITE);
	}
	
	public void loadTexture(String path){
		texture = renderer.getTextureLoader().getTexture(path);
	}

	@Override
	public void render() {
		texture.bind();
		renderer.renderTextured(id, color);
	}
	
	public void setColor(Color c){
		if (c != null){
			color = c;
		}
	}
	
	private void setTextureCoordinates(Vector2 tl, Vector2 br){
		if (tl == null){
			texTopLeft = new Vector2();
		}
		else {
			texTopLeft = tl.copy();
		}

		if (br == null) {
			texBotRight = new Vector2(texture.getWidth(), texture.getHeight());
		}
		else {
			texBotRight = br.copy();
		}
		
		renderer.setTextureData(id, texTopLeft, texBotRight);
		setTextureSize(texture.getImageWidth(), texture.getImageHeight());
	}
	
	public void setTextureSize(int w, int h){
		textureSize.set(w, h);
		Vector2.pixelCoordsToNormal(textureSize);
	}
	
	// TODO: implement rotation
	public void updateVertices(Vector2 pos, Vector2 scale, float rotation)
	{
		Vector2 halfSize = textureSize.copy().mul(0.5f);
		vertTopLeft = pos.copy().add(-halfSize.x, halfSize.y).mul(scale);
		vertBotRight = pos.copy().add(halfSize.x, -halfSize.y).mul(scale);
		
		renderer.setVertexData(id, vertTopLeft, vertBotRight);
	}
}
