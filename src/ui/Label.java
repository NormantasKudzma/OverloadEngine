package ui;

import engine.BaseGame;
import graphics.Color;
import graphics.SimpleFont;

import java.awt.Font;

import utils.Vector2;

public class Label extends Component {
	private SimpleFont font;
	
	public Label(BaseGame game, String text){
		this(game, new SimpleFont(text));
	}
	
	public Label(BaseGame game, SimpleFont font){
		super(game);
		this.font = font;
	}
	
	public SimpleFont getSimpleFont(){
		return font;
	}
	
	public String getText(){
		return font.getText();
	}
	
	public void setFont(Font f){
		font.setFont(f);
	}
	
	public void setText(String text){
		font.setText(text);
	}
	
	public void setColor(Color c){
		font.setColor(c);
	}
	
	@Override
	public void render() {
		render(getPosition(), getScale(), getRotation());
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		if (isVisible){
			font.render(position, scale, rotation);
		}
	}
}
