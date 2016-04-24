package graphics;

import utils.Vector2;

public interface Renderable {
	public void destroy();
	
	public void render();
	public void render(Vector2 position, Vector2 scale, float rotation);
	
	// Color is set separately, so buffers are not modified during render
	public void setColor(Color c);
}
