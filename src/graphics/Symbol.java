package graphics;

import utils.Vector2;

public class Symbol {
	public Sprite2D sprite;
	public Vector2 offset;
	
	public Symbol(Sprite2D spr, Vector2 offset){
		sprite = spr;
		this.offset = offset;
	}
}
