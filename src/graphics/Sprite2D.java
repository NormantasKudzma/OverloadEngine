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
import engine.Updatable;

public class Sprite2D implements Renderable, Updatable, ICloneable {
	private Renderer renderer;
	private Texture texture;						// Sprite's texture
	private Vector2 internalScale = new Vector2();	// Vertex positioning in normalized coordinates (real object size)
	private Vector2 topLeft;
	private Vector2 botRight;
	private int id;
	
	// Internal vector for render calculations
	private Vector2 renderOffset = new Vector2();
	
	private Sprite2D(){
		
	}
	
	public Sprite2D(String path){
		this(path, new Vector2(), null);
	}
	
	public Sprite2D(Texture tex){
		this(tex, new Vector2(), null);
	}
	
	public Sprite2D(String path, Vector2 tl, Vector2 br){
		init();
		loadTexture(path);
		setClippingBounds(tl, br);
	}
	
	public Sprite2D(Texture tex, Vector2 tl, Vector2 br){
		init();
		texture = tex;
		setClippingBounds(tl, br);
	}
	
	public Sprite2D clone(){
		Sprite2D clone = new Sprite2D();
		clone.init();
		clone.texture = texture;
		clone.setClippingBounds(topLeft.copy(), botRight.copy());
		clone.setInternalScale(internalScale.copy());
		clone.renderOffset = renderOffset.copy();
		return clone;
	}
	
	public void destroy(){
		renderer.releaseId(id);
		texture = null;
	}
	
	public Vector2 getInternalScale(){
		return internalScale;
	}
	
	public Vector2 getHalfSize(){
		return internalScale.copy().mul(0.5f);
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public Vector2 getRenderOffset(){
		return renderOffset;
	}
	
	public static Sprite2D getSpriteFromSheet(int x, int y, int w, int h, Sprite2D sheet){
		Vector2 sheetSizeCoef = new Vector2(sheet.getTexture().getWidth(), sheet.getTexture().getHeight());
		sheetSizeCoef.div(sheet.getTexture().getImageWidth(), sheet.getTexture().getImageHeight());
		
		Vector2 topLeft = new Vector2(x, y);
		Vector2 botRight = topLeft.copy().add(new Vector2(w, h));
		
		topLeft.mul(sheetSizeCoef);
		botRight.mul(sheetSizeCoef);
		
		Sprite2D sprite = new Sprite2D(sheet.getTexture(), topLeft, botRight);
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
 
		// translate to the right location and prepare to draw
		renderOffset.set(internalScale).mul(scale).mul(0.5f);
		
		texture.bind();

		glTranslatef(position.x - renderOffset.x, position.y + renderOffset.y, 0);
		glRotatef(rotation, 0, 0, 1.0f);
		glScalef(scale.x, -scale.y, 1.0f);
 
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
