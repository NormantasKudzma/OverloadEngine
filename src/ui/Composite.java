package ui;

import java.util.ArrayList;

import utils.Vector2;

import engine.BaseGame;

public class Composite extends Component {
	protected Component lastClickable;
	protected ArrayList<Component> children;
	protected ArrayList<Component> destroyList;
	
	public Composite(BaseGame game) {
		super(game);
	}
	
	public void addChild(Component c){
		if (c != null){
			c.setParent(this);
			children.add(c);
			c.setPosition(c.getPosition().copy().add(getPosition()));
		}
	}
	
	public ArrayList<Component> getChildren(){
		return children;
	}
	
	@Override
	protected void initialize(){
		super.initialize();
		children = new ArrayList<Component>(1);
		destroyList = new ArrayList<Component>(1);
	}
	
	@Override
	public boolean onHover(Vector2 pos) {
		if (!isVisible){
			return false;
		}
		
		boolean ret = super.onHover(pos);
		
		lastClickable = null;
		if (ret){
			for (Component child : children){
				if (child.onHover(pos)){
					lastClickable = child;
				}
			}
		}
	     
		return ret;
	}

	@Override
	public boolean onClick(Vector2 pos) {
		if (!super.onClick(pos)){
			return false;
		}
		
		if (lastClickable != null){
			lastClickable.onClick(pos);
		}
		
		return true;
	}
	
	@Override
	public void render() {
		if (isVisible){
			if (sprite != null){
				super.render();
			}
			
			for (Component component : children){
				component.render();
			}
		}
	}
	
	public void setPosition(Vector2 pos){
		Vector2 delta = pos.copy().sub(getPosition());
		for (Component component : children){
			component.setPosition(component.getPosition().copy().add(delta));
		}
		super.setPosition(pos.x, pos.y);
	}
	
	public void setPosition(float x, float y){	
		setPosition(new Vector2(x, y));
	}
	
	@Override
	public void update(float deltaTime) {
		if (isVisible){
			super.update(deltaTime);
			for (Component component : children){
				component.update(deltaTime);
				if (component.isDestroyed()){
					destroyList.add(component);
				}
			}
			
			for (int i = 0; i < destroyList.size(); ++i){
				destroyList.get(i).onDestroy();
				destroyList.get(i).destroy();
			}
			destroyList.clear();
		}
	}
}
