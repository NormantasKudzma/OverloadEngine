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
	private static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
	
	private Texture texture;						// Sprite's texture
	private Vector2 internalScale = new Vector2();	// Vertex positioning in normalized coordinates (real object size)
	private Vector2 topLeft;
	private Vector2 botRight;
	
	// Internal vector for render calculations
	private Vector2 renderOffset = new Vector2();
	private int colorBufferId = GL15.glGenBuffers();
	private int posBufferId = GL15.glGenBuffers();
	private int texBufferId = GL15.glGenBuffers();
	private FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(16);
	private FloatBuffer posBuffer = BufferUtils.createFloatBuffer(8);
	private FloatBuffer texBuffer = BufferUtils.createFloatBuffer(8);
	
	public Sprite2D(String path){
		this(path, new Vector2(), null);
	}
	
	public Sprite2D(Texture tex){
		this(tex, new Vector2(), null);
	}
	
	public Sprite2D(String path, Vector2 tl, Vector2 br){
		loadTexture(path);
		setClippingBounds(tl, br);
		init();
	}
	
	public Sprite2D(Texture tex, Vector2 tl, Vector2 br){
		setTexture(tex);
		setClippingBounds(tl, br);
		init();
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
		texture.bind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posBufferId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW);
	      
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texBufferId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, texBuffer, GL15.GL_STATIC_DRAW);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, colorBuffer, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
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
 
		// translate to the right location and prepare to draw
		renderOffset.set(internalScale).mul(scale).mul(0.5f);
        
		glTranslatef(position.x - renderOffset.x, position.y + renderOffset.y, 0);
		glScalef(scale.x, -scale.y, 1.0f);
		glRotatef(rotation, 0, 0, 1.0f);
 
		if (c != null){
			setBufferColors(c);
		}
		else {
			setBufferColors(WHITE);
		}
		
		texture.bind();
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, posBufferId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 0, 0);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, texBufferId);
		GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		
		/*GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, colorBufferId);
		GL11.glColorPointer(4, GL11.GL_FLOAT, 0, 0);*/
		         
		GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		//GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDrawArrays(GL11.GL_QUADS, 0, 4);
		//GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
		GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
		GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			
		// Bind and render buffer		
		/*GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL11.glVertexPointer(2, GL11.GL_FLOAT, 32, 0);
		 
		GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		//GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo, GL15.GL_DYNAMIC_DRAW);
		GL11.glColorPointer(4, GL11.GL_FLOAT, 32, 16);*/
		
		/*texture.bind();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vbo, GL15.GL_DYNAMIC_DRAW);
		GL11.glTexCoordPointer(2, 32, );*/
		
		//GL11.glDrawArrays(GL11.GL_TRIANGLE_FAN, 0, 4);

        // Restore the model view matrix to prevent contamination
		glPopMatrix();
	}
	
	private void setBufferColors(Color c){		
		colorBuffer.clear();
		for (int i = 0; i < 16; i += 4){
			colorBuffer.put(c.r)
				.put(c.g)
				.put(c.b)
				.put(c.a);
		}
		colorBuffer.flip();
	}

	private void setBufferTexCoords(Vector2 tl, Vector2 br){
		float buf[] = new float[]{
			tl.x, tl.y,
			br.x, tl.y,
			br.x, br.y,
			tl.x, br.y
		};
		texBuffer.put(buf);
		texBuffer.flip();
		init();
	}
	
	private void setBufferVertices(Vector2 pos){
		float buf[] = new float[]{
			0.0f, 0.0f,
			pos.x, 0.0f,
			pos.x, pos.y,
			0.0f, pos.y
		};
		posBuffer.put(buf);
		posBuffer.flip();
		init();
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
	
	private void setTexture(Texture tex){
		texture = tex;
	}
	
	@Override
	public void update(float deltaTime) {
		// stub
	}
}
