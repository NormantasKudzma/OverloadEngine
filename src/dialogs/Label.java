package dialogs;

import engine.BaseGame;
import graphics.Color;
import graphics.SimpleFont;
import utils.Vector2;

public class Label extends Component{
	private SimpleFont font;
	
	public Label(BaseGame game, String text){
		this(game, new SimpleFont(text));
	}
	
	public Label(BaseGame game, SimpleFont font){
		super(game);
		this.font = font;
	}
	
	public String getText(){
		return font.getText();
	}
	
	@Override
	protected void initialize() {
		
	}
	
	public void setText(String text){
		font.setText(text);
	}
	
	@Override
	public void render() {
		render(getPosition(), getScale(), getRotation(), getColor());
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation, Color c) {
		font.render(position, scale, rotation, c);
	}
}
