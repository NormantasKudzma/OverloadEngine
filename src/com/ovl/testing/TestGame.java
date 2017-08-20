package com.ovl.testing;

import java.util.ArrayList;

import com.ovl.game.BaseGame;
import com.ovl.game.GameObject;
import com.ovl.graphics.Color;
import com.ovl.graphics.DynamicBatchLayer;
import com.ovl.graphics.Layer;
import com.ovl.graphics.Primitive;
import com.ovl.graphics.SimpleFont;
import com.ovl.graphics.SortedLayer;
import com.ovl.graphics.Sprite;
import com.ovl.graphics.StaticBatchLayer;
import com.ovl.graphics.UnsortedLayer;
import com.ovl.ui.Button;
import com.ovl.ui.Label;
import com.ovl.ui.OnClickListener;
import com.ovl.utils.FastMath;
import com.ovl.utils.OverloadRandom;
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
	
	@Override
	public void init() {
		showUnsorted = new Button(this, "");
		showUnsorted.setPosition(-0.7f, 0.8f);
		showUnsorted.setScale(0.25f, 0.25f);
		showUnsorted.setClickListener(new OnClickListener(){
			@Override
			public void clickFunction(Vector2 pos) {
				removeLayer(unsortedLayer.getIndex());
				removeLayer(sortedLayer.getIndex());
				removeLayer(staticBatchLayer.getIndex());
				removeLayer(dynamicBatchLayer.getIndex());
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
				addLayer(staticBatchLayer);
			}
		});
		addObject(showStatic);
		
		SimpleFont textDynamic = SimpleFont.create("Static");
		textDynamic.setPosition(-0.7f, 0.4f);
		addObject(textDynamic);
		
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
				addLayer(dynamicBatchLayer);
			}
		});
		addObject(showDynamic);
		
		SimpleFont textStatic = SimpleFont.create("Dynamic");
		textStatic.setPosition(-0.7f, 0.2f);
		addObject(textStatic);

		unsortedLayer = new UnsortedLayer("unsorted", 100);
		sortedLayer = new SortedLayer("sorted", 110);
		staticBatchLayer = new StaticBatchLayer("static", 120);
		dynamicBatchLayer = new DynamicBatchLayer("dynamic", 130);
		
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
				final float r2d = FastMath.RAD2DEG;
				final Vector2 pos = new Vector2(-0.9f + xStep * 0.5f + xStep * i, 1.0f - yStep * 0.4f - yStep * j);
				GameObject o = new GameObject(this){
					/*float timeSum = OverloadRandom.nextRandom(1000) * 0.01f;
					
					@Override
					public void update(float deltaTime) {
						timeSum += deltaTime * 3.0f;
						setPosition(pos.x, pos.y + FastMath.sinDeg((int)(timeSum * r2d) % 360) * 0.05f);
					}*/
				};
				o.setSprite(Sprite.getSpriteFromSheet(x, y, 64, 64, sheets[j]));
				o.setPosition(pos);
				unsortedLayer.addObject(o);
				sortedLayer.addObject(o);
				staticBatchLayer.addObject(o);
				dynamicBatchLayer.addObject(o);
			}
		}
		
		staticBatchLayer.finish();
		dynamicBatchLayer.finish();
		
		/*MouseController c = (MouseController)ControllerManager.getInstance().getController(Controller.Type.TYPE_MOUSE);
		c.setMouseMoveListener(new ControllerEventListener(){
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				showUnsorted.onHover(pos);
				showSorted.onHover(pos);
				showStatic.onHover(pos);
				showDynamic.onHover(pos);
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
			Label l = new Label(this, text){
				float changeDelta = OverloadRandom.nextRandom(100) * 0.001f + 0.35f;
				float nextChange = changeDelta;
				float vSpeed = OverloadRandom.nextRandom(30) * 0.0001f + 0.0025f;
				float vSpeedChange = OverloadRandom.nextRandom(10000) * 0.0001f * 0.00001f;
				
				@Override
				public void update(float deltaTime) {
					super.update(deltaTime);
					getPosition().y -= vSpeed;
					vSpeed += vSpeedChange;
					
					nextChange -= deltaTime;
					if (nextChange <= 0.0f){
						nextChange = changeDelta;
						setText("" + (char)(OverloadRandom.nextRandom(93) + 33));
					}
				}
			};
			l.setLifetime(8.0f);
			l.setColor(new Color(0.05f, 0.85f, 0.08f, 1.0f));
			l.setPosition(pos.copy());
			addObject(l);
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
