package com.ovl.testing;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;

import com.ovl.engine.EngineConfig;
import com.ovl.engine.android.OverloadEngineAndroid;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		
		if (info.reqGlEsVersion >= 0x20000){		
			EngineConfig cfg = new EngineConfig();
			cfg.game = new TestGame();
			cfg.isDebug = false;
			OverloadEngineAndroid engine = new OverloadEngineAndroid(cfg);
			setContentView(engine.getSurfaceView(this));
		}
	}
}