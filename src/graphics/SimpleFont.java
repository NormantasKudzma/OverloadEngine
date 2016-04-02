package graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import physics.PhysicsBody;
import utils.ConfigManager;
import utils.Paths;
import utils.Vector2;
import engine.Entity;

public class SimpleFont extends Entity<Sprite2D> {
	private static final float MAGIC_SCALE = 1.4f;
	private static final HashMap<Character, Symbol> defaultSymbolMap = new HashMap<Character, Symbol>(128);

	private String text;
	private Symbol firstSymbol;
	private HashMap<Character, Symbol> symbolMap;
	private ArrayList<Symbol> textSymbols = new ArrayList<Symbol>();

	static {
		HashMap<Character, Symbol> map = createFont(Paths.DEFAULT_FONT_IMG, Paths.DEFAULT_FONT_JSON);
		
		Iterator<Entry<Character, Symbol>> it = map.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry<Character, Symbol> pair = (Map.Entry<Character, Symbol>)it.next();
			defaultSymbolMap.put(pair.getKey(), pair.getValue());
	        it.remove();
		}
	}

	public SimpleFont(String text) {
		this(text, defaultSymbolMap);
	}
	
	public SimpleFont(String text, String fontImg, String fontJson){
		this(text, createFont(fontImg, fontJson));
	}
	
	public SimpleFont(String text, HashMap<Character, Symbol> font){
		super(null);
		initEntity(PhysicsBody.EBodyType.NON_INTERACTIVE);
		symbolMap = font;
		firstSymbol = font.values().iterator().next().clone();
		setText(text);
	}
	
	public static HashMap<Character, Symbol> createFont(String fontImg, String fontJson){
		HashMap<Character, Symbol> map = new HashMap<Character, Symbol>();
		try {
			Sprite2D sheet = new Sprite2D(fontImg);

			JSONObject json = ConfigManager.loadConfigAsJson(fontJson);
			char chr;
			int x, y, w, h;
			
			Vector2 offset;
			JSONObject symbolJson = null;
			JSONArray symbolsJsonArray = json.getJSONArray("symbols");
			for (int i = 0; i < symbolsJsonArray.length(); ++i) {
				symbolJson = symbolsJsonArray.getJSONObject(i);
				chr = symbolJson.getString("symbol").charAt(0);
				x = symbolJson.getInt("x");
				y = symbolJson.getInt("y");
				w = symbolJson.getInt("w");
				h = symbolJson.getInt("h");
				if (symbolJson.has("offset")) {
					JSONArray offsetJson = symbolJson.getJSONArray("offset");
					offset = new Vector2(offsetJson.getInt(0), offsetJson.getInt(1));
					Vector2.pixelCoordsToNormal(offset);
				}
				else {
					offset = Vector2.zero;
				}
				Symbol symbol = new Symbol(Sprite2D.getSpriteFromSheet(x, y, w, h, sheet), offset);
				map.put(chr, symbol);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return map;
	}

	public String getText() {
		return text;
	}

	public void setColor(Color c){
		Symbol s = null;
		for (int i = 0; i < textSymbols.size(); i++) {
			s = textSymbols.get(i);
			s.sprite.setColor(c);
		}
	}
	
	public void setText(String text) {
		this.text = text;
		/*for (Symbol s : textSymbols){
			s.sprite.destroy();
		}*/
		
		Symbol s;
		textSymbols.clear();
		for (int i = 0; i < text.length(); i++) {
			s = symbolMap.get(text.charAt(i));
			if (s != null) {
				textSymbols.add(s);
			}
		}
	}
	
	@Override
	public void render(Vector2 position, Vector2 scale, float rotation) {
		float step = firstSymbol.sprite.getHalfSize().x * MAGIC_SCALE * scale.x;
		Vector2 internalPos = position.copy().add(-step * 0.5f * text.length() + step * 0.5f, 0);
		Symbol s;
		for (int i = 0; i < textSymbols.size(); i++) {
			s = textSymbols.get(i);
			internalPos.y = s.offset.y * scale.y * 1.5f + position.y;
			s.sprite.render(internalPos, scale, rotation);
			internalPos.x += step;
		}
	}
}
