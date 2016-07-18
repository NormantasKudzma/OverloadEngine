package engine;

import graphics.Color;
import graphics.Texture;
import graphics.TextureLoader;
import utils.Vector2;

public interface Renderer {
	public static final int DATA_PER_VERTEX = 8;
	public static final int VERTICES_PER_SPRITE = 4;
	public static final int DATA_PER_SPRITE = VERTICES_PER_SPRITE * DATA_PER_VERTEX;
	
	public void init();
	
	public int genSpriteId();
	
	public TextureLoader getTextureLoader();
	
	public void postRender();
	
	public void preRender();
	
	public void releaseId(int id);
	
	public void render(int id, Texture tex, Vector2 size, Vector2 position, Vector2 scale, float rotation);
	
	public void setColorData(int id, Color c);
	
	public void setVertexData(int id, Vector2 pos);
	
	public void setTextureData(int id, Vector2 tl, Vector2 br);
}
