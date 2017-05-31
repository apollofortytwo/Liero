package com.mygdx.game;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	static int udpPort = 27962, tcpPort = 27962;
	static String ip = "10.194.10.207";
	
	public static Kryo register(EndPoint endPoint){
		Kryo kryo = endPoint.getKryo();
		kryo.register(Message.class);
		kryo.register(CharacterMove.class);
		kryo.register(Character.class);
		kryo.register(Bullet.class);
		kryo.register(IdentificationNumber.class);
		kryo.register(HasDisconnected.class);
		kryo.register(float[][].class);
		kryo.register(float[].class);
		kryo.register(Map.class);
		
		return kryo;
	}
	
	public static class Message{
		String message;
	}
	
	public static class HasDisconnected{
		int id;
	}
	
	public static class CharacterMove{
		int id;
		int x, y;
	}
	
	public static class Character{
		int id;
		int x,y;
		
	}
	
	public static class Bullet{
		int id;
		int xPos, yPos;
		int x, y;
		int xforce, yforce;
		
	}
	
	public static class Map{
		float[][] map;
		
	}
	
	public static class IdentificationNumber{
		int serial;
	}
}
