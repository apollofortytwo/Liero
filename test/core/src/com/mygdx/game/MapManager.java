package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class MapManager {
	Box2dMap b2dMap;
	public static Pixmap currentMap;
	public ArrayList<Rectangle> points = new ArrayList<Rectangle>();
	OrthographicCamera cam;
	World world;
	
	MapManager(Pixmap map, OrthographicCamera cam, World world) {
		currentMap = map;
		this.cam = cam;
		b2dMap = new Box2dMap(this,world);
		
		
		
	}
	
	public void update(){

		b2dMap.update();
		currentMap.setColor(Color.WHITE);

		Vector3 pos = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Circle c = new Circle();
		c.set(pos.x / Main.SCALE, pos.y / Main.SCALE, 5);

		b2dMap.clearStaticBodies();

		for (Rectangle rect : points) {
			b2dMap.createGround(rect);
		}
		
		if (Gdx.input.isKeyPressed(Keys.Q)) {
			Vector3 posMap = new Vector3(pos.x / Main.SCALE, pos.y / Main.SCALE, 0);
			mapFill(posMap);
		}
		

	}

	public void looper(int seg) {
		looper(new Rectangle(0, 0, MapManager.currentMap.getWidth(), MapManager.currentMap.getHeight()), seg);
	}

	public void looper(Rectangle rect, int seg) {

		if (this.recttOnlyContainsBlack(currentMap, rect)) {

			if (!points.contains(rect)) {
				points.add(rect);
			}

			return;
		} else if (seg > 0) {
			looper(new Rectangle(rect.x, rect.y, rect.width / 2, rect.height / 2), seg - 1);
			looper(new Rectangle(rect.x + (rect.width / 2), rect.y, rect.width / 2, rect.height / 2), seg - 1);
			looper(new Rectangle(rect.x + (rect.width / 2), rect.y + (rect.height / 2), rect.width / 2, rect.height / 2), seg - 1);
			looper(new Rectangle(rect.x, rect.y + (rect.height / 2), rect.width / 2, rect.height / 2), seg - 1);
		}

	}
	


	private boolean recttOnlyContainsBlack(Pixmap map, Rectangle rectt) {
		for (int x = (int) rectt.x; x < rectt.x + rectt.width; x++) {
			for (int y = (int) rectt.y; y < rectt.y + rectt.height; y++) {
				if (!new Color(map.getPixel(x, y)).equals(Color.BLACK)) {
					return false;
				}
			}
		}
		return true;
	}


	

	
	public void mapFill(Vector3 pos) {
		ArrayList<Rectangle> toRemove = new ArrayList<Rectangle>();

		MapManager.currentMap.fillCircle((int) pos.x, (int) pos.y, 10);

		Circle c = new Circle();
		c.set(pos.x, pos.y, 10);

		for (Rectangle rect : this.points) {
			if (Intersector.overlaps(c, rect)) {
				toRemove.add(rect);
				b2dMap.createDebris(rect, c);
			}
		}
		this.points.removeAll(toRemove);

		this.looper(Main.seg);
		Explosion.explode(40, 10f, 3200f, c.x, c.y);
		Explosion.drawExplosion(c);
	}
	

	

}
