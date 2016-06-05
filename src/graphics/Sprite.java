package graphics;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import physics.Transform;
import utils.ICloneable;
import utils.Vector2;
import engine.OverloadEngine;
import engine.Updatable;

public class Sprite implements Renderable, Updatable, ICloneable {
	private Renderer renderer;
	private Texture texture;						// Sprite's texture
	private Vector2 internalScale = new Vector2();	// Vertex positioning in normalized coordinates (real object size)
	private Vector2 topLeft;
	private Vector2 botRight;
	private int id;
	
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
		texture = null;
		renderer = null;
		topLeft = null;
		botRight = null;
		internalScale = null;
		size = null;
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
		renderer = Renderer.getInstance();
		id = renderer.genSpriteId();
		renderer.setColorData(id, Color.WHITE);
	}
	
	public void loadTexture(String path){
		try {
			texture = TextureLoader.getInstance().getTexture(path);
		}
		catch (IOException e){
			System.err.println("Could not load texture from " + path);
			e.printStackTrace();
		}
	}
	
	public void render(){
		render(Vector2.one, Vector2.one, 0.0f);
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		// store the current model matrix
		glPushMatrix();

		// calculate the center pivot of object
		// TODO: implement pivot points
		float scaleY = rotation != 0.0f ? scale.y / OverloadEngine.aspectRatio : scale.y;
		size.set(internalScale).mul(scale.x, scaleY).mul(0.5f);
		
		texture.bind();

		glTranslatef(position.x, position.y, 0);
		glRotatef(rotation, 0, 0, 1.0f);
		glTranslatef(-size.x, size.y, 0);
		glScalef(scale.x, -scaleY, 1.0f);
 
		renderer.render(id);

        // Restore the model view matrix to prevent contamination
		glPopMatrix();
	}
	
	public void setColor(Color c){
		if (c != null){
			renderer.setColorData(id, c);
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
	
	@Override
	public void update(float deltaTime) {
		// stub
	}
}
