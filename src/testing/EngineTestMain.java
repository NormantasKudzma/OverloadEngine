package testing;

import engine.OverloadEngine;

public class EngineTestMain {
	public static void main(String[] args){
		OverloadEngine engine = new OverloadEngine();
		TestGame game = new TestGame();
		engine.setGame(game);
		engine.setDebugDraw(true);
		engine.setFullscreen(false);
		engine.run();
	}
}
