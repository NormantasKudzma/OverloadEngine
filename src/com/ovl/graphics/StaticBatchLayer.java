package com.ovl.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.ShaderParams;
import com.ovl.engine.Vbo;
import com.ovl.game.GameObject;

public class StaticBatchLayer extends Layer {
	class Batch {
		Vbo vbo;
		ShaderParams id;
		HashMap<String, ParamSetter> params;
		ByteBuffer indices;
		ArrayList<GameObject> objects = new ArrayList<GameObject>();
		boolean isReady = false;
	}
	
	static final Renderer renderer;	
	private HashMap<Integer, Batch> objects = new HashMap<Integer, Batch>();

	static {
		renderer = OverloadEngine.getInstance().renderer;
	}
	
	public StaticBatchLayer(String name, int index) {
		super(name, index);
	}
	
	public void addObject(GameObject obj){
		if (obj == null || !(obj.getSprite() instanceof Sprite)){
			return;
		}

		int texId = ((Sprite)obj.getSprite()).getTexture().getId();
		
		Batch batch = objects.get(texId);
		if (batch == null){
			batch = new Batch();
			objects.put(texId, batch);
		}
		batch.objects.add(obj);
	}
	
	public void finish(){
		if (objects.isEmpty()){
			return;
		}
		
		for (Map.Entry<Integer, Batch> entry : objects.entrySet()){
			Batch batch = entry.getValue();
			if (batch.isReady || batch.objects.isEmpty()){
				continue;
			}
			
			batch.vbo = renderer.createVbo(Sprite.defaultShaderName, batch.objects.size() * 2 * Renderer.VERTICES_PER_SPRITE, Renderer.VERTICES_PER_SPRITE);
		
			batch.params = new HashMap<String, ParamSetter>();
			batch.params.put(Shader.U_COLOR, ParamSetterFactory.buildDefault(Sprite.defaultShader, Shader.U_COLOR));
			batch.params.put(Shader.U_TEXTURE, ParamSetterFactory.build(Sprite.defaultShader, Shader.U_TEXTURE, ((Sprite)batch.objects.get(0).getSprite()).getTexture()));
			batch.params.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(Sprite.defaultShader, Shader.U_MVPMATRIX));
			
			final int SIZEOF_SHORT = 2;
			final int VERTS_PER_SPRITE = 6;
			batch.indices = ByteBuffer.allocateDirect(batch.objects.size() * VERTS_PER_SPRITE * SIZEOF_SHORT).order(ByteOrder.nativeOrder());		
			batch.isReady = true;
			
			GameObject obj = null;
			for (int i = 0; i < batch.objects.size(); ++i){
				obj = batch.objects.get(i);
				obj.getSprite().useShader(batch.vbo, batch.params);

				short offset = (short) (i * 4);
				batch.indices.putShort((short) (offset + 0))
					.putShort((short) (offset + 3))
					.putShort((short) (offset + 1))
					.putShort((short) (offset + 0))
					.putShort((short) (offset + 2))
					.putShort((short) (offset + 3));
			}
			batch.indices.rewind();
			
			batch.id = batch.objects.get(0).getSprite().getShaderParams();
		}
	}

	@Override
	public void update(float deltaTime) {
		Collection<Batch> values = objects.values();
		Batch batch = null;
		Iterator<Batch> it = values.iterator();
		while (it.hasNext()){
			batch = it.next();
			for (int j = 0; j < batch.objects.size(); ++j){
				batch.objects.get(j).update(deltaTime);
			}
		}
	}
	
	@Override
	public void destroy() {
		Collection<Batch> values = objects.values();
		Batch batch = null;
		Iterator<Batch> it = values.iterator();
		while (it.hasNext()){
			batch = it.next();
			for (int j = 0; j < batch.objects.size(); ++j){
				batch.objects.get(j).getSprite().setShaderParams(null);
			}
			renderer.deleteVbo(batch.id);
		}
	}

	@Override
	public void clear() {
		objects.clear();
	}

	@Override
	public void render() {
		Collection<Batch> values = objects.values();
		Batch batch = null;
		Iterator<Batch> it = values.iterator();
		while (it.hasNext()){
			batch = it.next();
			renderer.renderIndexed(batch.id, Renderer.PrimitiveType.Triangles, batch.indices, batch.objects.size() * 6);
		}
	}
}
