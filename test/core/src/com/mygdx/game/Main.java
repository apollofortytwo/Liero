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
	static int seg = 8;

	public static final int SCALE = 32;

	@Override
	public void create() {

		Box2D.init();
		Bullet.init();
		world = new World(new Vector2(0, 9.81f), true);
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

		mm = new MapManager(img.getTextureData().consumePixmap(), cam, world);

		player = new Player(32, 0, world);
		mm.looper(seg);
		Explosion.world = world;
	}

	public void update() {
		cam.position.set(new Vector2(player.x, player.y), 0);
		Gdx.graphics.setTitle(String.valueOf(Gdx.graphics.getFramesPerSecond()));
		mm.update();

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
		rh.setCulling(false);

		update();

		world.step(1 / 30f, 6, 6);

		b2dr.render(world, matrix);

		sr.end();

	}

}
