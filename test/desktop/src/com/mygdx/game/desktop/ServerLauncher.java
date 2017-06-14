package com.mygdx.game.desktop;

import java.io.IOException;
import java.net.Inet4Address;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.Main;
import com.mygdx.game.Network;
import com.mygdx.game.ServerProgram;

public class ServerLauncher {
	public static void main (String[] args) throws IOException {
		Network.ip = Inet4Address.getLocalHost().getHostAddress();
        Thread serverThread = new Thread() {
            public void run() {
                try {
					new ServerProgram();
					ServerProgram.main(args);
				} catch (IOException e) {
				}
            }
        };

        Thread clientThread = new Thread() {
            public void run() {
                DesktopLauncher.launchGame();
            }
        };

        serverThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        clientThread.start();

	}
}
