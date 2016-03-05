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
import game.IUpdatable;

import java.io.IOException;

import utils.Vector2;

public class Sprite2D implements IRenderable, IUpdatable {
	/*private static final float DEFAULT_SPRITE_W = (float)OverloadEngine.gridW / (float)OverloadEngine.frameWidth;
	private static final float DEFAULT_SPRITE_H = (float)OverloadEngine.gridH / (float)OverloadEngine.frameHeight;*/
	
	private Texture texture;	// Sprite's texture
	private Vector2 internalScale = new Vector2(1.0f, 1.0f);	// Sprite size in game units (0;0)->(2;2)
	private Vector2 topLeft;
	private Vector2 botRight;
	private Vector2 halfSize;
	
	// Internal vectors for render calculations
	private Vector2 renderOffset = new Vector2();
	private Vector2 renderFullScale = new Vector2();
	
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
		texture = tex;
		setClippingBounds(tl, br);
		//internalScale.set(DEFAULT_SPRITE_SIZE / texture.getWidth(), DEFAULT_SPRITE_SIZE / texture.getHeight());
		//internalScale.set(DEFAULT_SPRITE_W / texture.getWidth(), DEFAULT_SPRITE_H / texture.getHeight());
	}
	
	public Vector2 getInternalScale(){
		return internalScale;
	}
	
	public Vector2 getHalfSize(){
		return halfSize;
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public Vector2 getRenderOffset(){
		return renderOffset;
	}
	
	public void loadTexture(String path){
		try {
			texture = TextureLoader.getInstance().getTexture(path);
			//internalScale.set(DEFAULT_SPRITE_W / texture.getWidth(), DEFAULT_SPRITE_H / texture.getHeight());
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void render(){
		render(Vector2.one);
	}
	
	public void render(Vector2 position){
		render(position, 0.0f);
	}
	
	public void render(Vector2 position, float rotation){
		render(position, rotation, Vector2.one);
	}
	
	@Override
	public void render(Vector2 position, float rotation, Vector2 scale) {
		// store the current model matrix
        glPushMatrix();
        // bind to the appropriate texture for this sprite
        texture.bind();
 
        // translate to the right location and prepare to draw
        renderFullScale.set(scale).mul(internalScale);
        renderOffset.set(halfSize).mul(renderFullScale);
        
        glTranslatef(position.x - topLeft.x * renderFullScale.x - renderOffset.x, position.y + topLeft.y * renderFullScale.y + renderOffset.y, 0);
        glScalef(renderFullScale.x, -renderFullScale.y, 1.0f);
        glRotatef(rotation, 0, 0, 1.0f);
 
        // draw a quad textured to match the sprite
        glBegin(GL_QUADS);
        {
        	glTexCoord2f(topLeft.x, topLeft.y);
        	glVertex2f(topLeft.x, topLeft.y);
 
        	glTexCoord2f(topLeft.x, botRight.y);
        	glVertex2f(topLeft.x, botRight.y);
 
			glTexCoord2f(botRight.x, botRight.y);
			glVertex2f(botRight.x, botRight.y);
 
			glTexCoord2f(botRight.x, topLeft.y);
			glVertex2f(botRight.x, topLeft.y);
        }
        glEnd();
 
        // restore the model view matrix to prevent contamination
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
		
		halfSize = Vector2.sub(botRight, topLeft).mul(0.5f);
	}
	
	@Override
	public void update(float deltaTime) {
		// stub
	}
}
