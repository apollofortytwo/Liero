package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Network.CharacterMove;
import com.mygdx.game.Network.HasDisconnected;
import com.mygdx.game.Network.IdentificationNumber;

public class ClientProgram extends Listener {

	Client client;
	int id;

	public ArrayList<PhysicsCharacter> characterList = new ArrayList<PhysicsCharacter>();
	public ArrayList<Network.Bullet> bulletList = new ArrayList<Network.Bullet>();

	Main main;

	ClientProgram(Main main) throws IOException {
		this.main = main;
		client = new Client();
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
			for (PhysicsCharacter character : characterList) {
				Network.Character character1 = character.character;
				if (character1.id == cm.id) {
					if (!Main.world.isLocked()) {
						character1.x = cm.x;
						character1.y = cm.y;
						character.body.setTransform(cm.x, cm.y, 0);
					}
					return;
				}
			}

		} else if (object instanceof Network.Character) {
			com.mygdx.game.Network.Character character = (com.mygdx.game.Network.Character) object;

			if (!Main.world.isLocked()) {
				PolygonShape shape = new PolygonShape();
				shape.setAsBox(64 / 32, 64 / 32);

				BodyDef bd = new BodyDef();
				bd.position.set(character.x, character.y);
				bd.type = BodyType.DynamicBody;

				Body body = Main.world.createBody(bd);
				Fixture fixture = body.createFixture(shape, 10);

				UserData ud = new UserData();
				ud.name = "Player";
				body.setUserData(ud);
				fixture.setFriction(10.0f);
				body.setGravityScale(10f);

				body.setFixedRotation(true);

				PhysicsCharacter character1 = new PhysicsCharacter();
				character1.body = body;
				character1.character = character;
				characterList.add(character1);

			}

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

		} else if (object instanceof HasDisconnected) {
			HasDisconnected dc = (HasDisconnected) object;
			Network.Character toRemove = null;
			for (PhysicsCharacter character : characterList) {
				Network.Character character1 = character.character;
				if (character1.id == dc.id) {
					toRemove = character1;

				}
			}

			characterList.remove(toRemove);

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