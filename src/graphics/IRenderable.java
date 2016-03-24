package graphics;

import graphics.Color;

import physics.Transform;
import utils.Vector2;

public interface IRenderable {
	public void render();
	public void render(Transform t, Color c);
	public void render(Vector2 position, Vector2 scale, float rotation, Color c);
}
