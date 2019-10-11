package com.ovl.graphics;

import java.util.ArrayList;

import com.ovl.engine.ShaderParams;
import com.ovl.engine.Vbo;
import com.ovl.game.GameObject;

public class SortedLayer extends Layer {
	private static final int FLAG_TEX_TARGET = 	0;
	private static final int FLAG_TEX_ID = 		8;
	private static final int FLAG_VBO_ID = 		16;
	private static final int FLAG_SHADER_ID = 	24;
	
	private class Item {
		int flag;
		GameObject obj;
		
		Item(int f, GameObject o){
			flag = f;
			obj = o;
		}
	}
	
	private ArrayList<Item> objects = new ArrayList<Item>();
	
	public SortedLayer(String name, int index) {
		super(name, index);
	}

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < objects.size(); ++i){
			objects.get(i).obj.update(deltaTime);
		}
	}

	@Override
	public void addObject(GameObject obj) {
		if (obj == null){
			return;
		}
		
		int flag = 0x00000000;
		
		if (obj.getSprite() != null){
			ShaderParams params = obj.getSprite().getShaderParams();
			Vbo vbo = params.getVbo();
			flag |= ((vbo.getShader().getProgramId() + 1) & 0xff) << FLAG_SHADER_ID;
			flag |= ((vbo.getId() + 1) & 0xff) << FLAG_VBO_ID;
		}
		
		if (obj.getSprite() instanceof Sprite){
			Sprite sp = (Sprite)obj.getSprite();
			if (sp.getTexture() != null){
				Texture tex = sp.getTexture();
				flag |= (tex.getId() + 1) << FLAG_TEX_ID;
				flag |= (tex.getTarget() + 1) << FLAG_TEX_TARGET;
			}
		}
		
		Item item = new Item(flag, obj);
		boolean added = false;
		for (int i = 0; i < objects.size(); ++i){
			if (objects.get(i).flag - flag <= 0){
				objects.add(i, item);
				added = true;
				break;
			}
		}
		if (!added){
			objects.add(item);
		}
	}

	public void removeObject(GameObject gameObject){
		objects.remove(gameObject);
	}

	@Override
	public void destroy() {
		for (int i = 0; i < objects.size(); ++i){
			objects.get(i).obj.destroy();
		}
		objects.clear();
	}

	@Override
	public void render() {
		for (int i = 0; i < objects.size(); ++i){
			objects.get(i).obj.render();
		}
	}

	@Override
	public void unloadResources() {
		for (int i = 0; i < objects.size(); ++i){
			objects.get(i).obj.unloadResources();
		}
	}

	@Override
	public void reloadResources() {
		for (int i = 0; i < objects.size(); ++i){
			objects.get(i).obj.reloadResources();
		}
	}
}
