package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.mygdx.game.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Box2D.init();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new Main(), config);
	}
}
