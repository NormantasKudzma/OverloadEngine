package graphics;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

import physics.Transform;

import utils.Vector2;
import engine.IUpdatable;

public class Sprite2D implements IRenderable, IUpdatable {
	private Texture texture;						// Sprite's texture
	private Vector2 internalScale = new Vector2();	// Vertex positioning in normalized coordinates (real object size)
	private Vector2 topLeft;
	private Vector2 botRight;
	
	// Internal vector for render calculations
	private Vector2 renderOffset = new Vector2();
	
	public Sprite2D(){}
	
	public Sprite2D(String path){
		this(path, new Vector2(), null);
	}
	
	public Sprite2D(Texture tex){
		this(tex, new Vector2(), null);
	}
	
	public Sprite2D(String path, Vector2 tl, Vector2 br){
		loadTexture(path);
		setClippingBounds(tl, br);
	}
	
	public Sprite2D(Texture tex, Vector2 tl, Vector2 br){
		setTexture(tex);
		setClippingBounds(tl, br);
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
	
	public void loadTexture(String path){
		try {
			setTexture(TextureLoader.getInstance().getTexture(path));
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void render(){
		render(Vector2.one, Vector2.one, 0.0f, null);
	}
	
	@Override
	public void render(Transform t, Color c) {
		render(t.getPosition(), t.getScale(), t.getRotation(), c);
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation, Color c) {
		// store the current model matrix
		glPushMatrix();
		if (c != null){
			GL11.glColor4ub(c.getRedByte(), c.getGreenByte(), c.getBlueByte(), c.getAlphaByte());
		}
		// bind to the appropriate texture for this sprite
		texture.bind();
 
		// translate to the right location and prepare to draw
		renderOffset.set(internalScale).mul(scale).mul(0.5f);
        
		glTranslatef(position.x - renderOffset.x, position.y + renderOffset.y, 0);
		glScalef(scale.x, -scale.y, 1.0f);
		glRotatef(rotation, 0, 0, 1.0f);
 
		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			glTexCoord2f(topLeft.x, topLeft.y);
			glVertex2f(0.0f, 0.0f);
 
			glTexCoord2f(topLeft.x, botRight.y);
			glVertex2f(0.0f, internalScale.y);
 
			glTexCoord2f(botRight.x, botRight.y);
			glVertex2f(internalScale.x, internalScale.y);
 
			glTexCoord2f(botRight.x, topLeft.y);
			glVertex2f(internalScale.x, 0.0f);
		}
		glEnd();
 
		// Restore colors and alpha
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        // Restore the model view matrix to prevent contamination
		glPopMatrix();
	}
	
	public void setClippingBounds(Vector2 topLeftCorner, Vector2 bottomRightCorner){
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
	}
	
	public void setInternalScale(float w, float h){
		internalScale.set(w, h);
		Vector2.pixelCoordsToNormal(internalScale);
	}
	
	private void setTexture(Texture tex){
		texture = tex;
	}
	
	@Override
	public void update(float deltaTime) {
		// stub
	}
}
