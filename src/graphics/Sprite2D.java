package graphics;

import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glTranslatef;

import graphics.Color;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

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
	private int vboId = GL15.glGenBuffers();
	private FloatBuffer vbo = BufferUtils.createFloatBuffer(32);
	
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
			texture = TextureLoader.getInstance().getTexture(path);
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
 
		// translate to the right location and prepare to draw
		renderOffset.set(internalScale).mul(scale).mul(0.5f);
        
		glTranslatef(position.x - renderOffset.x, position.y + renderOffset.y, 0);
		glScalef(scale.x, -scale.y, 1.0f);
		glRotatef(rotation, 0, 0, 1.0f);
 
		setBufferColors(c);
		
		texture.bind();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 32, 0);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, 8);
		GL11.glColorPointer(4, GL11.GL_FLOAT, 32, 16);

		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        // Restore the model view matrix to prevent contamination
		glPopMatrix();
	}
	
	public void setBufferColors(Color c){
		float[] rgba = c.getRgba();
		for (int i = 4; i < 32; i += 8){
			vbo.put(i, rgba[0]).put(i + 1, rgba[1]).put(i + 2, rgba[2]).put(i + 3, rgba[3]);
		}
		vbo.rewind();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo, GL15.GL_STATIC_DRAW);
	}

	private void setBufferTexCoords(Vector2 tl, Vector2 br){
		vbo.put(2, tl.x).put(3, tl.y)
		.put(10, br.x).put(11, tl.y)
		.put(18, br.x).put(19, br.y)
		.put(26, tl.x).put(27, br.y);
		vbo.rewind();
		
		texture.bind();   
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo, GL15.GL_STATIC_DRAW);
	}
	
	private void setBufferVertices(Vector2 pos){
		vbo.put(8, pos.x)
		.put(16, pos.x)
		.put(17, pos.y)
		.put(25, pos.y);
		
		vbo.rewind();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo, GL15.GL_STATIC_DRAW);
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
		
		setBufferTexCoords(topLeft, botRight);
	}
	
	public void setInternalScale(float w, float h){
		internalScale.set(w, h);
		Vector2.pixelCoordsToNormal(internalScale);

		setBufferVertices(internalScale);
	}
	
	@Override
	public void update(float deltaTime) {
		// stub
	}
}
