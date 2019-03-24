package com.ovl.testing;

import java.util.ArrayList;
import java.util.HashMap;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.Vbo;
import com.ovl.game.BaseGame;
import com.ovl.game.GameObject;
import com.ovl.graphics.DynamicBatchLayer;
import com.ovl.graphics.Layer;
import com.ovl.graphics.Primitive;
import com.ovl.graphics.Sprite;
import com.ovl.graphics.StaticBatchLayer;
import com.ovl.ui.Button;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;

public class TestGame extends BaseGame {
	private Primitive highlight;
	private ArrayList<Vector2> clicks = new ArrayList<Vector2>();
	
	Layer unsortedLayer;// = new UnsortedLayer("unsorted", 100);
	Layer sortedLayer;// = new SortedLayer("sorted", 110);
	StaticBatchLayer staticBatchLayer;// = new StaticBatchLayer("static", 120);
	DynamicBatchLayer dynamicBatchLayer;
	
	Button showUnsorted;
	Button showSorted;
	Button showStatic;
	Button showDynamic;
	Button showBad;

	public void init(){
		{
			Renderer r = OverloadEngine.getInstance().renderer;
			Shader outlineShader = r.createShader("Outline");
			Vbo outlineVbo = r.createVbo("Outline", 16, 4);

			/*Shader outlineShader = r.createShader("Texture");
			Vbo outlineVbo = r.createVbo("Texture", 16, 4);*/
			
			Sprite sprite = new Sprite(Paths.getResources() + "r.png");
			
			HashMap<String, ParamSetter> params = new HashMap<>();
			params.put(Shader.U_COLOR, ParamSetterFactory.build(outlineShader, Shader.U_COLOR, sprite.getColor()));
			params.put(Shader.U_TEXTURE, ParamSetterFactory.build(outlineShader, Shader.U_TEXTURE, sprite.getTexture()));
			params.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(outlineShader, Shader.U_MVPMATRIX));
			sprite.useShader(outlineVbo, params);
			
			GameObject obj = new GameObject();
			obj.setSprite(sprite);
			obj.setPosition(-0.2f, 0.0f);
			addObject(obj);
		}
		{
			/*Sprite sprite = new Sprite(Paths.getResources() + "r.png");
			GameObject obj = new GameObject();
			obj.setSprite(sprite);
			obj.setPosition(0.2f, 0.0f);
			addObject(obj);*/
		}
	}
	
	public void init_o() {
		/*showUnsorted = new Button(this, "");
		showUnsorted.setPosition(-0.7f, 0.8f);
		showUnsorted.setScale(0.25f, 0.25f);
		showUnsorted.setClickListener(new OnClickListener(){
			@Override
			public void clickFunction(Vector2 pos) {
				removeLayer(unsortedLayer.getIndex());
				removeLayer(sortedLayer.getIndex());
				removeLayer(staticBatchLayer.getIndex());
				removeLayer(dynamicBatchLayer.getIndex());
				removeLayer(badLayer.getIndex());
				addLayer(unsortedLayer);
			}
		});
		addObject(showUnsorted);
		
		SimpleFont textUnsorted = SimpleFont.create("Unsorted");
		textUnsorted.setPosition(-0.7f, 0.8f);
		addObject(textUnsorted);
		
		showSorted = new Button(this, "");
		showSorted.setPosition(-0.7f, 0.6f);
		showSorted.setScale(0.25f, 0.25f);
		showSorted.setClickListener(new OnClickListener(){
			@Override
			public void clickFunction(Vector2 pos) {
				removeLayer(unsortedLayer.getIndex());
				removeLayer(sortedLayer.getIndex());
				removeLayer(staticBatchLayer.getIndex());
				removeLayer(dynamicBatchLayer.getIndex());
				removeLayer(badLayer.getIndex());
				addLayer(sortedLayer);
			}
		});
		addObject(showSorted);
		
		SimpleFont textSorted = SimpleFont.create("Sorted");
		textSorted.setPosition(-0.7f, 0.6f);
		addObject(textSorted);
		
		showStatic = new Button(this, "");
		showStatic.setPosition(-0.7f, 0.4f);
		showStatic.setScale(0.25f, 0.25f);
		showStatic.setClickListener(new OnClickListener(){
			@Override
			public void clickFunction(Vector2 pos) {
				removeLayer(unsortedLayer.getIndex());
				removeLayer(sortedLayer.getIndex());
				removeLayer(staticBatchLayer.getIndex());
				removeLayer(dynamicBatchLayer.getIndex());
				removeLayer(badLayer.getIndex());
				addLayer(staticBatchLayer);
			}
		});
		addObject(showStatic);
		
		SimpleFont textStatic = SimpleFont.create("Static");
		textStatic.setPosition(-0.7f, 0.4f);
		addObject(textStatic);
		
		showDynamic = new Button(this, "");
		showDynamic.setPosition(-0.7f, 0.2f);
		showDynamic.setScale(0.25f, 0.25f);
		showDynamic.setClickListener(new OnClickListener(){
			@Override
			public void clickFunction(Vector2 pos) {
				removeLayer(unsortedLayer.getIndex());
				removeLayer(sortedLayer.getIndex());
				removeLayer(staticBatchLayer.getIndex());
				removeLayer(dynamicBatchLayer.getIndex());
				removeLayer(badLayer.getIndex());
				addLayer(dynamicBatchLayer);
			}
		});
		addObject(showDynamic);
		
		SimpleFont textDynamic = SimpleFont.create("Dynamic");
		textDynamic.setPosition(-0.7f, 0.2f);
		addObject(textDynamic);
		
		showBad = new Button(this, "");
		showBad.setPosition(-0.7f, 0.0f);
		showBad.setScale(0.25f, 0.25f);
		showBad.setClickListener(new OnClickListener(){
			@Override
			public void clickFunction(Vector2 pos) {
				removeLayer(unsortedLayer.getIndex());
				removeLayer(sortedLayer.getIndex());
				removeLayer(staticBatchLayer.getIndex());
				removeLayer(dynamicBatchLayer.getIndex());
				removeLayer(badLayer.getIndex());
				addLayer(badLayer);
			}
		});
		addObject(showBad);
		
		SimpleFont textBad = SimpleFont.create("Bad");
		textBad.setPosition(-0.7f, 0.0f);
		addObject(textBad);
		

		unsortedLayer = new UnsortedLayer("unsorted", 100);
		sortedLayer = new SortedLayer("sorted", 110);
		staticBatchLayer = new StaticBatchLayer("static", 120);
		dynamicBatchLayer = new DynamicBatchLayer("dynamic", 130);
		badLayer = new BadLayer("bad", 140);
		
		addLayer(unsortedLayer);

		Sprite sheets[] = new Sprite[]{
			new Sprite(Paths.getResources() + "brick.png"),
			new Sprite(Paths.getResources() + "brick1.png"),
			new Sprite(Paths.getResources() + "brick2.png"),
			new Sprite(Paths.getResources() + "brick3.png"),
			new Sprite(Paths.getResources() + "brick4.png"),
			new Sprite(Paths.getResources() + "brick5.png"),
			new Sprite(Paths.getResources() + "brick6.png"),
			new Sprite(Paths.getResources() + "catacomb.png"),
			new Sprite(Paths.getResources() + "crypt.png"),
			new Sprite(Paths.getResources() + "gallery.png"),
			new Sprite(Paths.getResources() + "gehena.png"),
			new Sprite(Paths.getResources() + "hive.png"),
			new Sprite(Paths.getResources() + "moss.png"),
			new Sprite(Paths.getResources() + "mucus.png"),
			new Sprite(Paths.getResources() + "normal.png"),
			new Sprite(Paths.getResources() + "pandem1.png"),
			new Sprite(Paths.getResources() + "pandem2.png"),
			new Sprite(Paths.getResources() + "pandem3.png"),
			new Sprite(Paths.getResources() + "pandem4.png"),
			new Sprite(Paths.getResources() + "rock.png"),
			new Sprite(Paths.getResources() + "tunnel.png"),
			new Sprite(Paths.getResources() + "pandem6.png"),
			new Sprite(Paths.getResources() + "q1.png"),
			new Sprite(Paths.getResources() + "q2.png"),
			new Sprite(Paths.getResources() + "q3.png"),
			new Sprite(Paths.getResources() + "q4.png"),
			new Sprite(Paths.getResources() + "q5.png"),
			new Sprite(Paths.getResources() + "q6.png"),
			new Sprite(Paths.getResources() + "q7.png"),
		};
		
		final int numsprites = 21;
		int x = 0;
		int y = 0;
		float xStep = (1.8f / numsprites);
		float yStep = (2.0f / sheets.length);
		
		for (int i = 0; i < numsprites; ++i){
			x = (i % 8) * 64;
			y = (i / 8) * 64;
			
			for (int j = 0; j < sheets.length; ++j){
				final Vector2 pos = new Vector2(-0.9f + xStep * 0.5f + xStep * i, 1.0f - yStep * 0.4f - yStep * j);
				GameObject o = new GameObject();
				o.setSprite(Sprite.getSpriteFromSheet(x, y, 64, 64, sheets[j]));
				o.setPosition(pos);
				unsortedLayer.addObject(o);
				sortedLayer.addObject(o.clone());
				staticBatchLayer.addObject(o.clone());
				dynamicBatchLayer.addObject(o.clone());
				badLayer.addObject(o.clone());
			}
		}
		
		staticBatchLayer.finish();
		//dynamicBatchLayer.finish();
		badLayer.finish();
		
		/*MouseController c = (MouseController)ControllerManager.getInstance().getController(Controller.Type.TYPE_MOUSE);
		c.setMouseMoveListener(new ControllerEventListener(){
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				showUnsorted.onHover(pos);
				showSorted.onHover(pos);
				showStatic.onHover(pos);
				showDynamic.onHover(pos);
				showBad.onHover(pos);
			}
		});
		c.addKeybind(0, new ControllerEventListener(){

			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				if (params[0] == 1){
					postClick(pos);
				}
			}
			
		});
		c.startController();*/
	}
	
	public void postClick(Vector2 pos){
		synchronized (clicks){
			clicks.add(pos);
		}
	}
	
	void createLetter(String text, Vector2 pos){
		{
			
		}
		/*{
			Label l = new Label(this, text){
				@Override
				public void update(float deltaTime) {
					super.update(deltaTime);
					getPosition().y -= 0.0025f;
					setLifetime(2.0f);
				}
			};
			l.setScale(1.2f, 1.2f);
			l.setColor(new Color(0.05f, 1.0f, 0.08f, 0.25f));
			l.setPosition(pos.copy().add(0.0f, 0.01f));
			addObject(l);
		}*/
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		synchronized (clicks){
			for (int i = 0; i < clicks.size(); ++i){
				Vector2 pos = clicks.get(i);
				showUnsorted.onClick(pos);
				showSorted.onClick(pos);
				showStatic.onClick(pos);
				showDynamic.onClick(pos);
				showBad.onClick(pos);
			}
			clicks.clear();
		}
	}
	
	public final Vector2 clampToGrid(Vector2 pos){
		int x = Math.round(pos.x / 0.1f);
		int y = Math.round(pos.y / 0.1f);
		
		//col += Math.round(pos.x / 0.2f) & 1;
		
		x += (~x & 1) - (y & 1);
		y += (~y & 1) - (x & 1);
		//
		pos.x = x * 0.1f;
		pos.y = y * 0.1f;
		return pos;
	}
	
	public final void highlight(Vector2 pos){
		highlight.setVertex(0, pos.x - 0.1f, pos.y);
		highlight.setVertex(1, pos.x, pos.y + 0.1f);
		highlight.setVertex(2, pos.x + 0.1f, pos.y);
		highlight.setVertex(3, pos.x, pos.y - 0.1f);
	}
}
