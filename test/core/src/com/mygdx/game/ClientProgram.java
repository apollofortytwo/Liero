package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Network.CharacterMove;
import com.mygdx.game.Network.HasDisconnected;
import com.mygdx.game.Network.IdentificationNumber;

public class ClientProgram extends Listener {

	Client client;
	int id;
	MapManager mm;
	public ArrayList<Network.Character> characterList = new ArrayList<Network.Character>();
	public ArrayList<Network.Bullet> bulletList = new ArrayList<Network.Bullet>();

	ClientProgram(MapManager mm) throws IOException {
		this.mm = mm;
		client = new Client(20000000, 4000000);
		client.start();

		Network.register(client);

		client.connect(5000, Network.ip, Network.tcpPort, Network.udpPort);
		client.addListener(this);
	}

	public void connected(Connection connection) {
		System.out.println("welcome: " + connection.getID() + " You've joined the server");
	}

	public void received(Connection c, Object object) {

		if (object instanceof Network.CharacterMove) {
			Network.CharacterMove cm = (Network.CharacterMove) object;
			for (Network.Character character : characterList) {
				if (character.id == cm.id) {
					character.x = cm.x;
					character.y = cm.y;
					return;
				}
			}

		} else if (object instanceof Network.Character) {
			com.mygdx.game.Network.Character character = (com.mygdx.game.Network.Character) object;
			characterList.add(character);

		} else if (object instanceof Network.Bullet) {
			Network.Bullet bullet = (Network.Bullet) object;
			bulletList.add(bullet);
			Main.recieveBullet(bullet);

		} else if (object instanceof Network.Message) {
			Network.Message message = (Network.Message) object;
			System.out.println(message.message);

		} else if (object instanceof IdentificationNumber) {
			IdentificationNumber id = (IdentificationNumber) object;
			System.out.println("your identification Number is: " + id.serial);
			this.id = id.serial;

		} else if (object instanceof Network.Map) {
			Network.Map map = (Network.Map) object;
			if(MapManager.currentMap != null){
				mergeMap(MapManager.currentMap, map.map);

			} 
			
		} else if (object instanceof HasDisconnected) {
			HasDisconnected dc = (HasDisconnected) object;
			Network.Character toRemove = null;
			for (Network.Character character : characterList) {
				if (character.id == dc.id) {
					toRemove = character;

				}
			}

			characterList.remove(toRemove);

		}

	}

	public void update() {

	}

	public void sendMyMap(Pixmap map) {
		float[][] array = new float[map.getWidth()][map.getHeight()];

		for (int x = 0; x < map.getWidth(); x++) {
			for (int y = 0; y < map.getHeight(); y++) {
				if (new Color(map.getPixel(x, y)).equals(Color.BLACK)) {
					array[x][y] = 1;
				} else {
					array[x][y] = 0;
				}
			}

		}

		Network.Map networkMap = new Network.Map();
		networkMap.map = array;
		client.sendTCP(networkMap);

	}

	public void mergeMap(Pixmap myMap, float[][] map) {
		for (int x = 0; x < map.length; x++) {
			for (int y = 0; y < map[x].length; y++) {
				if (map[x][y] == 1) {
					myMap.setColor(Color.BLACK);
					myMap.drawPixel(x, y);
				}
			}
		}
	}

	public void sendMyCharacter(Player player) {
		Network.Character character = new Network.Character();
		character.x = player.x;
		character.y = player.y;
		character.id = id;

		client.sendTCP(character);
	}

	public void sendBullet(Network.Bullet bullet) {
		bullet.id = id;
		client.sendTCP(bullet);
	}

	public void disconnected(Connection c) {
		System.out.println("Client disconnected");
	}

	public void updateCharacterPosition(Vector2 vector2) {
		CharacterMove character = new CharacterMove();
		character.x = (int) vector2.x;
		character.y = (int) vector2.y;
		character.id = id;

		client.sendTCP(character);
	}

}
