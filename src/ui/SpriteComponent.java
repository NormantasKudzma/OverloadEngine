package ui;

import engine.BaseGame;
import graphics.Renderable;
import graphics.Sprite;
import utils.Vector2;

public class SpriteComponent extends Component{
	public enum EUIState {
		NORMAL(0),
		CLICKED(2);
		
		int index;
		
		private EUIState(int i){
			index = i;
		}
		
		public int getIndex(boolean isHovered){
			return index + (isHovered ? 1 : 0);
		}
	}

	private boolean isHoveredOver = false;
	private Renderable[] sprites;
	private EUIState state = EUIState.NORMAL;
					
	public SpriteComponent(BaseGame game) {
		super(game);
	}
	
	@Override
	public Renderable getSprite() {
		return sprites[EUIState.NORMAL.getIndex(false)];
	}
	
	public EUIState getState(){
		return state;
	}
	
	@Override
	protected void initialize() {
		sprites = new Sprite[EUIState.values().length * 2];
	}
	
	public boolean isHovered(){
		return isHoveredOver;
	}
	
	@Override
	public boolean onHover(Vector2 pos) {
		boolean ret = isMouseOver(pos);
		setHovered(ret);
		return ret;
	}
	
	public void setHovered(boolean isHovered){
		isHoveredOver = isHovered;
		setState(state);
	}
	
	public void setState(EUIState state){
		this.state = state;
		
		sprite = sprites[state.getIndex(isHoveredOver)];
		if (sprite == null){
			sprite = sprites[state.getIndex(false)];
		}
	}
	
	@Override
	public void setSprite(Renderable spr) {
		setSprite(spr, EUIState.NORMAL, false);
	}
	
	public void setSprite(Renderable spr, EUIState state, boolean isHover){
		sprites[state.getIndex(isHover)] = spr;
		setState(EUIState.NORMAL);
	}
	
	public void setSprites(Renderable[] sprites){
		for (int i = 0; i < this.sprites.length && i < sprites.length; ++i){
			this.sprites[i] = sprites[i];
		}
		setState(EUIState.NORMAL);
	}
}
