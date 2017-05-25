package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.utils.Array;

import box2dLight.RayHandler;

public class Main extends ApplicationAdapter {

	public static RayHandler rh;

	public static ShapeRenderer sr;

	public static OrthographicCamera cam;
	CameraController cc;
	Player player;
	World world;
	Box2DDebugRenderer b2dr;
	MapManager mm;
	private int seg = 8;

	public static final int SCALE = 32;

	@Override
	public void create() {

		Box2D.init();
		Bullet.init();
		world = new World(new Vector2(0, 900f), true);
		b2dr = new Box2DDebugRenderer();
		rh = new RayHandler(world);

		sr = new ShapeRenderer();
		sr.setAutoShapeType(true);

		float w = (float) Gdx.graphics.getWidth();
		float h = (float) Gdx.graphics.getHeight();
		cam = new OrthographicCamera(w, (w + h) / 2);

		cam.setToOrtho(true);
		cam.combined.scl(1000);

		cc = new CameraController(cam);
		Gdx.input.setInputProcessor(cc);

		Texture img = new Texture("map.jpg");
		if (!img.getTextureData().isPrepared()) {
			img.getTextureData().prepare();
		}

		mm = new MapManager(img.getTextureData().consumePixmap());

		player = new Player(32, 0, world);
		mm.looper(seg);
		Explosion.world = world;
	}

	public void update() {
		cam.position.set(new Vector2(player.x, player.y), 0);
		Gdx.graphics.setTitle(String.valueOf(Gdx.graphics.getFramesPerSecond()));

		try {
			for (Contact contact : world.getContactList()) {
				this.bulletCheck(contact);
				this.fallCheck(contact);
			}
		} catch (Exception e) {

		}
		for(Debris d: debris){
			d.shorten(debris);
		}
		

	}

	private void fallCheck(Contact contact) {
		if (contact.getFixtureA().getUserData().equals("Falling")) {
			if (contact.getFixtureB().getUserData().equals("Ground")) {
				contact.getFixtureA().setUserData("Player");
			}
		}
		if (contact.getFixtureB().getUserData().equals("Falling")) {
			if (contact.getFixtureA().getUserData().equals("Ground")) {
				contact.getFixtureB().setUserData("Player");
			}
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		cam.update();
		cc.update();
		player.update();

		Matrix4 matrix = cam.combined.scl(SCALE);
		sr.setProjectionMatrix(matrix);
		sr.begin(ShapeRenderer.ShapeType.Point);

		rh.setCombinedMatrix(matrix);
		rh.updateAndRender();

		// points.clear();

		update();
		mm.currentMap.setColor(Color.WHITE);

		Vector3 pos = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		Circle c = new Circle();
		c.set(pos.x / SCALE, pos.y / SCALE, 5);
		sr.setColor(Color.RED);

		sr.circle(c.x, c.y, c.radius);
		sr.setColor(Color.WHITE);
		clearStaticBodies();

		if (Gdx.input.isKeyPressed(Keys.Q)) {
			Vector3 posMap = new Vector3(pos.x / SCALE, pos.y / SCALE, 0);
			mapFill(posMap);
		}

		for (Rectangle rect : mm.points) {
			createGround(rect);
		}

		world.step(1 / 30f, 6, 6);

		b2dr.render(world, matrix);

		sr.end();

	}

	public void clearStaticBodies() {
		Array<Body> bodies = new Array<Body>();
		world.getBodies(bodies);

		for (Body body : bodies) {
			if (body.getType().equals(BodyType.StaticBody)) {
				world.destroyBody(body);
			}
		}
	}

	ArrayList<Debris> debris = new ArrayList<Debris>();
	Random rand = new Random();

	public void debris(Rectangle rect, Circle c) {
		if (rand.nextInt(100) > 75) {
			debris.add(new Debris(world, rect));
		}

	}

	public void bulletCheck(Contact contact) {

		Fixture bullet = null;
		Vector3 pos = new Vector3();
		if (contact.getFixtureA().getUserData().equals("Bullet")
				&& !contact.getFixtureA().getUserData().equals("Player")) {
			bullet = contact.getFixtureA();
			pos = new Vector3(contact.getFixtureA().getBody().getPosition(), 0);

		}

		if (contact.getFixtureB().getUserData().equals("Bullet")
				&& !contact.getFixtureA().getUserData().equals("Player")) {
			bullet = contact.getFixtureB();
			pos = new Vector3(contact.getFixtureB().getBody().getPosition(), 0);

		}

		if (bullet != null) {
			mapFill(pos);
			world.destroyBody(bullet.getBody());
		}
	}

	public void mapFill(Vector3 pos) {
		ArrayList<Rectangle> toRemove = new ArrayList<Rectangle>();

		mm.currentMap.fillCircle((int) pos.x, (int) pos.y, 10);

		Circle c = new Circle();
		c.set(pos.x, pos.y, 10);

		for (Rectangle rect : mm.points) {
			if (Intersector.overlaps(c, rect)) {
				toRemove.add(rect);
				this.debris(rect, c);
			}
		}
		mm.points.removeAll(toRemove);

		mm.looper(seg);
		Explosion.explode(40, 10f, 3200f, c.x, c.y);

	}

	public void createGround(Rectangle rect) {
		BodyDef bd = new BodyDef();
		bd.position.set(rect.x + rect.getWidth() / 2, rect.y + rect.getHeight() / 2);

		bd.type = BodyDef.BodyType.StaticBody;
		Body body = world.createBody(bd);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(rect.width / 2, rect.height / 2);
		Fixture fix = body.createFixture(shape, 0);
		fix.setUserData("Ground");
	}

}
