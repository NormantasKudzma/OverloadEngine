package com.ovl.graphics;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
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
import com.ovl.utils.Vector2;

public class DynamicBatchLayer extends Layer {
	private class Rect {
		int x, y, w, h;

		public Rect(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		@Override
		public boolean equals(Object o) {
			if (!(o instanceof Rect)){
				return false;
			}
			Rect r = (Rect)o;
			return this == o || (x == r.x && y == r.y && w == r.w && h == r.h);
		}
	}

	private class PackedSprite {
		Rect sourceUV;
		Rect targetUV;
		GameObject o;
		ShaderParams id;

		public PackedSprite(GameObject o, Rect targetUV, Rect sourceUV) {
			this.o = o;
			this.targetUV = targetUV;
			this.sourceUV = sourceUV;
		}
	}

	static final Renderer renderer;

	private Vbo vbo;
	private ShaderParams id;
	private HashMap<String, ParamSetter> params;
	private ByteBuffer indices;
	private Texture tex;
	private boolean isReady = false;
	private ArrayList<PackedSprite> objects = new ArrayList<PackedSprite>();
	private HashMap<Integer, ArrayList<PackedSprite>> spritesToPack = new HashMap<Integer, ArrayList<PackedSprite>>();

	private static final int packingSpace = 8;
	private int atlasWidth, atlasHeight;
	private Vector2 packPos = new Vector2();

	static {
		renderer = OverloadEngine.getInstance().renderer;
	}

	public DynamicBatchLayer(String name, int index) {
		super(name, index);
	}

	public void addObject(GameObject obj) {
		/** unsupported atm
		/*if (obj == null || !(obj.getSprite() instanceof Sprite)) {
			return;
		}
		
		Sprite sprite = ((Sprite)obj.getSprite());
		
		Vector2 sheetSizeCoef = new Vector2(sprite.getTexture().getWidth(), sprite.getTexture().getHeight());
		sheetSizeCoef.div(sprite.getTexture().getImageWidth(), sprite.getTexture().getImageHeight());
		
		Vector2 texCoords[] = sprite.getUV_TopLeft_BotRight();
		Vector2 tl = texCoords[0].copy().div(sheetSizeCoef);
		Vector2 br = texCoords[1].copy().div(sheetSizeCoef);

		Rect uv = new Rect((int)tl.x, (int)tl.y, (int)(br.x - tl.x), (int)(br.y - tl.y));
		Rect rect = new Rect(-100, -100, uv.w, uv.h);
		
		int texId = sprite.getTexture().getId();
		if (spritesToPack.containsKey(texId)){
			ArrayList<PackedSprite> boxes = spritesToPack.get(texId);
			boolean found = false;
			for (int i = 0; i < boxes.size(); ++i){
				if (boxes.get(i).sourceUV.equals(uv)){
					rect = boxes.get(i).targetUV;
					found = true;
					break;
				}
			}
			if (!found){
				boxes.add(new PackedSprite(null, rect, uv));
			}
		}
		else
		{
			ArrayList<PackedSprite> boxes = new ArrayList<PackedSprite>();
			boxes.add(new PackedSprite(null, rect, uv));
			spritesToPack.put(texId, boxes);
		}

		objects.add(new PackedSprite(obj, rect, uv));*/
	}

	public void finish() {
		long t0 = System.nanoTime();
		
		if (objects.isEmpty() || isReady) {
			return;
		}

		vbo = renderer.createVbo(Sprite.defaultShaderName, objects.size() * 8, Renderer.VERTICES_PER_SPRITE);

		final int SIZEOF_SHORT = 2;
		final int VERTS_PER_SPRITE = 6;
		indices = ByteBuffer.allocateDirect(objects.size() * VERTS_PER_SPRITE * SIZEOF_SHORT).order(ByteOrder.nativeOrder());

		ArrayList<PackedSprite> remaining = new ArrayList<PackedSprite>();
		Iterator<ArrayList<PackedSprite>> it = spritesToPack.values().iterator();
		while (it.hasNext()){
			remaining.addAll(it.next());
		}
		
		while (!remaining.isEmpty()){
			pack(remaining.remove(0));
		}

		PackedSprite ps = null;
		ByteBuffer texData = ByteBuffer.allocateDirect(atlasWidth * atlasHeight * 4).order(ByteOrder.nativeOrder());
		for (int i = 0; i < objects.size(); ++i) {
			ps = objects.get(i);
			
			Texture texture = ((Sprite)ps.o.getSprite()).getTexture();

			int pixelW = (int)(texture.getImageWidth() / texture.getWidth());
			
			ByteBuffer buf = renderer.getTextureLoader().getTextureData(texture);
			
			byte line[] = new byte[ps.targetUV.w * 4];
			texData.position((ps.targetUV.y * atlasWidth + ps.targetUV.x) * 4);
			int startPixel = (ps.sourceUV.y * pixelW + ps.sourceUV.x) * 4;
			int stride = pixelW * 4;
			for (int j = 0; j < ps.targetUV.h; ++j){
				buf.position(startPixel + stride * j);
				buf.get(line, 0, line.length);
				texData.put(line, 0, line.length);
				texData.position(texData.position() + (atlasWidth - ps.targetUV.w) * 4);
			}
			texData.rewind();
		}
		
		// unsupported atm
		//tex = renderer.getTextureLoader().createTexture(texData, atlasWidth, atlasHeight, TextureLoader.TexSize.NON_POT);
		params = new HashMap<String, ParamSetter>();
		params.put(Shader.U_COLOR, ParamSetterFactory.buildDefault(Sprite.defaultShader, Shader.U_COLOR));
		params.put(Shader.U_TEXTURE, ParamSetterFactory.build(Sprite.defaultShader, Shader.U_TEXTURE, tex));
		params.put(Shader.U_MVPMATRIX, ParamSetterFactory.buildDefault(Sprite.defaultShader, Shader.U_MVPMATRIX));
		
		/*
		// DEBUG DEBUG DEBUG -> write to file
		try {
			// cia nebutina, galima tiesiai tex kurt is datos
			byte[] raw = new byte[atlasWidth * atlasHeight * 4];
			texData.get(raw);

		    BufferedImage image = new BufferedImage(atlasWidth, atlasHeight, BufferedImage.TYPE_INT_RGB);
			final String ext = "PNG";
		    for (int x=0; x<atlasWidth; x++) {
		        for (int y=0; y<atlasHeight; y++) {
		            int i = (x + (atlasWidth * y)) * 4;
		            int r = raw[i] & 0xFF;
		            int g = raw[i + 1] & 0xFF;
		            int b = raw[i + 2] & 0xFF;
		            image.setRGB(x, atlasHeight - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
		        }
		    }
		    try {
		        ImageIO.write(image, ext, new File("C:\\Users\\nkudzma\\Desktop\\image.jpg"));
		    } catch (IOException ex) {
		        ex.printStackTrace();
		    }
		}
		catch (Exception e){
			e.printStackTrace();
		}
		// -DEBUG DEBUG
		*/
		
		/**
		 * TODO: figure out wtf is this long time forgotten coefficient
		 */
		Vector2 sheetSizeCoef = new Vector2(tex.getWidth(), tex.getHeight());
		sheetSizeCoef.div(tex.getImageWidth(), tex.getImageHeight());
		
		for (int i = 0; i < objects.size(); ++i) {
			ps = objects.get(i);
			ps.id = renderer.generateShaderParams(vbo);
			if (id == null){
				id = ps.id;
				
				for (Map.Entry<String, ParamSetter> kv : params.entrySet()){
					ps.id.addParam(kv.getKey(), kv.getValue());
				}
			}
			
			/* Unsupported atm
			 * Vector2 tl = new Vector2(ps.targetUV.x, ps.targetUV.y).mul(sheetSizeCoef);
			Vector2 br = new Vector2(ps.targetUV.x + ps.targetUV.w, ps.targetUV.y + ps.targetUV.h).mul(sheetSizeCoef);
			renderer.setTextureData(ps.id, tl, br);
			
			Vector2[] verts = ((Sprite)ps.o.getSprite()).getVerts();
			renderer.setVertexData(ps.id, verts);

			short offset = (short) (i * 4);
			indices.putShort((short) (offset + 0))
				.putShort((short) (offset + 3))
				.putShort((short) (offset + 1))
				.putShort((short) (offset + 0))
				.putShort((short) (offset + 2))
				.putShort((short) (offset + 3));*/
		}
		indices.rewind();
		
		long t1 = System.nanoTime();
		System.out.println("Time to beat : " + ((t1 - t0) / 1000000000.0));
	}

	@Override
	public void update(float deltaTime) {
		for (int i = 0; i < objects.size(); ++i) {
			objects.get(i).o.update(deltaTime);
		}
	}

	@Override
	public void destroy() {
		for (int i = 0; i < objects.size(); ++i) {
			renderer.releaseId(objects.get(i).id);
		}
		renderer.deleteVbo(id);
		objects.clear();
	}

	@Override
	public void render() {
		renderer.renderIndexed(id, Renderer.PrimitiveType.Triangles, indices, objects.size() * 6);
	}

	private void pack(PackedSprite sprite) {
		PackedSprite box = sprite;
		if (atlasWidth == 0) {
			box.targetUV.x = 0;
			box.targetUV.y = 0;
			packPos.x += box.targetUV.w + packingSpace;
		}
		else {
			// if we can place the item, we move to the next row
			if (!placeBox(box)) {
				// Can't fit on the current row, let's move to the next
				if (packPos.x + box.targetUV.w > atlasHeight) {
					packPos.y = atlasHeight;
					packPos.x = 0;
				}
				box.targetUV.x = (int) packPos.x;
				box.targetUV.y = (int) packPos.y;
				packPos.x += box.targetUV.w + packingSpace;
			}
		}

		atlasWidth = Math.max(atlasWidth, box.targetUV.x + box.targetUV.w + packingSpace);
		atlasHeight = Math.max(atlasHeight, box.targetUV.y + box.targetUV.h + packingSpace);
	}

	private boolean placeBox(PackedSprite box) {
		final int xStep = box.targetUV.w + 4;
		final int yStep = box.targetUV.h + 4;
		for (int x = 0; x < atlasWidth; x += xStep) {
			for (int y = 0; y < atlasWidth; y += yStep) {
				if (!boxCollide(box, x, y)) {
					box.targetUV.x = x;
					box.targetUV.y = y;
					return true;
				}
			}
		}
		return false;
	}

	private boolean boxCollide(PackedSprite box, int x, int y) {
		PackedSprite s2 = null;
		Rect uv = null;
		for (int i = 0; i < objects.size(); i++) {
			s2 = objects.get(i);
			if (s2 == box){
				continue;
			}
			
			uv = s2.targetUV;
			if (!(uv.x + uv.w < x || uv.x > x + box.targetUV.w || uv.y + uv.h < y || uv.y > y + box.targetUV.h)){
				return true;
			}
		}
		return false;
	}

	@Override
	public void unloadResources() {
		// FIXME
	}

	@Override
	public void reloadResources() {
		// FIXME
	}
}
