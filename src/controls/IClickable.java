package controls;

import utils.Vector2;

public interface IClickable {
	public boolean isMouseOver(Vector2 pos);
	public boolean onHover(Vector2 pos);
	public boolean onClick(Vector2 pos);
}
