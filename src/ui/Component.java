package ui;

import java.util.ArrayList;

import physics.PhysicsBody;

import utils.Vector2;
import engine.BaseGame;
import engine.Entity;
import graphics.Sprite;

public abstract class Component extends Entity<Sprite> implements IClickable{
	protected Component parent;
	protected ArrayList<Component> children = new ArrayList<Component>(1);
	protected ArrayList<Component> destroyList = new ArrayList<Component>(1);
	protected String name;
	protected Component lastClickable;
	
	public Component(BaseGame game){
		super(game);
		initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
		initialize();
	}	
	
	public void addChild(Component c){
		if (c != null){
			c.setParent(this);
			children.add(c);
			c.setPosition(c.getPosition().add(getPosition()));
		}
	}
	
	public void clickFunction(){
		//
	}
	
	public ArrayList<Component> getChildren(){
		return children;
	}
	
	public String getName(){
		return name;
	}
	
	public Component getParent(){
		return parent;
	}

	protected abstract void initialize();
	
	@Override
	public boolean isMouseOver(Vector2 pos) {
		if (!isVisible || sprite == null){
			return false;
		}
		
		return  pos.x < getPosition().x + sprite.getRenderOffset().x &&
				pos.x > getPosition().x - sprite.getRenderOffset().x &&
				pos.y < getPosition().y + sprite.getRenderOffset().y &&
			    pos.y > getPosition().y - sprite.getRenderOffset().y;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (parent != null){
			parent.getChildren().remove(this);
		}
	}
	
	@Override
	public boolean onHover(Vector2 pos) {
		if (!isVisible){
			return false;
		}
		
		boolean ret = isMouseOver(pos);
		
		lastClickable = null;
		if (ret){
			for (Component child : children){
				if (child.onHover(pos)){
					lastClickable = child;
					break;
				}
			}
		}
	     
		return ret;
	}

	@Override
	public boolean onClick(Vector2 pos) {
		if (!isVisible){
			return false;
		}
		
		boolean ret = isMouseOver(pos);
		
		if (lastClickable != null){
			lastClickable.clickFunction();
		}
		
		return ret;
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
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setParent(Component c){
		parent = c;
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
