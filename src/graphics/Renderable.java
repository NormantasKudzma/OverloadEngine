package graphics;

import utils.Vector2;

public interface Renderable {
	public void destroy();
	
	/**
	 * @return Size of this renderable (unscaled)
	 */
	public Vector2 getSize();
	
	public void render();
	public void render(Vector2 position, Vector2 scale, float rotation);
	
	public Color getColor();
	public void setColor(Color c);
}
