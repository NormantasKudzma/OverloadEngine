package com.ovl.graphics;

import java.util.HashMap;
import java.util.Map;

import com.ovl.engine.OverloadEngine;
import com.ovl.engine.ParamSetter;
import com.ovl.engine.ParamSetterFactory;
import com.ovl.engine.Renderer;
import com.ovl.engine.Shader;
import com.ovl.engine.ShaderParams;
import com.ovl.engine.Vbo;
import com.ovl.utils.ICloneable;
import com.ovl.utils.Vector2;

public class Sprite implements Renderable, ICloneable {
	protected static final Renderer renderer;
	public static final String defaultShaderName;
	public static final Shader defaultShader;
	protected static final Vbo defaultVbo;
	
	private Texture texture;						// Sprite's texture
	private Vector2 textureSize = new Vector2();
	private Vector2 verts[] = new Vector2[4];
	private Vector2 uv[] = new Vector2[4];
	
	private Color color = new Color();
	private ShaderParams id;
	
	static {
		defaultShaderName = "Texture";
		renderer = OverloadEngine.getInstance().renderer;
		defaultShader = renderer.createShader(defaultShaderName);
		defaultVbo = renderer.createVbo(defaultShaderName, 1024, 4);
	}
	
	private Sprite(){
		
	}
	
	public Sprite(String path){
		this(path, new Vector2(), null);
	}
	
	public Sprite(Texture tex){
		this(tex, new Vector2(), null);
	}
	
	public Sprite(String path, Vector2 tl, Vector2 br){
		loadTexture(path);
		init();
		setUV(tl, br);
	}
	
	public Sprite(Texture tex, Vector2 tl, Vector2 br){
		texture = tex;
		init();
		setUV(tl, br);
	}
	
	@Override
	public Sprite clone(){
		Sprite clone = new Sprite(texture, uv[1], uv[3]);
		clone.useShader(id.getVbo(), id.getParams());
		clone.textureSize.set(textureSize);
		for (int i = 0; i < clone.verts.length; ++i){
			clone.verts[i].set(verts[i]);
		}
		return clone;
	}
	
	@Override
	public void destroy(){
		renderer.releaseId(id);
		id = null;
		texture = null;
		textureSize = null;
	}
	
	@Override
	public Color getColor(){
		return color;
	}
	
	public Vector2 getTextureSize(){
		return textureSize.copy();
	}
	
	@Override
	public Vector2 getSize(){
		return textureSize.copy();
	}
	
	public Texture getTexture(){
		return texture;
	}
	
	public static Sprite getSpriteFromSheet(int x, int y, int w, int h, Sprite sheet){
		Vector2 sheetSizeCoef = new Vector2(sheet.getTexture().getWidth(), sheet.getTexture().getHeight());
		sheetSizeCoef.div(sheet.getTexture().getImageWidth(), sheet.getTexture().getImageHeight());
		
		Vector2 topLeft = new Vector2(x, y);
		Vector2 botRight = topLeft.copy().add(new Vector2(w, h));
		
		topLeft.mul(sheetSizeCoef);
		botRight.mul(sheetSizeCoef);
		
		Sprite sprite = new Sprite(sheet.getTexture(), topLeft, botRight);
		sprite.setTextureSize_Pixel(w, h);
		return sprite;
	}	
	
	private void init(){
		for (int i = 0; i < verts.length; ++i){
			verts[i] = new Vector2();
		}
		
		for (int i = 0; i < uv.length; ++i){
			uv[i] = new Vector2();
		}
		
		HashMap<String, ParamSetter> shaderParams = new HashMap<String, ParamSetter>();
		shaderParams.put(Shader.U_COLOR, ParamSetterFactory.build(defaultShader, Shader.U_COLOR, color));
		shaderParams.put(Shader.U_TEXTURE, ParamSetterFactory.build(defaultShader, Shader.U_TEXTURE, texture));
		shaderParams.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(defaultShader, Shader.U_MVPMATRIX));
		
		useShader(defaultVbo, shaderParams);	
	}
	
	@Override
	public void useShader(Vbo vbo, HashMap<String, ParamSetter> params){
		if (id != null){
			renderer.releaseId(id);
		}
		
		id = renderer.generateShaderParams(vbo);
		
		for (Map.Entry<String, ParamSetter> kv : params.entrySet()){
			id.addParam(kv.getKey(), kv.getValue());
		}
		
		renderer.setVertexData(id, Shader.A_TEXCOORD, uv);
			
		refreshVertexData();
	}
	
	public ShaderParams getShaderParams(){
		return id;
	}
	
	public void setShaderParams(ShaderParams params){
		if (id != null){
			renderer.releaseId(id);
		}
		id = params;
	}
	
	public void loadTexture(String path){
		texture = renderer.getTextureLoader().getTexture(path);
	}

	@Override
	public void render() {
		renderer.render(id, Renderer.PrimitiveType.TriangleStrip);
	}
	
	@Override
	public void setColor(Color c){
		color.set(c);
	}
	
	public void setUV(Vector2 tl, Vector2 br){
		if (tl == null){
			tl = new Vector2();
		}

		if (br == null) {
			br = new Vector2(texture.getWidth(), texture.getHeight());
		}

		uv[0].set(tl.x, br.y);
		uv[1].set(tl.x, tl.y);
		uv[2].set(br.x, br.y);
		uv[3].set(br.x, tl.y);
		
		//renderer.setTextureData(id, texTopLeft, texBotRight);
		renderer.setVertexData(id, Shader.A_TEXCOORD, uv);
		setTextureSize_Pixel(texture.getImageWidth(), texture.getImageHeight());
	}
	
	public void setUV(Vector2[] uv){
		for (int i = 0; i < this.uv.length; ++i){
			this.uv[i].set(uv[i]);
		}
		
		renderer.setVertexData(id, Shader.A_TEXCOORD, uv);
	}
	
	public Vector2[] getUV(){
		return uv;
	}
	
	public Vector2[] getVerts(){
		return verts;
	}
	
	public void setTextureSize_Pixel(int w, int h){
		textureSize.set(w, h).pixelToNormal();
	}
	
	public void setTextureSize_Normal(Vector2 s){
		textureSize.set(s);
	}
	
	@Override
	public void updateVertices(Vector2 pos, Vector2 scale, float angle){		
		Vector2 halfSize = textureSize.copy().mul(0.5f).mul(scale);
		verts[0].set(-halfSize.x, -halfSize.y);
		verts[1].set(-halfSize.x, halfSize.y);
		verts[2].set(halfSize.x, -halfSize.y);
		verts[3].set(halfSize.x, halfSize.y);
		
		for (int i = 0; i < verts.length; ++i){
			if (angle != 0.0f)
			{
				verts[i].rotate(angle);
			}
			verts[i].add(pos);
		}
		refreshVertexData();
	}
	
	private void refreshVertexData(){
		renderer.setVertexData(id, Shader.A_POSITION, verts);
	}

	@Override
	public void unloadResources() {

	}

	@Override
	public void reloadResources() {
		refreshVertexData();
		setUV(uv);
	}
}
