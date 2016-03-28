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
	
	public void setColor(Color c){
		font.setColor(c);
	}
	
	@Override
	public void render() {
		render(getPosition(), getScale(), getRotation());
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		font.render(position, scale, rotation);
	}
}
