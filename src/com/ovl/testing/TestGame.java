package com.ovl.testing;

import java.util.HashMap;

import org.apache.commons.lang3.mutable.MutableFloat;

import com.ovl.controls.Controller;
import com.ovl.controls.ControllerEventListener;
import com.ovl.controls.ControllerManager;
import com.ovl.controls.pc.KeyboardController;
import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.Vbo;
import com.ovl.game.BaseGame;
import com.ovl.game.GameObject;
import com.ovl.graphics.Color;
import com.ovl.graphics.Primitive;
import com.ovl.graphics.Sprite;
import com.ovl.physics.PhysicsBody.BodyType;
import com.ovl.ui.Label;
import com.ovl.utils.OverloadRandom;
import com.ovl.utils.Paths;
import com.ovl.utils.Vector2;

public class TestGame extends BaseGame {
	private Primitive highlight;
	
	@Override
	public void init() {
		/*{
			GameObject obj = new GameObject();
			obj.initEntity(BodyType.NON_INTERACTIVE);
			obj.setPosition(0.0f, 0.0f);
			obj.setSprite(new Sprite(Paths.getUI() + "square_yellow.png"));
			//obj.setColor(new Color(0.0f, 0.0f, 1.0f));
			addObject(obj);	
		}*/

		final GameObject obj = new GameObject();
		obj.initEntity(BodyType.NON_INTERACTIVE);
		obj.setPosition(-0.30f, -0.30f);
		obj.setSprite(new Sprite(Paths.getUI() + "square_blue.png"));
		addObject(obj);
		
		Renderer r = OverloadEngine.getInstance().renderer;
		Shader sh = r.createShader("Blur");
		Vbo vbo = r.createVbo("Blur", 256, 4);
		
		Sprite blurredSprite = new Sprite(Paths.getResources() + "m.png");
		
		GameObject blurredObj = new GameObject();
		blurredObj.initEntity(BodyType.NON_INTERACTIVE);
		blurredObj.setPosition(0.35f, 0.35f);
		blurredObj.setSprite(blurredSprite);
		addObject(blurredObj);

		final MutableFloat blurStrength = new MutableFloat(0.001f);
		
		HashMap<String, ParamSetter> shaderParams = new HashMap<String, ParamSetter>();
		shaderParams.put(Shader.U_COLOR, ParamSetterFactory.build(sh, Shader.U_COLOR, blurredSprite.getColor()));
		shaderParams.put(Shader.U_TEXTURE, ParamSetterFactory.build(sh, Shader.U_TEXTURE, blurredSprite.getTexture()));
		shaderParams.put("u_BlurStr", ParamSetterFactory.build(sh, "u_BlurStr", blurStrength));
		shaderParams.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(sh, Shader.U_MVPMATRIX));
		blurredSprite.useShader(vbo, shaderParams);
		
		blurredSprite.updateVertices(new Vector2(0.3f, 0.3f), new Vector2(1.0f, 1.0f), 0.0f);
		
		/*GameObject clone1 = obj.clone();
		clone1.setPosition(1.0f, 0.0f);
		addObject(clone1);
		
		final GameObject clone2 = obj.clone();
		clone2.setPosition(1.67f, 0.0f);
		addObject(clone2);
		*/
		
		//primitive vbo test here
		
		/*{
			Vector2[] verts = new Vector2[]{
							new Vector2(0.7f, -1.0f),
							new Vector2(1.0f, 1.0f),
							};
			Primitive p = new Primitive(verts, Renderer.PrimitiveType.Lines);
			GameObject obj2 = new GameObject();
			obj2.initEntity(BodyType.NON_INTERACTIVE);
			obj2.setSprite(p);
			obj2.setPosition(1.0f, 1.0f);
			obj2.setColor(new Color(0.25f, 0.9f, 0.2f));
			addObject(obj2);
		}*/
		/*{
			Vector2[] verts = new Vector2[]{
							new Vector2(-1f, 0.4f),
							new Vector2(0f, 0.4f),
							};
			Primitive p = new Primitive(verts, Renderer.PrimitiveType.Lines);
			GameObject obj2 = new GameObject();
			obj2.initEntity(BodyType.NON_INTERACTIVE);
			obj2.setSprite(p);
			obj2.setPosition(1.0f, 1.0f);
			obj2.setColor(new Color(0.25f, 0.9f, 0.2f));
			addObject(obj2);
		}
		
		{
			Vector2[] verts = new Vector2[]{
							new Vector2(-0.8f, -0.8f),
							new Vector2(0f, 0.8f),
							new Vector2(0.8f, -0.8f),
							//new Vector2(0.0f, 0.0f)
							};
			Primitive p = new Primitive(verts, Renderer.PrimitiveType.Triangles);
			GameObject obj2 = new GameObject();
			obj2.initEntity(BodyType.NON_INTERACTIVE);
			obj2.setSprite(p);
			obj2.setPosition(1.0f, 1.0f);
			obj2.setColor(new Color(0.9f, 0.9f, 0.9f, 0.25f));
			addObject(obj2);
		}*/
		/*{
			Vector2[] verts = new Vector2[]{
							//new Vector2(0.85f, 0.8f),
							//new Vector2(-0.75f, -0.8f),
							new Vector2(-0.45f, 0.5f),
							new Vector2(0.55f, -0.5f),
							};
			Primitive p = new Primitive(verts, Renderer.PrimitiveType.Lines);
			GameObject obj2 = new GameObject();
			obj2.initEntity(BodyType.NON_INTERACTIVE);
			obj2.setSprite(p);
			obj2.setPosition(1.0f, 1.0f);
			obj2.setColor(new Color(0.8f, 0.1f, 0.5f));
			addObject(obj2);
		}

		{
			Vector2[] verts = new Vector2[]{
							//new Vector2(0.85f, 0.8f),
							//new Vector2(-0.75f, -0.8f),
							new Vector2(-0.45f, 0f),
							new Vector2(0.55f, 0f),
							};
			Primitive p = new Primitive(verts, Renderer.PrimitiveType.Lines);
			GameObject obj2 = new GameObject();
			obj2.initEntity(BodyType.NON_INTERACTIVE);
			obj2.setSprite(p);
			obj2.setPosition(1.0f, 1.0f);
			obj2.setColor(new Color(0.2f, 0.0f, 0.8f));
			addObject(obj2);
		}*/
		/*{
			Vector2[] verts = new Vector2[]{new Vector2(0.5f, -0.5f), new Vector2(0.5f, 0.5f)};
			Primitive p = new Primitive(verts, Renderer.PrimitiveType.Lines);
			GameObject obj2 = new GameObject();
			obj2.initEntity(BodyType.NON_INTERACTIVE);
			obj2.setSprite(p);
			obj2.setPosition(1.0f, 1.0f);
			addObject(obj2);
		}*/
		
		
		/*super.init();
		
		{
			final int size = 80;
			Vector2 verts[] = new Vector2[size];
			
			for (int i = 0; i < 20; i += 1){
				verts[i * 2] = new Vector2(0, 1.8f - i * 0.2f);
				verts[i * 2 + 1] = new Vector2(0.2f + i * 0.2f, 2.0f);
	
				verts[i * 2 + 40] = new Vector2(0, 0.2f + i * 0.2f);
				verts[i * 2 + 41] = new Vector2(0.2f + i * 0.2f, 0.0f);
			}
			
			Primitive grid = new Primitive(verts, Renderer.PrimitiveType.Lines);
			
			GameObject gridObj = new GameObject();
			gridObj.setSprite(grid);
			gridObj.initEntity(BodyType.NON_INTERACTIVE);
			addObject(gridObj);
		}
		
		float hw = 0.1f;
		
		Vector2 verts[] = new Vector2[4];
		verts[0] = new Vector2(1.0f - hw, 1.0f + hw);
		verts[1] = new Vector2(1.0f, 1.0f + 2 * hw);
		verts[2] = new Vector2(1.0f + hw, 1.0f + hw);
		verts[3] = new Vector2(1.0f, 1.0f);
		
		highlight = new Primitive(verts, Renderer.PrimitiveType.Quads);
		
		GameObject hlObj = new GameObject();
		hlObj.setSprite(highlight);
		hlObj.initEntity(BodyType.NON_INTERACTIVE);
		addObject(hlObj);*/
		
		/*MouseController c = (MouseController)ControllerManager.getInstance().getController(Controller.Type.TYPE_MOUSE);
		c.setMouseMoveListener(new ControllerEventListener(){
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				//Vector2.pixelCoordsToNormal(pos);
				clampToGrid(pos);
				highlight(pos);
			}
		});
		c.startController();*/
		
		/*float w = 0.05f;
		for (float i = w * 0.5f; i <= 2.0f; i += w){
			createLetter("a", new Vector2(i, 2.05f + OverloadRandom.nextRandom(200) * 0.001f));
		}*/
		
		KeyboardController k = (KeyboardController)ControllerManager.getInstance().getController(Controller.Type.TYPE_KEYBOARD);
		// LEFT
		k.addKeybind(203, new ControllerEventListener(){
			long last = 0;
			final long delay = 150;
			
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				long time = System.currentTimeMillis();
				if (time > last + delay){
					blurStrength.add(-0.001f);
					System.out.println("now " + blurStrength);
				}
			}
		});
		// RIGHT
		k.addKeybind(205, new ControllerEventListener(){
			long last = 0;
			final long delay = 150;
			
			@Override
			public void handleEvent(long eventArg, Vector2 pos, int... params) {
				long time = System.currentTimeMillis();
				if (time > last + delay){
					blurStrength.add(0.001f);
					System.out.println("now " + blurStrength);
				}
			}
		});
		k.startController();
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
