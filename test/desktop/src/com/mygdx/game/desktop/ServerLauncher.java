package com.mygdx.game.desktop;

import java.io.IOException;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Main;
import com.mygdx.game.ServerProgram;

public class ServerLauncher {
	public static void main (String[] arg) throws IOException {
		new ServerProgram();
	}
}
