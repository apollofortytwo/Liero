package com.mygdx.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Box2dMap {
	public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	MapManager mm;
	World world;

	Box2dMap(MapManager mm, World world) {
		this.mm = mm;
		this.world = world;
		generateWalls();

	}

	public void update() {

		collisionDetection();
		for (Debris d : debris) {
			d.shorten(debris);
		}
		Explosion.update();
	}

	public void render(ShapeRenderer sr) {
		renderWalls(sr);
	}

	public void collisionDetection() {
		try {
			for (Contact contact : world.getContactList()) {
				bulletCheck(contact);
			}

		} catch (Exception e) {

		}

	}

	public void bulletCheck(Contact contact) {
		Fixture bullet = null;
		Vector3 pos = new Vector3();
		if (((UserData) contact.getFixtureA().getBody().getUserData()).name.equals("Bullet")) {
			bullet = contact.getFixtureA();
			pos = new Vector3(contact.getFixtureA().getBody().getPosition(), 0);
		}
		if (((UserData) contact.getFixtureB().getBody().getUserData()).name.equals("Bullet")) {
			bullet = contact.getFixtureB();
			pos = new Vector3(contact.getFixtureB().getBody().getPosition(), 0);
		}
		explodeAndRemoveBullet(bullet);

	}

	public void explodeAndRemoveBullet(Fixture bullet) {
		if (bullet != null) {
			mm.mapFill(new Vector3(bullet.getBody().getPosition().x, bullet.getBody().getPosition().y, 0));
			((UserData) bullet.getBody().getUserData()).toDelete = true;

		}
	}

	ArrayList<Debris> debris = new ArrayList<Debris>();
	Random rand = new Random();

	public void createDebris(Rectangle rect, Circle c) {
		if (rand.nextInt(100) > 75) {
			debris.add(new Debris(world, rect));
		}

	}

	public void clearStaticBodies() {
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);

		for (Iterator<Body> iter = bodies.iterator(); iter.hasNext();) {
			Body body = iter.next();
			if (body != null) {
				if (((UserData) body.getUserData()).name.equals("Ground")) {
					((UserData) body.getUserData()).toDelete = true;
				}
			}
		}

	}

	Body leftBody;
	Body topBody;
	Body bottomBody;
	Body rightBody;

	public void generateWalls() {
		BodyDef leftBd = new BodyDef();
		leftBd.position.set(-5, 0);
		leftBd.type = BodyDef.BodyType.StaticBody;
		leftBody = world.createBody(leftBd);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(5, mm.currentMap.getHeight() * 32);
		Fixture fix = leftBody.createFixture(shape, 0);
		UserData ud = new UserData();
		ud.name = "Wall";
		leftBody.setUserData(ud);

		BodyDef topBd = new BodyDef();
		topBd.position.set(0, -5);
		topBd.type = BodyDef.BodyType.StaticBody;
		topBody = world.createBody(topBd);
		shape = new PolygonShape();
		shape.setAsBox(mm.currentMap.getWidth() * 32, 5);
		fix = topBody.createFixture(shape, 0);
		ud = new UserData();
		ud.name = "Wall";
		topBody.setUserData(ud);

		BodyDef bottomBd = new BodyDef();
		bottomBd.position.set(0, mm.currentMap.getHeight() + 5);
		bottomBd.type = BodyDef.BodyType.StaticBody;
		bottomBody = world.createBody(bottomBd);
		shape = new PolygonShape();
		shape.setAsBox(mm.currentMap.getWidth() * 32, 5);
		fix = bottomBody.createFixture(shape, 0);
		ud = new UserData();
		ud.name = "Wall";
		bottomBody.setUserData(ud);

		BodyDef rightBd = new BodyDef();
		rightBd.position.set(mm.currentMap.getWidth() + 5, 0);
		rightBd.type = BodyDef.BodyType.StaticBody;
		rightBody = world.createBody(rightBd);
		shape = new PolygonShape();
		shape.setAsBox(5, mm.currentMap.getHeight() * 32);
		fix = rightBody.createFixture(shape, 0);
		ud = new UserData();
		ud.name = "Wall";
		rightBody.setUserData(ud);

	}

	public void renderWalls(ShapeRenderer sr) {

		sr.begin();
		sr.setColor(Color.RED);
		Vector2 pos = leftBody.getWorldCenter();
		sr.rect(pos.x - 5, pos.y - 100, 10, mm.currentMap.getHeight() * 64);

		pos = topBody.getWorldCenter();
		sr.rect(pos.x - 100, pos.y - 5, mm.currentMap.getWidth() * 64, 10);

		pos = bottomBody.getWorldCenter();
		sr.rect(pos.x - 100, pos.y - 5, mm.currentMap.getWidth() * 64, 10);

		pos = rightBody.getWorldCenter();
		sr.rect(pos.x - 5, pos.y - 100, 10, mm.currentMap.getHeight() * 64);
		
		sr.end();
	}

	public void createGround(Rectangle rect) {
		BodyDef bd = new BodyDef();
		bd.position.set(rect.x + rect.getWidth() / 2, rect.y + rect.getHeight() / 2);

		bd.type = BodyDef.BodyType.StaticBody;
		Body body = world.createBody(bd);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rect.width / 2, rect.height / 2);
		Fixture fix = body.createFixture(shape, 0);
		UserData ud = new UserData();
		ud.name = "Ground";
		body.setUserData(ud);
	}
}
