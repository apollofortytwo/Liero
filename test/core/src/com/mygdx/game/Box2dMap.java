package com.mygdx.game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.math.Circle;
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

public class Box2dMap {
	public static ArrayList<Bullet> bulletList = new ArrayList<Bullet>();
	MapManager mm;
	World world;

	Box2dMap(MapManager mm, World world) {
		this.mm = mm;
		this.world = world;

	}

	public void update() {

		collisionDetection();
		for (Debris d : debris) {
			d.shorten(debris);
		}
		Explosion.update();
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
				if (body.getType().equals(BodyType.StaticBody)) {
					((UserData) body.getUserData()).toDelete = true;
				}
			}
		}

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
