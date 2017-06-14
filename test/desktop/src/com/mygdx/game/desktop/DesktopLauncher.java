package com.mygdx.game.desktop;

import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.game.Main;
import com.mygdx.game.Network;

public class DesktopLauncher {

	public static void main (String[] arg) {
		String address = JOptionPane.showInputDialog("Enter the IP address: ");
		Network.ip = address;

		launchGame();
        
	}
	
	public static void launchGame(){
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.fullscreen = false;
        config.forceExit = true;
        config.vSyncEnabled = true;
		
		new LwjglApplication(new Main(), config);
	}
}
