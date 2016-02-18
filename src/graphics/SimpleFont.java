package graphics;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import utils.ConfigManager;
import utils.Paths;
import utils.Vector2;

public class SimpleFont implements IRenderable {
	private static final float MAGIC_SCALE = 1.4f;
	private static final HashMap<Character, Symbol> symbols = new HashMap<Character, Symbol>(128);
	private static Symbol defaultSymbol;

	private String text;
	private Vector2 internalPos;
	private ArrayList<Symbol> textSymbols = new ArrayList<Symbol>();

	static {
		Sprite2D sheet = new Sprite2D(Paths.DEFAULT_FONT_IMG);
		float size = 64.0f;

		JSONObject json = ConfigManager.loadConfigAsJson(Paths.DEFAULT_FONT_JSON);
		float wstep = json.getInt("width");
		float hstep = json.getInt("height");
		JSONObject s;
		char chr;
		int x, y;
		float offset;
		for (int i = 0; i < json.length() - 2; i++) {
			s = json.getJSONObject("" + i);
			chr = s.getString("symbol").charAt(0);
			x = s.getInt("x");
			y = s.getInt("y");
			if (s.has("offset")) {
				offset = s.getInt("offset") / size;
			}
			else {
				offset = 0;
			}
			Symbol symbol = new Symbol(new Sprite2D(sheet.getTexture(), new Vector2(x / size, y / size), new Vector2((x + wstep) / size, (y + hstep) / size)), offset);
			symbols.put(chr, symbol);
		}
		defaultSymbol = symbols.get('Q');
	}

	public SimpleFont(String text) {
		this.setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		textSymbols.clear();
		Symbol s;
		for (int i = 0; i < text.length(); i++) {
			s = symbols.get(text.charAt(i));
			if (s == null) {
				s = defaultSymbol;
			}
			textSymbols.add(s);
		}
	}

	@Override
	public void render(Vector2 position, float rotation, Vector2 scale) {
		float step = defaultSymbol.sprite.getHalfSize().x * MAGIC_SCALE * scale.x;
		internalPos = position.copy().add(-step * 0.5f * text.length() + step * 0.5f, 0);
		Symbol s;
		for (int i = 0; i < textSymbols.size(); i++) {
			s = textSymbols.get(i);
			internalPos.y = s.offset * scale.y * 1.5f + position.y;
			s.sprite.render(internalPos, rotation, scale);
			internalPos.x += step;
		}
	}
}
