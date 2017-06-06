package com.mygdx.game;

import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.mygdx.game.Network.CharacterMove;
import com.mygdx.game.Network.HasDisconnected;
import com.mygdx.game.Network.IdentificationNumber;

public class ClientProgram extends Listener {

	Client client;
	int id;
	
	public ArrayList<Network.Character> characterList = new ArrayList<Network.Character>();
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
			for(Network.Character character: characterList){
				if(character.id == cm.id){
					character.x = cm.x;
					character.y =cm.y;
					return;
				}
			}
			
		} else if (object instanceof Network.Character) {
			com.mygdx.game.Network.Character character = (com.mygdx.game.Network.Character) object;
			characterList.add(character);
			
			
		} else if (object instanceof Network.Bullet) {
			Network.Bullet bullet = (Network.Bullet) object;
			bulletList.add(bullet);
			
		}else if (object instanceof Network.BulletDead) {
			Network.BulletDead bulletDead = (Network.BulletDead) object;
			for(Network.Bullet bullet: bulletList){
				if(bullet.id == bulletDead.id){
					bulletList.remove(bullet);
					main.mm.mapFill(new Vector3(bullet.x,bullet.y,0));
					return;
				}
			}
			
		} else if (object instanceof Network.Message) {
			Network.Message message = (Network.Message) object;
			System.out.println(message.message);
			
		} else if (object instanceof IdentificationNumber) {
			IdentificationNumber id = (IdentificationNumber) object;
			System.out.println("your identification Number is: " + id.serial);
			this.id = id.serial;
			
		} else if( object instanceof HasDisconnected){
			HasDisconnected dc = (HasDisconnected) object;
			Network.Character toRemove = null;
			for(Network.Character character: characterList){
				if(character.id == dc.id){
					toRemove = character;
					
				}
			}
			
			characterList.remove(toRemove);
			
			
		}

	}
	
	public void update(){
		
	}
	
	public void sendMyCharacter(Player player){
		Network.Character character = new Network.Character();
		character.x = player.x;
		character.y = player.y;
		character.id = id;
		
		client.sendTCP(character);
	}
	
	public void sendBullet(Network.Bullet bullet){
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