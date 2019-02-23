package com.ovl.graphics;

import java.util.ArrayList;
import java.util.HashMap;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.Vbo;
import com.ovl.game.GameObject;

public class BadLayer extends Layer {

	protected ArrayList<GameObject> gameObjectList = new ArrayList<GameObject>();
	protected ArrayList<GameObject> destroyList = new ArrayList<GameObject>();
	
	private Vbo badVbo;

	public BadLayer(String name, int index) {
		super(name, index);
	}
	
	@Override
	public void addObject(GameObject gameObject){
		gameObjectList.add(gameObject);
		
	}
	
	@Override
	public void clear(){
		gameObjectList.clear();
		destroyMarkedObjects();
	}
	
	public void destroy() {
		for (GameObject i : gameObjectList) {
			i.destroy();
		}
		clear();
	}
	
	public void destroyMarkedObjects(){
		if (destroyList.isEmpty()){
			return;
		}
		
		for (int i = 0; i < destroyList.size(); ++i){
			destroyList.get(i).onDestroy();
			destroyList.get(i).destroy();
		}
		destroyList.clear();
	}
	
	public ArrayList<GameObject> getDestroyList(){
		return destroyList;
	}
	
	public void finish() {
		Renderer renderer = OverloadEngine.getInstance().renderer;
		badVbo = renderer.createVbo(Sprite.defaultShaderName, gameObjectList.size() * 2 * Renderer.VERTICES_PER_SPRITE, Renderer.VERTICES_PER_SPRITE);
		
		for (int i = 0; i < gameObjectList.size(); ++i){
			GameObject obj = gameObjectList.get(i);
			Sprite s = (Sprite)obj.getSprite();
			
			HashMap<String, ParamSetter> params = new HashMap<String, ParamSetter>();
			params.put(Shader.U_COLOR, ParamSetterFactory.buildDefault(Sprite.defaultShader, Shader.U_COLOR));
			params.put(Shader.U_TEXTURE, ParamSetterFactory.build(Sprite.defaultShader, Shader.U_TEXTURE, s.getTexture()));
			params.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(Sprite.defaultShader, Shader.U_MVPMATRIX));

			s.useShader(badVbo, params);
		}
	}
	
	@Override
	public void render() {
		for (int i = 0; i < gameObjectList.size(); ++i){
			gameObjectList.get(i).render();
		}
	}

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < gameObjectList.size(); ++i){
			gameObjectList.get(i).update(deltaTime);
		}
	}
}
