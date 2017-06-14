package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class ServerProgram {

	Server server;
	ArrayList<Network.Character> characterList = new ArrayList<Network.Character>();

	public ServerProgram() throws IOException {
		System.out.println("Creating the server...");
		server = new Server();

		Network.register(server);
		server.addListener(new Listener() {

			public void connected(Connection connection) {
				Network.Message message = new Network.Message();
				message.message = "Client" + connection.getID() + " has joined the server";
				server.sendToAllTCP(message);

				Network.IdentificationNumber id = new Network.IdentificationNumber();
				id.serial = connection.getID();
				server.sendToTCP(connection.getID(), id);

				for (Network.Character character : characterList) {
					server.sendToTCP(connection.getID(), character);
				}
			}

			public void received(Connection c, Object object) {
				// System.out.println("Client" + c.getID() + " has sent a
				// object");
				if (object instanceof Network.CharacterMove) {

					Network.CharacterMove cm = (Network.CharacterMove) object;

					for (Network.Character character : characterList) {
						if (character.id == c.getID()) {
							character.x = cm.x;
							character.y = cm.y;
							server.sendToAllExceptTCP(c.getID(), cm);
						}
					}
				} else if (object instanceof Network.Character) {
					Network.Character character = (Network.Character) object;
					System.out.println("Character has been added from user:" + character.id + " | Position: "
							+ character.x + ", " + character.y);
					characterList.add(character);
					server.sendToAllExceptTCP(c.getID(), character);
				} else if (object instanceof Network.Bullet) {
					Network.Bullet bullet = (Network.Bullet) object;
					server.sendToAllExceptTCP(c.getID(), bullet);
					
				}

			}

			public void disconnected(Connection c) {
				System.out.println("Client disconnected");
				Network.HasDisconnected dc = new Network.HasDisconnected();
				dc.id = c.getID();
				server.sendToAllTCP(dc);
			}
		});

		server.bind(Network.tcpPort, Network.udpPort);
		server.start();

	}
	public static void main (String[] args) throws IOException {
		new ServerProgram();
	}
	
}